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

import io.ktor.client.engine.mock.*
import io.ktor.http.content.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
import love.forte.simbot.component.qguild.message.*
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

    /**
     * 频道成员和私信会话即使 ID 相同也有不同的目标语义。
     */
    @Test
    fun memberSendDestinationRetainsGuildAndMemberIds() = runTest {
        val app = app { error("A short-circuited operation must not make requests") }
        val bot = app.bot() as QGBotImpl
        try {
            app.interceptors {
                onMessageSend { _, context, _ ->
                    val target = assertIs<QGMessageSendTarget.Member>(
                        assertIs<QGMessageSendContext>(context).destination
                    )
                    assertEquals("guild".ID, target.guildId)
                    assertEquals("member".ID, target.memberId)
                    assertEquals("member".ID, target.id)
                    assertEquals(QGMessageSendTarget.member("guild".ID, "member".ID), target)
                    assertNotEquals(QGMessageSendTarget.member("other".ID, "member".ID), target)
                    assertNotEquals<QGMessageSendTarget>(QGMessageSendTarget.dms("member".ID), target)
                    receipt
                }
            }
            sendSources(bot).filterIsInstance<QGMemberImpl>().single().send("text")
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

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
                    .forEach { assertEquals(listOf(sendOption), assertIs<QGMessageSendContext>(it).options) }
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
                    assertIs<QGMessageReplyContext>(it)
                }
                captured.takeLast(3)
                    .forEach { assertEquals(listOf(replyOption), assertIs<MessageSendContext.Reply>(it).options.filterNot { option -> option is QGMessageReferenceOption }) }
            }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }


    @Test
    fun replyOptionsReplaceAndRemoveAutomaticReferences() = runTest {
        var clear = false
        var requests = 0
        val app = app { request ->
            requests++
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            if (clear) {
                assertTrue(body["message_reference"] == null || body["message_reference"] == kotlinx.serialization.json.JsonNull, body.toString())
            } else {
                assertEquals("replacement", body.getValue("message_reference").jsonObject.getValue("message_id").jsonPrimitive.content)
            }
            respondOk("""{"id":"sent","channel_id":"channel","guild_id":"guild","content":"text","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":1}""")
        }
        val bot = app.bot() as QGBotImpl
        try {
            app.interceptors {
                onMessageSend { _, context, chain ->
                    val reply = assertIs<QGMessageReplyContext>(context)
                    chain.proceed(reply.withOptions(if (clear) emptyList() else listOf(QGMessageReferenceOption.create("replacement".ID))))
                }
            }
            val sources = replySources(bot).take(2)
            for (source in sources) source.reply("text")
            clear = true
            for (source in sources) source.reply("text")
            assertEquals(4, requests)
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
