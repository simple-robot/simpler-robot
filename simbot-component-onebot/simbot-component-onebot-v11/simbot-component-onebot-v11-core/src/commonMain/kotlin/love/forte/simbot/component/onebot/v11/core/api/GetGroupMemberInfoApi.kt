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
 * [`get_group_member_info`-获取群成员信息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_member_info-获取群成员信息)
 *
 * @author ForteScarlet
 */
public class GetGroupMemberInfoApi private constructor(
    override val body: Any,
) : OneBotApi<GetGroupMemberInfoResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetGroupMemberInfoResult>
        get() = GetGroupMemberInfoResult.serializer()

    override val apiResultDeserializer:
        DeserializationStrategy<OneBotApiResult<GetGroupMemberInfoResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_group_member_info"

        private val RES_SER: KSerializer<OneBotApiResult<GetGroupMemberInfoResult>> =
            OneBotApiResult.serializer(GetGroupMemberInfoResult.serializer())

        /**
         * 构建一个 [GetGroupMemberInfoApi].
         *
         * @param groupId 群号
         * @param userId QQ 号
         * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            groupId: ID,
            userId: ID,
            noCache: Boolean? = null,
        ): GetGroupMemberInfoApi = GetGroupMemberInfoApi(Body(groupId, userId, noCache))
    }

    /**
     * @param groupId 群号
     * @param userId QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
        @SerialName("user_id")
        internal val userId: ID,
        @SerialName("no_cache")
        internal val noCache: Boolean? = null,
    )
}

/**
 * [GetGroupMemberInfoApi] 的响应体。
 *
 * @property groupId 群号
 * @property userId QQ 号
 * @property nickname 昵称
 * @property card 群名片／备注
 * @property sex 性别，`male` 或 `female` 或 `unknown`
 * @property age 年龄
 * @property area 地区
 * @property joinTime 加群时间戳
 * @property lastSentTime 最后发言时间戳
 * @property level 成员等级
 * @property role 角色，`owner` 或 `admin` 或 `member`
 * @property unfriendly 是否不良记录成员
 * @property title 专属头衔
 * @property titleExpireTime 专属头衔过期时间戳
 * @property cardChangeable 是否允许修改群名片
 */
@Serializable
public data class GetGroupMemberInfoResult @ApiResultConstructor constructor(
    @SerialName("group_id")
    public val groupId: LongID,
    @SerialName("user_id")
    public val userId: LongID,
    public val nickname: String,
    public val card: String = "",
    public val sex: String = "unknown",
    public val age: Int = -1,
    public val area: String = "",
    @SerialName("join_time")
    public val joinTime: Int = -1,
    @SerialName("last_sent_time")
    public val lastSentTime: Int = -1,
    public val level: String = "",
    public val role: String = "member",
    public val unfriendly: Boolean = false,
    public val title: String = "",
    @SerialName("title_expire_time")
    public val titleExpireTime: Int = -1,
    @SerialName("card_changeable")
    public val cardChangeable: Boolean = false,
)
