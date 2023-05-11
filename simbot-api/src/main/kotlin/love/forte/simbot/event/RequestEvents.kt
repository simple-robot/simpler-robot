/*
 * Copyright (c) 2021-2023 ForteScarlet.
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

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.JSTP
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 *
 * 与 **请求** 有关的事件。
 *
 * 常见情况下，一个请求可能有两种情况：
 * - 外界向当前bot为某种目的而发出的**申请**。比如一个入群申请、好友添加申请。
 *
 * - 外界向当前bot所发出的一种**邀请**。比如对BOT的入群邀请。
 *
 * 一个请求可能会有一些附加信息, 例如一些属性或者文本消息。
 * 目前提供了一个 [message] 来代表一次申请中可能存在的 **文本消息**。
 * 如果实现实现不支持此属性或者属性为空，将得到null。
 *
 * @see JoinRequestEvent
 *
 * @author ForteScarlet
 */
@BaseEvent
public interface RequestEvent : Event, UserInfoContainer {
    /**
     * 事件标识。
     */
    override val id: ID
    
    /**
     * 当前[Bot]。
     */
    override val bot: Bot
    
    /**
     * 一个申请事件可能会存在附加的文本消息。
     */
    public val message: String?
    
    /**
     * 这个请求的 **申请人**。
     *
     * @see JoinRequestEvent.requester
     */
    @JSTP
    public suspend fun requester(): UserInfo
    
    
    /**
     * 通常情况下, [user] 等同于 [requester].
     */
    @JSTP
    override suspend fun user(): UserInfo
    
    /**
     * 此申请的类型。
     */
    public val type: Type
    
    /**
     * 同意/接受此次请求。
     */
    @JvmBlocking
    @JvmAsync
    @ExperimentalSimbotApi
    public suspend fun accept(): Boolean
    
    /**
     * 拒绝/回绝此次请求。
     */
    @JvmBlocking
    @JvmAsync
    @ExperimentalSimbotApi
    public suspend fun reject(): Boolean
    
    
    /**
     * [RequestEvent] 的类型。
     */
    public enum class Type {
        /**
         * 主动申请。
         */
        APPLICATION,
        
        /**
         * 邀请/被邀请。
         */
        INVITATION
        
    }
    
    
    public companion object Key : BaseEventKey<RequestEvent>("api.request") {
        override fun safeCast(value: Any): RequestEvent? = doSafeCast(value)
    }
}

/**
 * [RequestEvent] 事件的子类型，代表一个 **加入** 申请。
 *
 * 加入申请可能是外人想要进入当前某个组织内，
 * 或者由外界的人邀请当前BOT进入他们的某个组织。
 *
 * 假若申请人是当前的bot（例如被邀请加入其他组织），那么理论上来讲应当满足 [requester] == [bot].
 *
 * 对于一个添加请求来讲，[申请人][requester] 不一定是他自己主动发起的，那么就可能存在一个 [邀请人][inviter].
 * [inviter] 不一定存在，需要参考实现平台是否支持，以及当前申请事件的语境是否真的存在邀请人。
 *
 *
 * @see RequestEvent
 * @see GuildJoinRequestEvent
 * @see GroupJoinRequestEvent
 */
@BaseEvent
public interface JoinRequestEvent : RequestEvent {
    
    /**
     * 这个添加请求的 **申请人**。
     *
     * 假如BOT是被邀请者，则此值可能代表 [bot], 而邀请的人则为 [inviter]。
     *
     */
    @JSTP
    override suspend fun requester(): UserInfo
    
    /**
     * 邀请人。当无法获取或不存在时得到null。
     */
    @JSTP
    public suspend fun inviter(): UserInfo?
    
    
    public companion object Key : BaseEventKey<JoinRequestEvent>("api.join_request", RequestEvent) {
        override fun safeCast(value: Any): JoinRequestEvent? = doSafeCast(value)
    }
}


/**
 * 一个与频道服务器相关的申请事件。
 */
public interface GuildRequestEvent : RequestEvent, GuildInfoContainer {
    public companion object Key : BaseEventKey<GuildRequestEvent>(
        "api.guild_request", RequestEvent
    ) {
        override fun safeCast(value: Any): GuildRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个频道的加入申请事件。
 *
 * @see GuildRequestEvent
 * @see JoinRequestEvent
 */
public interface GuildJoinRequestEvent : JoinRequestEvent, GuildRequestEvent {
    
    /**
     * 想要申请加入的人的信息。假如这是一个BOT被邀请的事件，则此信息可能等于 [bot].
     */
    @JSTP
    override suspend fun requester(): UserInfo
    
    
    public companion object Key : BaseEventKey<GuildJoinRequestEvent>(
        "api.guild_join_request", JoinRequestEvent, GuildRequestEvent
    ) {
        override fun safeCast(value: Any): GuildJoinRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个与群相关的请求事件。
 *
 * @see GroupJoinRequestEvent
 */
public interface GroupRequestEvent : RequestEvent, GroupInfoContainer {
    public companion object Key : BaseEventKey<GroupRequestEvent>(
        "api.group_request", RequestEvent
    ) {
        override fun safeCast(value: Any): GroupRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个群的加入申请事件。
 *
 * @see GroupRequestEvent
 * @see JoinRequestEvent
 */
public interface GroupJoinRequestEvent : GroupRequestEvent, JoinRequestEvent {
    
    /**
     * 想要加入目标群的人的信息。假如是BOT被邀请的事件，则此值可能等同于 [bot].
     */
    @JSTP
    override suspend fun requester(): UserInfo
    
    public companion object Key : BaseEventKey<GroupJoinRequestEvent>(
        "api.group_join_request", GroupRequestEvent, JoinRequestEvent
    ) {
        override fun safeCast(value: Any): GroupJoinRequestEvent? = doSafeCast(value)
    }
}


/**
 * 一个与频道相关的请求事件。
 */
public interface ChannelRequestEvent : RequestEvent, ChannelInfoContainer {
    
    public companion object Key : BaseEventKey<ChannelRequestEvent>(
        "api.channel_request", RequestEvent
    ) {
        override fun safeCast(value: Any): ChannelRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个与[用户][User]相关的请求事件.
 * 请求事件不能保证能够得到完整的 [User] 对象，但是应尽可能提供基本的用户信息 [UserInfo].
 *
 * @see FriendRequestEvent
 */
public interface UserRequestEvent : RequestEvent, UserInfoContainer {
    public companion object Key : BaseEventKey<UserRequestEvent>(
        "api.user_request", RequestEvent
    ) {
        override fun safeCast(value: Any): UserRequestEvent? = doSafeCast(value)
    }
}


/**
 * 一个[好友][Friend]申请。
 * 请求事件不能保证能够得到完整的 [Friend] 对象，但是应尽可能提供基本的用户信息 [FriendInfo].
 */
public interface FriendRequestEvent : UserRequestEvent, FriendInfoContainer {
    public companion object Key : BaseEventKey<FriendRequestEvent>(
        "api.friend_request", UserRequestEvent
    ) {
        override fun safeCast(value: Any): FriendRequestEvent? = doSafeCast(value)
    }
}


/**
 * 好友添加申请。
 *
 * @see JoinRequestEvent
 * @see FriendRequestEvent
 */
public interface FriendAddRequestEvent : JoinRequestEvent, FriendRequestEvent {
    public companion object Key : BaseEventKey<FriendAddRequestEvent>(
        "api.friend_add_request", JoinRequestEvent, FriendRequestEvent
    ) {
        override fun safeCast(value: Any): FriendAddRequestEvent? = doSafeCast(value)
    }
}
