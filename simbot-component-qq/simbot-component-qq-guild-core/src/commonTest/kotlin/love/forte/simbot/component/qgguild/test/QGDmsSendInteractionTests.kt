package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.event.QGSendSupportInteractionEvent
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.dms.QGDmsContactImpl
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.model.User
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class QGDmsSendInteractionTests : AbstractInteractionTests() {

    private fun dms(bot: QGBotImpl): QGDmsContactImpl {
        return QGDmsContactImpl(
            bot,
            source = User(
                id = "654321",
                username = "username"
            ),
            "123456",
            null
        )
    }

    private fun MockRequestHandleScope.respondSendOk(req: HttpRequestData): HttpResponseData {
        // Message
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
    fun testQGMemberSendInteraction() = runTest {
        // TEXT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to dms(it)
            },
            listener = { (atob, dms), e, _, _ ->
                atob.value = true
                assertIs<QGDmsContact>(e.content)
                assertSame(dms, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
            }
        ) { (atob, dms), _ ->
            dms.send("TEXT")
            assertTrue(atob.value)
        }

        // Message
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to dms(it)
            },
            listener = { (atob, dms), e, _, _ ->
                atob.value = true
                assertIs<QGDmsContact>(e.content)
                assertSame(dms, e.content)
                val message = e.message
                assertIs<InteractionMessage.Message>(message)
                val messageMessage = message.message
                assertIs<Text>(messageMessage)
                assertEquals("TEXT", messageMessage.text)
            }
        ) { (atob, dms), _ ->
            dms.send(Text { "TEXT" })
            assertTrue(atob.value)
        }
    }

}