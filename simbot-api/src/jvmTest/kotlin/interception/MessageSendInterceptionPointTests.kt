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
package interception

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.*
import love.forte.simbot.message.MessageReceipt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * 验证消息发送拦截点的上下文传递、终端调用及重复继续执行行为。
 */
class MessageSendInterceptionPointTests {
    /**
     * 模拟组件入口承诺的具体回执类型。
     */
    private interface ExpectedReceipt : MessageReceipt

    /**
     * 模拟组件增加回复元数据，并通过协变复制保留其具体类型。
     */
    private class ComponentReply private constructor(
        bot: Bot,
        override val source: Any,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
        override val options: List<ReplyOption>,
        val metadata: Any,
        private val resolveTarget: suspend () -> Any?,
    ) : MessageSendContext.Reply(bot, originalMessage, message) {
        constructor(
            bot: Bot,
            source: Any,
            message: InteractionMessage,
            options: List<ReplyOption>,
            metadata: Any,
            target: suspend () -> Any?,
        ) : this(bot, source, message, message, options.toList(), metadata, target)

        override val destination: Any get() = source

        override suspend fun target(): Any? = resolveTarget()

        override fun withMessage(message: InteractionMessage): ComponentReply =
            ComponentReply(bot, source, originalMessage, message, options, metadata, resolveTarget)

        override fun withOptions(options: Collection<ReplyOption>): ComponentReply =
            ComponentReply(bot, source, originalMessage, message, options.toList(), metadata, resolveTarget)
    }

    private val bot = mockk<Bot>()
    private val sender = mockk<SendSupport>()
    private val replier = mockk<ReplySupport>()
    private val receipt = mockk<MessageReceipt>()
    private val expectedReceipt = mockk<ExpectedReceipt>()
    private val original = InteractionMessage.valueOf("original")
    private val changed = InteractionMessage.valueOf("changed")

    /**
     * 经抽象接口连续替换消息和选项后，组件回复类型、元数据与惰性目标仍保留。
     */
    @Test
    fun componentReplyPreservesFieldsAcrossInterceptors() = runTest {
        val manager = InterceptorManager()
        val source = Any()
        val target = Any()
        val metadata = Any()
        val option = object : ReplyOption {}
        val initialOptions = mutableListOf(option)
        var resolutions = 0
        val initial = ComponentReply(bot, source, original, initialOptions, metadata) {
            resolutions++
            target
        }
        initialOptions.clear()
        manager.onMessageSend(-1) { _, context, chain ->
            val reply = context as MessageSendContext.Reply
            assertEquals(listOf(option), reply.options)
            chain.proceed(reply.withMessage(changed))
        }
        manager.onMessageSend(1) { _, context, chain ->
            val replacement = mutableListOf(option)
            val updated = (context as MessageSendContext.Reply).withOptions(replacement)
            replacement.clear()
            chain.proceed(updated)
        }

        assertSame(
            expectedReceipt, MessageSendInterceptionPoint.invokeWithReceipt<ExpectedReceipt>(
                manager, initial,
            ) { context ->
                val reply = kotlin.test.assertIs<ComponentReply>(context)
                assertSame(metadata, reply.metadata)
                assertSame(bot, reply.bot)
                assertSame(source, reply.source)
                assertSame(original, reply.originalMessage)
                assertSame(changed, reply.message)
                assertEquals(listOf(option), reply.options)
                assertEquals(0, resolutions)
                assertSame(target, reply.target())
                expectedReceipt
            })
        assertEquals(1, resolutions)
        assertSame(original, initial.message)
    }

    /**
     * 默认回复替换选项时复制列表，保留已经替换的消息及目标解析。
     */
    @Test
    fun defaultReplyOptionsCopyPreservesMessageAndTarget() = runTest {
        val option = object : ReplyOption {}
        val options = mutableListOf(option)
        var resolutions = 0
        val initial = TestReply(bot, replier, original) {
            resolutions++
            sender
        }
        val replaced = initial.withMessage(changed).withOptions(options)
        options.clear()

        assertEquals(0, resolutions)
        assertSame(original, replaced.originalMessage)
        assertSame(changed, replaced.message)
        assertSame(replier, replaced.source)
        assertEquals(listOf(option), replaced.options)
        assertSame(sender, replaced.target())
        assertEquals(1, resolutions)
    }

