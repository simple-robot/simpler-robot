/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.internal

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.KookComponent
import love.forte.simbot.component.kook.bot.KookBotConfiguration
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.event.internal.KookBotSelfChannelMessageEventImpl
import love.forte.simbot.component.kook.event.internal.KookBotSelfMessageEventImpl
import love.forte.simbot.component.kook.event.internal.KookChannelMessageEventImpl
import love.forte.simbot.component.kook.event.internal.KookContactMessageEventImpl
import love.forte.simbot.component.kook.message.*
import love.forte.simbot.core.event.createSimpleEventDispatcherImpl
import love.forte.simbot.event.*
import love.forte.simbot.interception.InterceptorManager
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.onMessageSend
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import love.forte.simbot.kook.api.message.SendMessageResult
import love.forte.simbot.kook.api.userchat.TargetInfo
import love.forte.simbot.kook.api.userchat.UserChatView
import love.forte.simbot.kook.event.TextEventExtra
import love.forte.simbot.kook.objects.SimpleChannel
import love.forte.simbot.kook.objects.SimpleGuild
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.stdlib.BotConfiguration
import love.forte.simbot.kook.stdlib.BotFactory
import love.forte.simbot.kook.stdlib.Ticket
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.Text
import kotlin.coroutines.CoroutineContext
import kotlin.test.*
import love.forte.simbot.kook.event.Event as KEvent

/**
 * 验证标准能力的真实发送路径、委托边界以及短路语义。
 */
