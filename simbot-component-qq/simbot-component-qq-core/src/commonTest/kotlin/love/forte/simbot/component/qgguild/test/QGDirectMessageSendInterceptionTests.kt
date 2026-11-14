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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.SendOption
import love.forte.simbot.application.interceptors
import love.forte.simbot.application.listeners
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.bot.InternalForInheritanceQGBotApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.event.MessageSentEvent
import love.forte.simbot.event.process
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.onMessageSend
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.stdlib.MessageDestination
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * 验证按 ID 发送与公开解析辅助的完整拦截边界。
 */
@OptIn(QGInternalApi::class)
class QGDirectMessageSendInterceptionTests : AbstractInteractionTests() {
    private val receipt = object : QGMessageReceipt {
        override suspend fun delete(vararg options: DeleteOption) = Unit
    }

    /**
     * 同时覆盖 Bot 直接入口及带构建回调的独立辅助入口。
     */
    private fun entries(bot: QGBot, callback: (String) -> Unit): List<suspend () -> QGMessageReceipt> {
        val content = QGGroupAndC2CMessageContentImpl("content".ID, "original", emptyList())
        return listOf(
            { bot.sendTo("channel".ID, "original") },
            { bot.sendTo("channel".ID, Text { "original" }) },
            { bot.sendTo("channel".ID, content) },
            { bot.sendDmsTo("dms".ID, "original") },
            { bot.sendDmsTo("dms".ID, Text { "original" }) },
            { bot.sendDmsTo("dms".ID, content) },
            { bot.sendMessage("channel", "original", { callback("pre") }, { callback("post") }) },
            { bot.sendMessage("channel", Text { "original" }, { callback("pre") }, { callback("post") }) },
            { bot.sendMessage("channel", content, { callback("pre") }, { callback("post") }) },
            { bot.sendGroupMessage("group", content, { callback("pre") }, { callback("post") }) },
            { bot.sendUserMessage("user", content, { callback("pre") }, { callback("post") }) },
        )
    }

