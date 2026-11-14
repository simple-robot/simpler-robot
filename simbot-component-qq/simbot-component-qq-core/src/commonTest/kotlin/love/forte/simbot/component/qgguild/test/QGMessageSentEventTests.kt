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

package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.SendOption
import love.forte.simbot.application.interceptors
import love.forte.simbot.application.listeners
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.event.QGC2CMessageCreateEventImpl
import love.forte.simbot.component.qguild.internal.event.QGGroupMessageCreateEventImpl
import love.forte.simbot.component.qguild.internal.group.idGroup
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.event.MessageSentEvent
import love.forte.simbot.event.process
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.onMessageSend
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.qguild.event.GroupMessageCreate
import kotlin.test.*

/**
 * 验证通知反映真实终端成功，独立于外层拦截结果和通知监听结果。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QGMessageSentEventTests : AbstractInteractionTests() {
    /**
     * 统一测试调度器使通知是否发生的断言无需时间等待。
     */
    @Test
    fun notificationsDescribeOnlySuccessfulTerminalSends() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        var mode = "success"
        var requests = 0
        val events = mutableListOf<MessageSentEvent>()
        val sendOption = object : SendOption {}
        val replyOption = object : ReplyOption {}
        val shortReceipt = object : QGMessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) = Unit
        }
        val wrongReceipt = object : MessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) = Unit
        }
        val app = launchSimpleApplication {
            config { coroutineContext = dispatcher }
            eventDispatcher { coroutineContext = dispatcher }
            useQQGuild {
                botManager {
                    botConfigure { _, _, _ ->
                        disableWs = true
                        apiClientEngine = MockEngine { request ->
                            requests++
                            if (mode == "platform-failure") {
                                respond("""{"code":11253,"message":"rejected"}""", HttpStatusCode.Forbidden)
                            } else {
                                val fields =
                                    Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
                                assertEquals("sent", fields["content"]?.jsonPrimitive?.content)
                                respondOk("""{"id":"sent-$requests","timestamp":1698435200}""")
                            }
                        }
                    }
                }
            }
        }
        val bot = app.bot() as QGBotImpl
        try {
            app.listeners {
                process<MessageSentEvent> {
                    events.add(it)
                    // 通知监听器失败不得改变已取得的发送回执。
                    error("notification listener failure")
                }
            }
            app.interceptors {
                onMessageSend { _, context, chain ->
                    if (mode == "short-circuit") return@onMessageSend shortReceipt
                    val updated = when (context) {
                        is MessageSendContext.Send -> context.withMessage(InteractionMessage.valueOf("sent"))
                            .withOptions(listOf(sendOption))

                        is MessageSendContext.Reply -> context.withMessage(InteractionMessage.valueOf("sent"))
                            .withOptions(listOf(replyOption))

                    }
                    val result = chain.proceed(updated)
                    if (mode == "outer-failure") error("outer interceptor failure")
                    if (mode == "wrong-receipt") return@onMessageSend wrongReceipt
                    if (mode == "replace-receipt") return@onMessageSend shortReceipt
                    result
                }
            }
            val group = idGroup(bot, "group".ID)
            val sent = group.send("original")
            runCurrent()
            assertEquals(1, events.size)
            val sendEvent = events.single()
            assertSame(sent, sendEvent.receipt)
            assertSame(group, sendEvent.context.source)
            assertSame(bot, sendEvent.bot)
            assertSame(bot.component, sendEvent.component)
            assertEquals("original", assertIs<InteractionMessage.Text>(sendEvent.context.originalMessage).text)
            assertEquals("sent", assertIs<InteractionMessage.Text>(sendEvent.context.message).text)
            assertEquals(listOf(sendOption), assertIs<MessageSendContext.Send>(sendEvent.context).options)

            val received = Json.decodeFromString(
                GroupMessageCreate.serializer(),
                """{"op":0,"id":"event","s":1,"d":{"id":"received","author":{"member_openid":"member"},"content":"received","timestamp":"2023-11-06T13:37:18+08:00","group_openid":"group"}}"""
            )
            val replySource = QGGroupMessageCreateEventImpl(bot, "{}", received, "event")
            val replied = replySource.reply("reply-original")
            runCurrent()
            assertEquals(2, events.size)
            assertSame(replied, events.last().receipt)
            assertSame(replySource, events.last().context.source)
            assertEquals(listOf(replyOption), assertIs<MessageSendContext.Reply>(events.last().context).options)
            assertNotEquals(sendEvent.id, events.last().id)

            mode = "outer-failure"
            assertFailsWith<IllegalStateException> { group.send("original") }
            runCurrent()
            assertEquals(3, events.size)
            assertEquals("sent", assertIs<InteractionMessage.Text>(events.last().context.message).text)

            val c2cReceived = Json.decodeFromString(
                C2CMessageCreate.serializer(),
                """{"op":0,"id":"event","s":1,"d":{"id":"received","author":{"user_openid":"user"},"content":"received","timestamp":"2023-11-06T13:37:18+08:00"}}"""
            )
            val typedReplySource = QGC2CMessageCreateEventImpl(bot, "{}", c2cReceived, "event")
            mode = "wrong-receipt"
            assertFailsWith<IllegalStateException> { typedReplySource.reply("original") }
            runCurrent()
            assertEquals(4, events.size)
            assertIs<QGMessageReceipt>(events.last().receipt)
            assertNotSame(wrongReceipt, events.last().receipt)

            mode = "replace-receipt"
            assertSame(shortReceipt, typedReplySource.reply("original"))
            runCurrent()
            assertEquals(5, events.size)
            assertNotSame(shortReceipt, events.last().receipt)

            mode = "short-circuit"
            assertSame(shortReceipt, group.send("original"))
            runCurrent()
            assertEquals(5, events.size)

            mode = "platform-failure"
            assertFailsWith<QQGuildApiException> { group.send("original") }
            runCurrent()
            assertEquals(5, events.size)
            assertEquals(6, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