    /**
     * 类型正确的短路可直接返回，无需进入发送终端。
     */
    @Test
    fun typedShortCircuitReturnsCompatibleReceipt() = runTest {
        val manager = InterceptorManager()
        val expected = mockk<ExpectedReceipt>()
        manager.register(MessageSendInterceptionPoint) { _, _, _ -> expected }

        val result: ExpectedReceipt = MessageSendInterceptionPoint.invokeWithReceipt<ExpectedReceipt>(
            manager, TestSend(bot, sender, original),
        ) { error("target reached") }

        assertSame(expected, result)
    }

    /**
     * 类型错误的短路在链出口报错，并给出预期与实际类型。
     */
    @Test
    fun typedShortCircuitRejectsIncompatibleReceipt() = runTest {
        val manager = InterceptorManager()
        manager.register(MessageSendInterceptionPoint) { _, _, _ -> receipt }

        val failure = assertFailsWith<IllegalStateException> {
            MessageSendInterceptionPoint.invokeWithReceipt<ExpectedReceipt>(
                manager, TestSend(bot, sender, original),
            ) { error("target reached") }
        }

        kotlin.test.assertTrue(failure.message.orEmpty().contains("ExpectedReceipt"))
        kotlin.test.assertTrue(failure.message.orEmpty().contains("returned"))
    }

    /**
     * 中间层和终端无需满足最终类型，外层仍可修正结果。合法替换不要求同一实例。
     */
    @Test
    fun onlyFinalReceiptIsValidated() = runTest {
        val manager = InterceptorManager()
        val expected = mockk<ExpectedReceipt>()
        var targetCalls = 0
        manager.register(MessageSendInterceptionPoint, -1) { _, context, chain ->
            assertSame(expectedReceipt, chain.proceed(context))
            expected
        }
        manager.register(MessageSendInterceptionPoint, 1) { _, context, chain ->
            chain.proceed(context)
        }

        val result = MessageSendInterceptionPoint.invokeWithReceipt<ExpectedReceipt>(
            manager, TestSend(bot, sender, original),
        ) {
            targetCalls++
            expectedReceipt
        }

        assertSame(expected, result)
        assertEquals(1, targetCalls)
    }

    /**
     * 终端成功后外层替换为错误类型，仍在链出口拒绝，已发生的发送不回滚。
     */
    @Test
    fun incompatibleFinalReceiptFailsAfterSuccessfulTerminal() = runTest {
        val manager = InterceptorManager()
        val expected = mockk<ExpectedReceipt>()
        var targetCalls = 0
        manager.register(MessageSendInterceptionPoint) { _, context, chain ->
            assertSame(expected, chain.proceed(context))
            receipt
        }

        assertFailsWith<IllegalStateException> {
            MessageSendInterceptionPoint.invokeWithReceipt<ExpectedReceipt>(
                manager, TestSend(bot, sender, original),
            ) {
                targetCalls++
                expected
            }
        }
        assertEquals(1, targetCalls)
    }

    /**
     * 发送消息替换保留来源与原始消息，并隔离调用方列表的后续修改。
     */
    @Test
    fun sendMessageReplacementPreservesOriginalSourceAndOptions() = runTest {
        val option = object : SendOption {}
        val options = mutableListOf(option)
        val initial = TestSend(bot, sender, original, options)
        options.clear()

        val replaced = initial.withMessage(changed)

        assertSame(bot, replaced.bot)
        assertSame(sender, replaced.source)
        assertSame(sender, replaced.target())
        assertSame(original, replaced.originalMessage)
        assertSame(changed, replaced.message)
        assertSame(original, initial.message)
        assertEquals(listOf(option), replaced.options)
        assertSame(initial.options, replaced.options)
    }

    /**
     * 直发复制保留独立来源与惰性目标，隔离选项列表且不丢失原始消息。
     */
    @Test
    fun directSendCopiesPreserveSourceAndLazyTarget() = runTest {
        val source = Any()
        val target = Any()
        val option = object : SendOption {}
        val options = mutableListOf(option)
        var resolutions = 0
        val initial = TestSend(bot, source, original, options) {
            resolutions++
            target
        }
        options.clear()
        val replacementOptions = mutableListOf<SendOption>(option)
        val replaced = initial.withMessage(changed).withOptions(replacementOptions)
        replacementOptions.clear()

        assertEquals(0, resolutions)
        assertSame(bot, replaced.bot)
        assertSame(source, replaced.source)
        assertSame(original, replaced.originalMessage)
        assertSame(changed, replaced.message)
        assertEquals(listOf(option), initial.options)
        assertEquals(listOf(option), replaced.options)
        assertSame(target, replaced.target())
        assertEquals(1, resolutions)
    }

