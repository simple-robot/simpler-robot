package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.qguild.event.QGSendSupportInteractionEvent
import love.forte.simbot.component.qguild.event.QGSendSupportPreSendEvent
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupImpl
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Text
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class QGGroupSendInteractionTests : AbstractInteractionTests() {

    private fun group(bot: QGBotImpl): QGGroupImpl {
        return QGGroupImpl(bot, UUID.random(), null, null, true)
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
                    {"id":"${UUID.random()}","timestamp":1698435200}
                """.trimIndent()
        )
    }

    @Test
    fun testQGGroupSendInteraction() = runTest {
        // TEXT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to group(it)
            },
            listener = { (atob, group), e, _, _ ->
                atob.value = true
                assertIs<QGGroup>(e.content)
                assertSame(group, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
            }
        ) { (atob, group), _ ->
            group.send("TEXT")
            assertTrue(atob.value)
        }

        // Message
        testInteractionEvent<_, QGSendSupportPreSendEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to group(it)
            },
            listener = { (atob, group), e, _, _ ->
                atob.value = true
                assertIs<QGGroup>(e.content)
                assertSame(group, e.content)
                val message = e.message
                assertIs<InteractionMessage.Message>(message)
                val messageMessage = message.message
                assertIs<Text>(messageMessage)
                assertEquals("TEXT", messageMessage.text)
            }
        ) { (atob, group), _ ->
            group.send(Text { "TEXT" })
            assertTrue(atob.value)
        }

        // 修改TEXT->TEXT_EDIT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it, "TEXT_EDIT") },
            prepare = {
                atomic(false) to group(it)
            },
            listener = { (atob, group), e, _, _ ->
                atob.value = true
                assertIs<QGSendSupportPreSendEvent>(e)
                assertIs<QGGroup>(e.content)
                assertSame(group, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
                // edit message
                e.currentMessage = InteractionMessage.valueOf("TEXT_EDIT")
            }
        ) { (atob, group), _ ->
            group.send("TEXT")
            assertTrue(atob.value)
        }
    }

}