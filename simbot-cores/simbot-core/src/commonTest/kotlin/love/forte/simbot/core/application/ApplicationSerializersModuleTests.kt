/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.core.application

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class ApplicationSerializersModuleTests {
    abstract class Father

    @Serializable
    @SerialName("kitty")
    data object Kitty : Father()

    @Serializable
    @SerialName("child")
    data object Child : Father()

    class TestComponent : Component {
        override val id: String = "ApplicationSerializersModuleTests.component"
        override val serializersModule: SerializersModule = SerializersModule {
            polymorphic(Father::class) {
                subclass(Child.serializer())
            }
        }

        companion object : ComponentFactory<TestComponent, Unit> {
            override val key: ComponentFactory.Key =
                object : ComponentFactory.Key {}

            override fun create(
                context: ComponentConfigureContext,
                configurer: ConfigurerFunction<Unit>
            ): TestComponent = TestComponent()
        }
    }

    class TestComponent2 : Component {
        override val id: String = "ApplicationSerializersModuleTests.component2"
        override val serializersModule: SerializersModule = SerializersModule {
            polymorphic(Father::class) {
                subclass(Child.serializer())
                defaultDeserializer { Child.serializer() }
            }
        }

        companion object : ComponentFactory<TestComponent2, Unit> {
            override val key: ComponentFactory.Key =
                object : ComponentFactory.Key {}

            override fun create(
                context: ComponentConfigureContext,
                configurer: ConfigurerFunction<Unit>
            ): TestComponent2 = TestComponent2()
        }
    }

    @Test
    fun parentSerializersModuleTest() = runTest {
        val app = launchSimpleApplication {
            config {
                serializersModule = SerializersModule {
                    polymorphic(Father::class) {
                        subclass(Kitty.serializer())
                    }
                }
            }
            install(TestComponent)
        }

        val json = Json { serializersModule = app.components.serializersModule }

        assertEquals(Kitty, json.decodeFromString(PolymorphicSerializer(Father::class), """{"type":"kitty"}"""))
        assertEquals(Child, json.decodeFromString(PolymorphicSerializer(Father::class), """{"type":"child"}"""))
    }

    @Test
    fun parentSerializersModuleWithDefaultTest() = runTest {
        val app = launchSimpleApplication {
            config {
                serializersModule = SerializersModule {
                    polymorphic(Father::class) {
                        subclass(Kitty.serializer())
                        defaultDeserializer { Kitty.serializer() }
                    }
                }
            }
            install(TestComponent)
        }

        val json = Json { serializersModule = app.components.serializersModule }

        assertEquals(Kitty, json.decodeFromString(PolymorphicSerializer(Father::class), """{"type":"kitty"}"""))
        assertEquals(Child, json.decodeFromString(PolymorphicSerializer(Father::class), """{"type":"child"}"""))
        assertEquals(Kitty, json.decodeFromString(PolymorphicSerializer(Father::class), """{}"""))
    }

    @Test
    fun parentSerializersModuleWithDefaultButBeOverWrittenTest() = runTest {
        val app = launchSimpleApplication {
            config {
                serializersModule = SerializersModule {
                    polymorphic(Father::class) {
                        subclass(Kitty.serializer())
                        defaultDeserializer { Kitty.serializer() }
                    }
                }
            }
            install(TestComponent2)
        }

        val json = Json { serializersModule = app.components.serializersModule }

        assertEquals(Kitty, json.decodeFromString(PolymorphicSerializer(Father::class), """{"type":"kitty"}"""))
        assertEquals(Child, json.decodeFromString(PolymorphicSerializer(Father::class), """{"type":"child"}"""))
        assertEquals(Kitty, json.decodeFromString(PolymorphicSerializer(Father::class), """{}"""))
    }

}
