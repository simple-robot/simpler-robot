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

import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.suspendrunner.STP


/**
 * 针对 [ReplySupport] 的内部交互事件，包括送信前的拦截与送信成功后的通知。
 *
 * @since 4.11.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface ReplySupportInteractionEvent : BotEvent, InternalMessageInteractionEvent {
    /**
     * 所属Bot。
     */
    override val bot: Bot

    /**
     * 当前进行行为的 [ReplySupport] 实例。
     */
    override val content: ReplySupport

    /**
     * [ReplySupport.reply] 所尝试进行传递的参数。
     */
    override val message: InteractionMessage
}

/**
 * 针对 [ReplySupport.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * 注意：尽可能避免在处理此事件的时候再次进行消息发送，
 * 以避免出现循环导致应用程序资源匮乏或无法正常运行。
 *
 * @since 4.11.0
 * @see ReplySupport
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface ReplySupportPreReplyEvent : ReplySupportInteractionEvent, InternalMessagePreSendEvent

/**
 * 针对 [ReplySupport.reply] 的内部通知事件。
 * 会在 [ReplySupport.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * 注意：尽可能避免在处理此事件的时候再次进行消息发送，
 * 以避免出现循环导致应用程序资源匮乏或无法正常运行。
 *
 * @see ReplySupport
 *
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface ReplySupportPostReplyEvent : ReplySupportInteractionEvent, InternalMessagePostSendEvent

// region MessageEvent

/**
 * 针对 [MessageEvent.reply] 的内部交互事件。
 *
 * @see MessageEvent
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface MessageEventInteractionEvent : ReplySupportInteractionEvent {
    override val content: MessageEvent
}

/**
 * 针对 [MessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @see MessageEvent
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface MessageEventPreReplyEvent : MessageEventInteractionEvent, ReplySupportPreReplyEvent

/**
 * 针对 [MessageEvent.reply] 的内部通知事件。
 * 会在 [MessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @see MessageEvent
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface MessageEventPostReplyEvent : MessageEventInteractionEvent, ReplySupportPostReplyEvent
// endregion

// region Contact

/**
 * 针对 [ContactMessageEvent.reply] 的内部交互事件。
 *
 * @see ContactMessageEvent
 * @since 4.11.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ContactMessageEventInteractionEvent : MessageEventInteractionEvent {
    override val content: ContactMessageEvent

    /**
     * 发送目标为一个 [Contact], 同 [ContactMessageEvent.content]。
     *
     * @see ContactMessageEvent.content
     */
    @STP
    override suspend fun target(): Contact = content.content()
}

/**
 * 针对 [ContactMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see ContactMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ContactMessageEventPreReplyEvent : ContactMessageEventInteractionEvent, MessageEventPreReplyEvent {
    override val content: ContactMessageEvent

    /**
     * 发送目标为一个 [Contact], 同 [ContactMessageEvent.content]。
     *
     * @see ContactMessageEvent.content
     */
    @STP
    override suspend fun target(): Contact = content.content()
}

/**
 * 针对 [ContactMessageEvent.reply] 的内部通知事件。
 * 会在 [ContactMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see ContactMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ContactMessageEventPostReplyEvent : ContactMessageEventInteractionEvent, MessageEventPostReplyEvent {
    override val content: ContactMessageEvent

    /**
     * 发送目标为一个 [Contact], 同 [ContactMessageEvent.content]。
     *
     * @see ContactMessageEvent.content
     */
    @STP
    override suspend fun target(): Contact = content.content()
}

// endregion

//region ChatRoom

