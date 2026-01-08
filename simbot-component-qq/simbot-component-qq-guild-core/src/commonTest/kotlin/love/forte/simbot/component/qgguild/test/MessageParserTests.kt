package love.forte.simbot.component.qgguild.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QQGuildComponent
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

/**
 *
 * @author ForteScarlet
 */
class MessageParserTests {
    @OptIn(ExperimentalQGApi::class)
    @Test
    fun testKeyboardParse() = runTest {
        val builders = MessageParsers.parse(
            QGMarkdown.create("content") + QGKeyboard.createById("1"),
        )

        assertEquals(1, builders.size)
        val build = builders.first().build()
        assertEquals("content", build.markdown?.content)

        val bot = QGBotImpl(
            BotFactory.create("", "", ""),
            QQGuildComponent(),
            object : EventDispatcher {
                override fun push(event: Event): Flow<EventResult> {
                    TODO("Not yet implemented")
                }

                override fun dispose(listener: EventListener) {
                    TODO("Not yet implemented")
                }

                override fun register(
                    propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?,
                    listener: EventListener
                ): EventListenerRegistrationHandle {
                    TODO("Not yet implemented")
                }

                override val listeners: Sequence<EventListener>
                    get() = TODO("Not yet implemented")
            },
            QGBotComponentConfiguration()
        )

        val bodies = MessageParsers.parseToGroupAndC2C(
            bot = bot,
            message = QGMarkdown.create("content") + QGKeyboard.createById("1"),
            builderType = SendingMessageParser.GroupBuilderType.GROUP,
            targetOpenid = "123",
            factory = {
                GroupAndC2CSendBody.create("", GroupAndC2CSendBody.MSG_TYPE_MARKDOWN)
            }
        )

        assertEquals(1, bodies.size)
        assertEquals("content", bodies.first().markdown?.content)
        assertEquals("1", bodies.first().keyboard?.id)
    }

}