/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import love.forte.simbot.ability.*
import love.forte.simbot.application.interceptors
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGTextChannelImpl
import love.forte.simbot.component.qguild.internal.dms.QGDmsContactImpl
import love.forte.simbot.component.qguild.internal.event.*
import love.forte.simbot.component.qguild.internal.friend.idFriend
import love.forte.simbot.component.qguild.internal.group.idGroup
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.onMessageSend
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.qguild.event.GroupAtMessageCreate
import love.forte.simbot.qguild.event.GroupMessageCreate
import love.forte.simbot.qguild.model.*
import kotlin.test.*

/**
 * 验证 QQ 各标准发送和回复入口的短路、选项、异常及调用次数。
 */
class QGMessageSendInterceptionTests : AbstractInteractionTests() {
    private val receipt = object : QGMessageReceipt {
        override suspend fun delete(vararg options: DeleteOption) = Unit
    }

    private val sendOption = object : SendOption {}
    private val replyOption = object : ReplyOption {}

    private fun sendSources(bot: QGBotImpl): List<SendSupport> = listOf(
        idGroup(bot, "group".ID),
        idFriend(bot, "friend".ID),
        QGDmsContactImpl(bot, User("user", "user"), "guild", null),
        QGTextChannelImpl(
            bot,
            SimpleChannel("channel", "guild", "channel", ChannelType.TEXT, ChannelSubType.SMALL_TALK, 0, "0", "0"),
        ),
        QGMemberImpl(
            bot,
            SimpleMember(User("member", "member"), "member", emptyList(), "2023-11-06T13:37:18+08:00"),
            "guild".ID,
        ),
    )

    private fun replySources(bot: QGBotImpl): List<ReplySupport> {
        val message = Json.decodeFromString(
            Message.serializer(),
            """{"id":"message","channel_id":"channel","guild_id":"guild","content":"received","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":"1"}"""
        )
        val groupData =
            """{"id":"message","author":{"member_openid":"member"},"content":"received","timestamp":"2023-11-06T13:37:18+08:00","group_openid":"group"}"""
        val c2cData =
            """{"id":"message","author":{"user_openid":"user"},"content":"received","timestamp":"2023-11-06T13:37:18+08:00"}"""
        return listOf(
            QGAtMessageCreateEventImpl(bot, "{}", message, "event"),
            QGDirectMessageCreateEventImpl(bot, "{}", message, "event"),
            QGGroupMessageCreateEventImpl(
                bot,
                "{}",
                Json.decodeFromString(
                    GroupMessageCreate.serializer(),
                    """{"op":0,"id":"event","s":1,"d":$groupData}"""
                ),
                "event",
            ),
            QGGroupAtMessageCreateEventImpl(
                bot,
                "{}",
                Json.decodeFromString(
                    GroupAtMessageCreate.serializer(),
                    """{"op":0,"id":"event","s":1,"d":$groupData}"""
                ),
                "event",
            ),
            QGC2CMessageCreateEventImpl(
                bot,
                "{}",
                Json.decodeFromString(C2CMessageCreate.serializer(), """{"op":0,"id":"event","s":1,"d":$c2cData}"""),
                "event",
            ),
        )
    }

