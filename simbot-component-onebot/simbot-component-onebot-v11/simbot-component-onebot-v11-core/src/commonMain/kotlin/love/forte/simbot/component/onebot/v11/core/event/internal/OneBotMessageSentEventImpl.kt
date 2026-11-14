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

package love.forte.simbot.component.onebot.v11.core.event.internal

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.event.MessageSentEvent
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.message.MessageReceipt

/**
 * 记录平台终端已经成功返回回执时的上下文，独立于整条拦截链的最终结果。
 */
internal class OneBotMessageSentEventImpl(
    /**
     * 发送终端实际使用的不可变上下文。
     */
    override val context: MessageSendContext,
    /**
     * 平台终端的真实回执，独立于外层拦截器返回的结果。
     */
    override val receipt: MessageReceipt,
) : MessageSentEvent {
    /**
     * 每次成功通知各自独立的事件标识。
     */
    override val id: ID = UUID.random()

    /**
     * 取得真实回执时记录的时间。
     */
    override val time: Timestamp = Timestamp.now()
}

/**
 * 在 Bot 作用域尽力投递成功通知。
 */
internal fun OneBotBotImpl.notifyMessageSent(context: MessageSendContext, receipt: MessageReceipt) {
    val event = OneBotMessageSentEventImpl(context, receipt)
    launch {
        pushEvent(event).collect()
    }.invokeOnCompletion { cause ->
        if (cause != null) {
            logger.error("Failed to dispatch message sent notification: {}", event.id, cause)
        }
    }
}
