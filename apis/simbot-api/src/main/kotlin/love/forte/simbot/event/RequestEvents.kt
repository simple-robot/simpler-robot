/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 *
 * 与 **请求** 有关的事件。
 *
 * 一个请求可能有两种情况：
 * - 外界向当前bot为某种目的而发出的**申请**。
 *      常见为一个入群申请、好友添加申请。
 *
 * - 外界向当前bot所发出的一种**邀请**。
 *      常见为对BOT的入群邀请。
 *
 * 一个请求可能会有一些附加信息, 例如一些属性或者文本消息。
 * 目前提供了一个 [RequestEvent.message] 来代表一次申请中可能存在的 **文本消息**。
 * 如果实现实现不支持此属性或者属性为空，将得到null。
 *
 * @see JoinRequestEvent
 *
 * @author ForteScarlet
 */
public interface RequestEvent : Event, UserInfoContainer {
    override val metadata: Event.Metadata
    override val bot: Bot

    /**
     *
     */
    public val message: String?

    /**
     * 这个请求的 **发起者**。
     */
    public suspend fun requester(): UserInfo

    // Impl
    @Api4J
    public val requester: UserInfo
        get() = runBlocking { requester() }


    /**
     * 通常情况下, [user] 等同于 [requester].
     */
    override suspend fun user(): UserInfo = requester()


    /**
     * 请求事件的可见范围。
     * 对于请求事件，可见范围普遍为 [Event.VisibleScope.INTERNAL] 或 [Event.VisibleScope.PRIVATE].
     */
    override val visibleScope: Event.VisibleScope


    /**
     * 此申请的类型。
     */
    public val type: Type

    /**
     * 是否同意/接受此次请求。
     */
    @ExperimentalSimbotApi
    public suspend fun accept(): Boolean

    @Api4J
    @ExperimentalSimbotApi
    public fun acceptBlocking(): Boolean = runBlocking { accept() }

    /**
     * 是否拒绝/回绝此次请求。
     */
    @ExperimentalSimbotApi
    public suspend fun reject(): Boolean

    @Api4J
    @ExperimentalSimbotApi
    public fun rejectBlocking(): Boolean = runBlocking { reject() }


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
public interface JoinRequestEvent : RequestEvent {

    /**
     * 邀请人。当无法获取或不存在时得到null。
     */
    public suspend fun inviter(): UserInfo?
    public val inviter: UserInfo? get() = runBlocking { inviter() }

    public companion object Key : BaseEventKey<JoinRequestEvent>("api.join_request", RequestEvent) {
        override fun safeCast(value: Any): JoinRequestEvent? = doSafeCast(value)
    }
}


/**
 * 一个与频道服务器相关的申请事件。
 */
public interface GuildRequestEvent : RequestEvent, GuildInfoContainer {

    override suspend fun guild(): GuildInfo

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

    override suspend fun group(): GroupInfo
    override suspend fun requester(): UserInfo

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
public interface GroupJoinRequestEvent : GroupRequestEvent {

    override suspend fun group(): GroupInfo
    override suspend fun requester(): UserInfo

    public companion object Key : BaseEventKey<GroupJoinRequestEvent>(
        "api.group_join_request", GroupRequestEvent
    ) {
        override fun safeCast(value: Any): GroupJoinRequestEvent? = doSafeCast(value)
    }
}


/**
 * 一个与频道相关的请求事件。
 */
public interface ChannelRequestEvent : RequestEvent, ChannelInfoContainer {

    override suspend fun channel(): ChannelInfo


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

    override suspend fun user(): UserInfo

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

    /**
     * [friend] 仅代表申请人的基本信息，不代表他已经成为了好友。
     */
    override suspend fun friend(): FriendInfo

    // Impl
    override suspend fun user(): UserInfo = friend()

    @Api4J
    override val user: UserInfo
        get() = friend

    public companion object Key : BaseEventKey<FriendRequestEvent>(
        "api.friend_request", UserRequestEvent
    ) {
        override fun safeCast(value: Any): FriendRequestEvent? = doSafeCast(value)
    }
}

//
// // 添加请求
//
// /**
//  * 一个添加请求。
//  * 大多数情况下，bot需要拥有一定的相关权限才可能收到相关的添加请求，比如bot需要是一个管理员，
//  * 并且在添加的时候需要进行验证。
//  *
//  * 默认提供了三个类型的请求事件类型：
//  * - [GroupAddRequestEvent]
//  * - [GuildAddRequestEvent]
//  * - [FriendAddRequestEvent]
//  */
// public interface AddRequestEvent : RequestEvent {
//
//     public companion object Key : BaseEventKey<AddRequestEvent>("api.add_request", RequestEvent) {
//         override fun safeCast(value: Any): AddRequestEvent? = doSafeCast(value)
//     }
// }
//
//
// /**
//  * 群添加申请
//  *
//  * @see AddRequestEvent
//  * @see GuildRequestEvent
//  */
// public interface GroupAddRequestEvent : AddRequestEvent, GuildRequestEvent {
//
//     public companion object Key : BaseEventKey<GroupAddRequestEvent>(
//         "api.group_add_request", AddRequestEvent, GuildRequestEvent
//     ) {
//         override fun safeCast(value: Any): GroupAddRequestEvent? = doSafeCast(value)
//     }
// }
//
// /**
//  * 频道添加申请
//  *
//  * @see AddRequestEvent
//  * @see GuildRequestEvent
//  */
// public interface GuildAddRequestEvent : AddRequestEvent, GuildRequestEvent {
//
//     public companion object Key : BaseEventKey<GuildAddRequestEvent>(
//         "api.guild_add_request", AddRequestEvent, GuildRequestEvent
//     ) {
//         override fun safeCast(value: Any): GuildAddRequestEvent? = doSafeCast(value)
//     }
// }
//
/**
 * 好友添加申请。
 *
 * @see JoinRequestEvent
 * @see FriendRequestEvent
 */
public interface FriendAddRequestEvent : JoinRequestEvent, FriendRequestEvent {

    override suspend fun requester(): FriendInfo

    override suspend fun friend(): FriendInfo = requester()

    public companion object Key : BaseEventKey<FriendAddRequestEvent>(
        "api.friend_add_request", JoinRequestEvent, FriendRequestEvent
    ) {
        override fun safeCast(value: Any): FriendAddRequestEvent? = doSafeCast(value)
    }
}