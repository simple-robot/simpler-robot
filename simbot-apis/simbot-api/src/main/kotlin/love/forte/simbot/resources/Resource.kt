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
 */

@file:JvmName("Resources")

package love.forte.simbot.resources

import kotlinx.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.resources.Resource.Companion.toResource
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

/**
 *
 * 一个[资源][Resource]. 代表一个允许获取数据流的资源，这通常代表了对本地资源（比如文件）、远程资源（比如某个链接）等资源，
 * 一般可以使用在接收者需要上传某些资源或者得到了一些能够下载的资源的情况下。比如上传图片、下载图片。
 *
 * @see StandardResource
 *
 * @author ForteScarlet
 */
public interface Resource : Closeable {
    
    /**
     * 得到资源名称。
     */
    public val name: String
    
    /**
     * 得到当前资源中所对应的数据流。
     */
    @Throws(IOException::class)
    public fun openStream(): InputStream
    
    /**
     * [StandardResource] 在使用的过程中可能会产生一些需要手动进行 [close] 的产物，
     * 因此在不使用 [StandardResource] 的时候，使用 [close] 对其进行关闭。
     *
     * [close] 操作仅代表针对当前的 [Resource] 对象本身，不会对任何已经产生的结果造成影响。
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun close()
    
    
    public companion object {
        
        /**
         * 使用 [URL] 作为一个 [StandardResource].
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("of")
        public fun URL.toResource(name: String = toString()): URLResource = URLResource(this, name)
        
        /**
         * 使用 [File] 作为一个 [StandardResource].
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("of")
        public fun File.toResource(name: String = toString()): FileResource = FileResource(this, name)
        
        /**
         * 使用 [Path] 作为一个 [StandardResource].
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("of")
        public fun Path.toResource(name: String = toString()): PathResource = PathResource(this, name)
        
        /**
         * 使用字节数组作为一个 [StandardResource].
         */
        @JvmStatic
        @JvmName("of")
        public fun ByteArray.toResource(name: String): ByteArrayResource =
            ByteArrayResource(name, this)
        
        /**
         * 拷贝提供的 [inputStream] 并作为 [StandardResource] 返回。
         * 不会自动关闭 [inputStream], 需要由调用者处理。
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("of")
        @Throws(IOException::class)
        public fun InputStream.toResource(name: String? = null): StandardResource {
            val temp = createTempFile(
                Path(".simbot/tmp").also {
                    Files.createDirectories(it)
                    it.toFile().deleteOnExit()
                },
            )
            temp.outputStream(StandardOpenOption.CREATE).use(this::copyTo)
            temp.toFile().deleteOnExit()
            
            return PathResource(temp, name ?: temp.toString()) { temp.deleteIfExists() }
        }
    }
    
    /**
     * 尝试将 [Resource] 视作 [StandardResource] 的序列化器。
     * 如果实际类型不是 [StandardResource], 则会引发异常。
     */
    @ExperimentalSimbotApi
    public object AsStandardSerializer : KSerializer<Resource> {
        override fun deserialize(decoder: Decoder): Resource {
            return StandardResource.serializer().deserialize(decoder)
        }
        
        override val descriptor: SerialDescriptor = StandardResource.serializer().descriptor
        
        override fun serialize(encoder: Encoder, value: Resource) {
            if (value !is StandardResource) throw SerializationException("Type of value must be StandardResource, but ${value::class}")
            
            StandardResource.serializer().serialize(encoder, value)
        }
    }
}


/**
 * [Resource] 的标准实现类型.
 *
 * [StandardResource] 支持序列化，但是序列化结果并不可靠。
 * [StandardResource] 的实现类型大多数依靠 [File]、[Path] 等系统资源，
 * 而这些系统资源无法保障其可用性和有效性（有些实现中可能还会用到临时资源），
 * 因此序列化再反序列化的结果不一定仍然可用（例如对应的系统资源已被删除）。
 *
 * @see Resource.toResource
 */
@SerialName("simbot.resource.standard")
@Serializable
public sealed class StandardResource : Resource, Closeable


/**
 * 使用[URL]作为输入流来源的 [StandardResource].
 */
@SerialName("simbot.resource.url")
@Serializable
public class URLResource(
    @Serializable(URLSerializer::class)
    public val url: URL,
    override val name: String = url.toString(),
) : StandardResource() {
    
    /**
     * @see URL.openStream
     */
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
 * 使用[File]作为输入流来源的 [StandardResource].
 */
@SerialName("simbot.resource.file")
@Serializable
public class FileResource(
    @Serializable(FileSerializer::class)
    public val file: File,
    override val name: String = file.toString(),
    @Transient
    private val doClose: () -> Unit = {},
) : StandardResource() {
    
    /**
     * @see FileInputStream
     */
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
 * 使用[Path]作为输入流来源的 [StandardResource].
 *
 * @param doClose 当执行 [close] 时可以选择提供执行操作。
 */
@SerialName("simbot.resource.path")
@Serializable
public class PathResource(
    @Serializable(PathSerializer::class)
    public val path: Path,
    override val name: String = path.toString(),
    @Transient
    private val doClose: () -> Unit = {},
) : StandardResource() {
    
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
 * 使用 [ByteArray] 字节数组作为输入流来源的 [StandardResource].
 */
@SerialName("simbot.resource.bytes")
@Serializable
public class ByteArrayResource(override val name: String, private val byteArray: ByteArray) :
    StandardResource() {
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
        endIndex: Int = byteArray.size,
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


/**
 * 将作为receiver的输入流转化为一个 [StandardResource], 同时关闭输入流。
 */
@Throws(IOException::class)
public fun InputStream.useToResource(name: String? = null): StandardResource =
    use { i -> i.toResource(name) }