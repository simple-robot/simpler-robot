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

import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotGroupPostSendEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotGroupPreSendEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt

internal class OneBotGroupPreSendEventImpl(
    override val content: OneBotGroup,
    override val bot: OneBotBot,
    message: OneBotSegmentsInteractionMessage
) : AbstractMessagePreSendEventImpl(message), OneBotGroupPreSendEvent

internal class OneBotGroupPostSendEventImpl(
    override val content: OneBotGroup,
    override val bot: OneBotBot,
    override val receipt: OneBotMessageReceipt,
    override val message: OneBotSegmentsInteractionMessage
) : AbstractMessagePostSendEventImpl(), OneBotGroupPostSendEvent
