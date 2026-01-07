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
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.overwriteWith
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.actor.internal.OneBotMemberApiResultImpl
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotMemberPostSendEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.*
import love.forte.simbot.message.MessageReceipt
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
@OptIn(ApiResultConstructor::class)
class MemberSendInteractionTests {
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
    fun testSendTextInterception() {
        val bot: OneBotBotImpl = spykBot(mockk(relaxed = true, relaxUnitFun = true))
        val member = spyk(
            OneBotMemberApiResultImpl(
                source = mockk(relaxed = true),
                bot = bot,
            ),
        )

        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)

        runBlocking { member.send("Hello World") }

        coVerify { member.send("Hello World") }
        coVerify {
            bot.emitMessagePreSendEvent(
                match {
                    it.message is OneBotSegmentsInteractionMessage &&
                        (it.message as? OneBotSegmentsInteractionMessage)?.message is InteractionMessage.Text &&
                        (it.message as? OneBotSegmentsInteractionMessage)?.segments == null
                }
            )
        }
    }

    @Test
    fun testSendTextInterceptionExceptionCollection() {
        val app = runBlocking { launchSimpleApplication() }

        val l1 = EventListener { throw IllegalStateException("ERROR1") }
        val l2 = EventListener { throw IllegalArgumentException("ERROR2") }

        app.eventDispatcher.register(l1)
        app.eventDispatcher.register(l2)

        val dispatcher = spyk(app.eventDispatcher, recordPrivateCalls = true)

        val bot = spykBot(dispatcher)

        val member = spyk(
            OneBotMemberApiResultImpl(
                source = mockk(relaxed = true),
                bot = bot,
            ),
        )
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)

        val error = assertFailsWith<OneBotInternalInterceptionException> {
            runBlocking { member.send("Hello World") }
        }

        assertEquals(2, error.suppressed.size)
        assertIs<IllegalStateException>(error.suppressed[0])
        assertEquals("ERROR1", error.suppressed[0].message)
        assertIs<IllegalArgumentException>(error.suppressed[1])
        assertEquals("ERROR2", error.suppressed[1].message)

        coVerify { member.send("Hello World") }
        coVerify {
            bot.emitMessagePreSendEvent(
                match {
                    it.message is OneBotSegmentsInteractionMessage &&
                        (it.message as? OneBotSegmentsInteractionMessage)?.message is InteractionMessage.Text &&
                        (it.message as? OneBotSegmentsInteractionMessage)?.segments == null
                }
            )
        }
    }

    @Test
    fun testSendTextInterceptionAndModify() {
        val app = runBlocking { launchSimpleApplication() }

        app.eventDispatcher.process<InternalMessagePreSendEvent> { event ->
            val message = event.message
            val currentMessage = event.currentMessage
            assertSame(message, currentMessage)
            assertIs<OneBotSegmentsInteractionMessage>(message)
            assertIs<OneBotSegmentsInteractionMessage>(currentMessage)
            assertIs<InteractionMessage.Text>(message.message)
            assertEquals("Hello", message.message.text)
            assertNull(message.segments)

            event.currentMessage = InteractionMessage.valueOf("Hello World")
        }

        val bot = spykBot(app.eventDispatcher)

        val member = spyk(
            OneBotMemberApiResultImpl(
                source = mockk(relaxed = true),
                bot = bot,
            ),
            recordPrivateCalls = true
        )
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)

        runBlocking { member.send("Hello") }

        coVerify { member.send("Hello") }
        coVerify {
            bot.emitMessagePreSendEvent(
                match {
                    it.message is OneBotSegmentsInteractionMessage &&
                        (it.message as? OneBotSegmentsInteractionMessage)?.message is InteractionMessage.Text &&
                        ((it.message as? OneBotSegmentsInteractionMessage)?.message as InteractionMessage.Text)
                            .text == "Hello" &&
                        (it.message as? OneBotSegmentsInteractionMessage)?.segments == null
                }
            )
        }

        coVerify { member invoke "sendText" withArguments listOf("Hello World") }
    }

    @Test
    fun testMemberPostSend() = runTest {
        val app = launchSimpleApplication()

        val receiptFromEvent = CompletableDeferred<MessageReceipt>()

        app.eventDispatcher.process<OneBotMemberPostSendEvent> { event ->
            receiptFromEvent.complete(event.receipt)
        }

        val dispatcher = spyk(app.eventDispatcher, recordPrivateCalls = true)

        val bot = spykBot(dispatcher)

        val member = spyk(
            OneBotMemberApiResultImpl(
                source = mockk(relaxed = true),
                bot = bot,
            ),
        )

        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)
        val receiptFromSend = member.send("Hello World")

        coVerify { member.send("Hello World") }

        assertSame(receiptFromSend, receiptFromEvent.await())
    }
}
