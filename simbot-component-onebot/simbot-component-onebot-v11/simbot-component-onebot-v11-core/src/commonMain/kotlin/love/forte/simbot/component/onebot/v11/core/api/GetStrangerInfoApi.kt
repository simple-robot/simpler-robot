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
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`get_stranger_info`-获取陌生人信息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_stranger_info-获取陌生人信息)
 *
 * @author ForteScarlet
 */
public class GetStrangerInfoApi private constructor(
    override val body: Any,
) : OneBotApi<GetStrangerInfoResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetStrangerInfoResult>
        get() = GetStrangerInfoResult.serializer()

    override val apiResultDeserializer:
        DeserializationStrategy<OneBotApiResult<GetStrangerInfoResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_stranger_info"

        private val RES_SER: KSerializer<OneBotApiResult<GetStrangerInfoResult>> =
            OneBotApiResult.serializer(GetStrangerInfoResult.serializer())

        /**
         * 构建一个 [GetStrangerInfoApi].
         *
         * @param userId QQ 号
         * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
         */
        @JvmStatic
        @JvmOverloads
        public fun create(userId: ID, noCache: Boolean? = null): GetStrangerInfoApi =
            GetStrangerInfoApi(Body(userId, noCache))
    }

    /**
     * @property userId QQ 号
     * @property noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     */
    @Serializable
    internal data class Body(
        @SerialName("user_id")
        internal val userId: ID,
        @SerialName("no_cache")
        internal val noCache: Boolean? = null,
    )
}

/**
 * [GetStrangerInfoApi] 的响应体。
 *
 * @property userId QQ 号
 * @property nickname 昵称
 * @property sex 性别，`male` 或 `female` 或 `unknown`
 * @property age 年龄
 */
@Serializable
public data class GetStrangerInfoResult @ApiResultConstructor constructor(
    @SerialName("user_id")
    public val userId: LongID,
    public val nickname: String,
    public val sex: String,
    public val age: Int,
)
