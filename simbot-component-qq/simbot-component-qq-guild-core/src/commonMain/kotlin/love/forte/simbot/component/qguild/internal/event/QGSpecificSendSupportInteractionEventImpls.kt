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
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.MessageReceipt

// QGTextChannel implementations

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGTextChannelSendSupportPreSendEventImpl(
    override val bot: QGBot,
    override val content: QGTextChannel,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGTextChannelSendSupportPreSendEvent {
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

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGTextChannelSendSupportPostSendEventImpl(
    override val bot: QGBot,
    override val content: QGTextChannel,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGTextChannelSendSupportPostSendEvent {
    override val id: ID = UUID.random()
}

// QGMember implementations

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGMemberSendSupportPreSendEventImpl(
    override val bot: QGBot,
    override val content: QGMember,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGMemberSendSupportPreSendEvent {
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

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGMemberSendSupportPostSendEventImpl(
    override val bot: QGBot,
    override val content: QGMember,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGMemberSendSupportPostSendEvent {
    override val id: ID = UUID.random()
}

// QGDmsContact implementations

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGDmsContactSendSupportPreSendEventImpl(
    override val bot: QGBot,
    override val content: QGDmsContact,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGDmsContactSendSupportPreSendEvent {
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

    override suspend fun target(): QGDmsContact = content
}

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGDmsContactSendSupportPostSendEventImpl(
    override val bot: QGBot,
    override val content: QGDmsContact,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGDmsContactSendSupportPostSendEvent {
    override val id: ID = UUID.random()

    override suspend fun target(): QGDmsContact = content
}

// QGFriend implementations

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGFriendSendSupportPreSendEventImpl(
    override val bot: QGBot,
    override val content: QGFriend,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGFriendSendSupportPreSendEvent {
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

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGFriendSendSupportPostSendEventImpl(
    override val bot: QGBot,
    override val content: QGFriend,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGFriendSendSupportPostSendEvent {
    override val id: ID = UUID.random()
}

// QGGroup implementations

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGGroupSendSupportPreSendEventImpl(
    override val bot: QGBot,
    override val content: QGGroup,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGGroupSendSupportPreSendEvent {
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

@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGGroupSendSupportPostSendEventImpl(
    override val bot: QGBot,
    override val content: QGGroup,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGGroupSendSupportPostSendEvent {
    override val id: ID = UUID.random()
}
