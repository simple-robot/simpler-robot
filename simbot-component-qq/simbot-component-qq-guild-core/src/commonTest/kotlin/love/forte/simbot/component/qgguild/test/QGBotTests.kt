package love.forte.simbot.component.qgguild.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.bot.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGKeyboard
import love.forte.simbot.component.qguild.message.QGMarkdown
import love.forte.simbot.component.qguild.message.SendingMessageParser
import love.forte.simbot.event.*
import love.forte.simbot.message.plus
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.stdlib.BotFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [QGBot] interface and its implementation.
 */
class QGBotTests {

    /**
     * Creates a mock QGBot implementation for testing.
     */
    private fun createMockBot(): QGBot {
        val sourceBot = BotFactory.create("123", "test-app", "test-token")
        val component = QQGuildComponent()
        val eventDispatcher = object : EventDispatcher {
            override fun push(event: Event): Flow<EventResult> {
                return emptyFlow()
            }

            override fun dispose(listener: EventListener) {
                // No-op
            }

            override fun register(
                listener: EventListener
            ): EventListenerRegistrationHandle {
                return object : EventListenerRegistrationHandle {
                    override fun dispose() {
                        // No-op
                    }
                }
            }

            override fun register(
                propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?,
                listener: EventListener
            ): EventListenerRegistrationHandle {
                return object : EventListenerRegistrationHandle {
                    override fun dispose() {
                        // No-op
                    }
                }
            }

            override val listeners: Sequence<EventListener>
                get() = emptySequence()
        }

        return QGBotImpl(
            sourceBot,
            component,
            eventDispatcher,
            QGBotComponentConfiguration()
        )
    }

    @Test
    fun testIsMe() {
        val bot = createMockBot()

        // Bot ID should be "123" based on our mock creation
        assertTrue(bot.isMe("123".ID))
        assertFalse(bot.isMe("456".ID))
    }

    @Test
    fun testToString() {
        val bot = createMockBot()

        // toString should contain the bot ID
        val toString = bot.toString()
        assertTrue(toString.contains("123"), "Bot toString() should contain its ID")
    }

    @OptIn(FuzzyEventTypeImplementation::class, ExperimentalSimbotAPI::class)
    @Test
    fun testIsMention() {
        val bot = createMockBot()

        // Create a mock event that mentions the bot
        val mentionEvent = object : Event {
            override val id: ID = "event-1".ID
            override val time: Timestamp = Timestamp.now()
        }

        // Since we can't easily create a real mention event, we'll just verify the method exists
        // and doesn't throw an exception
        assertFalse(bot.isMention(mentionEvent))
    }

    @OptIn(ExperimentalQGApi::class)
    @Test
    fun testMessageCreation() = runTest {
        // Test creating different types of messages
        val markdown = QGMarkdown.create("# Hello")
        val keyboard = QGKeyboard.createById("123")

        // Create a message with markdown and keyboard
        val message = markdown + keyboard

        // Create a mock bot for testing
        val bot = createMockBot()

        // Parse the message using parseToGroupAndC2C
        val bodies = MessageParsers.parseToGroupAndC2C(
            bot = bot,
            message = message,
            builderType = SendingMessageParser.GroupBuilderType.GROUP,
            targetOpenid = "test-openid",
            factory = {
                GroupAndC2CSendBody.create("", GroupAndC2CSendBody.MSG_TYPE_MARKDOWN)
            }
        )

        // Verify the parsed message
        assertEquals(1, bodies.size)
        assertEquals("# Hello", bodies.first().markdown?.content)
        assertEquals("123", bodies.first().keyboard?.id)
    }
}
