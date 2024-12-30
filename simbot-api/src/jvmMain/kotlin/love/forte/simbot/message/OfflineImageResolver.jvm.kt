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

package love.forte.simbot.message

import love.forte.simbot.resource.*
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.toPath

/**
 * JVM 平台下的 [OfflineImageResolver] 扩展类型。
 */
public interface JvmOfflineImageResolver<C> : OfflineImageResolver<C> {
    /**
     * 处理类型为 [OfflineFileImage] 的 resource.
     */
    public fun resolveFile(resource: OfflineFileImage, context: C)

    /**
     * 处理类型为 [OfflinePathImage] 的 resource.
     */
    public fun resolvePath(resource: OfflinePathImage, context: C)

    /**
     * 处理类型为 [OfflineURIImage] 的 resource.
     */
    public fun resolveURI(resource: OfflineURIImage, context: C)

    /**
     * 对更多扩展类型进行处理，并在最终未知时使用 [resolveUnknownInternal].
     * 通常不需要重写。
     */
    override fun resolveUnknown(image: OfflineImage, context: C) {
        when (image) {
            is OfflineFileImage -> resolveFile(image, context)
            is OfflinePathImage -> resolvePath(image, context)
            is OfflineURIImage -> resolveURI(image, context)
            else -> resolveUnknownInternal(image, context)
        }
    }

    /**
     * 如果是未知的。
     */
    public fun resolveUnknownInternal(image: OfflineImage, context: C)
}

/**
 * 实现 [JvmOfflineImageResolver] 和 [JvmResourceResolver]，
 * 对其中可能出现的实际内容物（例如 [File] 或 [Path]）进行处理。
 */
@ScheduledDeprecatedResourceApi
public abstract class JvmOfflineImageValueResolver<C> :
    OfflineImageValueResolver<C>,
    JvmOfflineImageResolver<C>,
    JvmResourceResolver<C> {
    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [ByteArray]。
     */
    abstract override fun resolveByteArray(byteArray: ByteArray, context: C)

    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [String]。
     */
    abstract override fun resolveString(string: String, context: C)

    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [File]。
     */
    public abstract fun resolveFile(file: File, context: C)

    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [Path]。
     */
    public abstract fun resolvePath(path: Path, context: C)

    /**
     * 处理 [URI]。如果 [URI.scheme] == `"file"`，
     * 则会使用 [resolveFile]，否则使用 [resolveURINotFileScheme]。
     */
    public open fun resolveURI(uri: URI, context: C) {
        if (uri.scheme == "file") {
            resolvePath(uri.toPath(), context)
        } else {
            resolveURINotFileScheme(uri, context)
        }
    }

    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [URI.scheme] != `"file"`
     * 的 [URI]。
     */
    public abstract fun resolveURINotFileScheme(uri: URI, context: C)


    final override fun resolveByteArray(image: OfflineByteArrayImage, context: C) {
        super.resolveByteArray(image, context)
    }

    final override fun resolveByteArray(resource: ByteArrayResource, context: C) {
        super.resolveByteArray(resource, context)
    }

    final override fun resolveString(resource: StringResource, context: C) {
        super.resolveString(resource, context)
    }

    final override fun resolveResource(image: OfflineResourceImage, context: C) {
        super.resolveResource(image, context)
    }

    final override fun resolveFile(resource: OfflineFileImage, context: C) {
        resolveFile(resource.file, context)
    }

    final override fun resolveFile(resource: FileResource, context: C) {
        resolveFile(resource.file, context)
    }

    final override fun resolvePath(resource: OfflinePathImage, context: C) {
        resolvePath(resource.path, context)
    }

    final override fun resolvePath(resource: PathResource, context: C) {
        resolvePath(resource.path, context)
    }

    final override fun resolveURI(resource: OfflineURIImage, context: C) {
        resolveURI(resource.uri, context)
    }

    final override fun resolveURI(resource: URIResource, context: C) {
        resolveURI(resource.uri, context)
    }

}
