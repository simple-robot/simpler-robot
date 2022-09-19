/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.action.ActionType
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 * 一个与组织成员变动相关的事件。
 *
 * @see MemberIncreaseEvent
 * @see MemberDecreaseEvent
 */
@BaseEvent
public interface MemberChangedEvent :
    ChangedEvent, OrganizationEvent, MemberEvent {
    
    /**
     * 这次组织成员变动的**操作者**。
     *
     * 当这次成员变动是因为变动的这位成员 ([before]或[after]) 自己造成的，那么 [operator] 等同于这位成员。
     * 常见于成员的主动退群。
     *
     * 当这次成员变动是由于这位成员之外的其他人所造成的，那么 [operator] 等同于这位操作者。
     * 常见于某位成员被管理员踢出，或经由某位管理员审核后加入当前群聊等，此时这位管理员便是 [operator].
     *
     * 无法保证当前事件能够获取到 [operator] 的信息，当不支持对此信息进行获取的时候，将会得到 `null`。
     *
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun operator(): MemberInfo?
    
    
    public companion object Key :
        BaseEventKey<MemberChangedEvent>(
            "api.member_changed",
            ChangedEvent, OrganizationEvent, MemberEvent
        ) {
        override fun safeCast(value: Any): MemberChangedEvent? = doSafeCast(value)
    }
}


/**
 * 一个 **成员增加** 事件。
 * 其代表一个组织中增加了某个成员。
 *
 * @see IncreaseEvent
 * @see MemberChangedEvent
 */
public interface MemberIncreaseEvent :
    IncreaseEvent,
    MemberChangedEvent {
    
    /**
     * 成员增加事件发生所在的组织。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun source(): Organization
    
    /**
     * 增加的[成员][Member]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun after(): Member
    
    
    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType
    
    public companion object Key :
        BaseEventKey<MemberIncreaseEvent>(
            "api.member_increase",
            IncreaseEvent, MemberChangedEvent
        ) {
        override fun safeCast(value: Any): MemberIncreaseEvent? = doSafeCast(value)
    }
}

/**
 * 一个 **成员减少** 事件。
 * 其代表一个组织中减少了某个成员。
 *
 * @see DecreaseEvent
 * @see MemberChangedEvent
 */
public interface MemberDecreaseEvent :
    DecreaseEvent,
    MemberChangedEvent {
    
    
    /**
     * 离开的[成员][Member]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun before(): Member
    
    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType
    
    public companion object Key :
        BaseEventKey<MemberDecreaseEvent>(
            "api.member_decrease",
            DecreaseEvent, MemberChangedEvent
        ) {
        override fun safeCast(value: Any): MemberDecreaseEvent? = doSafeCast(value)
    }
}


/**
 *
 * [MemberIncreaseEvent] 事件类型的子集，代表为一个发生于 [Group] 的成员增加事件。
 *
 * @see MemberIncreaseEvent
 */
public interface GroupMemberIncreaseEvent : MemberIncreaseEvent, GroupEvent {
    
    /**
     * 成员增加事件发生所在的[群][Group]。
     */
    @JvmSynthetic
    override suspend fun group(): Group
    
    
    /**
     * 成员增加事件发生所在的[群][Group]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun source(): Group
    
    /**
     * 增加的[群成员][GroupMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun after(): GroupMember
    
    /**
     * 增加的[群成员][GroupMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun member(): GroupMember
    
    
    override val key: Event.Key<out GroupMemberIncreaseEvent>
    
    public companion object Key : BaseEventKey<GroupMemberIncreaseEvent>(
        "api.group_member_increase",
        MemberIncreaseEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMemberIncreaseEvent? = doSafeCast(value)
    }
    
}

/**
 *
 * [MemberIncreaseEvent] 事件类型的子集，代表为一个发生于 [Guild] 的成员增加事件。
 *
 * @see MemberIncreaseEvent
 */
public interface GuildMemberIncreaseEvent : MemberIncreaseEvent, GuildEvent {
    
    /**
     * 成员增加事件发生所在的[频道服务器][Guild]。
     */
    @JvmSynthetic
    override suspend fun guild(): Guild
    
    
    /**
     * 成员增加事件发生所在的[频道服务器][Guild]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun source(): Guild
    
    
    /**
     * 增加的[频道成员][GuildMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun after(): GuildMember
    
    
    /**
     * 增加的[频道成员][GuildMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun member(): GuildMember
    
    
    override val key: Event.Key<out GuildMemberIncreaseEvent>
    
    public companion object Key : BaseEventKey<GuildMemberIncreaseEvent>(
        "api.guild_member_increase",
        MemberIncreaseEvent, GuildEvent
    ) {
        override fun safeCast(value: Any): GuildMemberIncreaseEvent? = doSafeCast(value)
    }
}

/**
 *
 * [MemberDecreaseEvent] 的子类型事件，代表为一个发生于 [Group] 的成员减少事件。
 *
 * @see MemberDecreaseEvent
 */
public interface GroupMemberDecreaseEvent : MemberDecreaseEvent, GroupEvent {
    
    
    /**
     * 成员减少事件发生所在的[群][Group]。
     */
    @JvmSynthetic
    override suspend fun group(): Group
    
    
    /**
     * 成员减少事件发生所在的[群][Group]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun source(): Group
    
    
    /**
     * 减少的[群成员][GroupMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun before(): GroupMember
    
    
    /**
     * 减少的[群成员][GroupMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun member(): GroupMember
    
    
    override val key: Event.Key<out GroupMemberDecreaseEvent>
    
    public companion object Key : BaseEventKey<GroupMemberDecreaseEvent>(
        "api.group_member_decrease",
        MemberDecreaseEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMemberDecreaseEvent? = doSafeCast(value)
    }
    
}

/**
 *
 * [MemberDecreaseEvent] 的子类型事件，代表一个发生在 [Guild] 中的事件。
 *
 * @see MemberDecreaseEvent
 */
public interface GuildMemberDecreaseEvent : MemberDecreaseEvent, GuildEvent {
    
    /**
     * 成员减少事件发生所在的[频道服务器][Guild]。
     */
    @JvmSynthetic
    override suspend fun guild(): Guild
    
    
    /**
     * 成员减少事件发生所在的[频道服务器][Guild]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun source(): Guild
    
    
    /**
     * 减少的[频道成员][GuildMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun before(): GuildMember
    
    
    /**
     * 减少的[频道成员][GuildMember]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun member(): GuildMember
    
    
    override val key: Event.Key<out GuildMemberDecreaseEvent>
    
    public companion object Key : BaseEventKey<GuildMemberDecreaseEvent>(
        "api.guild_member_decrease",
        MemberDecreaseEvent, GuildEvent
    ) {
        override fun safeCast(value: Any): GuildMemberDecreaseEvent? = doSafeCast(value)
    }
}







