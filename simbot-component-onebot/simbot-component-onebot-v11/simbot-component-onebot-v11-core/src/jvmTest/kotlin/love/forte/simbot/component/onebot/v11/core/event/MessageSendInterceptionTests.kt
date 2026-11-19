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

package love.forte.simbot.component.onebot.v11.core.event

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.actor.internal.OneBotFriendApiResultImpl
import love.forte.simbot.component.onebot.v11.core.actor.internal.OneBotGroupApiResultImpl
import love.forte.simbot.component.onebot.v11.core.actor.internal.OneBotMemberApiResultImpl
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing
import love.forte.simbot.component.onebot.v11.core.api.SendMsgApi
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.firstOneBotBotManagerOrNull
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.event.internal.message.*
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.message.OneBotMessageReplyContext
import love.forte.simbot.component.onebot.v11.core.message.OneBotMessageReplyOption
import love.forte.simbot.component.onebot.v11.core.message.OneBotMessageSendContext
import love.forte.simbot.component.onebot.v11.core.message.OneBotMessageSendTarget
import love.forte.simbot.component.onebot.v11.core.useOneBot11
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotReply
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.core.event.impl.SimpleEventDispatcherImpl
import love.forte.simbot.definition.Actor
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.event.MessageSentEvent
import love.forte.simbot.event.process
import love.forte.simbot.interception.InterceptorManager
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.onMessageSend
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.Text
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*

/**
 * 验证真实组件发送入口、消息解析与拦截器容器之间的连接。
 */
@OptIn(ApiResultConstructor::class, ExperimentalCoroutinesApi::class)
class MessageSendInterceptionTests {
    private val sendOption = object : SendOption {}
    private val replyOption = object : ReplyOption {}

    private fun bot(
        manager: love.forte.simbot.interception.InterceptorContainer,
        processor: EventProcessor = SimpleEventDispatcherImpl(SimpleEventDispatcherConfigurationImpl()),
        context: CoroutineContext = EmptyCoroutineContext,
    ): OneBotBotImpl {
        val job = SupervisorJob()
        return spyk(
            OneBotBotImpl(
                uniqueId = "1234",
                coroutineContext = context + job,
                job = job,
                configuration = OneBotBotConfiguration(),
                component = OneBot11Component(),
                eventProcessor = processor,
                baseDecoderJson = OneBot11.DefaultJson,
                interceptorContainer = manager,
            )
        )
    }

    private fun senders(bot: OneBotBotImpl): List<SendSupport> = listOf(
        OneBotFriendApiResultImpl(mockk(relaxed = true), bot),
        OneBotGroupApiResultImpl(mockk(relaxed = true), bot, null),
        OneBotMemberApiResultImpl(mockk(relaxed = true), bot),
    )

    private fun repliers(bot: OneBotBotImpl): List<ReplySupport> = listOf(
        OneBotNormalGroupMessageEventImpl(null, mockk(relaxed = true), bot),
        OneBotAnonymousGroupMessageEventImpl(null, mockk(relaxed = true), bot),
        OneBotNoticeGroupMessageEventImpl(null, mockk(relaxed = true), bot),
        OneBotDefaultGroupMessageEventImpl(null, mockk(relaxed = true), bot),
        OneBotFriendMessageEventImpl(null, mockk(relaxed = true), bot),
        OneBotGroupPrivateMessageEventImpl(null, mockk(relaxed = true), bot),
        OneBotDefaultPrivateMessageEventImpl(null, mockk(relaxed = true), bot),
    )

