/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [删除频道成员](https://bot.q.qq.com/wiki/develop/api/openapi/member/delete_member.html)
 *
 * 用于删除 guild_id 指定的频道下的成员 user_id。
 *
 * - 需要使用的 token 对应的用户具备踢人权限。如果是机器人，要求被添加为管理员。
 * - 操作成功后，会触发频道成员删除事件。
 * - 无法移除身份为管理员的成员
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteMemberApi private constructor(
    guildId: String,
    userId: String,
    private val _body: Body
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    public companion object Factory : SimpleDeleteApiDescription(
        "/guilds/{guild_id}/members/{user_id}"
    ) {

        /**
         * 构造 [DeleteMemberApi].
         *
         * @param addBlacklist 删除成员的同时，是否将该用户添加到频道黑名单中。
         * @param deleteHistoryMsgDays 删除成员的同时，撤回该成员的消息，可以指定撤回消息的时间范围。
         * 注：消息撤回时间范围仅支持固定的天数：`3`，`7`，`15`，`30`。 特殊的时间范围：`-1`: 撤回全部消息。默认值为 `0` 不撤回任何消息。
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            userId: String,
            addBlacklist: Boolean = false,
            deleteHistoryMsgDays: Int = 0
        ): DeleteMemberApi = DeleteMemberApi(guildId, userId, Body(addBlacklist, deleteHistoryMsgDays))

    }

    override val path: Array<String> = arrayOf("guilds", guildId, "members", userId)

    override val body: Any
        get() = _body

    /**
     * [DeleteMemberApi] 请求用的参数体，由内部构建并使用。
     */
    @Serializable
    private data class Body(
        @SerialName("add_blacklist") val addBlacklist: Boolean,
        /**
         * 删除成员的同时，撤回该成员的消息，可以指定撤回消息的时间范围
         */
        @SerialName("delete_history_msg_days") val deleteHistoryMsgDays: Int
    ) {
        init {
            require(deleteHistoryMsgDays == -1 || deleteHistoryMsgDaysRangeContains(deleteHistoryMsgDays)) {
                "deleteHistoryMsgDays must be `-1` or in [0, 3, 7, 15, 30], but $deleteHistoryMsgDays"
            }
        }

        companion object {
            private const val DELETE_HISTORY_MSG_DAYS_RANGE =
                0 or (1 shl 3) or (1 shl 7) or (1 shl 15) or (1 shl 30) or (1 shl 0)

            private fun deleteHistoryMsgDaysRangeContains(value: Int): Boolean {
                return value <= 30 && (DELETE_HISTORY_MSG_DAYS_RANGE and (1 shl value)) != 0
            }

        }
    }
}
