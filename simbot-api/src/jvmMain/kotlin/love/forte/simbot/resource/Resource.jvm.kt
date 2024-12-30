/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("Resources")
@file:JvmMultifileClass

package love.forte.simbot.resource

import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import love.forte.simbot.resource.JvmStringReadableResource.Companion.DEFAULT_CHARSET
import java.io.*
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.readBytes
import kotlin.io.path.reader

/**
 * 从 [SourceResource] 中通过 [Source] 获取一个 [InputStream]。
 *
 * @since 4.10.0
 */
@Throws(Exception::class)
public fun SourceResource.inputStream(): InputStream {
    return source().asInputStream()
}

/**
 * 提供JVM平台独特实现的类型，与 [SourceResource] 内容相同。
 *
 * @see FileResource
 * @see PathResource
 * @see URIResource
 */
public sealed interface JvmSourceResource : SourceResource

/**
 * 能够获取到 [InputStream] 资源的 [Resource] 扩展实现。
 *
 * Deprecated since v4.10.0: 直接通过 [SourceResource.inputStream]
 * 或 [Source.asInputStream] 即可将 [SourceResource] 中的 [Source] 转化为 [InputStream]。
 *
 * ```kotlin
 * val input1 = resource.inputStream()
 * val input2 = resource.source().asInputStream()
 * ```
 *
 * Java 中分别对应 `Resources.inputStream(sourceResource)`
 * 和 `SourcesJvmKt.asInputStream(source)`。
 *
 * ```java
 * var input1 = Resources,inputStream(resource);
 * var input2 = SourcesJvmKt.asInputStream(resource.source());
 * ```
 *
 * @author forte
 */
@Deprecated(
    "Just use `SourceResource.inputStream()` to get InputStream from Source"
)
public interface InputStreamResource : SourceResource {
    /**
     * 获取可用于读取当前资源数据的输入流。
     */
    @Throws(IOException::class)
    public fun inputStream(): InputStream = source().asInputStream()
}

/**
 * JVM 平台下可以**读取**到字符串内容的 [StringReadableResource] 类型。
 * 与 [StringReadableResource] 相比，对相关方法增加了 [Charset] 参数。
 * 默认情况下使用 [Charsets.UTF_8] 格式编码。
 */
public interface JvmStringReadableResource : StringReadableResource {
    /**
     * 读取此资源的 [String] 内容。
     * 默认使用 [DEFAULT_CHARSET] 编码。
     */
    @Throws(IOException::class)
    override fun string(): String = string(DEFAULT_CHARSET)

    /**
     * 读取此资源的 [String] 内容。
     */
    @Throws(IOException::class)
    public fun string(charset: Charset): String

    public companion object {
        /**
         * 默认编码格式: [Charsets.UTF_8]
         */
        @JvmField
        public val DEFAULT_CHARSET: Charset = Charsets.UTF_8
    }
}

/**
 * 能够获取到 [Reader] 资源的 [Resource] 扩展实现。
 *
 * Deprecated since v4.10.0.
 * 可以直接获取到 [InputStream], 进而直接获取到 reader, 此接口意义不大。
 *
 * @author forte
 */
@Deprecated(
    "Just use `SourceResource.inputStream()` to get InputStream from Source"
)
public interface ReaderResource : JvmStringReadableResource {
    /**
     * 读取当前资源的字符串数据。
     */
    @Throws(IOException::class)
    override fun string(charset: Charset): String = reader(charset).use { it.readText() }

    /**
     * 获取可用于读取当前资源数据的读取流。
     * 默认使用 [JvmStringReadableResource.DEFAULT_CHARSET] 编码。
     */
    @Throws(IOException::class)
    public fun reader(): Reader = reader(DEFAULT_CHARSET)

    /**
     * 获取可用于读取当前资源数据的读取流。
     */
    @Throws(IOException::class)
    public fun reader(charset: Charset): Reader
}

/**
 * [FileResource] 接口表示一个可从文件获取流的资源。
 * 该接口提供一个 [File] 对象和两种方法来获取输入流和读取文件的字节数组。
 *
 * @author forte
 */
