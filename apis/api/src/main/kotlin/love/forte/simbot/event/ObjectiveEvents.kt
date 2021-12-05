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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 * 一个与 [Objectives] 相关的事件。
 * 可能是与联系人（好友、成员等）或者与组织（群、频道等）相关的事件。
 *
 * [ObjectiveEvent] 是一个标记用事件类型，不存在 Key, 不允许被直接监听。
 */
public sealed interface ObjectiveEvent : Event {
    override val bot: Bot
}


//region user events
/**
 * 一个与 [用户][User] 相关的事件。
 */
public interface UserEvent : ObjectiveEvent {
    /**
     * 这个[用户][User]。
     */
    public suspend fun user(): User

    @Api4J
    public val user: User
        get() = runBlocking { user() }

    public companion object Key : BaseEventKey<UserEvent>("api.user", setOf()) {
        override fun safeCast(value: Any): UserEvent? = doSafeCast(value)
    }

}

/**
 * 一个与 [成员][Member] 相关的事件。
 */
public interface MemberEvent : UserEvent {
    /**
     * 这个[成员][Member]
     */
    @JvmSynthetic
    public suspend fun member(): Member

    @Api4J
    public val member: Member
        get() = runBlocking { member() }

    @Api4J
    override val user: User
        get() = member

    @JvmSynthetic
    override suspend fun user(): User = member()

    public companion object Key : BaseEventKey<MemberEvent>("api.member", setOf(UserEvent)) {
        override fun safeCast(value: Any): MemberEvent? = doSafeCast(value)
    }
}


/**
 * 一个与 [好友][Friend] 相关的事件。
 */
public interface FriendEvent : UserEvent {
    /**
     * 这个[好友][Friend]
     */
    @JvmSynthetic
    public suspend fun friend(): Friend


    @Api4J
    public val friend: Friend
        get() = runBlocking { friend() }

    @Api4J
    override val user: User
        get() = friend

    @JvmSynthetic
    override suspend fun user(): User = friend()


    public companion object Key : BaseEventKey<FriendEvent>("api.friend", setOf(UserEvent)) {
        override fun safeCast(value: Any): FriendEvent? = doSafeCast(value)
    }
}

// /**
//  * 一个主要与 [Bot] 相关的事件。
//  */
// public interface BotEvent : UserEvent {
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
    /**
     * 这个[组织][Organization]
     */
    @JvmSynthetic
    public suspend fun organization(): Organization

    @Api4J
    public val organization: Organization
        get() = runBlocking { organization() }

    public companion object Key : BaseEventKey<OrganizationEvent>("api.organization", setOf()) {
        override fun safeCast(value: Any): OrganizationEvent? = doSafeCast(value)
    }
}

/**
 * 一个与 [群][Group] 相关的事件。
 */
public interface GroupEvent : OrganizationEvent {

    /**
     * 这个[群][Group]
     */
    @JvmSynthetic
    public suspend fun group(): Group

    @Api4J
    public val group: Group
        get() = runBlocking { group() }

    public companion object Key : BaseEventKey<GroupEvent>("api.group", setOf(OrganizationEvent)) {
        override fun safeCast(value: Any): GroupEvent? = doSafeCast(value)
    }
}


/**
 * 一个与 [频道服务器][Guild] 相关的事件。
 */
public interface GuildEvent : OrganizationEvent {
    /**
     * 这个[频道服务器][Guild]
     */
    @JvmSynthetic
    public suspend fun guild(): Guild


    @Api4J
    public val guild: Guild
        get() = runBlocking { guild() }

    public companion object Key : BaseEventKey<GuildEvent>("api.guild", setOf(OrganizationEvent)) {
        override fun safeCast(value: Any): GuildEvent? = doSafeCast(value)
    }
}


/**
 * 一个与 [频道][Channel] 相关的事件。
 */
public interface ChannelEvent : OrganizationEvent {
    /**
     * 这个[频道][Channel]
     */
    @JvmSynthetic
    public suspend fun channel(): Channel

    @Api4J
    public val channel: Channel
        get() = runBlocking { channel() }

    public companion object Key : BaseEventKey<ChannelEvent>("api.channel", setOf(OrganizationEvent)) {
        override fun safeCast(value: Any): ChannelEvent? = doSafeCast(value)
    }
}

//endregion



