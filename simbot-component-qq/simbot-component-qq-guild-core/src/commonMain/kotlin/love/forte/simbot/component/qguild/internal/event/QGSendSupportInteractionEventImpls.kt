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

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.event.QGSendSupportPostSendEvent
import love.forte.simbot.component.qguild.event.QGSendSupportPreSendEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.MessageReceipt


@OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
internal class QGSendSupportPreSendEventImpl(
    override val bot: QGBot,
    override val content: SendSupport,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : QGSendSupportPreSendEvent {
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
internal class QGSendSupportPostSendEventImpl(
    override val bot: QGBot,
    override val content: SendSupport,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : QGSendSupportPostSendEvent {
    override val id: ID = UUID.random()
}