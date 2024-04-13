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

import kotlin.jvm.JvmStatic


/**
 * 使用 [ResourceResolver] 分析处理一个 [Resource].
 * 类似于 `visitor` 的用法，与常见地访问器区别于通常情况下只会有一个 `resolve*`
 * 会最终执行。
 *
 * 在 JVM 平台会提供一个具有更多能力的类型。
 *
 * @author ForteScarlet
 */
public interface ResourceResolver<C> {
    /**
     * 处理一个未知的 [Resource] 类型的 resource.
     */
    public fun resolveUnknown(resource: Resource, context: C)

    /**
     * 处理一个 [ByteArrayResource] 类型的 resource.
     */
    public fun resolveByteArray(resource: ByteArrayResource, context: C)

    /**
     * 处理一个 [StringResource] 类型的 resource.
     */
    public fun resolveString(resource: StringResource, context: C)

    public companion object {
        /**
         * 使用 [this] 解析 [resource].
         */
        @JvmStatic
        public fun <C> ResourceResolver<C>.resolve(resource: Resource, context: C) {
            when (resource) {
                is ByteArrayResource -> resolveByteArray(resource, context)
                is StringResource -> resolveString(resource, context)
                else -> resolveUnknown(resource, context)
            }
        }
    }
}