    /**
     * 直发短路不解析目标，也不执行平台发送。
     */
    @Test
    fun directSendShortCircuitDoesNotResolveTarget() = runTest {
        val manager = InterceptorManager()
        manager.register(MessageSendInterceptionPoint) { _, _, _ -> expectedReceipt }
        val context = TestSend(bot, Any(), original) {
            error("target resolved")
        }

        assertSame(
            expectedReceipt, MessageSendInterceptionPoint.invokeWithReceipt<ExpectedReceipt>(
                manager, context,
            ) { error("terminal reached") })
    }

    /**
     * 回复上下文与其副本均延迟目标解析，保留回复来源及原始选项。
     */
    @Test
    fun replyMessageReplacementPreservesLazyTargetAndOptions() = runTest {
        val option = object : ReplyOption {}
        val options = mutableListOf(option)
        var resolutions = 0
        val initial = TestReply(bot, replier, original, options) {
            resolutions++
            sender
        }
        val replaced = initial.withMessage(changed)
        options.clear()

        assertEquals(0, resolutions)
        assertSame(bot, replaced.bot)
        assertSame(replier, replaced.source)
        assertSame(original, replaced.originalMessage)
        assertSame(changed, replaced.message)
        assertSame(original, initial.message)
        assertEquals(listOf(option), replaced.options)
        assertSame(initial.options, replaced.options)
        assertSame(sender, replaced.target())
        assertEquals(1, resolutions)
    }

    /**
     * 优先级和注册顺序决定嵌套次序，实际发送收到修改后的消息与选项。
     */
    @Test
    fun orderedInterceptorsPassModifiedMessageAndOptionsToSend() = runTest {
        val manager = InterceptorManager()
        val trace = mutableListOf<String>()
        val initialOption = object : SendOption {}
        val changedOption = object : SendOption {}
        coEvery { sender.send("changed", changedOption) } returns receipt
        manager.register(MessageSendInterceptionPoint, 10) { _, context, chain ->
            trace += "late-before"
            chain.proceed(context).also { trace += "late-after" }
        }
        manager.register(MessageSendInterceptionPoint, -10) { _, context, chain ->
            trace += "first-before"
            val send = context as MessageSendContext.Send
            chain.proceed(send.withMessage(changed).withOptions(listOf(changedOption)))
                .also { trace += "first-after" }
        }
        manager.register(MessageSendInterceptionPoint, -10) { _, context, chain ->
            trace += "second-before"
            chain.proceed(context).also { trace += "second-after" }
        }

        val result = MessageSendInterceptionPoint.invoke(
            manager,
            TestSend(bot, sender, original, listOf(initialOption)),
        ) { context ->
            trace += "target"
            val send = context as MessageSendContext.Send
            assertSame(original, send.originalMessage)
            (send.source as SendSupport).send(
                (send.message as InteractionMessage.Text).text,
                *send.options.toTypedArray()
            )
        }

        assertSame(receipt, result)
        assertEquals(
            listOf(
                "first-before",
                "second-before",
                "late-before",
                "target",
                "late-after",
                "second-after",
                "first-after"
            ),
            trace,
        )
        coVerify(exactly = 1) { sender.send("changed", changedOption) }
    }

    /**
     * 回复保留独立来源，终端使用下游消息及回复选项，并自行决定解析目标。
     */
    @Test
    fun replyTerminalReceivesModifiedMessageAndOptions() = runTest {
        val manager = InterceptorManager()
        val initialOption = object : ReplyOption {}
        val changedOption = object : ReplyOption {}
        var resolutions = 0
        val context = TestReply(bot, replier, original, listOf(initialOption)) {
            resolutions++
            sender
        }
        coEvery { replier.reply("changed", changedOption) } returns receipt
        manager.register(MessageSendInterceptionPoint) { _, current, chain ->
            assertEquals(0, resolutions)
            val reply = current as MessageSendContext.Reply
            chain.proceed(reply.withMessage(changed).withOptions(listOf(changedOption)))
        }

        val result = MessageSendInterceptionPoint.invoke(manager, context) { current ->
            val reply = current as MessageSendContext.Reply
            assertSame(sender, reply.target())
            assertSame(replier, reply.source)
            assertSame(original, reply.originalMessage)
            (reply.source as ReplySupport).reply(
                (reply.message as InteractionMessage.Text).text,
                *reply.options.toTypedArray()
            )
        }

        assertSame(receipt, result)
        assertEquals(1, resolutions)
        coVerify(exactly = 1) { replier.reply("changed", changedOption) }
    }

