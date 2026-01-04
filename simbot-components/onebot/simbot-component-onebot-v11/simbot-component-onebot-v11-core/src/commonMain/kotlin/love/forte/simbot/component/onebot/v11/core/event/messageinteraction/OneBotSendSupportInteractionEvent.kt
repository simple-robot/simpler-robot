/*
 *     Copyright (c) 2025-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.onebot.v11.core.event.messageinteraction

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.event.*
import love.forte.simbot.suspendrunner.STP


/**
 * OneBot组件中针对 [SendSupport] 的拦截或通知事件。
 *
 * @see love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
 * @see love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
 * @see love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
 *
 * @since 1.6.0
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotSendSupportInteractionEvent :
    SendSupportInteractionEvent,
    OneBotInternalMessageInteractionEvent {
    override val bot: OneBotBot
    override val content: SendSupport
}

/**
 * OneBot组件中针对 [SendSupport.send] 的拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 1.6.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotSendSupportPreSendEvent :
    SendSupportPreSendEvent,
    OneBotInternalMessagePreSendEvent,
    OneBotSendSupportInteractionEvent

/**
 * OneBot组件中针对 [SendSupport.send] 的通知事件。
 * 会在 [SendSupport.send] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 1.6.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotSendSupportPostSendEvent :
    SendSupportPostSendEvent,
    OneBotInternalMessagePostSendEvent,
    OneBotSendSupportInteractionEvent {
    override val message: OneBotSegmentsInteractionMessage
}

//region OneBotGroup

/**
 * OneBot组件针对 [OneBotGroup] 的交互事件。
 * @since 1.6.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupInteractionEvent : OneBotSendSupportInteractionEvent, ChatGroupInteractionEvent {
    override val content: OneBotGroup

    @STP
    override suspend fun target(): OneBotGroup = content
}

/**
 * OneBot组件针对 [OneBotGroup.send] 的拦截事件。
 * @since 1.6.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupPreSendEvent :
    OneBotSendSupportPreSendEvent,
    OneBotGroupInteractionEvent,
    ChatGroupPreSendEvent {
    override val content: OneBotGroup

    @STP
    override suspend fun target(): OneBotGroup = content

    /**
     * 拦截事件中的消息内容。
     */
    override val message: OneBotSegmentsInteractionMessage

    /**
     * 可修改的 [message]，初始为 [message]，
     * 修改后会在事件处理完成后被替换为原本的参数。
     *
     * 如果被设置为 [OneBotSegmentsInteractionMessage]
     * 之外的类型，效果同设置了一个 [OneBotSegmentsInteractionMessage.segments] 为 `null` 的值，
     * 最终都会在需要的情况下重新解析 [OneBotSegmentsInteractionMessage.segments]。
     */
    override var currentMessage: InteractionMessage
}

/**
 * OneBot组件针对 [OneBotGroup.send] 的通知事件。
 * @since 1.6.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupPostSendEvent :
    OneBotSendSupportPostSendEvent,
    OneBotGroupInteractionEvent,
    ChatGroupPostSendEvent {
    override val content: OneBotGroup

    @STP
    override suspend fun target(): OneBotGroup = content
    override val message: OneBotSegmentsInteractionMessage
}
//endregion

//region OneBotFriend

/**
 * OneBot组件针对 [OneBotFriend] 的交互事件。
 * @since 1.6.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotFriendInteractionEvent : OneBotSendSupportInteractionEvent, ContactInteractionEvent {
    override val content: OneBotFriend

    @STP
    override suspend fun target(): OneBotFriend = content
}

/**
 * OneBot组件针对 [OneBotFriend.send] 的拦截事件。
 * @since 1.6.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotFriendPreSendEvent :
    OneBotSendSupportPreSendEvent,
    OneBotFriendInteractionEvent,
    ContactPreSendEvent {
    override val content: OneBotFriend

    @STP
    override suspend fun target(): OneBotFriend = content

    /**
     * 拦截事件中的消息内容。
     */
    override val message: OneBotSegmentsInteractionMessage

    /**
     * 可修改的 [message]，初始为 [message]，
     * 修改后会在事件处理完成后被替换为原本的参数。
     *
     * 如果被设置为 [OneBotSegmentsInteractionMessage]
     * 之外的类型，效果同设置了一个 [OneBotSegmentsInteractionMessage.segments] 为 `null` 的值，
     * 最终都会在需要的情况下重新解析 [OneBotSegmentsInteractionMessage.segments]。
     */
    override var currentMessage: InteractionMessage
}

/**
 * OneBot组件针对 [OneBotFriend.send] 的通知事件。
 * @since 1.6.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotFriendPostSendEvent :
    OneBotSendSupportPostSendEvent,
    OneBotFriendInteractionEvent,
    ContactPostSendEvent {
    override val content: OneBotFriend

    @STP
    override suspend fun target(): OneBotFriend = content
    override val message: OneBotSegmentsInteractionMessage
}
//endregion

//region OneBotMember
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotMemberInteractionEvent : OneBotSendSupportInteractionEvent, MemberInteractionEvent {
    override val content: OneBotMember

    @STP
    override suspend fun target(): OneBotMember = content
}

@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotMemberPreSendEvent :
    OneBotSendSupportPreSendEvent,
    OneBotMemberInteractionEvent,
    MemberPreSendEvent {
    override val content: OneBotMember

    @STP
    override suspend fun target(): OneBotMember = content

    /**
     * 拦截事件中的消息内容。
     */
    override val message: OneBotSegmentsInteractionMessage

    /**
     * 可修改的 [message]，初始为 [message]，
     * 修改后会在事件处理完成后被替换为原本的参数。
     *
     * 如果被设置为 [OneBotSegmentsInteractionMessage]
     * 之外的类型，效果同设置了一个 [OneBotSegmentsInteractionMessage.segments] 为 `null` 的值，
     * 最终都会在需要的情况下重新解析 [OneBotSegmentsInteractionMessage.segments]。
     */
    override var currentMessage: InteractionMessage
}

@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotMemberPostSendEvent :
    OneBotSendSupportPostSendEvent,
    OneBotMemberInteractionEvent,
    MemberPostSendEvent {
    override val content: OneBotMember

    @STP
    override suspend fun target(): OneBotMember = content
    override val message: OneBotSegmentsInteractionMessage
}
//endregion
