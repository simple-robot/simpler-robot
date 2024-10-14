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

import kotlinx.io.*
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
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
 * 文件会在通过 [Resource.data] 读取数据时才会校验存在性。届时如果文件不存在，
 * 则会得到 [IllegalStateException] 异常。
 *
 * 如果不确定文件系统使用的路径分隔符，或可能在多个使用不同路径分隔符的系统上使用，
 * 则考虑使用 [fileResource(base, ...parts)][fileResource]。
 *
 * @param filePath 文件路径，是使用 _路径分隔符_ 的多个片段。
 * 其中， _路径分隔符_ 在不同的文件系统中可能是不同的，例如在 Unit 中的 `/`
 * 和在 Windows 的 `\`。
 *
 * @since 4.7.0
 */
@JvmName("valueOfPath")
@ExperimentalIOResourceAPI
public fun fileResource(filePath: String): Resource {
    val path = Path(filePath)
    return FilePathResource(path)
}

/**
 * 根据文件路径片段集得到一个基于对应文件的 [Resource]。
 *
 * 文件会在通过 [Resource.data] 读取数据时才会校验存在性。届时如果文件不存在，
 * 则会得到 [IllegalStateException] 异常。
 * 此异常的 [IllegalStateException.cause] 可能是：
 * - [kotlinx.io.files.FileNotFoundException]
 * - [kotlinx.io.IOException]
 * 如果是这两个类型，则成因参考 [kotlinx.io.files.FileSystem.source]。
 *
 * @since 4.7.0
 */
@JvmName("valueOfPath")
@ExperimentalIOResourceAPI
public fun fileResource(base: String, vararg parts: String): Resource {
    val path = Path(base, *parts)
    return FilePathResource(path)
}

/**
 * 一个可以得到 [kotlinx.io.RawSource] 的 [Resource]。
 *
 * @since 4.7.0
 */
@ExperimentalIOResourceAPI
public interface RawSourceResource : Resource {
    public fun source(): RawSource

    override fun data(): ByteArray {
        return source().buffered().use { it.readByteArray() }
    }
}

/**
 * 一个可以得到 [kotlinx.io.Source] 的 [Resource]。
 *
 * @since 4.7.0
 */
@ExperimentalIOResourceAPI
public interface SourceResource : RawSourceResource {
    override fun source(): Source
}

@ExperimentalIOResourceAPI
private data class FilePathResource(val path: Path) : RawSourceResource {
    override fun source(): RawSource = try {
        SystemFileSystem.source(path)
    } catch (fnf: FileNotFoundException) {
        throw IllegalStateException(fnf.message, fnf)
    } catch (io: IOException) {
        throw IllegalStateException(io.message, io)
    }
}