    /**
     * 重载入口恰好进入一次链，终端实际使用替换后的消息。
     */
    @Test
    fun sendsReplaceMessagesOnceAcrossAllOverloads() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val requests = mutableListOf<SendMsgApi.Body>()
        coEvery { bot.executeData<SendMsgResult>(any()) } coAnswers {
            requests += assertIs<SendMsgApi.Body>(assertIs<SendMsgApi>(firstArg()).body)
            SendMsgResult(114.ID)
        }
        val contexts = mutableListOf<MessageSendContext.Send>()
        manager.onMessageSend { _, context, chain ->
            contexts += assertIs<MessageSendContext.Send>(context)
            chain.proceed(context.withMessage(InteractionMessage.valueOf("changed")))
        }
        val message: Message = Text { "original" }
        val content = mockk<MessageContent>()
        try {
            for ((index, sender) in senders(bot).withIndex()) {
                val start = contexts.size
                sender.send("original")
                sender.send(message)
                sender.send(content)
                sender.send("original", sendOption)
                sender.send(message, sendOption)
                sender.send(content, sendOption)
                val calls = contexts.drop(start)
                assertEquals(6, calls.size)
                calls.forEach {
                    assertSame(sender, it.source)
                    assertSame(sender, it.target())
                    assertSame(bot, it.bot)
                    val destination = assertIs<OneBotMessageSendContext>(it).destination
                    val id = assertIs<Actor>(sender).id
                    if (index == 1) {
                        assertEquals(OneBotMessageSendTarget.group(id), assertIs<OneBotMessageSendTarget.Group>(destination))
                    } else {
                        assertEquals(OneBotMessageSendTarget.privateTarget(id), assertIs<OneBotMessageSendTarget.Private>(destination))
                    }
                }
                calls.take(3).forEach { assertTrue(it.options.isEmpty()) }
                calls.drop(3).forEach { assertEquals(listOf(sendOption), it.options) }
                assertEquals("original", assertIs<InteractionMessage.Text>(calls[0].originalMessage).text)
                assertSame(message, assertIs<InteractionMessage.Message>(calls[1].originalMessage).message)
                assertSame(content, assertIs<InteractionMessage.MessageContent>(calls[2].originalMessage).messageContent)
            }
            assertEquals(18, requests.size)
            requests.forEach { assertEquals("changed", assertIs<OneBotMessageOutgoing.StringValue>(it.message).value) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 各重载保留调用来源与传入选项，群回复由终端补全引用。
     */
    @Test
    fun repliesRetainSourceAndPlatformReferenceAcrossAllOverloads() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val requests = mutableListOf<SendMsgApi.Body>()
        coEvery { bot.executeData<SendMsgResult>(any()) } coAnswers {
            requests += assertIs<SendMsgApi.Body>(assertIs<SendMsgApi>(firstArg()).body)
            SendMsgResult(114.ID)
        }
        val contexts = mutableListOf<MessageSendContext.Reply>()
        manager.onMessageSend { _, context, chain ->
            contexts += assertIs<MessageSendContext.Reply>(context)
            chain.proceed(context.withMessage(InteractionMessage.valueOf("changed")))
        }
        val message: Message = Text { "original" }
        val content = mockk<MessageContent>()
        try {
            for ((index, replier) in repliers(bot).withIndex()) {
                val start = contexts.size
                replier.reply("original")
                replier.reply(message)
                replier.reply(content)
                replier.reply("original", replyOption)
                replier.reply(message, replyOption)
                replier.reply(content, replyOption)
                val calls = contexts.drop(start)
                assertEquals(6, calls.size)
                calls.forEach {
                    assertSame(replier, it.source)
                    val destination = assertIs<OneBotMessageReplyContext>(it).destination
                    if (index < 4) {
                        val id = assertIs<OneBotGroupMessageEventImpl>(replier).sourceEvent.groupId
                        assertEquals(OneBotMessageSendTarget.group(id), assertIs<OneBotMessageSendTarget.Group>(destination))
                    } else {
                        val id = assertIs<OneBotPrivateMessageEventImpl>(replier).sourceEvent.userId
                        assertEquals(OneBotMessageSendTarget.privateTarget(id), assertIs<OneBotMessageSendTarget.Private>(destination))
                    }
                }
                calls.take(3).forEach { assertTrue(it.options.isEmpty()) }
                calls.drop(3).forEach { assertEquals(listOf(replyOption), it.options) }
                for (request in requests.takeLast(6)) {
                    if (index < 4) {
                        val segments = assertIs<OneBotMessageOutgoing.SegmentsValue>(request.message).segments
                        val source = assertIs<OneBotGroupMessageEventImpl>(replier)
                        assertEquals(source.sourceEvent.messageId, assertIs<OneBotReply>(segments.first()).data.id)
                        assertEquals("changed", assertIs<OneBotText>(segments.last()).data.text)
                    } else {
                        assertEquals("changed", assertIs<OneBotMessageOutgoing.StringValue>(request.message).value)
                    }
                }
            }
            assertEquals(42, requests.size)
        } finally {
            bot.cancel()
        }
    }


    /**
     * 显式引用优先，清空选项后回到群回复默认引用，重复引用在请求前报错。
     */
    @Test
    fun replyOptionsOverrideAndFallBackToTheDefaultReference() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val requests = mutableListOf<SendMsgApi.Body>()
        coEvery { bot.executeData<SendMsgResult>(any()) } coAnswers {
            requests += assertIs<SendMsgApi.Body>(assertIs<SendMsgApi>(firstArg()).body)
            SendMsgResult(114.ID)
        }
        val callerQuote = OneBotMessageReplyOption.quote(41.ID)
        val interceptorQuote = OneBotMessageReplyOption.quote(42.ID)
        val trailingOption = object : ReplyOption {}
        val optionsSeen = mutableListOf<List<ReplyOption>>()
        var replacement: List<ReplyOption>? = null
        manager.onMessageSend { _, context, chain ->
            val reply = assertIs<OneBotMessageReplyContext>(context)
            optionsSeen += reply.options
            val options = replacement
            chain.proceed(if (options == null) reply else reply.withOptions(options))
        }
        try {
            val source = assertIs<OneBotGroupMessageEventImpl>(repliers(bot).first())
            source.reply("text", replyOption, callerQuote, trailingOption)
            replacement = listOf(interceptorQuote)
            source.reply("text", callerQuote)
            replacement = emptyList()
            source.reply("text", callerQuote)
            replacement = null
            assertFailsWith<IllegalArgumentException> {
                source.reply("text", callerQuote, interceptorQuote)
            }

            assertEquals(
                listOf(
                    listOf(replyOption, callerQuote, trailingOption),
                    listOf(callerQuote),
                    listOf(callerQuote),
                    listOf(callerQuote, interceptorQuote),
                ),
                optionsSeen,
            )
            val sentQuotes = requests.map {
                val segments = assertIs<OneBotMessageOutgoing.SegmentsValue>(it.message).segments
                assertIs<OneBotReply>(segments.first()).data.id
            }
            assertEquals(listOf(41.ID, 42.ID, source.sourceEvent.messageId), sentQuotes)
        } finally {
            bot.cancel()
        }
    }

