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

package love.forte.simbot.spring2.application

import kotlinx.coroutines.Job
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.application.*
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.toBotManagers
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.component.*
import love.forte.simbot.core.event.createSimpleEventDispatcherImpl
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactoriesConfigurator
import love.forte.simbot.plugin.toPlugins
import love.forte.simbot.spring.common.application.*
import love.forte.simbot.spring2.application.internal.SpringApplicationImpl
import love.forte.simbot.spring2.application.internal.SpringEventDispatcherConfigurationImpl

/**
 * Factory for [SpringApplication].
 */
public object Spring : SpringApplicationFactory {
    override fun create(
        configurer: ConfigurerFunction<
            ApplicationFactoryConfigurer<
                SpringApplicationBuilder,
                SpringApplicationEventRegistrar,
                SpringEventDispatcherConfiguration
                >
            >?
    ): SpringApplicationLauncher {
        return SpringApplicationLauncherImpl { create0(configurer) }
    }

    @OptIn(ExperimentalSimbotAPI::class)
    private fun create0(
        configurer: ConfigurerFunction<
            ApplicationFactoryConfigurer<
                SpringApplicationBuilder,
                SpringApplicationEventRegistrar,
                SpringEventDispatcherConfiguration
                >
            >?
    ): SpringApplicationImpl {
        val springConfigurer = SpringApplicationFactoryConfigurer().invokeBy(configurer)
        val configuration = springConfigurer.createConfigInternal(SpringApplicationBuilder())

        val registrar = object : AbstractApplicationEventRegistrar(), SpringApplicationEventRegistrar {
            public override val events: MutableMap<ApplicationLaunchStage<*>, MutableList<ApplicationEventHandler>>
                get() = super.events
        }

        // 事件调度器
        val dispatcherConfiguration = SpringEventDispatcherConfigurationImpl(SimpleEventDispatcherConfigurationImpl())
        springConfigurer.eventDispatcherConfigurers.forEach(dispatcherConfiguration::invokeBy)

        // 合并 Application coroutineContext into dispatcher coroutineContext, 且不要Job
        val minJobDispatcherContext = dispatcherConfiguration.coroutineContext.minusKey(Job)
        val minJobApplicationContext = configuration.coroutineContext.minusKey(Job)
        dispatcherConfiguration.coroutineContext = minJobApplicationContext + minJobDispatcherContext

        val dispatcher = createSimpleEventDispatcherImpl(dispatcherConfiguration.simple)

        // 事件注册器
        springConfigurer.applicationEventRegistrarConfigurations.forEach { c ->
            c.invokeWith(registrar)
        }

        // components
        val components = springConfigurer.componentFactoriesConfigurator.createAll(object : ComponentConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = configuration

            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = registrar
        }).toComponents(
            parentSerializersModule = configuration.serializersModule
        )

        // plugins
        val pluginCollections = springConfigurer.pluginFactoriesConfigurator.createAll(object : PluginConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = configuration

            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = registrar

            override val components: Components
                get() = components

            override val eventDispatcher: EventDispatcher
                get() = dispatcher
        })


        val plugins = pluginCollections.toPlugins()
        val botManagers = pluginCollections.filterIsInstance<BotManager>().toBotManagers()

        val events = applicationLaunchStages(registrar.events.mapValues { it.value.toList() })

        return SpringApplicationImpl(
            configuration,
            dispatcher,
            components,
            plugins,
            botManagers,
            events
        )
    }
}

private class SpringApplicationLauncherImpl(
    private val applicationCreator: () -> SpringApplicationImpl
) : SpringApplicationLauncher {
    override suspend fun launch(): SpringApplication {
        val application = applicationCreator()

        application.events.invokeOnEach(ApplicationLaunchStage.Launch) {
            invoke(application)
        }

        return application
    }
}


private class SpringApplicationFactoryConfigurer(
    public override val configConfigurers: MutableList<ConfigurerFunction<SpringApplicationBuilder>> = mutableListOf(),

    public override val applicationEventRegistrarConfigurations:
    MutableList<ConfigurerFunction<SpringApplicationEventRegistrar>> = mutableListOf(),

    public override val eventDispatcherConfigurers:
    MutableList<ConfigurerFunction<SpringEventDispatcherConfiguration>> = mutableListOf(),

    public override val componentFactoriesConfigurator:
    ComponentFactoriesConfigurator = ComponentFactoriesConfigurator(),
    public override val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : AbstractApplicationFactoryConfigurer<
    SpringApplicationBuilder,
    SpringApplicationEventRegistrar,
    SpringEventDispatcherConfiguration
    >(
    configConfigurers,
    applicationEventRegistrarConfigurations,
    eventDispatcherConfigurers,
    componentFactoriesConfigurator,
    pluginFactoriesConfigurator
) {
    @OptIn(InternalSimbotAPI::class)
    fun createConfigInternal(configBuilder: SpringApplicationBuilder): SpringApplicationConfiguration {
        return createConfig(configBuilder) {
            it.build()
        }
    }

    public override fun createAllComponents(context: ComponentConfigureContext): List<Component> {
        return super.createAllComponents(context)
    }

    public override fun createAllPlugins(context: PluginConfigureContext): List<Plugin> {
        return super.createAllPlugins(context)
    }
}