    /**
     * 策略拒绝保持原异常，不解析回复目标或进入终端。
     */
    @Test
    fun rejectionDoesNotResolveOrCallTarget() = runTest {
        val manager = InterceptorManager()
        val failure = RuntimeException("blocked")
        var resolutions = 0
        var targetCalls = 0
        val context = TestReply(bot, replier, original) {
            resolutions++
            sender
        }
        manager.register(MessageSendInterceptionPoint) { _, _, _ -> throw failure }

        val thrown = assertFailsWith<RuntimeException> {
            MessageSendInterceptionPoint.invoke(manager, context) {
                targetCalls++
                it.target()
                receipt
            }
        }

        assertSame(failure, thrown)
        assertEquals(0, resolutions)
        assertEquals(0, targetCalls)
    }

    /**
     * 协程取消保持原异常，不解析回复目标或进入终端。
     */
    @Test
    fun cancellationDoesNotResolveOrCallTarget() = runTest {
        val manager = InterceptorManager()
        val cancellation = CancellationException("cancelled")
        var resolutions = 0
        var targetCalls = 0
        val context = TestReply(bot, replier, original) {
            resolutions++
            sender
        }
        manager.register(MessageSendInterceptionPoint) { _, _, _ -> throw cancellation }

        val thrown = assertFailsWith<CancellationException> {
            MessageSendInterceptionPoint.invoke(manager, context) {
                targetCalls++
                it.target()
                receipt
            }
        }

        assertSame(cancellation, thrown)
        assertEquals(0, resolutions)
        assertEquals(0, targetCalls)
    }

    /**
     * 重复继续执行不做强制校验，每次都向终端传递本次消息。
     */
    @Test
    fun repeatedProceedReexecutesTheSend() = runTest {
        val manager = InterceptorManager()
        coEvery { sender.send("original", *emptyArray<SendOption>()) } returns receipt
        coEvery { sender.send("changed", *emptyArray<SendOption>()) } returns receipt
        manager.register(MessageSendInterceptionPoint) { _, context, chain ->
            val first = chain.proceed(context)
            assertSame(receipt, chain.proceed(context.withMessage(changed)))
            first
        }

        assertSame(
            receipt, MessageSendInterceptionPoint.invoke(
                manager,
                TestSend(bot, sender, original),
            ) { sender.send((it.message as InteractionMessage.Text).text, *emptyArray<SendOption>()) })
        coVerify(exactly = 1) { sender.send("original", *emptyArray<SendOption>()) }
        coVerify(exactly = 1) { sender.send("changed", *emptyArray<SendOption>()) }
    }

    /**
     * 捕获失败后重复继续执行会再次调用终端，并保留终端异常。
     */
    @Test
    fun failedProceedCanReexecuteTheSend() = runTest {
        val manager = InterceptorManager()
        val failure = IllegalArgumentException("platform failure")
        coEvery { sender.send("original", *emptyArray<SendOption>()) } throws failure
        manager.register(MessageSendInterceptionPoint) { _, context, chain ->
            assertSame(failure, assertFailsWith<IllegalArgumentException> { chain.proceed(context) })
            chain.proceed(context)
        }

        assertSame(failure, assertFailsWith<IllegalArgumentException> {
            MessageSendInterceptionPoint.invoke(
                manager,
                TestSend(bot, sender, original),
            ) { sender.send((it.message as InteractionMessage.Text).text, *emptyArray<SendOption>()) }
        })
        coVerify(exactly = 2) { sender.send("original", *emptyArray<SendOption>()) }
    }

