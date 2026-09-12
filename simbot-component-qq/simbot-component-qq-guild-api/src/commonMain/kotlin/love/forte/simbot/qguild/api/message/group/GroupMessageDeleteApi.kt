/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.message.group

import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmStatic

/**
 * [撤回群聊消息](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_messages_message_id.delete.html)
 *
 * 用于撤回群 `group_openid` 中的消息 `message_id`。发送超过两分钟的消息不可撤回。
 * 群管理员可撤回机器人自身和普通群成员的消息；普通成员只能撤回机器人自身发送的消息。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public class GroupMessageDeleteApi private constructor(
    groupOpenid: String,
    messageId: String,
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    public companion object Factory : SimpleDeleteApiDescription(
        "/v2/groups/{group_openid}/messages/{message_id}"
    ) {
        /**
         * 构建 [GroupMessageDeleteApi]。
         *
         * @param groupOpenid 目标群 OpenID。
         * @param messageId 目标消息 ID。
         */
        @JvmStatic
        public fun create(groupOpenid: String, messageId: String): GroupMessageDeleteApi =
            GroupMessageDeleteApi(groupOpenid, messageId)
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "messages", messageId)
}
