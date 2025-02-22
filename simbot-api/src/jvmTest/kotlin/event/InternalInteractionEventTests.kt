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

package event

import io.mockk.every
import io.mockk.mockk
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.definition.*
import love.forte.simbot.event.*
import kotlin.test.Test
import kotlin.test.assertIs


class InternalInteractionEventTests {

    @Test
    fun sendSupportInteractionEvent_tests() {
        mockTestSendSupportInteractionEvent<SendSupportInteractionEvent, SendSupport>()
        mockTestSendSupportInteractionEvent<SendSupportPreSendEvent, SendSupport>()
        mockTestSendSupportInteractionEvent<SendSupportPostSendEvent, SendSupport>()
        mockTestSendSupportInteractionEvent<ContactInteractionEvent, Contact>()
        mockTestSendSupportInteractionEvent<ContactPreSendEvent, Contact>()
        mockTestSendSupportInteractionEvent<ContactPostSendEvent, Contact>()
        mockTestSendSupportInteractionEvent<ChatRoomInteractionEvent, ChatRoom>()
        mockTestSendSupportInteractionEvent<ChatRoomPostSendEvent, ChatRoom>()
        mockTestSendSupportInteractionEvent<ChatRoomPreSendEvent, ChatRoom>()
        mockTestSendSupportInteractionEvent<ChatGroupInteractionEvent, ChatGroup>()
        mockTestSendSupportInteractionEvent<ChatGroupPreSendEvent, ChatGroup>()
        mockTestSendSupportInteractionEvent<ChatGroupPostSendEvent, ChatGroup>()
        mockTestSendSupportInteractionEvent<ChatChannelInteractionEvent, ChatChannel>()
        mockTestSendSupportInteractionEvent<ChatChannelPreSendEvent, ChatChannel>()
        mockTestSendSupportInteractionEvent<ChatChannelPostSendEvent, ChatChannel>()
        mockTestSendSupportInteractionEvent<MemberInteractionEvent, Member>()
        mockTestSendSupportInteractionEvent<MemberPreSendEvent, Member>()
        mockTestSendSupportInteractionEvent<MemberPostSendEvent, Member>()
    }

    private inline fun <
        reified T : SendSupportInteractionEvent,
        reified C : SendSupport
        > mockTestSendSupportInteractionEvent() {
        mockTestInteractionEvent<T, C>()
    }


    @Test
    fun replySupportInteractionEvent_tests() {
        mockTestReplySupportInteractionEvent<ReplySupportInteractionEvent, ReplySupport>()
        mockTestReplySupportInteractionEvent<ReplySupportPreReplyEvent, ReplySupport>()
        mockTestReplySupportInteractionEvent<ReplySupportPostReplyEvent, ReplySupport>()
        mockTestReplySupportInteractionEvent<ContactMessageEventInteractionEvent, ContactMessageEvent>()
        mockTestReplySupportInteractionEvent<ContactMessageEventPreReplyEvent, ContactMessageEvent>()
        mockTestReplySupportInteractionEvent<ContactMessageEventPostReplyEvent, ContactMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatRoomMessageEventInteractionEvent, ChatRoomMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatRoomMessageEventPreReplyEvent, ChatRoomMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatRoomMessageEventPostReplyEvent, ChatRoomMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatGroupMessageEventInteractionEvent, ChatGroupMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatGroupMessageEventPreReplyEvent, ChatGroupMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatGroupMessageEventPostReplyEvent, ChatGroupMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatChannelMessageEventInteractionEvent, ChatChannelMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatChannelMessageEventPreReplyEvent, ChatChannelMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatChannelMessageEventPostReplyEvent, ChatChannelMessageEvent>()
        mockTestReplySupportInteractionEvent<MemberMessageEventInteractionEvent, MemberMessageEvent>()
        mockTestReplySupportInteractionEvent<MemberMessageEventPreReplyEvent, MemberMessageEvent>()
        mockTestReplySupportInteractionEvent<MemberMessageEventPostReplyEvent, MemberMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatGroupMemberMessageEventInteractionEvent, ChatGroupMemberMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatGroupMemberMessageEventPreReplyEvent, ChatGroupMemberMessageEvent>()
        mockTestReplySupportInteractionEvent<ChatGroupMemberMessageEventPostReplyEvent, ChatGroupMemberMessageEvent>()
        mockTestReplySupportInteractionEvent<GuildMemberMessageEventInteractionEvent, GuildMemberMessageEvent>()
        mockTestReplySupportInteractionEvent<GuildMemberMessageEventPreReplyEvent, GuildMemberMessageEvent>()
        mockTestReplySupportInteractionEvent<GuildMemberMessageEventPostReplyEvent, GuildMemberMessageEvent>()
    }

    private inline fun <
        reified T : ReplySupportInteractionEvent,
        reified C : ReplySupport
        > mockTestReplySupportInteractionEvent() {
        mockTestInteractionEvent<T, C>()
    }

    private inline fun <
        reified T : InternalMessageInteractionEvent,
        reified C : Any
        > mockTestInteractionEvent() {
        val event = mockk<T>()
        val content = mockk<C>()
        every { event.content } returns content
        val eventContent: C = event.content as C
        assertIs<C>(eventContent)
    }

}
