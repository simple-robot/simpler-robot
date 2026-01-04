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

package love.forte.simbot.component.onebot.v11.core.bot

import io.ktor.client.engine.mock.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.application.Application
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.component.onebot.v11.core.oneBot11Bots
import love.forte.simbot.component.onebot.v11.core.useOneBot11
import love.forte.simbot.component.onebot.v11.message.segment.OneBotAt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotDice
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotUnknownSegment
import love.forte.simbot.core.application.launchSimpleApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class BotDecoderFromCustomComponentTests {

    @Test
    fun botDecoderWithCustomComponentTest() = runTest {
        val app = launchSimpleApplication {
            useOneBot11()
            install(TestCustomComponentFac)
        }

        val testSeg = doTest(app)
        assertIs<TestSegment>(testSeg)
        assertEquals("test", testSeg.data)
    }

    @OptIn(FragileSimbotAPI::class)
    @Test
    fun botDecoderWithoutCustomComponentTest() = runTest {
        val app = launchSimpleApplication {
            useOneBot11()
        }

        val testSeg = doTest(app)
        assertIs<OneBotUnknownSegment>(testSeg)
        assertEquals("test", testSeg.data!!.jsonPrimitive.content)
    }

    private suspend fun doTest(app: Application): OneBotMessageSegment {
        app.oneBot11Bots {
            val bot = register(
                OneBotBotConfiguration().apply {
                    botUniqueId = UUID.random().toString()
                    MockEngine {
                        respondOk()
                    }.apply {
                        wsClientEngine = this
                        apiClientEngine = this
                    }
                }
            )
            bot.initConfiguration()

            val segments = doSerial(bot)
            assertEquals(3, segments.size)
            val m1 = segments[0]
            val m2 = segments[1]
            val m3 = segments[2]
            assertIs<OneBotDice>(m1)
            assertIs<OneBotAt>(m3)
            assertEquals("all", m3.data.qq)
            assertTrue(m3.isAll)

            return m2
        }

        error("no")
    }

    private fun doSerial(bot: OneBotBot): List<OneBotMessageSegment> {
        val jsonStr = """[
          {"type": "dice", "data": {}},
          {"type": "test", "data": "test"},
          {"type": "at", "data": {"qq": "all"}}
        ]
        """.trimIndent()

        return bot.decoderJson.decodeFromString(
            ListSerializer(PolymorphicSerializer(OneBotMessageSegment::class)),
            jsonStr
        )
    }
}


private val TestCustomComponent = object : Component {
    override val id: String = "TestCustomComponent"

    override val serializersModule: SerializersModule = SerializersModule {
        polymorphic(OneBotMessageSegment::class) {
            subclass(TestSegment.serializer())
        }
    }
}

private val TestCustomComponentFac = object : ComponentFactory<Component, Unit> {
    override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

    override fun create(context: ComponentConfigureContext, configurer: ConfigurerFunction<Unit>): Component {
        return TestCustomComponent
    }
}


@SerialName("test")
@Serializable
private data class TestSegment(override val data: String) : OneBotMessageSegment
