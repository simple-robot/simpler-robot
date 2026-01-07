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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmStatic

/**
 * [`get_login_info`-获取登录号信息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_login_info-获取登录号信息)
 *
 * @author ForteScarlet
 */
public class GetLoginInfoApi private constructor() : OneBotApi<GetLoginInfoResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetLoginInfoResult>
        get() = GetLoginInfoResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<GetLoginInfoResult>>
        get() = RES_SER

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "get_login_info"

        private val RES_SER: KSerializer<OneBotApiResult<GetLoginInfoResult>> =
            OneBotApiResult.serializer(GetLoginInfoResult.serializer())

        private val INSTANCE: GetLoginInfoApi = GetLoginInfoApi()

        /**
         * 构建一个 [GetLoginInfoApi].
         */
        @JvmStatic
        public fun create(): GetLoginInfoApi = INSTANCE
    }
}

/**
 * [GetLoginInfoApi] 的响应体。
 *
 * @property userId QQ 号
 * @property nickname QQ 昵称
 */
@Serializable
public data class GetLoginInfoResult @ApiResultConstructor constructor(
    @SerialName("user_id")
    public val userId: LongID,
    public val nickname: String,
)
