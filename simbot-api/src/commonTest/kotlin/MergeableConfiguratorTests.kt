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
import love.forte.simbot.application.ApplicationEventRegistrar
import love.forte.simbot.application.Components
import love.forte.simbot.application.toComponents
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactoriesConfigurator
import love.forte.simbot.plugin.PluginFactory
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class MergeableConfiguratorTests {

    private class TestComponent private constructor() : Component {
        override val id: String = "simbot.test"
        override val serializersModule: SerializersModule = EmptySerializersModule()

        companion object Factory : ComponentFactory<TestComponent, Unit> {
            override val key: ComponentFactory.Key = object : ComponentFactory.Key {}


            override fun create(
                context: ComponentConfigureContext,
                configurer: ConfigurerFunction<Unit>
            ): TestComponent {
                configurer.invokeWith(Unit)
                return TestComponent()
            }
        }
    }

    private class TestPlugin private constructor() : Plugin {
        companion object Factory : PluginFactory<TestPlugin, Unit> {
            override val key: PluginFactory.Key = object : PluginFactory.Key {}


            override fun create(
                context: PluginConfigureContext,
                configurer: ConfigurerFunction<Unit>
            ): TestPlugin {
                configurer.invokeWith(Unit)
                return TestPlugin()
            }
        }
    }

    @Test
    fun mergeable_configuration_component_test() {
        var count = 0
        val configurator = ComponentFactoriesConfigurator().apply {
            add(TestComponent) {
                count++
            }
            add(TestComponent) {
                count++
            }
        }

        val context = object : ComponentConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = TODO("Not yet implemented")
            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = TODO("Not yet implemented")
        }

        configurator.create(context, TestComponent)

        assertEquals(count, 2)
    }

    @Test
    fun mergeable_configuration_plugin_test() {
        var count = 0
        val configurator = PluginFactoriesConfigurator().apply {
            add(TestPlugin) {
                count++
            }
            add(TestPlugin) {
                count++
            }
        }

        val context = object : PluginConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = TODO("Not yet implemented")
            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = TODO("Not yet implemented")
            override val components: Components = emptyList<Component>().toComponents()
            override val eventDispatcher: EventDispatcher
                get() = TODO("Not yet implemented")
        }

        configurator.create(context, TestPlugin)

        assertEquals(count, 2)
    }

}
