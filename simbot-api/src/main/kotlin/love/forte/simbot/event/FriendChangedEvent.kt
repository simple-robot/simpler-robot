/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.event

import love.forte.simbot.JSTP
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.Friend
import love.forte.simbot.message.doSafeCast


/**
 * 一个 [Bot] 的 [好友][Friend] 发生了变化，比如 [增加][FriendIncreaseEvent] 或 [减少][FriendDecreaseEvent] 了。
 */
@BaseEvent
public interface FriendChangedEvent : ChangedEvent, FriendEvent {
    
    /**
     * 发生好友变更的[Bot]。
     */
    @JSTP
    override suspend fun source(): Bot
    
    
    /**
     * 涉及到的[好友][Friend]。
     */
    @JvmSynthetic
    override suspend fun friend(): Friend
    
    
    public companion object Key : BaseEventKey<FriendChangedEvent>(
        "api.friend_changed",
        ChangedEvent, FriendEvent
    ) {
        override fun safeCast(value: Any): FriendChangedEvent? = doSafeCast(value)
    }
    
    
}


/**
 * 一个 **好友增加** 事件。
 *
 * 这代表的是好友 **已经** 被添加的事件，不同于 [UserRequestEvent].
 */
public interface FriendIncreaseEvent : IncreaseEvent, FriendChangedEvent {
    /**
     * 增加的[好友][Friend]。
     */
    @JSTP
    override suspend fun after(): Friend
    
    
    public companion object Key : BaseEventKey<FriendIncreaseEvent>(
        "api.friend_increase", IncreaseEvent, FriendChangedEvent
    ) {
        override fun safeCast(value: Any): FriendIncreaseEvent? = doSafeCast(value)
    }
}


/**
 * 一个 **好友减少** 事件。
 *
 * 这代表的是好友 **已经** 被移除的事件。
 */
public interface FriendDecreaseEvent : DecreaseEvent, FriendChangedEvent {
    
    /**
     * 减少的 [好友][Friend].
     */
    @JSTP
    override suspend fun before(): Friend
    
    
    public companion object Key : BaseEventKey<FriendDecreaseEvent>(
        "api.friend_decrease", DecreaseEvent, FriendChangedEvent
    ) {
        override fun safeCast(value: Any): FriendDecreaseEvent? = doSafeCast(value)
    }
}
