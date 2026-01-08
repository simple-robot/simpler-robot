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

package love.forte.simbot.component.qguild.utils

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.event.QGC2CMessageCreateEvent
import love.forte.simbot.component.qguild.event.QGDirectMessageCreateEvent
import love.forte.simbot.component.qguild.event.QGGroupAtMessageCreateEvent
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.event.*
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.event.InteractionMessage

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: QGTextChannel,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGTextChannelSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: QGMember,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGMemberSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: QGDmsContact,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGDmsContactSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: QGFriend,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGFriendSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: QGGroup,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGGroupSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: SendSupport,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

// 为各个具体事件类型提供专用的扩展函数，提供更好的类型安全

internal fun QGMessageReceipt.alsoEmitPostReplyEvent(
    bot: QGBotImpl,
    content: QGAtMessageCreateEvent,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGAtMessageCreateEventPostReplyEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostReplyEvent(
    bot: QGBotImpl,
    content: QGDirectMessageCreateEvent,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGDirectMessageCreateEventPostReplyEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostReplyEvent(
    bot: QGBotImpl,
    content: QGC2CMessageCreateEvent,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGC2CMessageCreateEventPostReplyEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}

internal fun QGMessageReceipt.alsoEmitPostReplyEvent(
    bot: QGBotImpl,
    content: QGGroupAtMessageCreateEvent,
    message: InteractionMessage,
): QGMessageReceipt {
    val event = QGGroupAtMessageCreateEventPostReplyEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}
