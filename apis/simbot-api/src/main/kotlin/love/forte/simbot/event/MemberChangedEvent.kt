/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

import love.forte.simbot.Api4J
import love.forte.simbot.action.ActionType
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.definition.Organization
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.utils.runInBlocking


/**
 * 一个与组织成员变动相关的事件。
 *
 * @see MemberIncreaseEvent
 * @see MemberDecreaseEvent
 */
public interface MemberChangedEvent<BEFORE : MemberInfo?, AFTER : MemberInfo?> :
    ChangedEvent<Organization, BEFORE, AFTER>, OrganizationEvent {

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
    public suspend fun operator(): MemberInfo?

    /**
     * 这次组织成员变动的**操作者**。
     *
     * @see operator
     */
    @Api4J
    public val operator: MemberInfo?
        get() = runInBlocking { operator() }


    public companion object Key :
        BaseEventKey<MemberChangedEvent<*, *>>("api.member_changed", ChangedEvent, OrganizationEvent) {
        override fun safeCast(value: Any): MemberChangedEvent<*, *>? = doSafeCast(value)
    }
}


/**
 * 一个 **成员增加** 事件。
 * 其代表一个组织中增加了某个成员。
 *
 * @see IncreaseEvent
 * @see MemberChangedEvent
 */
public interface MemberIncreaseEvent : IncreaseEvent<Organization, MemberInfo>,
    MemberChangedEvent<MemberInfo?, MemberInfo> {

    /**
     * 增加事件发生所在的组织。
     */
    override suspend fun source(): Organization

    /**
     * 增加的这个群成员的基础信息。
     */
    override suspend fun target(): MemberInfo

    /**
     * 群成员新增，[before] 所代表内容永远为null。
     */
    override val before: MemberInfo? get() = null

    /**
     * 群成员新增，[before] 所代表内容永远为null。
     */
    override suspend fun before(): MemberInfo? = null

    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType

    public companion object Key :
        BaseEventKey<MemberIncreaseEvent>("api.member_increase", IncreaseEvent, MemberChangedEvent) {
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
public interface MemberDecreaseEvent : DecreaseEvent<Organization, MemberInfo>,
    MemberChangedEvent<MemberInfo, MemberInfo?> {

    /**
     * 发生增加事件的组织。
     */
    override suspend fun source(): Organization

    /**
     * 离开群成员的基础信息。
     */
    override suspend fun target(): MemberInfo

    /**
     * 群成员离开，[after] 所代表内容永远为null。
     */
    override suspend fun after(): MemberInfo? = null

    /**
     * 群成员离开，[after] 所代表内容永远为null。
     */
    override val after: MemberInfo? get() = null

    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType

    public companion object Key :
        BaseEventKey<MemberDecreaseEvent>("api.member_decrease", DecreaseEvent, MemberChangedEvent) {
        override fun safeCast(value: Any): MemberDecreaseEvent? = doSafeCast(value)
    }
}