    /**
     * 通用执行入口将替换后的上下文交给终端处理。
     */
    @Test
    fun replacementContextIsPassedToTarget() = runTest {
        val manager = InterceptorManager()
        val context = TestSend(bot, sender, original)
        val replacement = TestReply(bot, replier, changed)
        var targetCalls = 0
        manager.register(MessageSendInterceptionPoint) { _, _, chain ->
            chain.proceed(replacement)
        }

        assertSame(receipt, MessageSendInterceptionPoint.invoke(manager, context) {
            assertSame(replacement, it)
            targetCalls++
            receipt
        })
        assertEquals(1, targetCalls)
    }


    @Test
    fun changedIdentityIsPassedToTarget() = runTest {
        val initial = TestSend(bot, sender, original)
        val invalid = listOf(
            TestSend(mockk<Bot>(), sender, original),
            TestSend(bot, Any(), original),
            TestSend(bot, sender, changed),
            TestSend(bot, sender, original, destination = Any()),
        )
        for (replacement in invalid) {
            val manager = InterceptorManager()
            manager.onMessageSend { _, _, chain -> chain.proceed(replacement) }
            var calls = 0
            assertSame(receipt, MessageSendInterceptionPoint.invoke(manager, initial) {
                assertSame(replacement, it)
                calls++
                receipt
            })
            assertEquals(1, calls)
        }
    }

    /**
     * 拦截器可直接返回回执短路，不触达后续拦截器、目标解析或终端。
     */
    @Test
    fun shortCircuitDoesNotResolveOrCallTarget() = runTest {
        val manager = InterceptorManager()
        var resolutions = 0
        manager.register(MessageSendInterceptionPoint, -1) { _, _, _ -> receipt }
        manager.register(MessageSendInterceptionPoint, 1) { _, _, _ -> error("downstream reached") }
        val context = TestReply(bot, replier, original) {
            resolutions++
            sender
        }

        assertSame(receipt, MessageSendInterceptionPoint.invoke(manager, context) { error("target reached") })
        assertEquals(0, resolutions)
    }

    /**
     * 已物化的注册视图在注销后仍直接放行，终端只执行一次。
     */
    @Test
    fun disposedMaterializedRegistrationBypassesItsInterceptor() = runTest {
        val manager = InterceptorManager()
        val registration = manager.register(MessageSendInterceptionPoint) { _, _, _ ->
            error("disposed interceptor reached")
        }
        // 绕过查询时过滤，确保验证的是已获取执行视图的失效检查。
        val registrations = manager.interceptors(MessageSendInterceptionPoint).toList()
        val snapshot = mockk<InterceptorContainer>()
        every { snapshot.interceptors(MessageSendInterceptionPoint) } returns registrations.asSequence()
        registration.dispose()
        coEvery { sender.send("original", *emptyArray<SendOption>()) } returns receipt

        assertSame(
            receipt, MessageSendInterceptionPoint.invoke(
                snapshot,
                TestSend(bot, sender, original),
            ) { sender.send((it.message as InteractionMessage.Text).text, *emptyArray<SendOption>()) })
        coVerify(exactly = 1) { sender.send("original", *emptyArray<SendOption>()) }
    }
}

private class TestSend private constructor(
    bot: Bot,
    override val source: Any,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    override val options: List<SendOption>,
    override val destination: Any,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Send(bot, originalMessage, message) {
    constructor(
        bot: Bot,
        source: Any,
        message: InteractionMessage,
        options: List<SendOption> = emptyList(),
        destination: Any = source,
        target: suspend () -> Any? = { source },
    ) : this(bot, source, message, message, options.toList(), destination, target)

    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): TestSend =
        TestSend(bot, source, originalMessage, message, options, destination, resolveTarget)

    override fun withOptions(options: Collection<SendOption>): TestSend =
        TestSend(bot, source, originalMessage, message, options.toList(), destination, resolveTarget)
}

private class TestReply private constructor(
    bot: Bot,
    override val source: Any,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    override val options: List<ReplyOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Reply(bot, originalMessage, message) {
    constructor(
        bot: Bot,
        source: Any,
        message: InteractionMessage,
        options: List<ReplyOption> = emptyList(),
        target: suspend () -> Any? = { null },
    ) : this(bot, source, message, message, options.toList(), target)

    override val destination: Any get() = source

    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): TestReply =
        TestReply(bot, source, originalMessage, message, options, resolveTarget)

    override fun withOptions(options: Collection<ReplyOption>): TestReply =
        TestReply(bot, source, originalMessage, message, options.toList(), resolveTarget)
}
