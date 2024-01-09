/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.ApplicationEventHandler
import love.forte.simbot.application.ApplicationEventRegistrar
import love.forte.simbot.application.ApplicationLaunchStage
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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


class ComponentTests {
    private val testContext = object : ComponentConfigureContext {
        override val applicationConfiguration: ApplicationConfiguration = object : ApplicationConfiguration {
            override val coroutineContext: CoroutineContext = EmptyCoroutineContext
        }

        override val applicationEventRegistrar: ApplicationEventRegistrar = object : ApplicationEventRegistrar {
            override fun <H : ApplicationEventHandler> addEventHandler(
                stage: ApplicationLaunchStage<H>,
                handler: H
            ) {
                // nothing... 😟
            }
        }
    }

    @Test
    fun componentToComponentsTest() {
        with(listOf(TestComponent1(0), TestComponent2(0))) {
            val components = toComponents()
            assertFalse(components.isEmpty())
            assertEquals(2, components.size)
        }
        with(listOf(TestComponent1(0))) {
            val components = toComponents()
            assertFalse(components.isEmpty())
            assertEquals(1, components.size)
        }
        with(listOf<Component>()) {
            val components = toComponents()
            assertTrue(components.isEmpty())
            assertEquals(0, components.size)
        }
    }

    @Test
    fun testComponentFactoriesConfigurator() {
        with(ComponentFactoriesConfigurator()) {
            add(TestComponent1) {
                value++
            }
            add(TestComponent1) {
                value++
            }


            val instance = create(testContext, TestComponent1)
            assertEquals(2, instance.value)
            val instance2 = create(testContext, TestComponent2)
            assertEquals(0, instance2.value)
        }
    }
}


private class TestComponent1(val value: Int) : Component {
    override val id: String = "test.cp1"
    override val serializersModule: SerializersModule = EmptySerializersModule()

    companion object : ComponentFactory<TestComponent1, TestComponentConfiguration> {
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<TestComponentConfiguration>
        ): TestComponent1 {
            val config = TestComponentConfiguration().invokeBy(configurer)
            return TestComponent1(config.value)
        }
    }
}

private class TestComponent2(val value: Int) : Component {
    override val id: String = "test.cp2"
    override val serializersModule: SerializersModule = EmptySerializersModule()

    companion object : ComponentFactory<TestComponent2, TestComponentConfiguration> {
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<TestComponentConfiguration>
        ): TestComponent2 {
            val config = TestComponentConfiguration().invokeBy(configurer)
            return TestComponent2(config.value)
        }
    }
}

private class TestComponentConfiguration {
    var value: Int = 0
}
