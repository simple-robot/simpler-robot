package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.event.QGSendSupportInteractionEvent
import love.forte.simbot.component.qguild.event.QGSendSupportPreSendEvent
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGTextChannelImpl
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.model.ChannelSubType
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.SimpleChannel
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class QGTextChannelSendInteractionTests : AbstractInteractionTests() {

    private fun channel(bot: QGBotImpl): QGTextChannelImpl {
        return QGTextChannelImpl(
            bot,
            SimpleChannel(
                "123456",
                "guildid",
                "guild",
                ChannelType.TEXT,
                ChannelSubType.SMALL_TALK,
                0,
                "0",
                "0",
            )
        )
    }

    private suspend fun MockRequestHandleScope.respondSendOk(
        req: HttpRequestData,
        assertText: String? = null
    ): HttpResponseData {
        if (assertText != null) {
            val body = req.body.toByteArray().decodeToString()
            val json = Json.decodeFromString(JsonElement.serializer(), body)
            assertEquals(assertText, json.jsonObject["content"]?.jsonPrimitive?.content)
        }

        return respondOk(
            """
                    {
                    "id":"${UUID.random()}",
                    "channel_id":"555555",
                    "guild_id":"666666",
                    "content":"TEXT",
                    "timestamp":"${Clock.System.now()}",
                    "author": {
                        "id":"654321",
                        "username":"username"
                        },
                        "seq_in_channel":1
                    }
                """.trimIndent()
        )
    }

    @Test
    fun testQGTextChannelSendInteraction() = runTest {
        // TEXT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to channel(it)
            },
            listener = { (atob, channel), e, _, _ ->
                atob.value = true
                assertIs<QGTextChannel>(e.content)
                assertSame(channel, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
            }
        ) { (atob, channel), _ ->
            channel.send("TEXT")
            assertTrue(atob.value)
        }

        // Message
        testInteractionEvent<_, QGSendSupportPreSendEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to channel(it)
            },
            listener = { (atob, channel), e, _, _ ->
                atob.value = true
                assertIs<QGTextChannel>(e.content)
                assertSame(channel, e.content)
                val message = e.message
                assertIs<InteractionMessage.Message>(message)
                val messageMessage = message.message
                assertIs<Text>(messageMessage)
                assertEquals("TEXT", messageMessage.text)
            }
        ) { (atob, channel), _ ->
            channel.send(Text { "TEXT" })
            assertTrue(atob.value)
        }

        // 修改TEXT->TEXT_EDIT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it, "TEXT_EDIT") },
            prepare = {
                atomic(false) to channel(it)
            },
            listener = { (atob, channel), e, _, _ ->
                atob.value = true
                assertIs<QGSendSupportPreSendEvent>(e)
                assertIs<QGTextChannel>(e.content)
                assertSame(channel, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
                // edit message
                e.currentMessage = InteractionMessage.valueOf("TEXT_EDIT")
            }
        ) { (atob, channel), _ ->
            channel.send("TEXT")
            assertTrue(atob.value)
        }
    }

}