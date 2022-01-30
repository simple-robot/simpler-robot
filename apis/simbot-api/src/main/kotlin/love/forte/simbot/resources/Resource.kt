/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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
import love.forte.simbot.definition.IDContainer
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

/**
 *
 * 一个[资源][Resource]. 资源存在两种形式：[标识资源][IDResource] 和 [流资源][StreamableResource].
 *
 * [IDResource] 代表一个持有唯一标识 [ID] 的资源，一般可以使用在接收者在允许通过ID解析的情况下。
 * [StreamableResource] 则代表一个允许获取数据流的资源，这通常代表了对本地资源（比如文件）、远程资源（比如某个链接）等资源，
 * 一般可以使用在接收者需要上传某些资源或者得到了一些能够下载的资源的情况下。比如上传图片、下载图片。
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
        public fun of(id: ID, name: String = id.literal): IDResource = IDResource(id, name)

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
        @Throws(IOException::class)
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
 * [IDResource] 通常情况下用于作为参数提供者来提供一个资源的标识，并由接受者来自行处理。
 *
 * @see Resource.of
 */
@SerialName("simbot.resource.id")
@Serializable
public open class IDResource(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public override val id: ID, override val name: String
) : Resource(), IDContainer {
    override fun toString(): String {
        return "Resource(id=$id, name=$name)"
    }
}


/**
 * 提供一个可以开启输入流的 [Resource] 实例。
 *
 * 通过 [openStream] 得到的数据流不会被管理，应当由使用者自行管理、关闭。
 *
 * [StreamableResource] 实现 [Closeable],
 * 一个被 close 的 [StreamableResource] 将不应再继续使用，但是 [StreamableResource.close] 不会影响到已经产生的流对象。
 *
 * @see Resource.of
 */
@SerialName("simbot.resource.streamable")
@Serializable
public sealed class StreamableResource : Resource(), Closeable {
    @Throws(IOException::class)
    public abstract fun openStream(): InputStream

    /**
     * [StreamableResource] 在使用的过程中可能会产生一些需要手动进行 [close] 的产物，
     * 因此在不使用 [StreamableResource] 的时候，使用 [close] 对其进行关闭。
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    abstract override fun close()
}


/**
 * 使用[URL]作为输入流来源的 [StreamableResource].
 */
@SerialName("simbot.resource.url")
@Serializable
public class URLResource(
    @Serializable(URLSerializer::class)
    public val url: URL,
    override val name: String = url.toString()
) : StreamableResource() {

    @Throws(IOException::class)
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
    public val file: File,
    override val name: String = file.toString(),
    @Transient
    private val doClose: () -> Unit = {}
) : StreamableResource() {

    @Throws(FileNotFoundException::class)
    override fun openStream(): FileInputStream {
        return FileInputStream(file)
    }

    override fun toString(): String {
        return "Resource(file=$file, name=$name)"
    }

    @JvmOverloads
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
 *
 * @property doClose 当执行 [close] 时可以选择提供执行操作。
 */
@SerialName("simbot.resource.path")
@Serializable
public class PathResource(
    @Serializable(PathSerializer::class)
    public val path: Path,
    override val name: String = path.toString(),
    @Transient
    private val doClose: () -> Unit = {}
) : StreamableResource() {

    @Throws(IOException::class)
    override fun openStream(): InputStream = path.inputStream(StandardOpenOption.READ)

    override fun toString(): String {
        return "Resource(path=$path, name=$name)"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @Throws(IOException::class)
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
    /**
     * 得到当前资源中字节数组的**副本**。
     */
    public val bytes: ByteArray get() = byteArray.copyOf()

    /**
     * 将当前资源中字节数组拷贝到目标数组中。
     */
    @JvmOverloads
    public fun copyTo(
        destination: ByteArray,
        destinationOffset: Int = 0,
        startIndex: Int = 0,
        endIndex: Int = byteArray.size
    ) {
        byteArray.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    /**
     * 字节数组的大小。
     */
    @JvmField
    public val size: Int = byteArray.size

    /**
     * 获取指定索引位的字节。
     */
    public operator fun get(index: Int): Byte = byteArray[index]

    /**
     * 得到字节数组输入流。
     */
    override fun openStream(): ByteArrayInputStream {
        return byteArray.inputStream()
    }

    override fun toString(): String {
        return "Resource(size of bytes=${size}, name=$name)"
    }

    override fun close() {
        // Nothing
    }
}


public fun ID.toResource(name: String): IDResource = Resource.of(this, name)

public fun URL.toResource(name: String = this.toString()): StreamableResource = Resource.of(this, name)

public fun File.toResource(name: String = this.toString()): StreamableResource = Resource.of(this, name)

public fun Path.toResource(name: String = this.toString()): StreamableResource = Resource.of(this, name)

public fun ByteArray.toResource(name: String): StreamableResource = Resource.of(this, name)

@Throws(IOException::class)
public fun InputStream.toResource(name: String? = null): StreamableResource = Resource.of(this, name)

@Throws(IOException::class)
public fun InputStream.useToResource(name: String? = null): StreamableResource = this.use { i -> i.toResource(name) }