package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.qguild.event.QGSendSupportInteractionEvent
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.friend.QGFriendImpl
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Text
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class QGFriendSendInteractionTests : AbstractInteractionTests() {

    private fun friend(bot: QGBotImpl): QGFriendImpl {
        return QGFriendImpl(bot, UUID.random())
    }

    private fun MockRequestHandleScope.respondSendOk(): HttpResponseData {
        return respondOk(
            """
                    {"id":"${UUID.random()}","timestamp":1698435200}
                """.trimIndent()
        )
    }

    @Test
    fun testQGFriendSendInteraction() = runTest {
        // TEXT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk() },
            prepare = {
                atomic(false) to friend(it)
            },
            listener = { (atob, friend), e, _, _ ->
                atob.value = true
                assertIs<QGFriend>(e.content)
                assertSame(friend, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
            }
        ) { (atob, friend), _ ->
            friend.send("TEXT")
            assertTrue(atob.value)
        }

        // Message
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk() },
            prepare = {
                atomic(false) to friend(it)
            },
            listener = { (atob, friend), e, _, _ ->
                atob.value = true
                assertIs<QGFriend>(e.content)
                assertSame(friend, e.content)
                val message = e.message
                assertIs<InteractionMessage.Message>(message)
                val messageMessage = message.message
                assertIs<Text>(messageMessage)
                assertEquals("TEXT", messageMessage.text)
            }
        ) { (atob, friend), _ ->
            friend.send(Text { "TEXT" })
            assertTrue(atob.value)
        }
    }

}