@Suppress("DEPRECATION")
public interface FileResource :
    JvmSourceResource,
    InputStreamResource,
    ReaderResource {
    /**
     * 与此资源关联的 [File]
     */
    public val file: File

    /**
     * 从与此资源关联的 [File] 创建新的 [InputStream]
     * @throws FileNotFoundException 如果文件不存在
     */
    @Throws(FileNotFoundException::class)
    override fun inputStream(): InputStream = file.inputStream()

    /**
     * 从与此资源关联的 [File] 创建新的 [Reader]。
     * 默认使用 [JvmStringReadableResource.DEFAULT_CHARSET] 编码。
     * @throws FileNotFoundException 如果文件不存在
     */
    @Throws(FileNotFoundException::class)
    override fun reader(): Reader = reader(DEFAULT_CHARSET)

    /**
     * 从与此资源关联的 [File] 创建新的 [Reader]
     * @throws FileNotFoundException 如果文件不存在
     */
    @Throws(FileNotFoundException::class)
    override fun reader(charset: Charset): Reader

    /**
     * 将与此资源关联的 [File] 读取为字节数组
     * @throws IOException 如果文件无法读取
     */
    @Throws(IOException::class)
    override fun data(): ByteArray = file.readBytes()

    /**
     * 将与此资源关联的 [File] 读取为字符串。
     * 默认使用 [JvmStringReadableResource.DEFAULT_CHARSET] 编码。
     * @throws IOException 如果文件无法读取
     */
    @Throws(IOException::class)
    override fun string(): String = string(DEFAULT_CHARSET)

    /**
     * 将与此资源关联的 [File] 读取为字符串
     * @throws IOException 如果文件无法读取
     */
    @Throws(IOException::class)
    override fun string(charset: Charset): String
}

/**
 * Converts a [File] to a [FileResource].
 * [charset] 会作为需要使用编码参数的方法的默认编码。
 * 默认使用 [JvmStringReadableResource.DEFAULT_CHARSET]。
 *
 * @return The converted [FileResource].
 */
@JvmName("valueOf")
@JvmOverloads
public fun File.toResource(charset: Charset = DEFAULT_CHARSET): FileResource =
    FileResourceImpl(this, charset)

private data class FileResourceImpl(override val file: File, private val charset: Charset) :
    FileResource, SourceResource {
    override fun string(): String = string(charset)
    override fun reader(): Reader = reader(charset)

    override fun string(charset: Charset): String = file.readText(charset)
    override fun reader(charset: Charset): Reader = file.reader(charset)

    override fun data(): ByteArray = file.readBytes()

    override fun source(): Source = inputStream().asSource().buffered()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileResourceImpl) return false

        if (file != other.file) return false
        if (charset != other.charset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + charset.hashCode()
        return result
    }

    override fun toString(): String = "FileResource(file=$file, charset=$charset)"
}

/**
 * [PathResource] 接口表示一个可从 [Path] 获取流的资源。
 * 该接口提供一个 [Path] 对象和两种方法来获取输入流和读取文件的字节数组。
 *
 * @author forte
 */
@Suppress("DEPRECATION")
public interface PathResource :
    JvmSourceResource,
    InputStreamResource,
    ReaderResource {
    /**
     * 与此资源关联的 [Path]
     */
    public val path: Path

    /**
     * 从与此资源关联的 [Path] 创建新的 [InputStream]
     * @throws IOException 如果路径无法打开
     */
    @Throws(IOException::class)
    override fun inputStream(): InputStream

    /**
     * 将与此资源关联的 [Path] 读取为字节数组
     * @throws Exception 如果在路径上执行该操作时出现错误
     */
    @Throws(IOException::class)
    override fun data(): ByteArray = path.readBytes()

    /**
     * 从与此资源关联的 [Path] 创建新的 [Reader]。
     * @throws IOException 如果路径无法打开
     */
    @Throws(IOException::class)
    override fun reader(charset: Charset): Reader

    /**
     * 将与此资源关联的 [Path] 读取为字符串
     * @throws Exception 如果在路径上执行该操作时出现错误
     */
    @Throws(IOException::class)
    override fun string(charset: Charset): String
}

/**
 * Converts the given [Path] to a [PathResource] with the specified options.
 *
 *
 * @param charset 读取内容如果需要 [Charset] 信息时的默认值
 * @param options the options to use for opening the resource (vararg)
 * @return the [PathResource] representing the converted path
 */
@JvmName("valueOf")
@JvmOverloads
public fun Path.toResource(
    charset: Charset = DEFAULT_CHARSET,
    vararg options: OpenOption
): PathResource =
    PathResourceImpl(this, charset, options)

private data class PathResourceImpl(
    override val path: Path,
    private val charset: Charset,
    private val openOptions: Array<out OpenOption>
) : PathResource, SourceResource {
    override fun inputStream(): InputStream = path.inputStream(options = openOptions)

    override fun reader(): Reader = reader(charset)
    override fun string(): String = string(charset)

    override fun reader(charset: Charset): Reader = path.reader(charset, options = openOptions)
    override fun string(charset: Charset): String = reader(charset).use(Reader::readText)

    override fun data(): ByteArray = path.readBytes()

    override fun source(): Source = inputStream().asSource().buffered()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PathResourceImpl) return false

        if (path != other.path) return false
        if (charset != other.charset) return false
        if (!openOptions.contentEquals(other.openOptions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + charset.hashCode()
        result = 31 * result + openOptions.contentHashCode()
        return result
    }

    override fun toString(): String = buildString {
        append("PathResource(path=")
        append(path)
        append(", charset=")
        append(charset)
        append(", openOptions=")
        openOptions.joinTo(buffer = this, separator = ", ", prefix = "[", postfix = "]", limit = 8)
        append(")")
    }
}

