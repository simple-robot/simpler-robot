/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.event.internal

import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.event.KookUpdatedChannelMessageEvent
import love.forte.simbot.component.kook.event.KookUpdatedPrivateMessageEvent
import love.forte.simbot.kook.event.Event
import love.forte.simbot.kook.event.UpdatedMessageEventExtra
import love.forte.simbot.kook.event.UpdatedPrivateMessageEventExtra


internal class KookUpdatedChannelMessageEventImpl(
    override val bot: KookBot,
    override val sourceEvent: Event<UpdatedMessageEventExtra>,
    override val sourceEventRaw: String
) : KookUpdatedChannelMessageEvent()

internal class KookUpdatedPrivateMessageEventImpl(
    override val bot: KookBot,
    override val sourceEvent: Event<UpdatedPrivateMessageEventExtra>,
    override val sourceEventRaw: String
) : KookUpdatedPrivateMessageEvent()
