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

package love.forte.simbot.event

import love.forte.simbot.bot.Bot
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.message.MessageReceipt

/**
 * 组件的实际消息发送终端成功取得回执后的通知，同时适用于 send 和 reply。
 *
 * 组件负责在发送终端成功后异步投递。
 *
 * @since 5.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface MessageSentEvent : BotEvent, InternalNotificationEvent {
    /**
     * 实际发送使用的上下文，包含最终消息、选项及原始调用来源。
     * @since 5.0
     */
    public val context: MessageSendContext

    /**
     * 实际发送终端返回的回执。
     * @since 5.0
     */
    public val receipt: MessageReceipt

    override val bot: Bot
        get() = context.bot
}
