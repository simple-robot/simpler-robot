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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
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
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.onMessageSend
import love.forte.simbot.message.*
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.qguild.event.GroupAtMessageCreate
import love.forte.simbot.qguild.event.GroupMessageCreate
import love.forte.simbot.qguild.model.*
import love.forte.simbot.qguild.model.Message
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
                val reference = QGMessageReferenceOption.create("reference".ID)
                assertSame(receipt, source.reply("text"))
                assertSame(receipt, source.reply(Text { "text" }))
                assertSame(receipt, source.reply(content))
                assertSame(receipt, source.reply("text", replyOption, reference))
                assertSame(receipt, source.reply(Text { "text" }, replyOption, reference))
                assertSame(receipt, source.reply(content, replyOption, reference))
                val captured = contexts.drop(before)
                assertEquals(6, captured.size)
                captured.forEach {
                    assertSame(source, it.source)
                    assertIs<QGMessageReplyContext>(it)
                }
                captured.take(3).forEach { assertTrue(assertIs<QGMessageReplyContext>(it).options.isEmpty()) }
                captured.takeLast(3).forEach {
                    assertEquals(listOf(replyOption, reference), assertIs<QGMessageReplyContext>(it).options)
                }
            }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }


    /**
     * 校验真实请求中的引用回退与显式覆盖，包括没有默认引用的 C2C 入口。
     */
    @Test
    fun replyReferencesUseFinalExplicitOptionsBeforeFallingBackToSource() = runTest {
        val references = mutableListOf<String?>()
        val explicit = QGMessageReferenceOption.create("explicit".ID)
        val replacement = QGMessageReferenceOption.create("replacement".ID)
        val app = app { request ->
            val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            references += body["message_reference"]?.takeUnless { it == JsonNull }
                ?.jsonObject?.getValue("message_id")?.jsonPrimitive?.content
            if (request.url.encodedPath.startsWith("/v2/")) {
                respondOk("""{"id":"sent","timestamp":1698435200}""")
            } else {
                respondOk("""{"id":"sent","channel_id":"channel","guild_id":"guild","content":"text","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":1}""")
            }
        }
        val bot = app.bot() as QGBotImpl
        try {
            app.interceptors {
                onMessageSend { _, context, chain ->
                    val reply = assertIs<QGMessageReplyContext>(context)
                    val message = assertIs<InteractionMessage.Text>(reply.message)
                    val next = when (message.text) {
                        "intercepted" -> reply.withOptions(reply.options + replacement)
                        "cleared" -> reply.withOptions(emptyList())
                        else -> reply
                    }
                    chain.proceed(next)
                }
            }
            for (source in replySources(bot)) {
                val fallback = if (source is QGC2CMessageCreateEventImpl) null else "message"
                source.reply("default", replyOption)
                source.reply("explicit", replyOption, explicit)
                source.reply("intercepted", replyOption)
                source.reply("cleared", explicit)
                assertEquals(listOf(fallback, "explicit", "replacement", fallback), references.takeLast(4))
                val requests = references.size
                assertFailsWith<IllegalArgumentException> { source.reply("duplicate", explicit, replacement) }
                assertEquals(requests, references.size)
            }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 验证无引用回复保留被动发送关联，并读取拦截后的最终选项。
     */
    @Test
    fun replyWithoutReferenceUsesFinalOptionsAndKeepsPassiveFields() = runTest {
        val requests = mutableListOf<JsonObject>()
        val noReference = StandardReplyOption.NO_REFERENCE
        val app = app { request ->
            requests += Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            if (request.url.encodedPath.startsWith("/v2/")) {
                respondOk("""{"id":"sent","timestamp":1698435200}""")
            } else {
                respondOk("""{"id":"sent","channel_id":"channel","guild_id":"guild","content":"text","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":1}""")
            }
        }
        val bot = app.bot() as QGBotImpl
        try {
            val received = Json.decodeFromString(
                Message.serializer(),
                """{"id":"received","channel_id":"channel","guild_id":"guild","content":"received","timestamp":"2023-11-06T13:37:18+08:00","author":{"id":"user","username":"user"},"seq_in_channel":"1","message_reference":{"message_id":"inherited"}}"""
            )
            val inherited = QGAtMessageCreateEventImpl(bot, "{}", received, "event").messageContent
            val wrappedContent = object : MessageContent by inherited {
                override val messages = Messages.of(Text { "wrapped" }, MessageIdReference("inherited".ID))
            }
            app.interceptors {
                onMessageSend { _, context, chain ->
                    val reply = assertIs<QGMessageReplyContext>(context)
                    val next = when ((reply.message as? InteractionMessage.Text)?.text) {
                        "added" -> reply.withOptions(reply.options + noReference)
                        "removed" -> reply.withOptions(reply.options - noReference)
                        else -> reply
                    }
                    chain.proceed(next)
                }
            }
            val sources = replySources(bot)
            for (source in sources) {
                val baseline = requests.size
                source.reply("without", noReference, noReference)
                source.reply("added")
                source.reply(inherited, noReference)
                source.reply(Messages.of(Text { "passive" }, QGReplyTo("bound".ID, 24)), noReference)
                source.reply("removed", noReference)

                val (without, added, content, passive, restored) = requests.drop(baseline)
                for (request in listOf(without, added, content, passive)) {
                    assertNull(request["message_reference"]?.takeUnless { it == JsonNull })
                }
                for (request in listOf(without, added, content, restored)) {
                    assertEquals("message", request.getValue("msg_id").jsonPrimitive.content)
                }
                assertEquals("bound", passive.getValue("msg_id").jsonPrimitive.content)
                if (source is QGAtMessageCreateEventImpl || source is QGDirectMessageCreateEventImpl) {
                    assertNull(passive["msg_seq"])
                } else {
                    assertEquals("1", without.getValue("msg_seq").jsonPrimitive.content)
                    assertEquals("24", passive.getValue("msg_seq").jsonPrimitive.content)
                }
                val reference = restored["message_reference"]?.takeUnless { it == JsonNull }
                    ?.jsonObject?.getValue("message_id")?.jsonPrimitive?.content
                assertEquals(if (source is QGC2CMessageCreateEventImpl) null else "message", reference)

                source.reply(wrappedContent, noReference)
                assertNull(requests.last()["message_reference"]?.takeUnless { it == JsonNull })
            }

            val media = Json.decodeFromString<MessageMedia>("""{"file_uuid":"media","file_info":"file","ttl":0}""")
            val split = Messages.of(QGMedia(media), QGMedia(media))
            val baseline = requests.size
            sources.filterIsInstance<QGGroupMessageCreateEventImpl>().single().reply(split, noReference)
            val splitRequests = requests.drop(baseline)
            assertEquals(2, splitRequests.size)
            for (request in splitRequests) {
                assertNull(request["message_reference"]?.takeUnless { it == JsonNull })
                assertEquals("message", request.getValue("msg_id").jsonPrimitive.content)
            }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 在发送或上传前拒绝同时传入的显式引用。
     */
    @Test
    fun replyWithoutReferenceRejectsExplicitReferencesBeforeRequests() = runTest {
        val app = app { error("Conflicting replies must not make requests") }
        val bot = app.bot() as QGBotImpl
        val noReference = StandardReplyOption.NO_REFERENCE
        try {
            for (source in replySources(bot)) {
                assertFailsWith<IllegalArgumentException> {
                    source.reply("text", noReference, QGMessageReferenceOption.create("reference".ID))
                }
                assertFailsWith<IllegalArgumentException> {
                    source.reply(Messages.of(Text { "text" }, QGReference.create("reference".ID)), noReference)
                }
                assertFailsWith<IllegalArgumentException> {
                    source.reply(Messages.of(Text { "text" }, MessageIdReference("reference".ID)), noReference)
                }
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
