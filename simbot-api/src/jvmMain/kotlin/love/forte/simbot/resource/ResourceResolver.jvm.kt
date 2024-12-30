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

package love.forte.simbot.resource

import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.toPath

/**
 * JVM 平台下 [ResourceResolver] 的进一步扩展类型。
 *
 */
@ScheduledDeprecatedResourceApi
public interface JvmResourceResolver<C> : ResourceResolver<C> {
    /**
     * 处理类型为 [FileResource] 的 resource.
     */
    public fun resolveFile(resource: FileResource, context: C)

    /**
     * 处理类型为 [PathResource] 的 resource.
     */
    public fun resolvePath(resource: PathResource, context: C)

    /**
     * 处理类型为 [URIResource] 的 resource.
     */
    public fun resolveURI(resource: URIResource, context: C)

    /**
     * 默认实现会额外处理JVM下更多的类型解析，通常不应重写此函数。
     *
     * @see resolveUnknownInternal
     */
    override fun resolveUnknown(resource: Resource, context: C) {
        when (resource) {
            is FileResource -> resolveFile(resource, context)
            is PathResource -> resolvePath(resource, context)
            is URIResource -> resolveURI(resource, context)
            else -> resolveUnknownInternal(resource, context)
        }
    }

    /**
     * 在JVM平台下的 resolve unknown。
     */
    public fun resolveUnknownInternal(resource: Resource, context: C)
}

/**
 * [JvmResourceResolver] 的更安全的抽象类型，将 [resolveUnknown] 固定为 `final`。
 */
@ScheduledDeprecatedResourceApi
public abstract class AbstractJvmResourceResolver<C> : JvmResourceResolver<C> {
    final override fun resolveUnknown(resource: Resource, context: C) {
        super.resolveUnknown(resource, context)
    }
}

/**
 * 基于 Resource 中的值进行处理。
 * 其中，如果 [URI.scheme] == `"file"` 则会被处理为 [Path].
 */
@ScheduledDeprecatedResourceApi
public interface JvmResourceValueResolver<C> : JvmResourceResolver<C> {
    /**
     * 解析 [FileResource] 类型的 resource，
     * 将值交由 [resolveFile] 处理。
     * 通常情况下不需要重写。
     */
    override fun resolveFile(resource: FileResource, context: C) {
        resolveFile(resource.file, context)
    }

    /**
     * 解析 [PathResource] 类型的 resource，
     * 将值交由 [resolvePath] 处理。
     * 通常情况下不需要重写。
     */
    override fun resolvePath(resource: PathResource, context: C) {
        resolvePath(resource.path, context)
    }

    /**
     * 解析 [URIResource] 类型的 resource，
     * 将值交由 [resolveURI] 处理。
     * 通常情况下不需要重写。
     */
    override fun resolveURI(resource: URIResource, context: C) {
        resolveURI(resource.uri, context)
    }

    /**
     * 解析 [ByteArrayResource] 类型的 resource，
     * 将值交由 [resolveByteArray] 处理。
     * 通常情况下不需要重写。
     */
    override fun resolveByteArray(resource: ByteArrayResource, context: C) {
        resolveByteArray(resource.data(), context)
    }

    /**
     * 解析 [StringResource] 类型的 resource，
     * 将值交由 [resolveString] 处理。
     * 通常情况下不需要重写。
     */
    override fun resolveString(resource: StringResource, context: C) {
        resolveString(resource.string(), context)
    }

    /**
     * 处理来自 [FileResource] 中的 [File]
     */
    public fun resolveFile(file: File, context: C)

    /**
     * 处理来自 [PathResource]中的 或 [URI.scheme] == `"file"` 的 [Path]
     */
    public fun resolvePath(path: Path, context: C)

    /**
     * 处理来自 [URIResource] 中的 [URI]。
     * 默认情况下，[resolveURI] 中如果 [URI.scheme] 为 `"file"`，
     * 则会分发到 [resolvePath]，否则使用 [resolveURINotFileScheme]。
     * 如果重写则会覆盖此逻辑，一般来讲不需要重写。
     */
    public fun resolveURI(uri: URI, context: C) {
        if (uri.scheme == "file") {
            resolvePath(uri.toPath(), context)
        } else {
            resolveURINotFileScheme(uri, context)
        }
    }

    /**
     * 处理来自 [URIResource] 且 [URI.scheme] != `"file"`
     * 的 [URI]。
     */
    public fun resolveURINotFileScheme(uri: URI, context: C)

    /**
     * 处理来自 [ByteArrayResource] 中的 [ByteArray].
     */
    public fun resolveByteArray(byteArray: ByteArray, context: C)

    /**
     * 处理来自 [StringResource] 中的 [String].
     */
    public fun resolveString(string: String, context: C)
}

/**
 * [JvmResourceValueResolver] 的更安全的抽象类型，
 * 会将部分直接解析 [Resource] 的函数固定为 `final`。
 */
@ScheduledDeprecatedResourceApi
public abstract class AbstractJvmResourceValueResolver<C> : JvmResourceValueResolver<C> {
    final override fun resolveFile(resource: FileResource, context: C) {
        super.resolveFile(resource, context)
    }

    final override fun resolvePath(resource: PathResource, context: C) {
        super.resolvePath(resource, context)
    }

    final override fun resolveURI(resource: URIResource, context: C) {
        super.resolveURI(resource, context)
    }

    final override fun resolveByteArray(resource: ByteArrayResource, context: C) {
        super.resolveByteArray(resource, context)
    }

    final override fun resolveString(resource: StringResource, context: C) {
        super.resolveString(resource, context)
    }
}