    /**
     * 短路可返回同类型回执，且无需解析 MessageContent 或懒加载目标。
     */
    @Test
    fun shortCircuitSkipsMessageParsingAndTargetResolution() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val receipt = mockk<OneBotMessageReceipt>()
        val content = mockk<MessageContent>()
        var calls = 0
        manager.onMessageSend { _, _, _ ->
            calls++
            receipt
        }
        try {
            for (sender in senders(bot)) assertSame(receipt, sender.send(content))
            for (replier in repliers(bot)) assertSame(receipt, replier.reply(content))
            assertEquals(10, calls)
            coVerify(exactly = 0) { bot.executeData<Any>(any()) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 每个发送和回复入口均拒绝最终类型不匹配的短路回执。
     */
    @Test
    fun wrongReceiptTypeIsRejectedAcrossAllOverloads() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val wrongReceipt = mockk<MessageReceipt>()
        val content = mockk<MessageContent>()
        manager.onMessageSend { _, _, _ -> wrongReceipt }
        try {
            val operations: List<suspend () -> MessageReceipt> = senders(bot).flatMap { sender ->
                listOf<suspend () -> MessageReceipt>(
                    { sender.send("original") },
                    { sender.send(Text { "original" }) },
                    { sender.send(content) },
                    { sender.send("original", sendOption) },
                    { sender.send(Text { "original" }, sendOption) },
                    { sender.send(content, sendOption) },
                )
            } + repliers(bot).flatMap { replier ->
                listOf(
                    { replier.reply("original") },
                    { replier.reply(Text { "original" }) },
                    { replier.reply(content) },
                    { replier.reply("original", replyOption) },
                    { replier.reply(Text { "original" }, replyOption) },
                    { replier.reply(content, replyOption) },
                )
            }
            for (operation in operations) {
                val failure = assertFailsWith<IllegalStateException> { operation() }
                assertContains(assertNotNull(failure.message), "OneBotMessageReceipt")
            }
            coVerify(exactly = 0) { bot.executeData<Any>(any()) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 中间结果可由外层拦截器替换，仅完整链的最终回执接受入口类型校验。
     */
    @Test
    fun outerInterceptorCanReplaceAnIntermediateWrongReceipt() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val receipt = mockk<OneBotMessageReceipt>()
        val intermediateReceipt = mockk<MessageReceipt>()
        manager.onMessageSend { _, context, chain ->
            assertSame(intermediateReceipt, chain.proceed(context))
            receipt
        }
        manager.onMessageSend { _, _, _ -> intermediateReceipt }
        try {
            for (sender in senders(bot)) assertSame(receipt, sender.send("short circuit"))
            for (replier in repliers(bot)) assertSame(receipt, replier.reply("short circuit"))
            coVerify(exactly = 0) { bot.executeData<Any>(any()) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 平台专属消息段替换须直接进入发送终端。
     */
    @Test
    fun nativeSegmentsReplacementIsPreserved() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val requests = mutableListOf<SendMsgApi.Body>()
        coEvery { bot.executeData<SendMsgResult>(any()) } coAnswers {
            requests += assertIs<SendMsgApi.Body>(assertIs<SendMsgApi>(firstArg()).body)
            SendMsgResult(114.ID)
        }
        val segments = listOf(OneBotText.create("native"))
        manager.onMessageSend { _, context, chain ->
            chain.proceed(
                context.withMessage(OneBotSegmentsInteractionMessage.create(InteractionMessage.valueOf("ignored"), segments))
            )
        }
        try {
            senders(bot).first().send("original")
            repliers(bot).last().reply("original")
            assertEquals(2, requests.size)
            requests.forEach { assertEquals(segments, assertIs<OneBotMessageOutgoing.SegmentsValue>(it.message).segments) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 目标仅按需读取，替换消息或选项仍保留原回复事件。
     */
    @Test
    fun replyTargetIsLazyAndSurvivesContextReplacement() = runTest {
        val manager = InterceptorManager()
        val bot = bot(manager)
        val event = spyk(OneBotNormalGroupMessageEventImpl(null, mockk(relaxed = true), bot))
        val target = mockk<love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup>()
        coEvery { event.content() } returns target
        val receipt = mockk<OneBotMessageReceipt>()
        val registration = manager.onMessageSend { _, _, _ -> receipt }
        try {
            assertSame(receipt, event.reply("original"))
            coVerify(exactly = 0) { event.content() }
            registration.dispose()
            manager.onMessageSend { _, context, _ ->
                val reply = assertIs<MessageSendContext.Reply>(context)
                assertSame(event, reply.source)
                assertSame(target, reply.target())
                val replaced = reply.withMessage(InteractionMessage.valueOf("changed")).withOptions(listOf(replyOption))
                assertSame(event, replaced.source)
                assertSame(target, replaced.target())
                assertEquals(listOf(replyOption), replaced.options)
                receipt
            }
            assertSame(receipt, event.reply("original"))
            coVerify(exactly = 2) { event.content() }
            coVerify(exactly = 0) { bot.executeData<Any>(any()) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 用户异常与取消立即传播，后续拦截器和平台终端均不执行。
     */
    @Test
    fun failuresAndCancellationPropagateWithoutSending() = runTest {
        for (failure in listOf(IllegalStateException("rejected"), CancellationException("cancelled"))) {
            val manager = InterceptorManager()
            val bot = bot(manager)
            var downstreamCalls = 0
            manager.onMessageSend { _, _, _ -> throw failure }
            manager.onMessageSend { _, context, chain ->
                downstreamCalls++
                chain.proceed(context)
            }
            try {
                assertSame(failure, assertFails { senders(bot).first().send("original") })
                assertSame(failure, assertFails { repliers(bot).first().reply("original") })
                assertEquals(0, downstreamCalls)
                coVerify(exactly = 0) { bot.executeData<Any>(any()) }
            } finally {
                bot.cancel()
            }
        }
    }

    /**
     * 工厂、管理器与 Bot 必须共用 Application 的容器。
     */
    @Test
    fun applicationFactoryPassesTheLiveContainerToBots() = runTest {
        val app = launchSimpleApplication { useOneBot11() }
        try {
            val manager = assertNotNull(app.botManagers.firstOneBotBotManagerOrNull())
            val bot = manager.register(OneBotBotConfiguration().apply { botUniqueId = "factory" })
            assertSame(app.interceptorManager, assertIs<OneBotBotImpl>(bot).interceptorContainer)
        } finally {
            app.cancel()
        }
    }

    /**
     * 每个真实发送终端各投递一次通知，记录最终上下文与平台回执。
     */
    @Test
    fun successfulTerminalsNotifyWithActualContextAndReceipt() = runTest {
        val manager = InterceptorManager()
        val dispatcher = SimpleEventDispatcherImpl(SimpleEventDispatcherConfigurationImpl())
        val events = mutableListOf<MessageSentEvent>()
        dispatcher.process<MessageSentEvent> { events += it }
        val bot = bot(manager, dispatcher, StandardTestDispatcher(testScheduler))
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)
        val finalContexts = mutableListOf<MessageSendContext>()
        val terminalReceipts = mutableListOf<MessageReceipt>()
        val chainReceipt = mockk<OneBotMessageReceipt>()
        manager.onMessageSend { _, context, chain ->
            val changed = when (context) {
                is MessageSendContext.Send -> context
                    .withMessage(InteractionMessage.valueOf("sent"))
                    .withOptions(listOf(sendOption))
                is MessageSendContext.Reply -> context
                    .withMessage(InteractionMessage.valueOf("replied"))
                    .withOptions(listOf(replyOption))
            }
            finalContexts += changed
            terminalReceipts += assertNotNull(chain.proceed(changed))
            chainReceipt
        }
        try {
            for (source in senders(bot)) assertSame(chainReceipt, source.send("original"))
            for (source in repliers(bot)) assertSame(chainReceipt, source.reply("original"))
            assertTrue(events.isEmpty())
            runCurrent()
            assertEquals(10, events.size)
            assertEquals(10, events.map { it.id }.toSet().size)
            for ((index, event) in events.withIndex()) {
                assertSame(bot, event.bot)
                assertSame(finalContexts[index], event.context)
                assertSame(terminalReceipts[index], event.receipt)
                assertNotSame(chainReceipt, event.receipt)
                assertEquals("original", assertIs<InteractionMessage.Text>(event.context.originalMessage).text)
            }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 短路返回与平台失败均不代表真实发送成功，因此没有成功通知。
     */
    @Test
    fun shortCircuitAndPlatformFailureDoNotNotify() = runTest {
        val manager = InterceptorManager()
        val dispatcher = SimpleEventDispatcherImpl(SimpleEventDispatcherConfigurationImpl())
        val events = mutableListOf<MessageSentEvent>()
        dispatcher.process<MessageSentEvent> { events += it }
        val bot = bot(manager, dispatcher, StandardTestDispatcher(testScheduler))
        val receipt = mockk<OneBotMessageReceipt>()
        val registration = manager.onMessageSend { _, _, _ -> receipt }
        try {
            assertSame(receipt, senders(bot).first().send("short circuit"))
            assertSame(receipt, repliers(bot).first().reply("short circuit"))
            runCurrent()
            assertTrue(events.isEmpty())
            registration.dispose()
            val failure = IllegalStateException("platform failed")
            coEvery { bot.executeData<SendMsgResult>(any()) } throws failure
            assertSame(failure, assertFails { senders(bot).first().send("failed") })
            assertSame(failure, assertFails { repliers(bot).first().reply("failed") })
            runCurrent()
            assertTrue(events.isEmpty())
        } finally {
            bot.cancel()
        }
    }

    /**
     * 平台已成功后的外层异常不能抹去已经发生的发送事实。
     */
    @Test
    fun outerInterceptorFailureStillNotifiesAfterTerminalSuccess() = runTest {
        val manager = InterceptorManager()
        val dispatcher = SimpleEventDispatcherImpl(SimpleEventDispatcherConfigurationImpl())
        val events = mutableListOf<MessageSentEvent>()
        dispatcher.process<MessageSentEvent> { events += it }
        val bot = bot(manager, dispatcher, StandardTestDispatcher(testScheduler))
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)
        val failure = IllegalStateException("after send")
        val receipts = mutableListOf<MessageReceipt>()
        manager.onMessageSend { _, context, chain ->
            receipts += assertNotNull(chain.proceed(context))
            throw failure
        }
        try {
            assertSame(failure, assertFails { senders(bot).first().send("sent") })
            assertSame(failure, assertFails { repliers(bot).first().reply("replied") })
            runCurrent()
            assertEquals(2, events.size)
            assertSame(receipts[0], events[0].receipt)
            assertSame(receipts[1], events[1].receipt)
        } finally {
            bot.cancel()
        }
    }

    /**
     * 发送成功后返回错误类型仍失败，但成功通知保留真实平台回执。
     */
    @Test
    fun wrongReceiptAfterTerminalSuccessStillNotifies() = runTest {
        val manager = InterceptorManager()
        val dispatcher = SimpleEventDispatcherImpl(SimpleEventDispatcherConfigurationImpl())
        val events = mutableListOf<MessageSentEvent>()
        dispatcher.process<MessageSentEvent> { events += it }
        val bot = bot(manager, dispatcher, StandardTestDispatcher(testScheduler))
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)
        val terminalReceipts = mutableListOf<MessageReceipt>()
        val wrongReceipt = mockk<MessageReceipt>()
        manager.onMessageSend { _, context, chain ->
            terminalReceipts += assertNotNull(chain.proceed(context))
            wrongReceipt
        }
        try {
            for (sender in senders(bot)) {
                assertFailsWith<IllegalStateException> { sender.send("sent") }
            }
            for (replier in repliers(bot)) {
                assertFailsWith<IllegalStateException> { replier.reply("replied") }
            }
            runCurrent()
            assertEquals(10, events.size)
            for ((index, event) in events.withIndex()) {
                assertSame(terminalReceipts[index], event.receipt)
                assertNotSame(wrongReceipt, event.receipt)
            }
            coVerify(exactly = 10) { bot.executeData<SendMsgResult>(any()) }
        } finally {
            bot.cancel()
        }
    }

    /**
     * 通知异步投递，监听器挂起或失败均不改变已成功的发送结果。
     */
    @Test
    fun notificationListenerFailureDoesNotAffectTheSendResult() = runTest {
        val manager = InterceptorManager()
        val dispatcher = SimpleEventDispatcherImpl(SimpleEventDispatcherConfigurationImpl())
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        dispatcher.process<MessageSentEvent> {
            entered.complete(Unit)
            release.await()
            error("notification listener failed")
        }
        var remainingListenerCalls = 0
        dispatcher.process<MessageSentEvent> { remainingListenerCalls++ }
        val bot = bot(manager, dispatcher, StandardTestDispatcher(testScheduler))
        coEvery { bot.executeData<SendMsgResult>(any()) } returns SendMsgResult(114.ID)
        try {
            val receipt = assertIs<OneBotMessageReceipt>(senders(bot).first().send("sent"))
            assertEquals(114.ID, receipt.messageId)
            assertFalse(entered.isCompleted)
            runCurrent()
            assertTrue(entered.isCompleted)
            assertEquals(0, remainingListenerCalls)
            release.complete(Unit)
            runCurrent()
            assertEquals(1, remainingListenerCalls)
            assertTrue(bot.isActive)
        } finally {
            bot.cancel()
        }
    }
}
