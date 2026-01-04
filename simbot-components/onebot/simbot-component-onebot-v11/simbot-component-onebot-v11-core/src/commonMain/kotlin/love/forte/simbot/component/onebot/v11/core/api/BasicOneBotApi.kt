/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.api

import io.ktor.http.*

/**
 * 基础性的针对 [OneBot API](https://github.com/botuniverse/onebot-11/tree/master/api) 的定义。
 *
 * 两个直接子类型：[OneBotApi], [CustomOneBotApi]。
 *
 * @author ForteScarlet
 * @since 1.9.0
 */
public sealed interface BasicOneBotApi<T : Any> {
    /**
     * 与 `action` 相关的公共、常量信息。
     */
    public object Actions {
        /**
         * [异步调用](https://github.com/botuniverse/onebot-11/blob/master/api/README.md#异步调用).
         *
         * 所有 API 都可以通过给 `action` 附加后缀 `_async` 来进行异步调用
         */
        public const val ASYNC_SUFFIX: String = "_async"

        /**
         * [限速调用](https://github.com/botuniverse/onebot-11/blob/master/api/README.md#限速调用)
         *
         * 所有 API 都可以通过给 `action` 附加后缀 `_rate_limited` 来进行限速调用。
         */
        public const val RATE_LIMITED_SUFFIX: String = "_rate_limited"
    }

    /**
     * 此 API 的请求方式。
     * OneBot协议中的标准API通常均为 POST，
     * 但是一些额外的扩展或自定义API可能是 GET 或其他方式。
     */
    public val method: HttpMethod

    /**
     * API 的 action（要进行的动作），会通过 [resolveUrlAction] 附加在 url 中。
     * 可以重写它来改变此逻辑。
     */
    public val action: String

    /**
     * 根据 [action] 和可能额外要求的 [actionSuffixes] 构建一个完整的请求地址。
     *
     * [urlBuilder] 中已经添加了基础的 `host` 等信息。
     */
    public fun resolveUrlAction(urlBuilder: URLBuilder, actionSuffixes: Collection<String>?) {
        if (actionSuffixes?.isEmpty() != false) {
            urlBuilder.appendPathSegments(action)
        } else {
            urlBuilder.appendPathSegments(
                buildString(action.length) {
                    append(action)
                    actionSuffixes.forEach { sf -> append(sf) }
                }
            )
        }
    }

    /**
     * 对 [urlBuilder] 进行一些额外的处理，例如当method为GET时为其添加查询参数。
     * 主要面向额外扩展的自定义实现来重写此方法。
     */
    public fun resolveUrlExtensions(urlBuilder: URLBuilder) {
    }

    /**
     * API的请求体。
     */
    public val body: Any?

    /**
     * 将 API 返回的 JSON 字符串反序列化为 [OneBotApiResult]。
     */
    public fun deserialize(responseBody: String): OneBotApiResult<T>
}
