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

package love.forte.simbot.component.qguild.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.qguild.api.interaction.InteractionResponseApi
import love.forte.simbot.qguild.event.InteractionCreate
import love.forte.simbot.qguild.event.InteractionCreateData
import love.forte.simbot.qguild.event.InteractionCreateResolvedData
import love.forte.simbot.suspendrunner.ST

/**
 * 互动事件创建（按钮回调）。
 *
 * @see InteractionCreate
 * @since 4.4.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGInteractionCreateEvent : QGBotEvent<InteractionCreate>() {
    /**
     * 原始的互动事件。
     */
    abstract override val sourceEventEntity: InteractionCreate

    override val id: ID
        get() = sourceEventEntity.id.ID

    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.data.timestamp.toTimestamp()

    /**
     * 互动事件 ID, 用于回应互动事件。
     */
    public val interactionId: ID
        get() = sourceEventEntity.data.id.ID

    /**
     * 互动类型。消息按钮: `11`, 自定义菜单: `12`
     */
    public val type: Int
        get() = sourceEventEntity.data.type

    /**
     * 互动事件的数据。
     */
    public val data: InteractionCreateData
        get() = sourceEventEntity.data.data

    /**
     * 事件发生的场景: `c2c`, `group`, `guild`
     */
    public val scene: String?
        get() = sourceEventEntity.data.scene

    /**
     * `0` 频道场景, `1` 群聊场景, `2` 单聊场景
     */
    public val chatType: Int
        get() = sourceEventEntity.data.chatType

    /**
     * 频道 openid, 仅频道场景提供
     */
    public val guildId: ID?
        get() = sourceEventEntity.data.guildId?.ID

    /**
     * 文字子频道 openid, 仅频道场景提供
     */
    public val channelId: ID?
        get() = sourceEventEntity.data.channelId?.ID

    /**
     * 单聊按钮触发用户 openid, 仅单聊场景提供
     */
    public val userOpenid: ID?
        get() = sourceEventEntity.data.userOpenid?.ID

    /**
     * 群 openid, 仅群聊场景提供
     */
    public val groupOpenid: ID?
        get() = sourceEventEntity.data.groupOpenid?.ID

    /**
     * 按钮触发用户的群成员 openid, 仅群聊场景提供
     */
    public val groupMemberOpenid: ID?
        get() = sourceEventEntity.data.groupMemberOpenid?.ID

    /**
     * 互动事件解析后的数据。
     */
    public val resolved: InteractionCreateResolvedData
        get() = sourceEventEntity.data.data.resolved

    /**
     * 回应此互动事件。
     *
     * 参考[官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html#点击回调按钮)：
     * > 由于 websocket 推送事件是单向的，开发者收到事件之后，需要进行一次"回应"，
     * > 告知QQ后台，事件已经收到，否则客户端会一直处于loading状态，直到超时。
     *
     * @param code `0` 成功, `1` 操作失败, `2` 操作频繁, `3` 重复操作, `4` 没有权限, `5` 仅管理员操作
     *
     * @see InteractionResponseApi
     */
    @ST
    public abstract suspend fun respond(code: Int)
}
