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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.*
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotManagers
import love.forte.simbot.bot.toBotManagers
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.component.*
import love.forte.simbot.core.event.SimpleEventDispatcher
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration
import love.forte.simbot.core.event.createSimpleEventDispatcherImpl
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.*
import kotlin.coroutines.CoroutineContext


/**
 *
 * @see Simple
 *
 */
private class SimpleApplicationImpl(
    override val configuration: SimpleApplicationConfiguration,
    override val eventDispatcher: SimpleEventDispatcher,
    override val components: Components,
    override val plugins: Plugins,
    override val botManagers: BotManagers,
    val events: ApplicationLaunchStages
) : SimpleApplication {
    private val job: Job
    override val coroutineContext: CoroutineContext

    init {
        val newJob = SupervisorJob(configuration.coroutineContext[Job])
        val newCoroutineContext = configuration.coroutineContext.minusKey(Job) + newJob

        this.job = newJob
        this.coroutineContext = newCoroutineContext
    }

    private inline fun <C : Any, reified H : NormalApplicationEventHandler<C>> invokeNormalHandler(
        stage: ApplicationLaunchStage<H>, block: H.() -> Unit
    ) {
        events[stage]?.forEach { handler ->
            (handler as? H)?.also { handler0 ->
                block(handler0)
            }
        }
    }

    override fun cancel(reason: Throwable?) {
        invokeNormalHandler(ApplicationLaunchStage.RequestCancel) {
            invoke(this@SimpleApplicationImpl)
        }

        job.cancel(reason?.let { CancellationException(reason.message, it) })

        invokeNormalHandler(ApplicationLaunchStage.Cancelled) {
            invoke(this@SimpleApplicationImpl)
        }
    }

    override suspend fun join() {
        job.join()
    }

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion { handle.invoke(it) }
    }

    override fun toString(): String {
        return "SimpleApplication(" +
            "isActive=$isActive, " +
            "isCompleted=$isCompleted, " +
            "eventDispatcher=$eventDispatcher, " +
            "components=$components, " +
            "plugins=$plugins)"
    }
}

/**
 * 用于构建 [SimpleApplication] 实例的 [ApplicationFactory] 实现。
 *
 */
public object Simple :
    ApplicationFactory<
        SimpleApplication,
        SimpleApplicationBuilder,
        SimpleApplicationLauncher,
        ApplicationEventRegistrar,
        SimpleEventDispatcherConfiguration
        > {

    override fun create(
        configurer: ConfigurerFunction<
            ApplicationFactoryConfigurer<
                SimpleApplicationBuilder,
                ApplicationEventRegistrar,
                SimpleEventDispatcherConfiguration
                >
            >?
    ): SimpleApplicationLauncher {
        return SimpleApplicationLauncherImpl { create0(configurer) }
    }

    @OptIn(ExperimentalSimbotAPI::class)
    private fun create0(
        configurer: ConfigurerFunction<
            ApplicationFactoryConfigurer<
                SimpleApplicationBuilder,
                ApplicationEventRegistrar,
                SimpleEventDispatcherConfiguration
                >
            >?
    ): SimpleApplicationImpl {
        val simpleConfigurer = SimpleApplicationFactoryConfigurer().invokeBy(configurer)

        // 配置信息
        val configuration = simpleConfigurer.createConfigInternal(SimpleApplicationBuilder())

        val registrar = object : AbstractApplicationEventRegistrar() {
            public override val events: MutableMap<ApplicationLaunchStage<*>, MutableList<ApplicationEventHandler>>
                get() = super.events
        }

        // 事件调度器
        val dispatcher = createSimpleEventDispatcherImpl {
            simpleConfigurer.eventDispatcherConfigurers.forEach { cf ->
                invokeBy(cf)
            }

            // 合并 Application coroutineContext into dispatcher coroutineContext, 且不要Job
            val minJobDispatcherContext = coroutineContext.minusKey(Job)
            val minJobApplicationContext = configuration.coroutineContext.minusKey(Job)
            coroutineContext = minJobApplicationContext + minJobDispatcherContext
        }

        // 事件注册器
        simpleConfigurer.applicationEventRegistrarConfigurations.forEach { c ->
            c.invokeWith(registrar)
        }

        // components
        val components = simpleConfigurer.componentFactoriesConfigurator.createAll(object : ComponentConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = configuration

            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = registrar
        }).toComponents()

        // plugins
        val pluginCollections = simpleConfigurer.pluginFactoriesConfigurator.createAll(object : PluginConfigureContext {
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

        return SimpleApplicationImpl(
            configuration,
            dispatcher,
            components,
            plugins,
            botManagers,
            events
        )
    }
}

private class SimpleApplicationFactoryConfigurer(
    public override val configConfigurers: MutableList<ConfigurerFunction<SimpleApplicationBuilder>> = mutableListOf(),
    public override val applicationEventRegistrarConfigurations:
    MutableList<ConfigurerFunction<ApplicationEventRegistrar>> =
        mutableListOf(),

    public override val eventDispatcherConfigurers:
    MutableList<ConfigurerFunction<SimpleEventDispatcherConfiguration>> =
        mutableListOf(),

    public override val componentFactoriesConfigurator:
    ComponentFactoriesConfigurator =
        ComponentFactoriesConfigurator(),
    public override val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : AbstractApplicationFactoryConfigurer<
    SimpleApplicationBuilder,
    ApplicationEventRegistrar,
    SimpleEventDispatcherConfiguration
    >(
    configConfigurers,
    applicationEventRegistrarConfigurations,
    eventDispatcherConfigurers,
    componentFactoriesConfigurator,
    pluginFactoriesConfigurator
) {
    fun createConfigInternal(configBuilder: SimpleApplicationBuilder): SimpleApplicationConfiguration {
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

/**
 * 通过 [Simple] 构建 [SimpleApplication] 时使用的构建器。
 */
public class SimpleApplicationBuilder : AbstractApplicationBuilder() {
    internal fun build(): SimpleApplicationConfiguration {
        val context = coroutineContext
        val job = SupervisorJob(context[Job])

        // 至少有个 Job
        return SimpleApplicationConfigurationImpl(context.minusKey(Job) + job)
    }
}

private class SimpleApplicationConfigurationImpl(override val coroutineContext: CoroutineContext) :
    SimpleApplicationConfiguration

private class SimpleApplicationLauncherImpl(
    private val applicationCreator: () -> SimpleApplicationImpl
) : SimpleApplicationLauncher {
    override suspend fun launch(): SimpleApplicationImpl {
        val application = applicationCreator()

        application.events.invokeOnEach(ApplicationLaunchStage.Launch) {
            invoke(application)
        }

        return application
    }
}