    /**
     * 所有重载只进入一次链，接受 QQ 类型短路回执且不解析网络目标。
     */
    @Test
    fun allStandardOverloadsCanShortCircuitWithoutRequests() = runTest {
        val app = app { error("A short-circuited operation must not make requests") }
        val bot = app.bot() as QGBotImpl
        val contexts = mutableListOf<MessageSendContext>()
        val content = QGGroupAndC2CMessageContentImpl("content".ID, "received", emptyList())
        try {
            app.interceptors {
                onMessageSend { _, context, _ ->
                    contexts.add(context)
                    receipt
                }
            }
            sendSources(bot).forEach { source ->
                val before = contexts.size
                assertSame(receipt, source.send("text"))
                assertSame(receipt, source.send(Text { "text" }))
                assertSame(receipt, source.send(content))
                assertSame(receipt, source.send("text", sendOption))
                assertSame(receipt, source.send(Text { "text" }, sendOption))
                assertSame(receipt, source.send(content, sendOption))
                val captured = contexts.drop(before)
                assertEquals(6, captured.size)
                captured.forEach {
                    assertSame(bot, it.bot)
                    assertSame(source, it.source)
                    assertSame(source, it.target())
                    assertSame(it.originalMessage, it.message)
                }
                captured.takeLast(3)
                    .forEach { assertEquals(listOf(sendOption), assertIs<MessageSendContext.Send>(it).options) }
            }
            replySources(bot).forEach { source ->
                val before = contexts.size
                assertSame(receipt, source.reply("text"))
                assertSame(receipt, source.reply(Text { "text" }))
                assertSame(receipt, source.reply(content))
                assertSame(receipt, source.reply("text", replyOption))
                assertSame(receipt, source.reply(Text { "text" }, replyOption))
                assertSame(receipt, source.reply(content, replyOption))
                val captured = contexts.drop(before)
                assertEquals(6, captured.size)
                captured.forEach {
                    assertSame(source, it.source)
                    assertIs<MessageSendContext.Reply>(it)
                }
                captured.takeLast(3)
                    .forEach { assertEquals(listOf(replyOption), assertIs<MessageSendContext.Reply>(it).options) }
            }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 具体回执入口拒绝错误类型，但允许外层替换内层结果后再通过出口校验。
     */
    @Test
    fun typedReceiptsAreCheckedAfterTheCompleteChainReturns() = runTest {
        val app = app { error("A short-circuited operation must not make requests") }
        val bot = app.bot() as QGBotImpl
        val channel = sendSources(bot).filterIsInstance<QGTextChannelImpl>().single()
        val replySource = replySources(bot).filterIsInstance<QGC2CMessageCreateEventImpl>().single()
        val wrongReceipt = object : MessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) = Unit
        }
        var replaceResult = false
        var downstreamCalls = 0
        try {
            app.interceptors {
                onMessageSend { _, context, chain ->
                    val result = chain.proceed(context)
                    if (replaceResult) receipt else result
                }
                onMessageSend { _, _, _ ->
                    downstreamCalls++
                    wrongReceipt
                }
            }
            val failure = assertFailsWith<IllegalStateException> { channel.send("text") }
            assertContains(failure.message.orEmpty(), "QGMessageReceipt")
            assertFailsWith<IllegalStateException> { channel.send(Text { "text" }, sendOption) }
            assertFailsWith<IllegalStateException> { replySource.reply("text") }
            assertFailsWith<IllegalStateException> { replySource.reply(Text { "text" }, replyOption) }
            assertFailsWith<IllegalStateException> { idGroup(bot, "group".ID).send("text") }
            assertFailsWith<IllegalStateException> {
                replySources(bot).filterIsInstance<QGGroupMessageCreateEventImpl>().single().reply("text")
            }

            // 内层结果无需满足入口类型；仅完整链的最终结果需要满足它。
            replaceResult = true
            val sent: QGMessageReceipt = channel.send("text")
            val replied: QGMessageReceipt = replySource.reply("text")
            assertSame(receipt, sent)
            assertSame(receipt, replied)
            assertEquals(8, downstreamCalls)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 拦截异常和取消保持原实例，后续链和发送终端不执行。
     */
    @Test
    fun interceptorFailuresAndCancellationPropagateUnchanged() = runTest {
        val app = app { error("A rejected operation must not make requests") }
        val bot = app.bot() as QGBotImpl
        val source = idGroup(bot, "group".ID)
        var failure: Throwable = IllegalStateException("rejected")
        var downstreamCalls = 0
        try {
            app.interceptors {
                onMessageSend { _, _, _ -> throw failure }
                onMessageSend { _, context, chain ->
                    downstreamCalls++
                    chain.proceed(context)
                }
            }
            assertSame(failure, assertFailsWith<IllegalStateException> { source.send("text") })
            failure = CancellationException("cancelled")
            assertSame(failure, assertFailsWith<CancellationException> { source.send("text") })
            assertEquals(0, downstreamCalls)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