    /**
     * 短路保留目标描述，且不提前执行解析、构建回调或网络请求。
     */
    @Test
    fun allDirectEntriesShortCircuitBeforeBuildersAndRequests() = runTest {
        val app = app { error("A short circuit must not make requests") }
        val bot = app.bot()
        val contexts = mutableListOf<MessageSendContext.Send>()
        try {
            app.interceptors {
                onMessageSend { _, context, _ ->
                    contexts += assertIs<MessageSendContext.Send>(context)
                    receipt
                }
            }
            entries(bot) { error("A short circuit must not execute builders") }.forEach {
                assertSame(receipt, it())
            }
            assertEquals(11, contexts.size)
            contexts.forEach {
                val direct = assertIs<QGMessageSendContext>(it)
                assertSame(bot, it.bot)
                assertSame(bot, it.source)
                assertSame(it.originalMessage, it.message)
                assertTrue(it.options.isEmpty())
                assertEquals(null, it.target())
            }
            val targets = contexts.map { assertIs<QGMessageSendTarget>(it.destination) }
            assertEquals(
                listOf(
                    MessageDestination.CHANNEL, MessageDestination.CHANNEL, MessageDestination.CHANNEL,
                    MessageDestination.DMS, MessageDestination.DMS, MessageDestination.DMS,
                    MessageDestination.CHANNEL, MessageDestination.CHANNEL, MessageDestination.CHANNEL,
                    MessageDestination.GROUP, MessageDestination.USER,
                ),
                targets.map { it.destination },
            )
            assertEquals(listOf("channel", "channel", "channel", "dms", "dms", "dms",
                "channel", "channel", "channel", "group", "user"), targets.map { it.id.toString() })
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 替换消息在同一链中生效，构建回调延迟且每次只执行一次。
     */
    @Test
    fun replacementRetainsCallbacksAndMakesOneRequestPerEntry() = runTest {
        var calls = 0
        var requests = 0
        val callbackOrder = mutableListOf<String>()
        val option = object : SendOption {}
        val app = app { request ->
            requests++
            val fields = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertEquals("replacement", fields["content"]?.jsonPrimitive?.content)
            if (request.url.encodedPath.startsWith("/v2/")) {
                respondOk("""{"id":"sent","timestamp":1698435200}""")
            } else {
                respondOk("""{"id":"sent","channel_id":"channel","guild_id":"guild","content":"replacement","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":1}""")
            }
        }
        val bot = app.bot()
        var initial: QGMessageSendContext? = null
        try {
            app.interceptors {
                onMessageSend { _, context, chain ->
                    calls++
                    val direct = assertIs<MessageSendContext.Send>(context)
                    initial = assertIs<QGMessageSendContext>(direct)
                    chain.proceed(direct.withMessage(InteractionMessage.valueOf("replacement")).withOptions(listOf(option)))
                }
                onMessageSend { _, context, chain ->
                    val copied = assertIs<QGMessageSendContext>(context)
                    val original = assertNotNull(initial)
                    assertSame(original.destination, copied.destination)
                    assertSame(original.originalMessage, copied.originalMessage)
                    assertSame(original.source, copied.source)
                    assertSame(original.bot, copied.bot)
                    assertEquals(listOf(option), copied.options)
                    assertEquals("replacement", assertIs<InteractionMessage.Text>(copied.message).text)
                    val options = mutableListOf<SendOption>(option)
                    val final = copied.withOptions(options).withMessage(copied.message)
                    options.clear()
                    assertSame(copied.destination, final.destination)
                    assertSame(original.originalMessage, final.originalMessage)
                    assertEquals(listOf(option), final.options)
                    chain.proceed(final)
                }
            }
            entries(bot) { callbackOrder += it }.forEachIndexed { index, send ->
                send()
                assertEquals(index + 1, calls)
                assertEquals(index + 1, requests)
            }
            assertEquals(List(5) { listOf("pre", "post") }.flatten(), callbackOrder)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 组件上下文构造和复制均隔离选项列表，保留强类型目标及原始消息。
     */
    @Test
    fun directContextCopiesKeepComponentFieldsAndOptionSnapshots() = runTest {
        val app = app { error("Copying a context must not make requests") }
        val bot = app.bot()
        try {
            val option = object : SendOption {}
            val options = mutableListOf<SendOption>(option)
            val message = InteractionMessage.valueOf("original")
            val target = QGMessageSendTarget.create(MessageDestination.DMS, "dms".ID)
            val initial = QGMessageSendContext.create(bot, bot, message, target, options)
            options.clear()
            val replacement = InteractionMessage.valueOf("replacement")
            val copied = initial.withMessage(replacement).withOptions(initial.options)

            assertIs<MessageSendContext.Send>(copied)
            assertSame(bot, copied.source)
            assertSame(bot, copied.bot)
            assertSame(message, copied.originalMessage)
            assertSame(replacement, copied.message)
            assertSame(target, copied.destination)
            assertEquals(null, copied.target())
            assertEquals(listOf(option), initial.options)
            assertEquals(listOf(option), copied.options)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 直接发送入口与普通组件入口使用相同的最终回执类型约束。
     */
    @Test
    fun everyDirectEntryChecksOnlyTheFinalReceiptType() = runTest {
        val app = app { error("A short circuit must not make requests") }
        val bot = app.bot()
        val wrong = object : MessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) = Unit
        }
        var fixFinalResult = false
        try {
            app.interceptors {
                onMessageSend { _, context, chain ->
                    val result = chain.proceed(context)
                    if (fixFinalResult) receipt else result
                }
                onMessageSend { _, _, _ -> wrong }
            }
            val sends = entries(bot) { error("A short circuit must not execute builders") }
            sends.forEach { send -> assertFailsWith<IllegalStateException> { send() } }
            fixFinalResult = true
            sends.forEach { send -> assertSame(receipt, send()) }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 真实发送后的通知独立于最外层回执校验结果，并保留最终直发上下文。
     */
    @Test
    fun successfulDirectSendNotifiesEvenWhenOuterReceiptIsInvalid() = runTest {
        val sentEvent = CompletableDeferred<MessageSentEvent>()
        var requests = 0
        val app = app {
            requests++
            respondOk("""{"id":"sent","channel_id":"channel","guild_id":"guild","content":"replacement","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":1}""")
        }
        val bot = app.bot()
        try {
            app.listeners { process<MessageSentEvent> { sentEvent.complete(it) } }
            app.interceptors {
                onMessageSend { _, context, chain ->
                    chain.proceed(context.withMessage(InteractionMessage.valueOf("replacement")))
                    object : MessageReceipt {
                        override suspend fun delete(vararg options: DeleteOption) = Unit
                    }
                }
            }
            assertFailsWith<IllegalStateException> { bot.sendTo("channel".ID, "original") }
            val event = withTimeout(5_000.milliseconds) { sentEvent.await() }
            assertIs<QGMessageReceipt>(event.receipt)
            val context = assertIs<QGMessageSendContext>(event.context)
            assertSame(bot, context.source)
            assertEquals("original", assertIs<InteractionMessage.Text>(context.originalMessage).text)
            assertEquals("replacement", assertIs<InteractionMessage.Text>(context.message).text)
            assertEquals(QGMessageSendTarget.create(MessageDestination.CHANNEL, "channel".ID), context.destination)
            assertEquals(1, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 自定义 Bot 没有官方容器时，独立解析辅助继续使用其原生发送能力。
     */
    @OptIn(InternalForInheritanceQGBotApi::class)
    @Test
    fun customBotImplementationKeepsNativeHelperBehavior() = runTest {
        var requests = 0
        val callbacks = mutableListOf<String>()
        val app = app {
            requests++
            respondOk("""{"id":"sent","channel_id":"channel","guild_id":"guild","content":"original","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":1}""")
        }
        val bot = app.bot()
        val custom = object : QGBot by bot {}
        try {
            app.interceptors { onMessageSend { _, _, _ -> error("Custom Bot has no application container") } }
            custom.sendMessage("channel", "original", { callbacks += "pre" }, { callbacks += "post" })
            assertEquals(listOf("pre", "post"), callbacks)
            assertEquals(1, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
