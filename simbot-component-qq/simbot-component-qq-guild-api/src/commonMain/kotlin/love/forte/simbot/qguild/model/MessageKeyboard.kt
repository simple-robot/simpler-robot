/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import kotlin.jvm.JvmStatic


/**
 * [消息交互=>消息按钮](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class MessageKeyboard(
    val id: String? = null,
    @SerialName("render_data")
    val renderData: RenderData? = null,
    val action: Action? = null,
) {
    public companion object {
        /**
         * Create a [MessageKeyboard]
         */
        @JvmStatic
        public fun create(id: String): MessageKeyboard =
            MessageKeyboard(id = id)
    }

    /**
     * [MessageKeyboard.renderData].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     */
    @ApiModel
    @Serializable
    public data class RenderData(
        val label: String,
        val visitedLabel: String,
        val style: Int,
    )

    /**
     * [Action.permission].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     */
    @ApiModel
    @Serializable
    public data class ActionPermission(
        val type: Int,
        @SerialName("specify_user_ids")
        val specifyUserIds: List<String>? = null,
        @SerialName("specify_role_ids")
        val specifyRoleIds: List<String>? = null,
    )

    /**
     * [MessageKeyboard.action].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     */
    @ApiModel
    @Serializable
    public data class Action(
        val permission: ActionPermission? = null,
        val data: String?,
        val reply: Boolean? = null,
        val enter: Boolean? = null,
        val anchor: Int? = null,
        @SerialName("unsupport_tips")
        val unsupportTips: String,
    )
}
