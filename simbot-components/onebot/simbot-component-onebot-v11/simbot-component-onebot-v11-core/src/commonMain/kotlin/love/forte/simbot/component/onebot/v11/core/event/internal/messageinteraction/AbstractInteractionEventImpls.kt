/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotInternalMessagePostSendEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotInternalMessagePreSendEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.InteractionMessage


@OptIn(FuzzyEventTypeImplementation::class)
internal abstract class AbstractMessagePreSendEventImpl(override val message: OneBotSegmentsInteractionMessage) :
    OneBotInternalMessagePreSendEvent {
    override val id: ID = UUID.random()

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    protected val currentMessageUsed = atomic(false)
    private var _currentMessage: InteractionMessage = message

    override var currentMessage: InteractionMessage
        get() = _currentMessage
        set(value) {
            if (currentMessageUsed.value) {
                error("`currentMessage` has been used.")
            }
            _currentMessage = value
        }

    internal fun useCurrentMessage(): InteractionMessage {
        if (!currentMessageUsed.compareAndSet(false, true)) {
            error("`currentMessage` has been used.")
        }
        return _currentMessage
    }
}

@OptIn(FuzzyEventTypeImplementation::class)
internal abstract class AbstractMessagePostSendEventImpl : OneBotInternalMessagePostSendEvent {
    override val id: ID = UUID.random()

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()
}
