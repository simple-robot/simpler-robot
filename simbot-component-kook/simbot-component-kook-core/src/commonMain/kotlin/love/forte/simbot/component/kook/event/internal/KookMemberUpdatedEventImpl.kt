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

import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.event.KookMemberUpdatedEvent
import love.forte.simbot.component.kook.internal.KookGuildImpl
import love.forte.simbot.component.kook.internal.KookMemberImpl
import love.forte.simbot.kook.event.Event
import love.forte.simbot.kook.event.UpdatedGuildMemberEventExtra
import love.forte.simbot.kook.objects.User


/**
 *
 * @author ForteScarlet
 */
internal class KookMemberUpdatedEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<UpdatedGuildMemberEventExtra>,
    private val sourceGuild: KookGuildImpl,
    private val newMember: KookMemberImpl,
    private val beforeMember: KookMemberImpl,
    override val sourceEventRaw: String
) : KookMemberUpdatedEvent() {
    override val beforeSource: User
        get() = beforeMember.source

    override suspend fun source(): KookGuildImpl = sourceGuild
    override suspend fun content(): KookMember = newMember
}
