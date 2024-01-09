/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import java.io.*
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.readBytes
import kotlin.io.path.readText
import kotlin.io.path.reader

/**
 * 能够获取到 [InputStream] 资源的 [Resource] 扩展实现。
 *
 * @author forte
 */
public interface InputStreamResource : Resource {

    /**
     * 读取当前资源的所有字节数据。
     * 默认通过 [inputStream] 读取。
     *
     */
    @Throws(Exception::class)
    override fun data(): ByteArray = inputStream().use { it.readAllBytes() }

    /**
     * 获取可用于读取当前资源数据的输入流。
     */
    @Throws(Exception::class)
    public fun inputStream(): InputStream
}

/**
 * 能够获取到 [Reader] 资源的 [Resource] 扩展实现。
 *
 * @author forte
 */
public interface ReaderResource : StringResource {
    /**
     * 读取当前资源的字符串数据。
     */
    @Throws(Exception::class)
    override fun string(): String = reader().use { it.readText() }

    /**
     * 获取可用于读取当前资源数据的读取流。
     */
    @Throws(Exception::class)
    public fun reader(): Reader
}

/**
 * [FileResource] 接口表示一个可从文件获取流的资源。
 * 该接口提供一个 [File] 对象和两种方法来获取输入流和读取文件的字节数组。
 *
 * @author forte
 */
public interface FileResource : InputStreamResource, ReaderResource {
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
     * 从与此资源关联的 [File] 创建新的 [Reader]
     * @throws FileNotFoundException 如果文件不存在
     */
    @Throws(FileNotFoundException::class)
    override fun reader(): Reader

    /**
     * 将与此资源关联的 [File] 读取为字节数组
     * @throws IOException 如果文件无法读取
     */
    @Throws(IOException::class)
    override fun data(): ByteArray = file.readBytes()

    /**
     * 将与此资源关联的 [File] 读取为字符串
     * @throws IOException 如果文件无法读取
     */
    @Throws(IOException::class)
    override fun string(): String
}

/**
 * Converts a [File] to a [FileResource].
 *
 * @return The converted [FileResource].
 */
@JvmName("valueOf")
@JvmOverloads
public fun File.toResource(charset: Charset = Charsets.UTF_8): FileResource = FileResourceImpl(this, charset)

private data class FileResourceImpl(override val file: File, private val charset: Charset) : FileResource {
    override fun string(): String = file.readText(charset)
    override fun reader(): Reader = file.reader(charset)
}

/**
 * [PathResource] 接口表示一个可从 [Path] 获取流的资源。
 * 该接口提供一个 [Path] 对象和两种方法来获取输入流和读取文件的字节数组。
 *
 * @author forte
 */
public interface PathResource : InputStreamResource, ReaderResource {
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
     * 从与此资源关联的 [Path] 创建新的 [Reader]
     * @throws IOException 如果路径无法打开
     */
    @Throws(IOException::class)
    override fun reader(): Reader

    /**
     * 将与此资源关联的 [Path] 读取为字节数组
     * @throws Exception 如果在路径上执行该操作时出现错误
     */
    @Throws(Exception::class)
    override fun data(): ByteArray = path.readBytes()

    /**
     * 将与此资源关联的 [Path] 读取为字符串
     * @throws Exception 如果在路径上执行该操作时出现错误
     */
    @Throws(Exception::class)
    override fun string(): String
}

/**
 * Converts the given [Path] to a [PathResource] with the specified options.
 *
 * @param options the options to use for opening the resource (vararg)
 * @return the [PathResource] representing the converted path
 */
@JvmName("valueOf")
@JvmOverloads
public fun Path.toResource(charset: Charset = Charsets.UTF_8, vararg options: OpenOption): PathResource =
    PathResourceImpl(this, charset, options)

private data class PathResourceImpl(
    override val path: Path,
    private val charset: Charset,
    private val openOptions: Array<out OpenOption>
) :
    PathResource {
    override fun inputStream(): InputStream = path.inputStream(options = openOptions)

    override fun reader(): Reader = path.reader(charset, options = openOptions)

    override fun string(): String = path.readText(charset)
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
}

/**
 * [URLResource] 是一个输入流资源的接口。
 *
 * @author forte
 */
public interface URLResource : InputStreamResource, StringResource {
    /**
     * 与此资源关联的 [URI]
     */
    public val url: URL

    /**
     * 该方法简单地打开一个连接到此 [URL] 的输入流，然后返回该输入流。
     *
     * @throws IOException 如果无法打开输入流，则抛出此异常。具体参看 [URL.openStream][java.net.URL.openStream]
     *
     * @return 返回从 `URL` 读取数据的输入流。
     */
    @Throws(IOException::class)
    override fun inputStream(): InputStream = url.openStream()

    /**
     * 读取 [url] 中的内容并作为字符串返回。
     *
     * @throws IOException 如果无法打开输入流，则抛出此异常。具体参看 [URL.openStream][java.net.URL.openStream]
     */
    @Throws(IOException::class)
    override fun string(): String
}

/**
 * Converts the current [URL] to a [URLResource].
 *
 * @return The converted [URLResource].
 */
@JvmName("valueOf")
@JvmOverloads
public fun URL.toResource(charset: Charset = Charsets.UTF_8): URLResource = URLResourceImpl(this, charset)

/**
 * Converts the current [URI] to a [URLResource].
 *
 * @return The converted [URLResource].
 *
 * @throws IllegalArgumentException If this URL is not absolute. see [URI.toURL]
 * @throws MalformedURLException
 * If a protocol handler for the URL could not be found,
 * or if some other error occurred while constructing the URL. see [URI.toURL]
 */
@Throws(MalformedURLException::class)
@JvmName("valueOf")
@JvmOverloads
public fun URI.toResource(charset: Charset = Charsets.UTF_8): URLResource = toURL().toResource(charset = charset)

private data class URLResourceImpl(override val url: URL, val charset: Charset) : URLResource {
    override fun string(): String = url.readText(charset)
}