/**
 * 针对 [ChatRoomMessageEvent.reply] 的内部交互事件。
 *
 * @since 4.11.0
 * @see ChatRoomMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ChatRoomMessageEventInteractionEvent : MessageEventInteractionEvent {
    override val content: ChatRoomMessageEvent

    /**
     * 发送目标为一个 [ChatRoom], 同 [ChatRoomMessageEvent.content]。
     *
     * @see ChatRoomMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatRoom = content.content()
}

/**
 * 针对 [ChatRoomMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see ChatRoomMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ChatRoomMessageEventPreReplyEvent : ChatRoomMessageEventInteractionEvent, MessageEventPreReplyEvent {
    override val content: ChatRoomMessageEvent

    /**
     * 发送目标为一个 [ChatRoom], 同 [ChatRoomMessageEvent.content]。
     *
     * @see ChatRoomMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatRoom = content.content()
}

/**
 * 针对 [ChatRoomMessageEvent.reply] 的内部通知事件。
 * 会在 [ChatRoomMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see ChatRoomMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ChatRoomMessageEventPostReplyEvent : ChatRoomMessageEventInteractionEvent, MessageEventPostReplyEvent {
    override val content: ChatRoomMessageEvent

    /**
     * 发送目标为一个 [ChatRoom], 同 [ChatRoomMessageEvent.content]。
     *
     * @see ChatRoomMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatRoom = content.content()
}

//region ChatGroup

/**
 * 针对 [ChatGroupMessageEvent.reply] 的内部交互事件。
 *
 * @since 4.11.0
 * @see ChatGroupMessageEvent
 */
public interface ChatGroupMessageEventInteractionEvent : ChatRoomMessageEventInteractionEvent {
    override val content: ChatGroupMessageEvent

