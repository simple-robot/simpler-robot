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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmStatic

/**
 * [`get_friend_list`-获取好友列表](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_friend_list-获取好友列表)
 *
 * @author ForteScarlet
 */
public class GetFriendListApi private constructor() : OneBotApi<List<GetFriendListResult>> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<List<GetFriendListResult>>
        get() = SER

    override val apiResultDeserializer:
        DeserializationStrategy<OneBotApiResult<List<GetFriendListResult>>>
        get() = RES_SER

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "get_friend_list"

        private val SER = ListSerializer(GetFriendListResult.serializer())

        private val RES_SER = OneBotApiResult.serializer(SER)

        private val INSTANCE: GetFriendListApi = GetFriendListApi()

        /**
         * 构建一个 [GetFriendListApi].
         */
        @JvmStatic
        public fun create(): GetFriendListApi = INSTANCE
    }
}

/**
 * [GetFriendListApi] 的响应体。
 *
 * @property userId QQ 号
 * @property nickname 昵称
 * @property remark 备注名
 */
@Serializable
public data class GetFriendListResult @ApiResultConstructor constructor(
    @SerialName("user_id")
    public val userId: LongID,
    public val nickname: String,
    public val remark: String,
)
