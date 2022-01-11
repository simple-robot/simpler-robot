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

import love.forte.simbot.action.ActionType
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.definition.Organization
import love.forte.simbot.message.doSafeCast

/**
 * 一个 **成员增加** 事件。
 * 其代表一个组织中增加了某个成员。
 */
public interface MemberIncreaseEvent : IncreaseEvent<Organization, MemberInfo>, OrganizationEvent {

    /**
     * 发生增加事件的组织。
     */
    override suspend fun source(): Organization

    /**
     * 增加群成员的基础信息。
     */
    override suspend fun target(): MemberInfo

    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType

    public companion object Key :
        BaseEventKey<MemberIncreaseEvent>("api.member_increase", setOf(IncreaseEvent, OrganizationEvent)) {
        override fun safeCast(value: Any): MemberIncreaseEvent? = doSafeCast(value)
    }
}

/**
 * 一个 **成员减少** 事件。
 * 其代表一个组织中减少了某个成员。
 */
public interface MemberDecreaseEvent : DecreaseEvent<Organization, MemberInfo>, OrganizationEvent {

    /**
     * 发生增加事件的组织。
     */
    override suspend fun source(): Organization

    /**
     * 离开群成员的基础信息。
     */
    override suspend fun target(): MemberInfo

    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType

    public companion object Key :
        BaseEventKey<MemberDecreaseEvent>("api.member_decrease", setOf(DecreaseEvent, OrganizationEvent)) {
        override fun safeCast(value: Any): MemberDecreaseEvent? = doSafeCast(value)
    }
}






