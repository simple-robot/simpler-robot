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

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.jvm.JvmStatic

/**
 * 计划被废弃的与 [Resource] 相关的API
 */
@RequiresOptIn(
    message = "计划被废弃的与 `Resource` 相关的API. 详见 " +
        "`love.forte.simbot.resource.ResourceResolver` 和 " +
        "`love.forte.simbot.resource.Resource` 中的有关说明。"
)
@Retention(BINARY)
@Target(CLASS, FUNCTION)
@MustBeDocumented
public annotation class ScheduledDeprecatedResourceApi

/**
 * 使用 [ResourceResolver] 分析处理一个 [Resource].
 * 类似于 `visitor` 的用法，与常见地访问器区别于通常情况下只会有一个 `resolve*`
 * 会最终执行。
 *
 * 在 JVM 平台会提供一个具有更多能力的类型。
 *
 * Note: 由于[Resource]现在已经通过 `sealed` 限制了子类型范围,
 * 因此可以直接使用 [ByteArrayResource] 或 [SourceResource]。
 * 得益于 `kotlinx-io`，明确 resolve 多平台（尤其是JVM平台）下的独特类型的情况已经不多了。
 * [ResourceResolver] 可能会在未来废弃, 且现在开始不再建议使用。
 *
 * @author ForteScarlet
 */
@ScheduledDeprecatedResourceApi
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
