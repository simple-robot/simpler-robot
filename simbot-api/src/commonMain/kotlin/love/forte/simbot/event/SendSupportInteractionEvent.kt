/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.event

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.MessageReceipt


/**
 * 针对 [SendSupport] 的内部交互事件，包括送信前的拦截与送信成功后的通知。
 *
 * @see SendSupport
 *
 * @since 4.11.0
 * @author ForteScarlet
 */
public interface SendSupportInteractionEvent : BotEvent, InternalMessageInteractionEvent {
    /**
     * 所属Bot。
     */
    override val bot: Bot

    /**
     * 当前进行行为的 [SendSupport] 实例。
     */
    override val content: SendSupport

    /**
     * [SendSupport.send] 所尝试进行传递的参数。
     */
    override val message: InteractionMessage
}

/**
 * 针对 [SendSupport.send] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see SendSupport
 */
public interface SendSupportPreSendEvent :
    SendSupportInteractionEvent, InternalInterceptionEvent {
    /**
     * 最初在 [SendSupport.send] 调用时传递的参数。
     * 不会因为 [currentMessage] 的变化而改变。
     */
    override val message: InteractionMessage

    /**
     * 可以进行修改的 [InteractionMessage] 内容，会在事件处理完成后被替换为原本的参数。
     *
     * [currentMessage] 的变化不会影响 [message] 的值。
     *
     * 修改不是线程安全的，不要并发地进行修改，也不可以在异步中延迟修改。
     * 如果在 [currentMessage] 已经被使用后仍尝试修改，会抛出 [IllegalStateException]。
     *
     * @throws IllegalStateException 如果在 [currentMessage] 已经被使用后仍尝试修改，会抛出此异常。
     */
    public var currentMessage: InteractionMessage
}

/**
 * 针对 [SendSupport.send] 的内部通知事件。
 * 会在 [SendSupport.send] 执行成功后带着它的相关结果进行异步通知。
 *
 * @see SendSupport
 *
 * @since 4.11.0
 */
public interface SendSupportPostSendEvent :
    SendSupportInteractionEvent, InternalNotificationEvent {
    /**
     * 在 [SendSupport.send] 调用时传递的参数。
     */
    override val message: InteractionMessage

    /**
     * [SendSupport.send] 执行成功后的结果。
     */
    public val receipt: MessageReceipt
}

//region Contact

/**
 * 针对 [Contact.send] 的内部交互事件。
 * @see Contact
 * @since 4.11.0
 */
public interface ContactInteractionEvent : SendSupportInteractionEvent {
    override val content: Contact
}

/**
 * 针对 [Member.send] 的内部交互事件, 在 [Member.send] 执行前拦截。
 * @see Contact
 * @since 4.11.0
 */
public interface ContactPreSendEvent : ContactInteractionEvent, SendSupportPreSendEvent {
    override val content: Contact
}

/**
 * 针对 [Member.send] 的内部交互事件, 在 [Member.send] 执行后通知。
 * @see Contact
 * @since 4.11.0
 */
public interface ContactPostSendEvent : ContactInteractionEvent, SendSupportPostSendEvent {
    override val content: Contact
}
//endregion

//region Member

/**
 * 针对 [Member] 的内部交互事件。
 * @see Member
 * @since 4.11.0
 */
public interface MemberInteractionEvent : SendSupportInteractionEvent {
    override val content: Member
}

/**
 * 针对 [Member] 的内部交互事件, 在 [Member.send] 执行前拦截。
 * @see Member
 * @since 4.11.0
 */
public interface MemberPreSendEvent : MemberInteractionEvent, SendSupportPreSendEvent {
    override val content: Member
}

/**
 * 针对 [Member] 的内部交互事件, 在 [Member.send] 执行后通知。
 * @see Member
 * @since 4.11.0
 */
public interface MemberPostSendEvent : MemberInteractionEvent, SendSupportPostSendEvent {
    override val content: Member
}
//endregion

//region ChatRoom

/**
 * 针对 [ChatRoom] 的内部交互事件。
 * @see ChatRoom
 * @since 4.11.0
 */
public interface ChatRoomInteractionEvent : SendSupportInteractionEvent {
    override val content: ChatRoom
}

/**
 * 针对 [ChatRoom] 的内部交互事件, 在 [ChatRoom.send] 执行前拦截。
 * @see ChatRoom
 * @since 4.11.0
 */
public interface ChatRoomPreSendEvent : ChatRoomInteractionEvent, SendSupportPreSendEvent {
    override val content: ChatRoom
}

/**
 * 针对 [ChatRoom] 的内部交互事件, 在 [ChatRoom.send] 执行后通知。
 * @see ChatRoom
 * @since 4.11.0
 */
public interface ChatRoomPostSendEvent : ChatRoomInteractionEvent, SendSupportPostSendEvent {
    override val content: ChatRoom
}
//endregion

//region ChatGroup

/**
 * 针对 [ChatGroup] 的内部交互事件。
 * @see ChatGroup
 * @since 4.11.0
 */
public interface ChatGroupInteractionEvent : ChatRoomInteractionEvent {
    override val content: ChatGroup
}

/**
 * 针对 [ChatGroup] 的内部交互事件, 在 [ChatGroup.send] 执行前拦截。
 * @see ChatGroup
 * @since 4.11.0
 */
public interface ChatGroupPreSendEvent : ChatGroupInteractionEvent, ChatRoomPreSendEvent {
    override val content: ChatGroup
}

/**
 * 针对 [ChatGroup] 的内部交互事件, 在 [ChatGroup.send] 执行后通知。
 * @see ChatGroup
 * @since 4.11.0
 */
public interface ChatGroupPostSendEvent : ChatGroupInteractionEvent, ChatRoomPostSendEvent {
    override val content: ChatGroup
}
//endregion

//region ChatChannel

/**
 * 针对 [ChatChannel] 的内部交互事件。
 *
 * @see ChatChannel
 *
 * @since 4.11.0
 */
public interface ChatChannelInteractionEvent : ChatRoomInteractionEvent {
    override val content: ChatChannel
}

/**
 * 针对 [ChatChannel] 的内部交互事件, 在 [ChatChannel.send] 执行前拦截。
 * @see ChatChannel
 * @since 4.11.0
 */
public interface ChatChannelPreSendEvent :
    ChatChannelInteractionEvent,
    ChatRoomPreSendEvent {
    override val content: ChatChannel
}

/**
 * 针对 [ChatChannel] 的内部交互事件, 在 [ChatChannel.send] 执行后通知。
 * @see ChatChannel
 * @since 4.11.0
 */
public interface ChatChannelPostSendEvent :
    ChatChannelInteractionEvent,
    ChatRoomPostSendEvent {
    override val content: ChatChannel
}
//endregion
