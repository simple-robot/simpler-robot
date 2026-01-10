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

package love.forte.simbot.component.onebot.v11.core.event.message

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.event.message.RawGroupMessageEvent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageContent
import love.forte.simbot.event.ChatGroupEvent
import love.forte.simbot.event.ChatGroupMessageEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.suspendrunner.STP


/**
 * [群消息事件](https://github.com/botuniverse/onebot-11/blob/master/event/message.md#群消息)
 *
 * @see RawGroupMessageEvent
 *
 * @see OneBotNormalGroupMessageEvent
 * @see OneBotAnonymousGroupMessageEvent
 * @see OneBotNoticeGroupMessageEvent
 *
 * @author ForteScarlet
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupMessageEvent : OneBotMessageEvent, ChatGroupEvent {
    override val sourceEvent: RawGroupMessageEvent
    override val messageContent: OneBotMessageContent

    /**
     * 事件发生所在群
     */
    override suspend fun content(): OneBotGroup

    /**
     * 消息子类型，正常消息是 `normal`，匿名消息是 `anonymous`，
     * 系统提示（如「管理员已禁止群内匿名聊天」）是 `notice`
     */
    public val subType: String
        get() = sourceEvent.subType

    /**
     * 消息 ID
     */
    public val messageId: ID
        get() = sourceEvent.messageId

    /**
     * 群号
     */
    public val groupId: LongID
        get() = sourceEvent.groupId

    /**
     * 发送者 QQ 号
     */
    public val userId: LongID
        get() = sourceEvent.userId

    /**
     * 原始消息内容
     */
    public val rawMessage: String
        get() = sourceEvent.rawMessage
}

/**
 * 正常消息类型的 [OneBotGroupMessageEvent]。
 * 即 [subType] == `normal`
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotNormalGroupMessageEvent : OneBotGroupMessageEvent, ChatGroupMessageEvent {
    override val messageContent: OneBotMessageContent
    override suspend fun content(): OneBotGroup
    override suspend fun author(): OneBotMember
}

/**
 * 匿名消息类型的 [OneBotGroupMessageEvent]。
 * 即 [subType] == `anonymous`
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotAnonymousGroupMessageEvent : OneBotGroupMessageEvent, ChatGroupMessageEvent {
    override val messageContent: OneBotMessageContent
    override suspend fun content(): OneBotGroup
    override suspend fun author(): OneBotMember
}

/**
 * 系统提示消息类型的 [OneBotGroupMessageEvent]。
 * 即 [subType] == `notice`
 */
@STP
public interface OneBotNoticeGroupMessageEvent : OneBotGroupMessageEvent {
    override val messageContent: OneBotMessageContent
    override suspend fun content(): OneBotGroup
}