/**
 * [URIResource] 是一个输入流资源的接口。
 *
 * @author forte
 */
@Suppress("DEPRECATION")
public interface URIResource :
    JvmSourceResource,
    InputStreamResource,
    JvmStringReadableResource {
    /**
     * 与此资源关联的 [URI]
     */
    public val uri: URI

    /**
     * 该方法简单地打开一个连接到 [uri] 的输入流，然后返回该输入流。
     *
     * @throws IllegalArgumentException see [URI.toURL]
     * @throws MalformedURLException see [URI.toURL]
     * @throws IOException 如果无法打开输入流，则抛出此异常。具体参看 [URL.openStream][java.net.URL.openStream]
     *
     * @return 返回从 `URL` 读取数据的输入流。
     */
    @Throws(IOException::class)
    override fun inputStream(): InputStream

    /**
     * 读取 [uri] 中的内容并作为字符串返回。
     *
     * @throws IllegalArgumentException see [URI.toURL]
     * @throws MalformedURLException see [URI.toURL]
     * @throws IOException 如果无法打开输入流，则抛出此异常。具体参看 [URL.openStream][java.net.URL.openStream]
     */
    @Throws(IOException::class)
    override fun string(): String = super.string()

    /**
     * 读取 [uri] 中的内容并作为字符串返回。
     *
     * @throws IllegalArgumentException see [URI.toURL]
     * @throws MalformedURLException see [URI.toURL]
     * @throws IOException 如果无法打开输入流，则抛出此异常。具体参看 [URL.openStream][java.net.URL.openStream]
     */
    @Throws(IOException::class)
    override fun string(charset: Charset): String
}

/**
 * Converts the current [URL] to a [URIResource].
 *
 * 使用 [URL] 构建的 [URIResource]
 * 在使用 [URIResource.string] 或 [URIResource.inputStream]
 * 时应当不会再产生 [IllegalArgumentException] 或 [MalformedURLException]
 * 了，因为 [URL] 已经初始化好了。
 * 取而代之的是 [URL.toResource] 可能会产生 [URISyntaxException]，
 * 因为需要使用 [URL.toURI]。
 *
 * @param charset 需要使用编码格式时的默认编码值，默认为 [JvmStringReadableResource.DEFAULT_CHARSET]。
 * @throws URISyntaxException see [URL.toURI]
 * @return The converted [URIResource].
 */
@kotlin.jvm.Throws(URISyntaxException::class)
@JvmName("valueOf")
@JvmOverloads
public fun URL.toResource(charset: Charset = DEFAULT_CHARSET): URIResource =
    URIResourceImpl(toURI(), charset, this)

/**
 * Converts the current [URI] to a [URIResource].
 *
 * @param charset 需要使用编码格式时的默认编码值，默认为 [JvmStringReadableResource.DEFAULT_CHARSET]。
 * @return The converted [URIResource].
 */
@JvmName("valueOf")
@JvmOverloads
public fun URI.toResource(charset: Charset = DEFAULT_CHARSET): URIResource =
    URIResourceImpl(this, charset, null)

private class URIResourceImpl(override val uri: URI, val charset: Charset, private var url: URL? = null) :
    URIResource,
    SourceResource {
    private val urlValue: URL
        get() = url ?: run {
            uri.toURL().also { url = it }
        }

    override fun inputStream(): InputStream = urlValue.openStream()
    override fun string(): String = string(charset)
    override fun string(charset: Charset): String = urlValue.readText(charset)

    override fun data(): ByteArray = inputStream().use { stream -> stream.readAllBytes() }

    override fun source(): Source = inputStream().asSource().buffered()

    override fun toString(): String = "URIResource(uri=$uri, charset=$charset)"
}

/**
 * 提供一个用于产生 [InputStream] 的供应函数 [provider]，
 * 并得到一个 [SourceResource]。
 *
 * 得到的结果每次使用 [SourceResource.source] 都会通过 [provider]
 * 获取一个 [Source]。[Source] 应当由使用者决定关闭时机，而不是在 [provider] 中。
 *
 * 函数本质上使用 [sourceResource]。
 *
 * @since v4.10.0
 */
@JvmName("valueOfInputStreamProvider")
public fun inputStreamResource(provider: () -> InputStream): SourceResource =
    sourceResource { provider().asSource().buffered() }
