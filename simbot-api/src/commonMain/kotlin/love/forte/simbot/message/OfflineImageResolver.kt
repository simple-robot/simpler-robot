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
import love.forte.simbot.resource.ResourceResolver.Companion.resolve
import kotlin.jvm.JvmStatic


/**
 * 使用 [OfflineImageResolver] 分析处理一个 [OfflineImage].
 * 类似于 `visitor` 的用法，与常见地访问器区别于通常情况下只会有一个 `resolve*`
 * 会最终执行。
 *
 * 在 JVM 平台会提供一个具有更多能力的类型。
 *
 * @author ForteScarlet
 */
public interface OfflineImageResolver<C> {

    /**
     * 处理一个类型为未知的 [OfflineImage] 的 image.
     */
    public fun resolveUnknown(image: OfflineImage, context: C)

    /**
     * 处理一个类型为 [OfflineByteArrayImage] 的 image.
     */
    public fun resolveByteArray(image: OfflineByteArrayImage, context: C)

    /**
     * 处理一个类型为 [OfflineResourceImage] 的 image.
     */
    public fun resolveResource(image: OfflineResourceImage, context: C)

    public companion object {
        /**
         * 使用 [this] 分析处理 [image].
         */
        @JvmStatic
        public fun <C> OfflineImageResolver<C>.resolve(image: OfflineImage, context: C) {
            when (image) {
                is OfflineByteArrayImage -> resolveByteArray(image, context)
                is OfflineResourceImage -> resolveResource(image, context)
                else -> resolveUnknown(image, context)
            }
        }
    }
}

/**
 * 继承 [OfflineImageResolver] 和 [ResourceResolver]，
 * 对其中可能出现的实际内容物（例如 [ByteArray] 或 [String]）进行处理。
 */
@ScheduledDeprecatedResourceApi
public interface OfflineImageValueResolver<C> :
    OfflineImageResolver<C>,
    ResourceResolver<C> {
    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [ByteArray]。
     */
    public fun resolveByteArray(byteArray: ByteArray, context: C)

    /**
     * 处理可能来自 [OfflineImage] 或 [Resource] 中的 [String]。
     */
    public fun resolveString(string: String, context: C)

    /**
     * 使用 [resolveByteArray] 处理 [OfflineByteArrayImage.data].
     */
    override fun resolveByteArray(image: OfflineByteArrayImage, context: C) {
        resolveByteArray(image.data(), context)
    }

    /**
     * 使用 [resolveByteArray] 处理 [ByteArrayResource.data].
     */
    override fun resolveByteArray(resource: ByteArrayResource, context: C) {
        resolveByteArray(resource.data(), context)
    }

    /**
     * 使用 [StringResource] 处理 [StringResource.string].
     */
    override fun resolveString(resource: StringResource, context: C) {
        resolveString(resource.string(), context)
    }

    override fun resolveResource(image: OfflineResourceImage, context: C) {
        resolve(image.resource, context)
    }
}