@OptIn(ApiResultType::class, ExperimentalSimbotAPI::class, ExperimentalCoroutinesApi::class)
class KookMessageSendInterceptionTests {
    /**
     * 三种发送来源的全部标准重载只进入一次链，并原样返回类型匹配的拦截结果。
     */
    @Test
    fun standardSendOverloadsKeepSourceOptionsAndShortCircuit() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager, backgroundScope.coroutineContext) { error("Short circuit must not issue HTTP requests") }
        val channel = channel(bot)
        val member = KookMemberImpl(bot, user, "guild")
        val chat = KookUserChatImpl(bot, chatView)
        val receipt = KookMessageCreatedReceipt(SendMessageResult("short-circuit", 1), true, bot)
        val option = object : SendOption {}
        val calls = mutableListOf<MessageSendContext.Send>()
        manager.onMessageSend { _, context, _ ->
            calls += assertIs<MessageSendContext.Send>(context)
            receipt
        }

        for (source: SendSupport in listOf(channel, member, chat)) {
            val message = Text.of("original")
            val content = event().toContent(false, bot)
            assertSame(receipt, source.send("original"))
            assertSame(receipt, source.send(message))
            assertSame(receipt, source.send(content))
            assertSame(receipt, source.send("original", option))
            assertSame(receipt, source.send(message, option))
            assertSame(receipt, source.send(content, option))
            val contexts = calls.takeLast(6)
            contexts.forEach {
                assertSame(source, it.source)
                assertSame(source, it.target())
                assertSame(it.message, it.originalMessage)
            }
            assertEquals(
                listOf(emptyList(), emptyList(), emptyList(), listOf(option), listOf(option), listOf(option)),
                contexts.map { it.options })
        }
        assertEquals(18, calls.size)
    }

    /**
     * 消息替换实际进入平台请求，成员发送不会委托联系人而丢失来源或重复拦截。
     */
    @Test
    fun replacementReachesChannelChatAndMemberRequests() = runTest {
        val manager = InterceptorManager()
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("replaced", body.getValue("content").jsonPrimitive.content)
            success()
        }
        var calls = 0
        manager.onMessageSend { _, context, chain ->
            calls++
            chain.proceed(context.withMessage(InteractionMessage.valueOf("replaced")))
        }
        channel(bot).send("original")
        KookUserChatImpl(bot, chatView).send(Text.of("original"))
        KookMemberImpl(bot, user, "guild").send(event().toContent(false, bot))
        assertEquals(3, calls)
        assertEquals(3, requests)
    }

    /**
     * 所有回复保留事件来源与引用；私聊短路无需提前创建会话。
     */
    @Test
    fun repliesKeepEventSourceAndEnterOnce() = runTest {
        val manager = InterceptorManager()
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("replaced", body.getValue("content").jsonPrimitive.content)
            if (request.url.encodedPath.endsWith("/message/create")) {
                assertEquals("incoming", body.getValue("quote").jsonPrimitive.content)
            }
            success()
        }
        val channel = channel(bot)
        val member = KookMemberImpl(bot, user, "guild")
        val guild = KookGuildImpl(bot, SimpleGuild("guild", "guild", "", "owner", "", 0, "", false, "", "", ""))
        val sources = listOf(
            KookChannelMessageEventImpl(bot, event(), member, channel, guild, ""),
            KookBotSelfChannelMessageEventImpl(bot, event(), channel, member, guild, ""),
            KookContactMessageEventImpl(bot, event(), ""),
            KookBotSelfMessageEventImpl(bot, event(), ""),
        )
        val option = object : ReplyOption {}
        val contexts = mutableListOf<MessageSendContext.Reply>()
        manager.onMessageSend { _, context, chain ->
            contexts += assertIs<MessageSendContext.Reply>(context)
            chain.proceed(context.withMessage(InteractionMessage.valueOf("replaced")))
        }

        for (source in sources) {
            source.reply("original", option)
            source.reply(Text.of("original"))
            source.reply(event().toContent(false, bot))
            contexts.takeLast(3).forEach { assertSame(source, it.source) }
            assertEquals(listOf(option), contexts[contexts.size - 3].options.filterNot { it is KookChannelMessageSendOption })
        }
        assertEquals(12, contexts.size)
        assertEquals(12, requests)
    }

    /**
     * 短路和取消都在目标解析与请求之前生效，原生发送重载也进入相同拦截点。
     */
    @Test
    fun shortCircuitAndCancellationAvoidNetworkForAllEntries() = runTest {
        val manager = InterceptorManager()
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { requests++; success() }
        val source = KookContactMessageEventImpl(bot, event(), "")
        val receipt = KookMessageCreatedReceipt(SendMessageResult("short-circuit", 1), true, bot)
        val registration = manager.onMessageSend { _, context, _ ->
            assertIs<MessageSendContext.Reply>(context)
            receipt
        }
        assertSame(receipt, source.reply("short circuit"))
        assertEquals(0, requests)
        registration.dispose()

        val failure = CancellationException("cancelled")
        manager.onMessageSend { _, _, _ -> throw failure }
        assertSame(failure, assertFailsWith<CancellationException> { source.reply("cancel") })
        assertEquals(0, requests)
        assertSame(failure, assertFailsWith<CancellationException> {
            channel(bot).send(SendChannelMessageApi.create(1, "channel", "native", null, null, null))
        })
        assertEquals(0, requests)
    }

    /**
     * 真实发送通知包含最终上下文，异步监听器失败不影响已返回的回执或 bot。
     */
    @Test
    fun successfulSendNotifiesFinalContextAndIsolatesListenerFailures() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { event ->
            events += event
            throw IllegalStateException("listener failed")
        }
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) { success() }
        val source = channel(bot)
        val option = object : SendOption {}
        manager.onMessageSend { _, context, chain ->
            val send = assertIs<MessageSendContext.Send>(context)
            chain.proceed(send.withMessage(InteractionMessage.valueOf("final")).withOptions(listOf(option)))
        }

        val receipt = source.send("original")
        assertTrue(events.isEmpty())
        runCurrent()
        val event = events.single()
        assertSame(receipt, event.receipt)
        assertSame(bot, event.bot)
        assertSame(source, event.context.source)
        assertEquals("original", assertIs<InteractionMessage.Text>(event.context.originalMessage).text)
        assertEquals("final", assertIs<InteractionMessage.Text>(event.context.message).text)
        assertEquals(listOf(option), assertIs<MessageSendContext.Send>(event.context).options)
        assertTrue(bot.isActive)
    }

    /**
     * 短路与平台失败均不会产生发送成功通知。
     */
    @Test
    fun shortCircuitAndPlatformFailureDoNotNotify() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { events += it }
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) {
            requests++
            throw IllegalStateException("platform failed")
        }
        val source = KookContactMessageEventImpl(bot, event(), "")
        val receipt = KookMessageCreatedReceipt(SendMessageResult("short-circuit", 1), true, bot)
        val registration = manager.onMessageSend { _, _, _ -> receipt }
        assertSame(receipt, source.reply("short circuit"))
        runCurrent()
        assertTrue(events.isEmpty())
        assertEquals(0, requests)

        registration.dispose()
        assertFailsWith<IllegalStateException> { source.reply("platform failure") }
        runCurrent()
        assertTrue(events.isEmpty())
        // 底层 HTTP 客户端可能重试失败请求，不限定物理请求次数。
        assertTrue(requests > 0)
    }

    /**
     * 平台回复成功后外层拦截器失败，仍保留真实发送通知及回复来源。
     */
    @Test
    fun successfulReplyStillNotifiesWhenOuterInterceptorFails() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { events += it }
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) { success() }
        val source = KookContactMessageEventImpl(bot, event(), "")
        val failure = IllegalStateException("outer interceptor failed")
        val option = object : ReplyOption {}
        manager.onMessageSend { _, context, chain ->
            val reply = assertIs<MessageSendContext.Reply>(context)
            chain.proceed(reply.withMessage(InteractionMessage.valueOf("final")).withOptions(listOf(option)))
            throw failure
        }

        assertSame(failure, assertFailsWith<IllegalStateException> { source.reply("original") })
        runCurrent()
        val notification = events.single()
        assertSame(source, notification.context.source)
        assertEquals("original", assertIs<InteractionMessage.Text>(notification.context.originalMessage).text)
        assertEquals("final", assertIs<InteractionMessage.Text>(notification.context.message).text)
        assertEquals(listOf(option), assertIs<MessageSendContext.Reply>(notification.context).options)
    }

    /**
     * 私聊文字入口按公开声明接受 KOOK 回执。
     */
    @Test
    fun textChatAcceptsCompatibleKookReceiptTypes() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager, backgroundScope.coroutineContext) { error("Short circuit must not issue HTTP requests") }
        val source = KookUserChatImpl(bot, chatView)
        val receipt = KookApiRequestedReceipt(Unit, true)
        manager.onMessageSend { _, _, _ -> receipt }

        assertSame(receipt, source.send("original"))
        assertSame(receipt, source.send("original", object : SendOption {}))
        assertSame(receipt, source.send(Text.of("original")))
    }

    /**
     * 外层可以替换发送结果，成功通知始终保留平台真实产生的回执。
     */
    @Test
    fun textChatAllowsReplacementWithSameReceiptType() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { events += it }
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) { requests++; success() }
        val replacement = KookMessageCreatedReceipt(SendMessageResult("replacement", 1), true, bot)
        var sent: MessageReceipt? = null
        manager.onMessageSend { _, context, chain ->
            sent = chain.proceed(context)
            replacement
        }

        val result: KookMessageReceipt = KookUserChatImpl(bot, chatView).send("original")
        assertSame(replacement, result)
        assertEquals(1, requests)
        runCurrent()
        assertSame(sent, events.single().receipt)
        assertNotSame(replacement, events.single().receipt)
    }

    /**
     * 中间层返回其他类型仍可由外层修正，避免在链内提前校验。
     */
    @Test
    fun outerInterceptorCanReplaceAnInnerReceiptOfAnotherType() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager, backgroundScope.coroutineContext) { error("Short circuit must not issue HTTP requests") }
        val innerReceipt = KookApiRequestedReceipt(Unit, true)
        val replacement = KookMessageCreatedReceipt(SendMessageResult("replacement", 1), true, bot)
        manager.onMessageSend(priority = -100) { _, context, chain ->
            assertSame(innerReceipt, chain.proceed(context))
            replacement
        }
        manager.onMessageSend(priority = 100) { _, _, _ -> innerReceipt }

        assertSame(replacement, KookUserChatImpl(bot, chatView).send("original"))
    }

    /**
     * 外层替换为其他 KOOK 回执后，通知仍携带实际发送回执。
     */
    @Test
    fun successfulSendNotifiesActualReceiptWhenResultIsReplaced() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { events += it }
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) { requests++; success() }
        val source = KookUserChatImpl(bot, chatView)
        val wrongReceipt = KookApiRequestedReceipt(Unit, true)
        var sent: MessageReceipt? = null
        manager.onMessageSend { _, context, chain ->
            sent = chain.proceed(context)
            wrongReceipt
        }

        assertSame(wrongReceipt, source.send("original"))
        assertEquals(1, requests)
        runCurrent()
        val notification = events.single()
        assertSame(source, notification.context.source)
        assertSame(sent, notification.receipt)
        assertIs<KookMessageCreatedReceipt>(notification.receipt)
    }

    /**
     * 测试只配置请求引擎，不启动连接或平台数据同步。
     */
    private fun bot(
        manager: InterceptorManager,
        context: CoroutineContext,
        eventProcessor: EventProcessor = object : EventProcessor {
            override fun push(event: Event) = emptyFlow<EventResult>()
        },
        handler: suspend MockRequestHandleScope.(io.ktor.client.request.HttpRequestData) ->
        io.ktor.client.request.HttpResponseData,
    ): KookBotImpl {
        val engine = MockEngine(handler)
        val configuration = BotConfiguration().apply {
            clientEngine = engine
            wsEngine = engine
            coroutineContext = context
        }
        return KookBotImpl(
            eventProcessor,
            BotFactory.create(Ticket.botWsTicket("bot", "token"), configuration),
            KookComponent(),
            KookBotConfiguration(configuration),
            manager,
        )
    }

    /**
     * 仅提供发送需要的频道字段。
     */
    private fun channel(bot: KookBotImpl): KookChatChannelImpl = KookChatChannelImpl(
        bot,
        SimpleChannel(
            "channel", "channel", "owner", "guild", "", false, "", 0, 0, 1,
            permissionSync = 0, hasPassword = false
        ),
    )

    /**
     * 构建可复用的原始文字消息事件。
     */
    private fun event(): KEvent<TextEventExtra> = KEvent(
        "GROUP", 1, "channel", "user", "received", "incoming", 0, "", TextEventExtra(1, author = user),
    )

    /**
     * 返回平台消息发送成功响应。
     */
    private fun MockRequestHandleScope.success() = respond(
        """{"code":0,"message":"ok","data":{"msg_id":"sent","msg_timestamp":1,"nonce":null}}""",
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

    /**
     * 五种频道专属入口均能在解析消息和执行 HTTP 前短路。
     */
    @Test
    fun protocolOverloadsShortCircuitOnceAndKeepArguments() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager, backgroundScope.coroutineContext) { error("Short circuit must not send") }
        val source = channel(bot)
        val request = SendChannelMessageApi.create(9, "other-channel", "native", "quote", "nonce", "temp", "template")
        val receipt = KookMessageCreatedReceipt(SendMessageResult("short", 1), false, bot)
        val contexts = mutableListOf<MessageSendContext>()
        manager.onMessageSend { _, context, _ ->
            contexts += context
            receipt
        }
        assertSame(receipt, source.send(request))
        assertSame(request, assertIs<KookChannelRequestMessage>(contexts.single().originalMessage).request)
        assertSame(receipt, source.send(9, "native", "quote".ID, "nonce", "temp".ID))
        assertSame(receipt, source.send("native", "quote".ID, "temp".ID))
        assertSame(receipt, source.send(Text.of("native"), "quote".ID, "temp".ID))
        assertSame(receipt, source.send(event().toContent(false, bot), "quote".ID, "temp".ID))
        assertEquals(5, contexts.size)
        contexts.forEach {
            assertSame(source, it.source)
            val native = it.message as? KookChannelRequestMessage
            if (native != null) {
                assertEquals("quote", native.request.quote)
                assertEquals("temp", native.request.tempTargetId)
                assertTrue((it as MessageSendContext.Send).options.isEmpty())
                return@forEach
            }
            val options = assertNotNull((when (it) {
                is MessageSendContext.Send -> it.options
                is MessageSendContext.Reply -> error("Not a send context")
            }).toKookChannelMessageSendOptions())
            assertEquals("quote", options.quote)
            assertEquals("temp", options.tempTargetId)
        }
    }

    /**
     * 原请求的目标可与频道对象不同，所有原生字段及真实发送通知均保留。
     */
    @Test
    fun nativeRequestKeepsAllFieldsAndNotifiesOnce() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { events += it }
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("other-channel", body.getValue("target_id").jsonPrimitive.content)
            assertEquals("native", body.getValue("content").jsonPrimitive.content)
            assertEquals("9", body.getValue("type").jsonPrimitive.content)
            assertEquals("quote", body.getValue("quote").jsonPrimitive.content)
            assertEquals("nonce", body.getValue("nonce").jsonPrimitive.content)
            assertEquals("temp", body.getValue("temp_target_id").jsonPrimitive.content)
            assertEquals("template", body.getValue("template_id").jsonPrimitive.content)
            success()
        }
        val api = SendChannelMessageApi.create(9, "other-channel", "native", "quote", "nonce", "temp", "template")
        var calls = 0
        manager.onMessageSend { _, context, chain ->
            calls++
            assertSame(api, assertIs<KookChannelRequestMessage>(context.message).request)
            chain.proceed(context)
        }
        val receipt = channel(bot).send(api)
        runCurrent()
        assertEquals(1, calls)
        assertEquals(1, requests)
        assertSame(receipt, events.single().receipt)
    }

    /**
     * 原生请求换为普通消息时只使用 options，替换原生请求时使用新请求参数。
     */
    @Test
    fun replacementKeepsProtocolParametersAndCanReplaceNativeRequest() = runTest {
        val manager = InterceptorManager()
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("replaced", body.getValue("content").jsonPrimitive.content)
            if (requests <= 2) {
                assertEquals(if (requests == 1) "other-channel" else "channel", body.getValue("target_id").jsonPrimitive.content)
                assertTrue("quote" !in body && "nonce" !in body && "temp_target_id" !in body && "template_id" !in body)
            } else {
                assertEquals("quote", body.getValue("quote").jsonPrimitive.content)
                assertEquals("temp", body.getValue("temp_target_id").jsonPrimitive.content)
            }
            success()
        }
        val registration = manager.onMessageSend { _, context, chain ->
            chain.proceed(context.withMessage(InteractionMessage.valueOf("replaced")))
        }
        val source = channel(bot)
        source.send(SendChannelMessageApi.create(9, "other-channel", "native", "quote", "nonce", "temp", "template"))
        source.send(9, "native", "quote".ID, "nonce", "temp".ID)
        source.send("native", "quote".ID, "temp".ID)
        source.send(Text.of("native"), "quote".ID, "temp".ID)
        source.send(event().toContent(false, bot), "quote".ID, "temp".ID)
        registration.dispose()
        manager.onMessageSend { _, context, chain ->
            chain.proceed(context.withMessage(KookChannelRequestMessage(
                SendChannelMessageApi.create(1, "new-channel", "replaced", "quote", null, "temp"),
            )))
        }
        source.send("native")
        assertEquals(6, requests)
    }

    /**
     * 公共消息直发扩展保留目标种类和选项，内部标准发送不重复进入此链。
     */
    @Test
    fun directExtensionsKeepTypedTargetsAndSupportReplacement() = runTest {
        val manager = InterceptorManager()
        val targets = mutableListOf<KookMessageSendTarget>()
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("replaced", body.getValue("content").jsonPrimitive.content)
            assertEquals("quote", body.getValue("quote").jsonPrimitive.content)
            assertEquals("nonce", body.getValue("nonce").jsonPrimitive.content)
            success()
        }
        val message = Text.of("original")
        manager.onMessageSend { _, context, chain ->
            val direct = assertIs<MessageSendContext.Send>(context)
            assertSame(message, direct.source)
            targets += assertIs<KookMessageSendTarget>(direct.destination)
            assertEquals(null, direct.target())
            chain.proceed(context.withMessage(InteractionMessage.valueOf("replaced")))
        }
        message.sendToChannel(bot, "channel", "quote", "nonce", "temp")
        message.sendToDirectByTargetId(bot, "user", "quote", "nonce")
        message.sendToDirectByChatCode(bot, "chat", "quote", "nonce")
        assertEquals(
            listOf(
                KookMessageSendTarget.channel("channel"),
                KookMessageSendTarget.user("user"),
                KookMessageSendTarget.chatCode("chat"),
            ),
            targets,
        )
        assertEquals(3, requests)
    }

    /**
     * 两层复制后保留 KOOK 目标字段和具体类型，终端仍只发送一次。
     */
    @Test
    fun componentContextSurvivesMessageAndOptionReplacementAcrossInterceptors() = runTest {
        val manager = InterceptorManager()
        val events = mutableListOf<MessageSentEvent>()
        val dispatcher = createSimpleEventDispatcherImpl {}
        dispatcher.process<MessageSentEvent> { events += it }
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext, dispatcher) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("other-channel", body.getValue("target_id").jsonPrimitive.content)
            assertEquals("replaced", body.getValue("content").jsonPrimitive.content)
            assertEquals("new-quote", body.getValue("quote").jsonPrimitive.content)
            assertTrue("nonce" !in body && "temp_target_id" !in body && "template_id" !in body)
            success()
        }
        val source = channel(bot)
        val api = SendChannelMessageApi.create(9, "other-channel", "native", "quote", "nonce", "temp", "template")
        var destination: KookMessageSendTarget? = null
        var calls = 0
        manager.onMessageSend(priority = -100) { _, context, chain ->
            calls++
            val initial = assertIs<KookMessageSendContext>(context)
            destination = initial.destination
            val replaced = initial.withMessage(InteractionMessage.valueOf("replaced"))
            assertSame(initial.destination, replaced.destination)
            assertSame(api, assertIs<KookChannelRequestMessage>(replaced.originalMessage).request)
            chain.proceed(replaced)
        }
        manager.onMessageSend(priority = 100) { _, context, chain ->
            calls++
            val current = assertIs<KookMessageSendContext>(context)
            val options = current.options.filterNot { it is KookChannelMessageSendOption.Quote }.toMutableList()
            options += KookChannelMessageSendOption.quote("new-quote".ID)
            val expectedOptions = options.toList()
            val replaced = current.withOptions(options)
            options.clear()
            assertSame(destination, replaced.destination)
            assertSame(source, replaced.source)
            assertSame(api, assertIs<KookChannelRequestMessage>(replaced.originalMessage).request)
            assertEquals(expectedOptions, replaced.options)
            chain.proceed(replaced)
        }

        source.send(api)
        runCurrent()
        assertEquals(2, calls)
        assertEquals(1, requests)
        val sent = assertIs<KookMessageSendContext>(events.single().context)
        assertSame(destination, sent.destination)
        assertEquals(null, sent.target())
        assertSame(api, assertIs<KookChannelRequestMessage>(sent.originalMessage).request)
    }

    /**
     * 组件上下文隔离初始化选项列表，连续复制仍保留目标及原始消息。
     */
    @Test
    fun componentContextSnapshotsOptionsAndKeepsTarget() = runTest {
        val bot = bot(InterceptorManager(), backgroundScope.coroutineContext) { error("No request expected") }
        val source = Text.of("original")
        val message = InteractionMessage.valueOf(source)
        val destination = KookMessageSendTarget.user("user")
        val option = KookChannelMessageSendOption.quote("quote".ID)
        val options = mutableListOf<SendOption>(option)
        val initial = KookMessageSendContext.create(bot, source, message, destination, options)
        options.clear()
        val replaced = initial.withMessage(InteractionMessage.valueOf("changed")).withOptions(initial.options)

        assertEquals(listOf(option), initial.options)
        assertEquals(listOf(option), replaced.options)
        assertSame(source, replaced.source)
        assertSame(message, replaced.originalMessage)
        assertSame(destination, replaced.destination)
        assertEquals(null, replaced.target())
    }


    @Test
    fun nativeRequestOptionsOverrideCurrentRequestWithoutSnapshotting() = runTest {
        val manager = InterceptorManager()
        var mode = 0
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals(if (mode == 2) "initial-channel" else "replacement-channel", body.getValue("target_id").jsonPrimitive.content)
            when (mode) {
                0 -> assertEquals("override", body.getValue("quote").jsonPrimitive.content)
                1, 2 -> assertTrue("quote" !in body)
                3 -> assertEquals("replacement-quote", body.getValue("quote").jsonPrimitive.content)
            }
            assertTrue("nonce" !in body)
            success()
        }
        val source = channel(bot)
        val original = SendChannelMessageApi.create(9, "initial-channel", "original", "initial-quote", "initial-nonce", null)
        manager.onMessageSend { _, context, chain ->
            val send = assertIs<KookMessageSendContext>(context)
            assertTrue(send.options.isEmpty())
            assertEquals(KookMessageSendTarget.channel("initial-channel"), send.destination)
            assertEquals(null, send.target())
            val message = if (mode == 2) {
                InteractionMessage.valueOf("text")
            } else {
                KookChannelRequestMessage(SendChannelMessageApi.create(
                    1, "replacement-channel", "replacement",
                    if (mode == 1) null else "replacement-quote", null, null,
                ))
            }
            val withOverride = send.withMessage(message).withOptions(listOf(KookChannelMessageSendOption.quote("override".ID)))
            val final = if (mode == 0) withOverride else withOverride.withOptions(emptyList())
            assertSame(send.destination, final.destination)
            chain.proceed(final)
        }
        repeat(4) {
            mode = it
            source.send(original)
        }
        assertEquals(4, requests)
        assertEquals("initial-quote", original.quote)
    }

    @Test
    fun replyOptionsCanReplaceAndRemoveAutomaticProtocolParameters() = runTest {
        val manager = InterceptorManager()
        var replace = false
        var requests = 0
        val bot = bot(manager, backgroundScope.coroutineContext) { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            if (replace) {
                assertEquals("new-quote", body.getValue("quote").jsonPrimitive.content)
                assertEquals("new-nonce", body.getValue("nonce").jsonPrimitive.content)
            } else {
                assertTrue("quote" !in body && "nonce" !in body)
            }
            success()
        }
        val channel = channel(bot)
        val member = KookMemberImpl(bot, user, "guild")
        val guild = KookGuildImpl(bot, SimpleGuild("guild", "guild", "", "owner", "", 0, "", false, "", "", ""))
        val sources = listOf(
            KookChannelMessageEventImpl(bot, event(), member, channel, guild, ""),
            KookBotSelfChannelMessageEventImpl(bot, event(), channel, member, guild, ""),
            KookContactMessageEventImpl(bot, event(), ""),
            KookBotSelfMessageEventImpl(bot, event(), ""),
        )
        manager.onMessageSend { _, context, chain ->
            val reply = assertIs<KookMessageReplyContext>(context)
            chain.proceed(reply.withOptions(if (replace) listOf(
                KookChannelMessageSendOption.quote("new-quote".ID),
                KookChannelMessageSendOption.nonce("new-nonce"),
            ) else emptyList()))
        }
        for (source in sources) {
            source.reply("text")
        }
        replace = true
        for (source in sources) {
            source.reply("text")
        }
        assertEquals(8, requests)
    }

    private val user = SimpleUser("user", "user", "")
    private val chatView = UserChatView("chat", 0, 0, 0, false, false, false, TargetInfo("user", "user", false, ""))
}
