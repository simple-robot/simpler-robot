/*
 * Copyright (c) 2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.event.*
import love.forte.simbot.suspendrunner.STP

/**
 * QQ组件中针对 [SendSupportInteractionEvent] 的子类型实现。
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGSendSupportInteractionEvent :
    QGInternalMessageInteractionEvent,
    SendSupportInteractionEvent {
    override val bot: QGBot
    override val content: SendSupport
}

/**
 * QG组件中针对 [SendSupport.send] 的拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGSendSupportPreSendEvent :
    SendSupportPreSendEvent,
    QGInternalMessagePreSendEvent,
    QGSendSupportInteractionEvent

/**
 * QG组件中针对 [SendSupport.send] 的通知事件。
 * 会在 [SendSupport.send] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGSendSupportPostSendEvent :
    SendSupportPostSendEvent,
    QGInternalMessagePostSendEvent,
    QGSendSupportInteractionEvent {
    override val message: InteractionMessage
}

// For QGTextChannel

/**
 * QG组件中针对 [QGTextChannel.send] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGTextChannelSendSupportInteractionEvent :
    QGSendSupportInteractionEvent,
    ChatChannelInteractionEvent {
    override val content: QGTextChannel

    @STP
    override suspend fun target(): QGTextChannel = content
}

/**
 * 针对 [QGTextChannel.send] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGTextChannelSendSupportPreSendEvent :
    QGTextChannelSendSupportInteractionEvent,
    QGSendSupportPreSendEvent,
    ChatChannelPreSendEvent {
    override val content: QGTextChannel

    @STP
    override suspend fun target(): QGTextChannel = content
}

/**
 * 针对 [QGTextChannel.send] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGTextChannelSendSupportPostSendEvent :
    QGTextChannelSendSupportInteractionEvent,
    QGSendSupportPostSendEvent,
    ChatChannelPostSendEvent {
    override val content: QGTextChannel

    @STP
    override suspend fun target(): QGTextChannel = content
}

// For QGMember

/**
 * QG组件中针对 [QGMember.send] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGMemberSendSupportInteractionEvent :
    QGSendSupportInteractionEvent,
    MemberInteractionEvent {
    override val content: QGMember

    @STP
    override suspend fun target(): QGMember = content
}

/**
 * 针对 [QGMember.send] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGMemberSendSupportPreSendEvent :
    QGMemberSendSupportInteractionEvent,
    QGSendSupportPreSendEvent,
    MemberPreSendEvent {

    override val content: QGMember

    @STP
    override suspend fun target(): QGMember = content
}

/**
 * 针对 [QGMember.send] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGMemberSendSupportPostSendEvent :
    QGMemberSendSupportInteractionEvent,
    QGSendSupportPostSendEvent,
    MemberPostSendEvent {

    override val content: QGMember

    @STP
    override suspend fun target(): QGMember = content
}

// For QGDmsContact

/**
 * QG组件中针对 [QGDmsContact.send] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGDmsContactSendSupportInteractionEvent :
    QGSendSupportInteractionEvent,
    ContactInteractionEvent {
    override val content: QGDmsContact

    @STP
    override suspend fun target(): QGDmsContact
}

/**
 * 针对 [QGDmsContact.send] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGDmsContactSendSupportPreSendEvent :
    QGDmsContactSendSupportInteractionEvent,
    QGSendSupportPreSendEvent,
    ContactPreSendEvent {
    override val content: QGDmsContact

    @STP
    override suspend fun target(): QGDmsContact
}

/**
 * 针对 [QGDmsContact.send] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGDmsContactSendSupportPostSendEvent :
    QGDmsContactSendSupportInteractionEvent,
    QGSendSupportPostSendEvent,
    ContactPostSendEvent {
    override val content: QGDmsContact

    @STP
    override suspend fun target(): QGDmsContact
}

// For QGFriend

/**
 * QG组件中针对 [QGFriend.send] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGFriendSendSupportInteractionEvent :
    QGSendSupportInteractionEvent {
    override val content: QGFriend

    @STP
    override suspend fun target(): QGFriend = content
}

/**
 * 针对 [QGFriend.send] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGFriendSendSupportPreSendEvent :
    QGFriendSendSupportInteractionEvent,
    QGSendSupportPreSendEvent {
    override val content: QGFriend

    @STP
    override suspend fun target(): QGFriend = content
}

/**
 * 针对 [QGFriend.send] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGFriendSendSupportPostSendEvent :
    QGFriendSendSupportInteractionEvent,
    QGSendSupportPostSendEvent {
    override val content: QGFriend

    @STP
    override suspend fun target(): QGFriend = content
}

// For QGGroup

/**
 * QG组件中针对 [QGGroup.send] 的拦截事件。
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGGroupSendSupportInteractionEvent :
    QGSendSupportInteractionEvent {
    override val content: QGGroup

    @STP
    override suspend fun target(): QGGroup = content
}

/**
 * 针对 [QGGroup.send] 的发送前拦截事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGGroupSendSupportPreSendEvent :
    QGGroupSendSupportInteractionEvent,
    QGSendSupportPreSendEvent {
    override val content: QGGroup

    @STP
    override suspend fun target(): QGGroup = content
}

/**
 * 针对 [QGGroup.send] 的发送后通知事件。
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface QGGroupSendSupportPostSendEvent :
    QGGroupSendSupportInteractionEvent,
    QGSendSupportPostSendEvent {
    override val content: QGGroup

    @STP
    override suspend fun target(): QGGroup = content
}
