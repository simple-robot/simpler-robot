/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.Bot
import love.forte.simbot.definition.Friend
import love.forte.simbot.message.doSafeCast


/**
 * 一个 **好友增加** 事件。
 *
 * 这代表的是好友 **已经** 被添加的事件，不同于 [UserRequestEvent].
 */
public interface FriendIncreaseEvent : IncreaseEvent<Bot, Friend>, FriendEvent {

    override suspend fun source(): Bot
    override suspend fun friend(): Friend

    //// Impl
    override suspend fun target(): Friend = friend()

    public companion object Key : BaseEventKey<FriendIncreaseEvent>(
        "api.friend_increase", IncreaseEvent, FriendEvent
    ) {
        override fun safeCast(value: Any): FriendIncreaseEvent? = doSafeCast(value)
    }
}


/**
 * 一个 **好友减少** 事件。
 *
 * 这代表的是好友 **已经** 被移除的事件。
 */
public interface FriendDecreaseEvent : DecreaseEvent<Bot, Friend>, FriendEvent {


    override suspend fun source(): Bot
    override suspend fun friend(): Friend

    //// Impl
    override suspend fun target(): Friend = friend()

    public companion object Key : BaseEventKey<FriendDecreaseEvent>(
        "api.friend_decrease", DecreaseEvent, FriendEvent
    ) {
        override fun safeCast(value: Any): FriendDecreaseEvent? = doSafeCast(value)
    }
}