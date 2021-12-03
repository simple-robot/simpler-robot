/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
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

import love.forte.simbot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 * 一个与 [Objectives] 相关的事件。
 * 可能是与联系人（好友、成员等）或者与组织（群、频道等）相关的事件。
 *
 * [ObjectiveEvent] 不存在 Key, 不允许被直接监听。
 */
public interface ObjectiveEvent : Event {
    override val bot: Bot

    /**
     * 与此事件相关的 [Objectives] 目标对象。
     *
     * 如果一个事件中存在多种 [ObjectiveEvent] 实现，则尽量避免直接使用 [objective] --- [objective] 的最终指向目标将会不确定。
     *
     */
    public val objective: Objectives


    // public companion object Key : BaseEventKey<ObjectiveEvent>("api.objective", setOf(Event)) {
    //     override fun safeCast(value: Any): ObjectiveEvent? = doSafeCast(value)
    // }

}


//region user events
/**
 * 一个与 [用户][User] 相关的事件。
 */
public interface UserEvent : ObjectiveEvent {
    override val objective: Objectives
        get() = user

    public val user: User

    public companion object Key : BaseEventKey<UserEvent>("api.user", setOf()) {
        override fun safeCast(value: Any): UserEvent? = doSafeCast(value)
    }

}

/**
 * 一个与 [成员][Member] 相关的事件。
 */
public interface MemberEvent : UserEvent {
    override val objective: Objectives
        get() = member
    override val user: User
        get() = member

    public val member: Member

    public companion object Key : BaseEventKey<MemberEvent>("api.member", setOf(UserEvent)) {
        override fun safeCast(value: Any): MemberEvent? = doSafeCast(value)
    }
}


/**
 * 一个与 [好友][Friend] 相关的事件。
 */
public interface FriendEvent : UserEvent {
    override val objective: Objectives
        get() = friend
    override val user: User
        get() = friend

    public val friend: Friend

    public companion object Key : BaseEventKey<FriendEvent>("api.friend", setOf(UserEvent)) {
        override fun safeCast(value: Any): FriendEvent? = doSafeCast(value)
    }
}

// /**
//  * 一个主要与 [Bot] 相关的事件。
//  */
// public interface BotEvent : UserEvent {
//     override val objective: Bot get() = bot
//
//     public companion object Key : BaseEventKey<FriendEvent>("api.bot", setOf(UserEvent)) {
//         override fun safeCast(value: Any): FriendEvent? = doSafeCast(value)
//     }
// }

//endregion


//region organization event
/**
 * 一个与 [组织][Organization] 相关的事件。
 */
public interface OrganizationEvent : ObjectiveEvent {
    override val objective: Objectives
        get() = organization

    public val organization: Organization

    public companion object Key : BaseEventKey<OrganizationEvent>("api.organization", setOf()) {
        override fun safeCast(value: Any): OrganizationEvent? = doSafeCast(value)
    }
}

/**
 * 一个与 [群][Group] 相关的事件。
 */
public interface GroupEvent : OrganizationEvent {
    override val objective: Objectives
        get() = group

    public val group: Group

    public companion object Key : BaseEventKey<GroupEvent>("api.group", setOf(OrganizationEvent)) {
        override fun safeCast(value: Any): GroupEvent? = doSafeCast(value)
    }
}


public interface GuildEvent : OrganizationEvent {
    override val objective: Objectives
        get() = guild

    public val guild: Guild

    public companion object Key : BaseEventKey<GuildEvent>("api.guild", setOf(OrganizationEvent)) {
        override fun safeCast(value: Any): GuildEvent? = doSafeCast(value)
    }
}


/**
 * 一个与 [频道][Channel] 相关的事件。
 */
public interface ChannelEvent : OrganizationEvent {
    override val objective: Objectives
        get() = channel

    public val channel: Channel

    public companion object Key : BaseEventKey<ChannelEvent>("api.channel", setOf(OrganizationEvent)) {
        override fun safeCast(value: Any): ChannelEvent? = doSafeCast(value)
    }
}

//endregion



