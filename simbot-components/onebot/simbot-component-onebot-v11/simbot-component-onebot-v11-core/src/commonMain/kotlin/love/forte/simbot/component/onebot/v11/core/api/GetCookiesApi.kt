/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`get_cookies`-获取 Cookies](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_cookies-获取-cookies)
 *
 * @author ForteScarlet
 */
public class GetCookiesApi private constructor(
    override val body: Any,
) : OneBotApi<GetCookiesResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetCookiesResult>
        get() = GetCookiesResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<GetCookiesResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_cookies"

        private val RES_SER: KSerializer<OneBotApiResult<GetCookiesResult>> =
            OneBotApiResult.serializer(GetCookiesResult.serializer())

        /**
         * 构建一个 [GetCookiesApi].
         *
         * @param domain 需要获取 cookies 的域名
         */
        @JvmStatic
        @JvmOverloads
        public fun create(domain: String? = null): GetCookiesApi = GetCookiesApi(Body(domain))
    }

    /**
     * @property domain 需要获取 cookies 的域名
     */
    @Serializable
    internal data class Body(
        internal val domain: String? = null,
    )
}

/**
 * [GetCookiesApi] 的响应体。
 *
 * @property cookies Cookies
 */
@Serializable
public data class GetCookiesResult @ApiResultConstructor constructor(
    public val cookies: String,
)
