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
 *
 */

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.action.ActionType
import love.forte.simbot.definition.Member
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.definition.Organization
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
    @JvmSynthetic
    public suspend fun operator(): MemberInfo?

    /**
     * 这次组织成员变动的**操作者**。
     *
     * @see operator
     */
    @Api4J
    public val operator: MemberInfo?

    public companion object Key :
        BaseEventKey<MemberChangedEvent>(
            "api.member_changed",
            ChangedEvent, OrganizationEvent, MemberEvent) {
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
    @JvmSynthetic
    override suspend fun source(): Organization

    /**
     * 成员增加事件发生所在的组织。
     */
    @Api4J
    override val source: Organization


    /**
     * 增加的[成员][Member]。
     */
    @JvmSynthetic
    override suspend fun after(): Member

    /**
     * 增加的[成员][Member]。
     */
    @Api4J
    override val after: Member

    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType

    public companion object Key :
        BaseEventKey<MemberIncreaseEvent>(
            "api.member_increase",
            IncreaseEvent, MemberChangedEvent) {
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
    @JvmSynthetic
    override suspend fun before(): Member

    /**
     * 离开的[成员][Member]
     */
    @Api4J
    override val before: Member

    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType

    public companion object Key :
        BaseEventKey<MemberDecreaseEvent>(
            "api.member_decrease",
            DecreaseEvent, MemberChangedEvent) {
        override fun safeCast(value: Any): MemberDecreaseEvent? = doSafeCast(value)
    }
}

// TODO
//  群、频道成员变动事件实现


/**
 * @see MemberIncreaseEvent
 */
@Deprecated("尚未实现")
public interface GroupMemberIncreaseEvent

/**
 * @see MemberIncreaseEvent
 */
@Deprecated("尚未实现")
public interface GuildMemberIncreaseEvent

/**
 * @see MemberDecreaseEvent
 */
@Deprecated("尚未实现")
public interface GroupMemberDecreaseEvent

/**
 * @see MemberDecreaseEvent
 */
@Deprecated("尚未实现")
public interface GuildMemberDecreaseEvent






