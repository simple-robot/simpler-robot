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

package love.forte.simbot.component.onebot.v11.core.event.internal.notice

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.notice.OneBotGroupAdminEvent
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupAdminEvent

internal class OneBotGroupAdminEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupAdminEvent,
    override val bot: OneBotBot
) : OneBotGroupAdminEvent {
    override val id: ID
        get() = with(sourceEvent) {
            "$postType-$noticeType-$subType-$groupId-$userId-$time"
        }.ID

    override suspend fun content(): OneBotMember {
        return bot.groupRelation.member(
            groupId = sourceEvent.groupId,
            memberId = sourceEvent.userId
        ) ?: error(
            "Member with id ${sourceEvent.userId} " +
                "in Group ${sourceEvent.groupId} is not found"
        )
    }

    override suspend fun source(): OneBotGroup {
        return bot.groupRelation.group(sourceEvent.groupId)
            ?: error("Group with id ${sourceEvent.groupId} is not found")
    }

    override fun toString(): String =
        eventToString("OneBotGroupAdminEvent")
}
