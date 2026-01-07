/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.event

import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.overwriteWith
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.event.internal.message.OneBotFriendMessageEventImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.message.OneBotGroupPrivateMessageEventImpl
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupPrivateMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.*
import love.forte.simbot.component.onebot.v11.core.useOneBot11
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull


/**
 *
 * @author ForteScarlet
 */
class PrivateMessageEventInteractionTests {
    private fun spykBot(dispatcher: EventDispatcher): OneBotBotImpl {
        return spyk(
            OneBotBotImpl(
                "1234",
                EmptyCoroutineContext,
                SupervisorJob(),
                OneBotBotConfiguration(),
                OneBot11Component(),
                dispatcher,
                Json(OneBot11.DefaultJson) {
                    serializersModule = serializersModule overwriteWith OneBot11.serializersModule
                }
            ),
            recordPrivateCalls = true
        )
    }

    @Test
    fun testFriendMessageEvent() = runTest {
        testInteractionEvent<
            OneBotFriendMessageEvent,
            OneBotFriendMessageEventPreReplyEvent,
            OneBotFriendMessageEventPostReplyEvent,
            > { bot ->
            OneBotFriendMessageEventImpl(
                null,
                mockk(relaxed = true),
                bot
            )
        }
    }

    @Test
    fun testGroupPrivateMessageEvent() = runTest {
        testInteractionEvent<
            OneBotGroupPrivateMessageEvent,
            OneBotGroupPrivateMessageEventPreReplyEvent,
            OneBotGroupPrivateMessageEventPostReplyEvent,
            > { bot ->
            OneBotGroupPrivateMessageEventImpl(
                null,
                mockk(relaxed = true),
                bot
            )
        }
    }

    @OptIn(ApiResultConstructor::class)
    private suspend inline fun <
        reified E,
        reified PRE_E : OneBotPrivateMessageEventPreReplyEvent,
        reified POST_E : OneBotPrivateMessageEventPostReplyEvent
        > testInteractionEvent(
        block: (b: OneBotBotImpl) -> E,
    ) where E : Event, E : ReplySupport {
        val app = launchSimpleApplication {
            useOneBot11()
        }

        app.eventDispatcher.process<E> { event ->
            event.reply("Hello")
        }

        app.eventDispatcher.process<PRE_E> { event ->
            event.currentMessage = InteractionMessage.valueOf("Hello, World")
        }

        val postEventDeferred = CompletableDeferred<POST_E>()

        app.eventDispatcher.process<POST_E> { event ->
            postEventDeferred.complete(event)
        }

        val bot = spykBot(app.eventDispatcher)
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)

        val event = spyk(block(bot), recordPrivateCalls = true)

        app.eventDispatcher.push(event).throwIfError().collect()

        coVerifyOrder {
            event.reply("Hello")
            event invoke "replyText" withArguments listOf("Hello, World")
        }

        val postEvent = postEventDeferred.await()
        val postEventMessage = postEvent.message
        assertIs<OneBotSegmentsInteractionMessage>(postEventMessage)
        assertIs<InteractionMessage.Text>(postEventMessage.message)
        assertNull(postEventMessage.segments)
        assertEquals("Hello, World", postEventMessage.message.text)
    }
}
