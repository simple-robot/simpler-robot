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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.ID
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

/**
 *
 * 一个[资源][Resource].
 *
 * @see IDResource
 * @see StreamableResource
 *
 * @author ForteScarlet
 */
@SerialName("simbot.resource")
@Serializable
public sealed class Resource {

    /**
     * 得到资源名称。
     */
    public abstract val name: String


    public companion object {

        /**
         * 使用 [ID] 构建一个 [IDResource].
         */
        @JvmStatic
        @JvmOverloads
        public fun of(id: ID, name: String = id.toString()): IDResource = IDResource(id, name)

        /**
         * 使用 [URL] 作为一个 [StreamableResource].
         */
        @JvmStatic
        @JvmOverloads
        public fun of(url: URL, name: String = url.toString()): StreamableResource = URLResource(url, name)

        /**
         * 使用 [File] 作为一个 [StreamableResource].
         */
        @JvmStatic
        @JvmOverloads
        public fun of(file: File, name: String = file.toString()): StreamableResource = FileResource(file, name)

        /**
         * 使用 [Path] 作为一个 [StreamableResource].
         */
        @JvmStatic
        @JvmOverloads
        public fun of(path: Path, name: String = path.toString()): StreamableResource = PathResource(path, name)

        /**
         * 使用字节数组作为一个 [StreamableResource].
         */
        @JvmStatic
        public fun of(byteArray: ByteArray, name: String): StreamableResource = ByteArrayResource(name, byteArray)

        /**
         * 拷贝提供的 [inputStream] 并作为 [StreamableResource] 返回。
         * 不会自动关闭 [inputStream], 需要由调用者处理。
         */
        @JvmStatic
        @JvmOverloads
        public fun of(inputStream: InputStream, name: String? = null): StreamableResource {
            val temp = createTempFile(
                Path(".simbot/tmp").also {
                    Files.createDirectories(it)
                    it.toFile().deleteOnExit()
                },
            )
            temp.outputStream(StandardOpenOption.CREATE).use(inputStream::copyTo)
            temp.toFile().deleteOnExit()

            return PathResource(temp, name ?: temp.toString()) { temp.deleteIfExists() }
        }
    }
}

/**
 * 一个提供了 [id] 信息的 [Resource].
 *
 * [IDResource] 通常情况下用于作为参数提供者来提供一个资源，并由接受者来自行处理。
 *
 */
@SerialName("simbot.resource.id")
@Serializable
public open class IDResource(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public open val id: ID, override val name: String
) : Resource() {
    override fun toString(): String {
        return "Resource(id=$id, name=$name)"
    }
}


/**
 * 提供一个可以开启输入流的 [Resource] 实例。
 *
 * [StreamableResource] 实现 [Closeable],
 * 一个被 close 的 [StreamableResource] 将不应再继续使用。
 *
 */
@SerialName("simbot.resource.streamable")
@Serializable
public sealed class StreamableResource : Resource(), Closeable {
    public abstract fun openStream(): InputStream

}


/**
 * 使用[URL]作为输入流来源的 [StreamableResource].
 */
@SerialName("simbot.resource.url")
@Serializable
public class URLResource(
    @Serializable(URLSerializer::class)
    private val url: URL,
    override val name: String = url.toString()
) : StreamableResource() {

    override fun openStream(): InputStream {
        return url.openStream()
    }

    override fun toString(): String {
        return "Resource(url=$url, name=$name)"
    }
    override fun close() {
    }
}

internal object URLSerializer : KSerializer<URL> {
    override fun deserialize(decoder: Decoder): URL {
        val url = decoder.decodeString()
        return URL(url)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.net.URL", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: URL) {
        encoder.encodeString(value.toString())
    }
}

/**
 * 使用[File]作为输入流来源的 [StreamableResource].
 */
@SerialName("simbot.resource.file")
@Serializable
public class FileResource(
    @Serializable(FileSerializer::class)
    private val file: File,
    override val name: String = file.toString(),
    @Transient
    private val doClose: () -> Unit = {}
) : StreamableResource() {

    override fun openStream(): FileInputStream {
        return FileInputStream(file)
    }
    override fun toString(): String {
        return "Resource(file=$file, name=$name)"
    }
    public fun randomAccessFile(mode: String = "r"): RandomAccessFile = RandomAccessFile(file, mode)

    override fun close() {
        doClose()
    }
}

internal object FileSerializer : KSerializer<File> {
    override fun deserialize(decoder: Decoder): File {
        val pathname = decoder.decodeString()
        return File(pathname)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("file", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: File) {
        encoder.encodeString(value.path)
    }

}


/**
 * 使用[Path]作为输入流来源的 [StreamableResource].
 */
@SerialName("simbot.resource.path")
@Serializable
public class PathResource(
    @Serializable(PathSerializer::class)
    private val path: Path,
    override val name: String = path.toString(),
    @Transient
    private val doClose: () -> Unit = {}
) : StreamableResource() {

    override fun openStream(): InputStream = path.inputStream(StandardOpenOption.READ)
    override fun toString(): String {
        return "Resource(path=$path, name=$name)"
    }
    @Suppress("MemberVisibilityCanBePrivate")
    public fun openStream(vararg options: OpenOption): InputStream = path.inputStream(*options)

    override fun close() {
        doClose()
    }
}

internal object PathSerializer : KSerializer<Path> {
    override fun deserialize(decoder: Decoder): Path = Path(decoder.decodeString())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.nio.Path", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.pathString)
    }
}

/**
 * 使用 [ByteArray] 字节数组作为输入流来源的 [StreamableResource].
 */
@SerialName("simbot.resource.bytes")
@Serializable
public class ByteArrayResource(override val name: String, private val byteArray: ByteArray) : StreamableResource() {
    override fun openStream(): ByteArrayInputStream {
        return byteArray.inputStream()
    }
    override fun toString(): String {
        return "Resource(byteArray(size)=${byteArray.size}, name=$name)"
    }
    override fun close() {
    }
}


public fun ID.toResource(name: String): IDResource = Resource.of(this, name)
public fun URL.toResource(name: String = this.toString()): StreamableResource = Resource.of(this, name)
public fun File.toResource(name: String = this.toString()): StreamableResource = Resource.of(this, name)
public fun Path.toResource(name: String = this.toString()): StreamableResource = Resource.of(this, name)
public fun ByteArray.toResource(name: String): StreamableResource = Resource.of(this, name)
public fun InputStream.toResource(name: String? = null): StreamableResource = Resource.of(this, name)
public fun InputStream.useToResource(name: String? = null): StreamableResource = this.use { i -> i.toResource(name) }