    /**
     * 发送目标为一个 [ChatGroup], 同 [ChatGroupMessageEvent.content]。
     *
     * @see ChatGroupMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatGroup = content.content()
}

/**
 * 针对 [ChatGroupMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see ChatGroupMessageEvent
 */
public interface ChatGroupMessageEventPreReplyEvent :
    ChatGroupMessageEventInteractionEvent,
    ChatRoomMessageEventPreReplyEvent {
    override val content: ChatGroupMessageEvent

    /**
     * 发送目标为一个 [ChatGroup], 同 [ChatGroupMessageEvent.content]。
     *
     * @see ChatGroupMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatGroup = content.content()
}

/**
 * 针对 [ChatGroupMessageEvent.reply] 的内部通知事件。
 * 会在 [ChatGroupMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see ChatGroupMessageEvent
 */
public interface ChatGroupMessageEventPostReplyEvent :
    ChatGroupMessageEventInteractionEvent,
    ChatRoomMessageEventPostReplyEvent {
    override val content: ChatGroupMessageEvent

    /**
     * 发送目标为一个 [ChatGroup], 同 [ChatGroupMessageEvent.content]。
     *
     * @see ChatGroupMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatGroup = content.content()
}
//endregion
//region ChatChannel

/**
 * 针对 [ChatChannelMessageEvent.reply] 的内部交互事件。
 *
 * @since 4.11.0
 * @see ChatChannelMessageEvent
 */
public interface ChatChannelMessageEventInteractionEvent : ChatRoomMessageEventInteractionEvent {
    override val content: ChatChannelMessageEvent

    /**
     * 发送目标为一个 [ChatChannel], 同 [ChatChannelMessageEvent.content]。
     *
     * @see ChatChannelMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatChannel = content.content()
}

/**
 * 针对 [ChatChannelMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see ChatChannelMessageEvent
 */
public interface ChatChannelMessageEventPreReplyEvent :
    ChatChannelMessageEventInteractionEvent,
    ChatRoomMessageEventPreReplyEvent {
    override val content: ChatChannelMessageEvent

    /**
     * 发送目标为一个 [ChatChannel], 同 [ChatChannelMessageEvent.content]。
     *
     * @see ChatChannelMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatChannel = content.content()
}

/**
 * 针对 [ChatChannelMessageEvent.reply] 的内部通知事件。
 * 会在 [ChatChannelMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see ChatChannelMessageEvent
 */
public interface ChatChannelMessageEventPostReplyEvent :
    ChatChannelMessageEventInteractionEvent,
    ChatRoomMessageEventPostReplyEvent {
    override val content: ChatChannelMessageEvent

    /**
     * 发送目标为一个 [ChatChannel], 同 [ChatChannelMessageEvent.content]。
     *
     * @see ChatChannelMessageEvent.content
     */
    @STP
    override suspend fun target(): ChatChannel = content.content()
}
//endregion
//endregion

//region Member

/**
 * 针对 [MemberMessageEvent.reply] 的内部交互事件。
 *
 * @since 4.11.0
 * @see MemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface MemberMessageEventInteractionEvent : MessageEventInteractionEvent {
    override val content: MemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [MemberMessageEvent.content]。
     *
     * @see MemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}

/**
 * 针对 [MemberMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see MemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface MemberMessageEventPreReplyEvent : MemberMessageEventInteractionEvent, MessageEventPreReplyEvent {
    override val content: MemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [MemberMessageEvent.content]。
     *
     * @see MemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}

/**
 * 针对 [MemberMessageEvent.reply] 的内部通知事件。
 * 会在 [MemberMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see MemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface MemberMessageEventPostReplyEvent : MemberMessageEventInteractionEvent, MessageEventPostReplyEvent {
    override val content: MemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [MemberMessageEvent.content]。
     *
     * @see MemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}
//endregion

//region GuildMember

/**
 * 针对 [GuildMemberMessageEvent.reply] 的内部交互事件。
 *
 * @since 4.11.0
 * @see GuildMemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface GuildMemberMessageEventInteractionEvent : MessageEventInteractionEvent {
    override val content: GuildMemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [GuildMemberMessageEvent.content]。
     *
     * @see GuildMemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}

/**
 * 针对 [GuildMemberMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see GuildMemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface GuildMemberMessageEventPreReplyEvent :
    GuildMemberMessageEventInteractionEvent,
    MessageEventPreReplyEvent {
    override val content: GuildMemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [GuildMemberMessageEvent.content]。
     *
     * @see GuildMemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}

/**
 * 针对 [GuildMemberMessageEvent.reply] 的内部通知事件。
 * 会在 [GuildMemberMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see GuildMemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface GuildMemberMessageEventPostReplyEvent :
    GuildMemberMessageEventInteractionEvent,
    MessageEventPostReplyEvent {
    override val content: GuildMemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [GuildMemberMessageEvent.content]。
     *
     * @see GuildMemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}
//endregion

//region ChatGroupMember

/**
 * 针对 [ChatGroupMemberMessageEvent.reply] 的内部交互事件。
 *
 * @since 4.11.0
 * @see ChatGroupMemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ChatGroupMemberMessageEventInteractionEvent : MessageEventInteractionEvent {
    override val content: ChatGroupMemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [ChatGroupMemberMessageEvent.content]。
     *
     * @see ChatGroupMemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}

/**
 * 针对 [ChatGroupMemberMessageEvent.reply] 的内部拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.11.0
 * @see ChatGroupMemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ChatGroupMemberMessageEventPreReplyEvent :
    ChatGroupMemberMessageEventInteractionEvent,
    MessageEventPreReplyEvent {
    override val content: ChatGroupMemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [ChatGroupMemberMessageEvent.content]。
     *
     * @see ChatGroupMemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}

/**
 * 针对 [ChatGroupMemberMessageEvent.reply] 的内部通知事件。
 * 会在 [ChatGroupMemberMessageEvent.reply] 执行成功后带着它的相关结果进行异步通知。
 *
 * @since 4.11.0
 * @see ChatGroupMemberMessageEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface ChatGroupMemberMessageEventPostReplyEvent :
    ChatGroupMemberMessageEventInteractionEvent,
    MessageEventPostReplyEvent {
    override val content: ChatGroupMemberMessageEvent

    /**
     * 发送目标为一个 [Member], 同 [ChatGroupMemberMessageEvent.content]。
     *
     * @see ChatGroupMemberMessageEvent.content
     */
    @STP
    override suspend fun target(): Member = content.content()
}
//endregion
