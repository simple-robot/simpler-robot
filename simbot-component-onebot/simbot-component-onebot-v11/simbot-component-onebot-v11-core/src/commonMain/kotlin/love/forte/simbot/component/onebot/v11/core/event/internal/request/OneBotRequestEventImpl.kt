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

package love.forte.simbot.component.onebot.v11.core.event.internal.request

import love.forte.simbot.ability.AcceptOption
import love.forte.simbot.ability.RejectOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotStranger
import love.forte.simbot.component.onebot.v11.core.actor.internal.toStranger
import love.forte.simbot.component.onebot.v11.core.api.GetStrangerInfoApi
import love.forte.simbot.component.onebot.v11.core.api.SetFriendAddRequestApi
import love.forte.simbot.component.onebot.v11.core.api.SetGroupAddRequestApi
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.request.*
import love.forte.simbot.component.onebot.v11.event.request.RawFriendRequestEvent
import love.forte.simbot.component.onebot.v11.event.request.RawGroupRequestEvent


internal abstract class OneBotRequestEventImpl : OneBotRequestEvent {
    override val id: ID = UUID.random()

    override suspend fun accept() {
        doAccept(emptyArray())
    }

    override suspend fun accept(vararg options: AcceptOption) {
        doAccept(options)
    }

    override suspend fun reject() {
        doReject(emptyArray())
    }

    override suspend fun reject(vararg options: RejectOption) {
        doReject(options)
    }

    protected abstract suspend fun doAccept(options: Array<out AcceptOption>)
    protected abstract suspend fun doReject(options: Array<out RejectOption>)
}

internal class OneBotFriendRequestEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawFriendRequestEvent,
    override val bot: OneBotBot,
) : OneBotRequestEventImpl(), OneBotFriendRequestEvent {
    override suspend fun doAccept(options: Array<out AcceptOption>) {
        val remark: String? = (
            options.firstOrNull { it is OneBotFriendRequestAcceptOption.Remark }
                as? OneBotFriendRequestAcceptOption.Remark
            )?.remark

        bot.executeData(
            SetFriendAddRequestApi.create(
                flag = sourceEvent.flag,
                approve = true,
                remark = remark
            )
        )
    }

    override suspend fun doReject(options: Array<out RejectOption>) {
        bot.executeData(
            SetFriendAddRequestApi.create(
                flag = sourceEvent.flag,
                approve = false,
            )
        )
    }

    override fun toString(): String =
        eventToString("OneBotFriendRequestEvent")
}

internal class OneBotGroupRequestEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupRequestEvent,
    override val bot: OneBotBotImpl,
) : OneBotRequestEventImpl(), OneBotGroupRequestEvent {
    override suspend fun doAccept(options: Array<out AcceptOption>) {
        bot.executeData(
            SetGroupAddRequestApi.create(
                flag = sourceEvent.flag,
                subType = sourceEvent.subType,
                approve = true
            )
        )
    }

    override suspend fun doReject(options: Array<out RejectOption>) {
        val reason = (
            options.firstOrNull { it is OneBotGroupRequestRejectOption.Reason }
                as? OneBotGroupRequestRejectOption.Reason
            )?.reason

        bot.executeData(
            SetGroupAddRequestApi.create(
                flag = sourceEvent.flag,
                subType = sourceEvent.subType,
                approve = false,
                reason = reason
            )
        )
    }

    override suspend fun content(): OneBotGroup {
        return bot.groupRelation.group(sourceEvent.groupId)
            ?: error("Group with id ${sourceEvent.groupId} not found")
    }

    override suspend fun requester(): OneBotStranger {
        return bot.executeData(
            GetStrangerInfoApi
                .create(sourceEvent.userId)
        )
            .toStranger(bot)
    }

    override fun toString(): String =
        eventToString("OneBotGroupRequestEvent")
}

