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
import love.forte.simbot.common.id.ID
import kotlin.jvm.JvmStatic

/**
 * [`get_group_member_list`-获取群成员列表](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_member_list-获取群成员列表)
 *
 * @author ForteScarlet
 */
public class GetGroupMemberListApi private constructor(
    override val body: Any,
) : OneBotApi<List<GetGroupMemberInfoResult>> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<List<GetGroupMemberInfoResult>>
        get() = SER

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<List<GetGroupMemberInfoResult>>>
        get() = R_SER

    public companion object Factory {
        private const val ACTION: String = "get_group_member_list"

        private val SER = ListSerializer(GetGroupMemberInfoResult.serializer())
        private val R_SER = OneBotApiResult.serializer(SER)

        /**
         * 构建一个 [GetGroupMemberListApi].
         *
         * @param groupId 群号
         */
        @JvmStatic
        public fun create(groupId: ID): GetGroupMemberListApi = GetGroupMemberListApi(Body(groupId))
    }

    /**
     * @property groupId 群号
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
    )
}
