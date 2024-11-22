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

import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlin.annotation.AnnotationTarget.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 一些尚处于实验阶段的、基于IO(主要指文件系统相关)的 [Resource] 相关API。
 *
 * 可能会在未来发生变更、或被删除，且不保证兼容性与稳定性。
 *
 * @since 4.7.0
 */
@RequiresOptIn("Experimental IO Resource API")
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Target(
    CLASS,
    ANNOTATION_CLASS,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPEALIAS
)
public annotation class ExperimentalIOResourceAPI

/**
 * 根据完整的文件路径 [filePath] 得到一个基于对应文件的 [Resource]。
 *
 * 如果不确定文件系统使用的路径分隔符，或可能在多个使用不同路径分隔符的系统上使用，
 * 则考虑使用 [fileResource(base, ...parts)][fileResource]。
 *
 * 对文件存在性的校验和错误报告可能不会立即报告，而是被推迟到真正读取数据时，
 * 参考 [SourceResource.source] 的可能异常。
 *
 * @param filePath 文件路径，是使用 _路径分隔符_ 的多个片段。
 * 其中， _路径分隔符_ 在不同的文件系统中可能是不同的，例如在 Unit 中的 `/`
 * 和在 Windows 的 `\`。
 *
 * @see SourceResource
 *
 * @since 4.7.0
 */
@JvmName("valueOfPath")
@ExperimentalIOResourceAPI
public fun fileResource(filePath: String): SourceResource {
    return Path(filePath).toResource()
}

/**
 * 根据文件路径片段集得到一个基于对应文件的 [Resource]。
 *
 * 对文件存在性的校验和错误报告可能不会立即报告，而是被推迟到真正读取数据时，
 * 参考 [SourceResource.source] 的可能异常。
 *
 * @see SourceResource
 *
 * @since 4.7.0
 */
@JvmName("valueOfPath")
@ExperimentalIOResourceAPI
public fun fileResource(base: String, vararg parts: String): SourceResource {
    return Path(base, *parts).toResource()
}

/**
 * 使用 [Path] 直接构建一个 [FilePathResource]
 *
 * 对文件存在性的校验和错误报告可能不会立即报告，而是被推迟到真正读取数据时，
 * 参考 [SourceResource.source] 的可能异常。
 *
 * @see SourceResource
 *
 * @since 4.8.0
 */
@JvmName("valueOf")
@ExperimentalIOResourceAPI
public fun Path.toResource(): SourceResource {
    return FilePathResource(this)
}

/**
 * 一个可以得到 [kotlinx.io.Source] 的 [Resource]。
 *
 * @see fileResource
 *
 * @since 4.7.0
 */
@ExperimentalIOResourceAPI
public interface SourceResource : Resource {
    /**
     * 得到一个用于本次数据读取的 [Source].
     *
     * 每次都将构建一个新的 [Source], 你需要自行按需[关闭][Source.close]它们。
     *
     * @throws kotlinx.io.files.FileNotFoundException
     * see [kotlinx.io.files.FileSystem.source], [RawSource.buffered]
     * @throws kotlinx.io.IOException
     * see [kotlinx.io.files.FileSystem.source], [RawSource.buffered]
     *
     * @see kotlinx.io.files.FileSystem.source
     * @see RawSource.buffered
     */
    @Throws(Exception::class)
    public fun source(): Source

    /**
     * 使用 [source] 并读取其中全部的字节数据。
     *
     * @throws IllegalStateException
     * see [Source.readByteArray]
     * @throws kotlinx.io.IOException
     * see [Source.readByteArray]
     *
     * @see source
     */
    @Throws(Exception::class)
    override fun data(): ByteArray = source().use { it.readByteArray() }
}

@ExperimentalIOResourceAPI
private data class FilePathResource(val path: Path) : SourceResource {
    private val source
        get() = SystemFileSystem.source(path)

    @Throws(Exception::class)
    override fun source(): Source = source.buffered()
}

