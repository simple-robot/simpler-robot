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

package love.forte.simbot.component.qguild.event

import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.event.*

/**
 * QQ组件中针对 [SendSupportInteractionEvent] 的子类型实现。
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGMessageEventInteractionEvent : MessageEventInteractionEvent,
    QGInternalMessageInteractionEvent {
    override val bot: QGBot
    override val content: QGBaseMessageEvent<*>
}

/**
 * QG组件中针对 [QGMessageEvent.reply] 的拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGMessageEventPreReplyEvent :
    QGMessageEventInteractionEvent,
    MessageEventPreReplyEvent {
    override val content: QGBaseMessageEvent<*>
    override val message: InteractionMessage
}

/**
 * QG组件中针对 [QGMessageEvent.reply] 的通知事件。
 *
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGMessageEventPostReplyEvent :
    QGMessageEventInteractionEvent,
    MessageEventPostReplyEvent {
    override val content: QGBaseMessageEvent<*>
    override val message: InteractionMessage
    override val receipt: QGMessageReceipt
}

/**
 * QG组件中针对 [QGAtMessageCreateEvent.reply] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGAtMessageCreateEventInteractionEvent :
    QGMessageEventInteractionEvent,
    ChatChannelMessageEventInteractionEvent {
    override val content: QGAtMessageCreateEvent
}

/**
 * 针对 [ChatChannelMessageEvent.reply] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGAtMessageCreateEventPreReplyEvent :
    QGAtMessageCreateEventInteractionEvent,
    QGMessageEventPreReplyEvent,
    ChatChannelMessageEventPreReplyEvent

/**
 * 针对 [ChatChannelMessageEvent.reply] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGAtMessageCreateEventPostReplyEvent :
    QGAtMessageCreateEventInteractionEvent,
    QGMessageEventPostReplyEvent,
    ChatChannelMessageEventPostReplyEvent

/**
 * QG组件中针对 [QGDirectMessageCreateEvent.reply] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGDirectMessageCreateEventInteractionEvent :
    QGMessageEventInteractionEvent,
    ContactMessageEventInteractionEvent {
    override val content: QGDirectMessageCreateEvent
}

/**
 * 针对 [QGDirectMessageCreateEvent.reply] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGDirectMessageCreateEventPreReplyEvent :
    QGDirectMessageCreateEventInteractionEvent,
    QGMessageEventPreReplyEvent,
    ContactMessageEventPreReplyEvent

/**
 * 针对 [QGDirectMessageCreateEvent.reply] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGDirectMessageCreateEventPostReplyEvent :
    QGDirectMessageCreateEventInteractionEvent,
    QGMessageEventPostReplyEvent,
    ContactMessageEventPostReplyEvent

/**
 * QG组件中针对 [QGC2CMessageCreateEvent.reply] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGC2CMessageCreateEventInteractionEvent :
    QGMessageEventInteractionEvent,
    ContactMessageEventInteractionEvent {
    override val content: QGC2CMessageCreateEvent
}

/**
 * 针对 [QGC2CMessageCreateEvent.reply] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGC2CMessageCreateEventPreReplyEvent :
    QGC2CMessageCreateEventInteractionEvent,
    QGMessageEventPreReplyEvent,
    ContactMessageEventPreReplyEvent

/**
 * 针对 [QGC2CMessageCreateEvent.reply] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGC2CMessageCreateEventPostReplyEvent :
    QGC2CMessageCreateEventInteractionEvent,
    QGMessageEventPostReplyEvent,
    ContactMessageEventPostReplyEvent

/**
 * QG组件中针对 [QGGroupAtMessageCreateEvent.reply] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGGroupAtMessageCreateEventInteractionEvent :
    QGMessageEventInteractionEvent,
    ChatGroupMessageEventInteractionEvent {
    override val content: QGGroupAtMessageCreateEvent
}

/**
 * 针对 [QGGroupAtMessageCreateEvent.reply] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGGroupAtMessageCreateEventPreReplyEvent :
    QGGroupAtMessageCreateEventInteractionEvent,
    QGMessageEventPreReplyEvent,
    ChatGroupMessageEventPreReplyEvent

/**
 * 针对 [QGGroupAtMessageCreateEvent.reply] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGGroupAtMessageCreateEventPostReplyEvent :
    QGGroupAtMessageCreateEventInteractionEvent,
    QGMessageEventPostReplyEvent,
    ChatGroupMessageEventPostReplyEvent