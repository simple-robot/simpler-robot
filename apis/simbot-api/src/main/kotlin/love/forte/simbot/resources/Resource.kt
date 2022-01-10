/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("Resources")
package love.forte.simbot.resources

import love.forte.simbot.ID
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

/**
 *
 * 一个[资源][Resource].
 *
 * @see IDResource
 * @see StreamableResource
 *
 * @author ForteScarlet
 */
public sealed class Resource {

    /**
     * 得到资源名称。
     */
    public abstract val name: String


    public companion object {

        @JvmStatic
        public fun of(id: ID, name: String): IDResource = IDResource(id, name)

        @JvmStatic
        public fun of(url: URL): StreamableResource = URLResource(url)

        @JvmStatic
        public fun of(file: File): StreamableResource = FileResource(file)

        @JvmStatic
        public fun of(path: Path): StreamableResource = PathResource(path)

        @JvmStatic
        public fun of(byteArray: ByteArray, name: String): StreamableResource = ByteArrayResource(name, byteArray)

        /**
         * 拷贝提供的 [inputStream] 并作为 [StreamableResource] 返回。
         * 不会自动关闭 [inputStream], 需要由调用者处理。
         */
        @JvmStatic
        public fun of(inputStream: InputStream): StreamableResource {
            val temp = kotlin.io.path.createTempFile(
                Path(".simbot/tmp").also {
                    Files.createDirectories(it)
                    it.toFile().deleteOnExit()
                },
            )
            temp.outputStream(StandardOpenOption.CREATE).use(inputStream::copyTo)
            temp.toFile().deleteOnExit()

            return PathResource(temp) { temp.deleteIfExists() }
        }
    }
}

/**
 * 一个提供了 [id] 信息的 [Resource].
 *
 * [IDResource] 通常情况下用于作为参数提供者来提供一个资源，并由接受者来自行处理。
 *
 */
public open class IDResource(public open val id: ID, override val name: String) : Resource()


/**
 * 提供一个可以开启输入流的 [Resource] 实例。
 *
 * [StreamableResource] 实现 [Closeable],
 * 一个被 close 的 [StreamableResource] 将不应再继续使用。
 *
 */
public abstract class StreamableResource : Resource(), Closeable {
    public abstract fun openStream(): InputStream

}


/**
 * 使用[URL]作为输入流来源的 [StreamableResource].
 */
public class URLResource(private val url: URL) : StreamableResource() {
    override val name: String = url.toString()
    override fun openStream(): InputStream {
        return url.openStream()
    }

    override fun close() {
    }
}

/**
 * 使用[File]作为输入流来源的 [StreamableResource].
 */
public class FileResource(private val file: File, private val doClose: () -> Unit = {}) : StreamableResource() {
    override val name: String = file.toString()
    override fun openStream(): FileInputStream {
        return FileInputStream(file)
    }

    public fun randomAccessFile(mode: String = "r"): RandomAccessFile = RandomAccessFile(file, mode)

    override fun close() {
        doClose()
    }
}

/**
 * 使用[Path]作为输入流来源的 [StreamableResource].
 */
public class PathResource(private val path: Path, private val doClose: () -> Unit = {}) : StreamableResource() {
    override val name: String = path.toString()
    override fun openStream(): InputStream = path.inputStream(StandardOpenOption.READ)

    @Suppress("MemberVisibilityCanBePrivate")
    public fun openStream(vararg options: OpenOption): InputStream = path.inputStream(*options)

    override fun close() {
        doClose()
    }
}

/**
 * 使用 [ByteArray] 字节数组作为输入流来源的 [StreamableResource].
 */
public class ByteArrayResource(override val name: String, private val byteArray: ByteArray) : StreamableResource() {
    override fun openStream(): ByteArrayInputStream {
        return byteArray.inputStream()
    }

    override fun close() {
    }
}


public fun ID.toResource(name: String): IDResource = Resource.of(this, name)
public fun URL.toResource(): StreamableResource = Resource.of(this)
public fun File.toResource(): StreamableResource = Resource.of(this)
public fun Path.toResource(): StreamableResource = Resource.of(this)
public fun ByteArray.toResource(name: String): StreamableResource = Resource.of(this, name)
public fun InputStream.toResource(): StreamableResource = Resource.of(this)
public fun InputStream.useToResource(): StreamableResource = this.use(Resource::of)