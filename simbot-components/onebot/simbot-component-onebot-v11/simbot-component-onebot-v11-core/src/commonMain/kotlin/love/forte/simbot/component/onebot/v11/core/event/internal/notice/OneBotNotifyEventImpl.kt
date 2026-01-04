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
import love.forte.simbot.common.id.LongID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.notice.*
import love.forte.simbot.component.onebot.v11.event.notice.RawNotifyEvent


/**
 *
 * @author ForteScarlet
 */
internal abstract class OneBotNotifyEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawNotifyEvent,
    override val bot: OneBotBotImpl
) : OneBotNotifyEvent {
    override val id: ID
        get() = with(sourceEvent) {
            "$postType-$noticeType-$subType-$groupId-$userId-$time"
        }.ID
}

/**
 *
 * @author ForteScarlet
 */
internal abstract class OneBotGroupNotifyEventImpl(
    override val sourceEventRaw: String?,
    override val groupId: LongID,
    override val sourceEvent: RawNotifyEvent,
    override val bot: OneBotBotImpl
) : OneBotGroupNotifyEvent, OneBotNotifyEventImpl(sourceEventRaw, sourceEvent, bot) {
    override suspend fun source(): OneBotGroup {
        return bot.groupRelation.group(groupId)
            ?: error("Group with id $groupId is not found")
    }

    override suspend fun content(): OneBotMember {
        return bot.groupRelation.member(
            groupId = groupId,
            memberId = sourceEvent.userId
        ) ?: error(
            "Member with id ${sourceEvent.userId} " +
                "in Group ${sourceEvent.groupId} is not found"
        )
    }
}

internal class OneBotHonorEventImpl(
    sourceEventRaw: String?,
    sourceEvent: RawNotifyEvent,
    bot: OneBotBotImpl
) : OneBotGroupNotifyEventImpl(sourceEventRaw, sourceEvent.groupId!!, sourceEvent, bot),
    OneBotHonorEvent {
    override fun toString(): String =
        eventToString("OneBotHonorEvent")
}

internal class OneBotLuckyKingEventImpl(
    sourceEventRaw: String?,
    sourceEvent: RawNotifyEvent,
    bot: OneBotBotImpl
) : OneBotGroupNotifyEventImpl(sourceEventRaw, sourceEvent.groupId!!, sourceEvent, bot),
    OneBotLuckyKingEvent {
    override fun toString(): String =
        eventToString("OneBotLuckyKingEvent")
}

internal class OneBotMemberPokeEventImpl(
    sourceEventRaw: String?,
    sourceEvent: RawNotifyEvent,
    bot: OneBotBotImpl
) : OneBotGroupNotifyEventImpl(sourceEventRaw, sourceEvent.groupId!!, sourceEvent, bot),
    OneBotMemberPokeEvent {
    override fun toString(): String =
        eventToString("OneBotMemberPokeEvent")
}

internal class OneBotBotSelfPokeEventImpl(
    sourceEventRaw: String?,
    sourceEvent: RawNotifyEvent,
    bot: OneBotBotImpl
) : OneBotGroupNotifyEventImpl(sourceEventRaw, sourceEvent.groupId!!, sourceEvent, bot),
    OneBotBotSelfPokeEvent {
    override fun toString(): String =
        eventToString("OneBotBotSelfPokeEvent")
}

internal class OneBotPrivatePokeEventImpl(
    sourceEventRaw: String?,
    sourceEvent: RawNotifyEvent,
    bot: OneBotBotImpl
) : OneBotNotifyEventImpl(sourceEventRaw, sourceEvent, bot),
    OneBotPrivatePokeEvent {
    override fun toString(): String =
        eventToString("OneBotPrivatePokeEvent")
}
