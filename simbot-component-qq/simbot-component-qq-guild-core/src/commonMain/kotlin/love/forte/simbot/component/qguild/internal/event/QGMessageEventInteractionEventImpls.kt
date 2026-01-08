/*
 * Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.InteractionMessage

/**
 * QG组件中针对 [QGMessageEvent.reply] 的拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.2.0
 */
@OptIn(ExperimentalSimbotAPI::class, FuzzyEventTypeImplementation::class)
internal abstract class QGMessageEventPreReplyEventImpl(
    override val bot: QGBot,
    override val content: QGBaseMessageEvent<*>,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGMessageEventPreReplyEvent {
    override val id: ID = UUID.random()

    private val currentMessageUsed = atomic(false)
    private var _currentMessage: InteractionMessage = message

    override var currentMessage: InteractionMessage
        get() = _currentMessage
        set(value) {
            if (currentMessageUsed.value) {
                error("`currentMessage` has been used.")
            }
            _currentMessage = value
        }


    fun useMessage(): InteractionMessage {
        if (!currentMessageUsed.compareAndSet(expect = false, value = true)) {
            error("`currentMessage` has been used.")
        }
        return _currentMessage
    }
}

/**
 * QG组件中针对 [QGMessageEvent.reply] 的通知事件。
 *
 * @since 4.2.0
 */
@OptIn(ExperimentalSimbotAPI::class, FuzzyEventTypeImplementation::class)
internal abstract class QGMessageEventPostReplyEventImpl(
    override val bot: QGBot,
    override val content: QGBaseMessageEvent<*>,
    override val message: InteractionMessage,
    override val receipt: QGMessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGMessageEventPostReplyEvent {
    override val id: ID = UUID.random()
}

/**
 * @see QGAtMessageCreateEventPreReplyEvent
 * @since 4.2.0
 */
internal class QGAtMessageCreateEventPreReplyEventImpl(
    bot: QGBot,
    override val content: QGAtMessageCreateEvent,
    message: InteractionMessage,
) : QGMessageEventPreReplyEventImpl(bot, content, message),
    QGAtMessageCreateEventPreReplyEvent


/**
 * @see QGAtMessageCreateEventPreReplyEvent
 * @since 4.2.0
 */
internal class QGAtMessageCreateEventPostReplyEventImpl(
    bot: QGBot,
    override val content: QGAtMessageCreateEvent,
    message: InteractionMessage,
    receipt: QGMessageReceipt,
) : QGMessageEventPostReplyEventImpl(bot, content, message, receipt),
    QGAtMessageCreateEventPostReplyEvent


// QGDirectMessageCreateEvent implementations

/**
 * @see QGDirectMessageCreateEventPreReplyEvent
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal class QGDirectMessageCreateEventPreReplyEventImpl(
    bot: QGBot,
    override val content: QGDirectMessageCreateEvent,
    message: InteractionMessage,
) : QGMessageEventPreReplyEventImpl(bot, content, message),
    QGDirectMessageCreateEventPreReplyEvent

/**
 * @see QGDirectMessageCreateEventPostReplyEvent
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal class QGDirectMessageCreateEventPostReplyEventImpl(
    bot: QGBot,
    override val content: QGDirectMessageCreateEvent,
    message: InteractionMessage,
    receipt: QGMessageReceipt,
) : QGMessageEventPostReplyEventImpl(bot, content, message, receipt),
    QGDirectMessageCreateEventPostReplyEvent

// QGC2CMessageCreateEvent implementations

/**
 * @see QGC2CMessageCreateEventPreReplyEvent
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal class QGC2CMessageCreateEventPreReplyEventImpl(
    bot: QGBot,
    override val content: QGC2CMessageCreateEvent,
    message: InteractionMessage,
) : QGMessageEventPreReplyEventImpl(bot, content, message),
    QGC2CMessageCreateEventPreReplyEvent

/**
 * @see QGC2CMessageCreateEventPostReplyEvent
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal class QGC2CMessageCreateEventPostReplyEventImpl(
    bot: QGBot,
    override val content: QGC2CMessageCreateEvent,
    message: InteractionMessage,
    receipt: QGMessageReceipt,
) : QGMessageEventPostReplyEventImpl(bot, content, message, receipt),
    QGC2CMessageCreateEventPostReplyEvent

// QGGroupAtMessageCreateEvent implementations

/**
 * @see QGGroupAtMessageCreateEventPreReplyEvent
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal class QGGroupAtMessageCreateEventPreReplyEventImpl(
    bot: QGBot,
    override val content: QGGroupAtMessageCreateEvent,
    message: InteractionMessage,
) : QGMessageEventPreReplyEventImpl(bot, content, message),
    QGGroupAtMessageCreateEventPreReplyEvent

/**
 * @see QGGroupAtMessageCreateEventPostReplyEvent
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal class QGGroupAtMessageCreateEventPostReplyEventImpl(
    bot: QGBot,
    override val content: QGGroupAtMessageCreateEvent,
    message: InteractionMessage,
    receipt: QGMessageReceipt,
) : QGMessageEventPostReplyEventImpl(bot, content, message, receipt),
    QGGroupAtMessageCreateEventPostReplyEvent
