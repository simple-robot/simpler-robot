/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.core.application

import kotlinx.coroutines.launch
import love.forte.simbot.*
import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.application.*
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.bot.ComponentMismatchException
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.utils.view
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


/**
 *
 * 实现 [ApplicationBuilder] 并实现通用的组件与提供者的配置。
 *
 * @author ForteScarlet
 */
public abstract class BaseApplicationBuilder<A : Application> :
    ApplicationBuilder<A> {
    private val componentConfigurations = mutableMapOf<Attribute<*>, Any.() -> Unit>()
    private val componentFactories = mutableMapOf<Attribute<*>, suspend () -> Component>()
    
    private var botRegisterConfig: (suspend BotRegistrarImpl.() -> Unit) = {}
    
    private val applicationLock = ReentrantReadWriteLock()
    
    @Volatile
    private lateinit var applicationInstance: A
    
    /**
     * 事件提供者配置。
     */
    private val eventProviderConfigurations =
        mutableMapOf<Attribute<*>, Any.() -> Unit>()
    
    /**
     * 事件提供者工厂。
     */
    private val eventProviderFactories =
        mutableMapOf<Attribute<*>, suspend (EventProcessor, List<Component>, ApplicationConfiguration) -> EventProvider>()
    
    /**
     * 注册一个 [组件][Component].
     */
    override fun <C : Component, Config : Any> install(
        componentFactory: ComponentFactory<C, Config>,
        configurator: Config.(perceivable: CompletionPerceivable<A>) -> Unit,
    ) {
        val key = componentFactory.key
        val newConfig: Any.() -> Unit = newConfigurator(key, componentConfigurations, configurator)
        componentConfigurations[key] = newConfig
        
        if (key in componentFactories) return
        
        componentFactories[key] = {
            val configuration = componentConfigurations[key]!!
            componentFactory.create(configuration)
        }
    }
    
    
    /**
     * 注册一个 [事件提供者][EventProvider].
     */
    override fun <P : EventProvider, Config : Any> install(
        eventProviderFactory: EventProviderFactory<P, Config>,
        configurator: Config.(perceivable: CompletionPerceivable<A>) -> Unit,
    ) {
        val key = eventProviderFactory.key
        val newConfig: Any.() -> Unit = newConfigurator(key, eventProviderConfigurations, configurator)
        eventProviderConfigurations[key] = newConfig
        
        if (key in eventProviderFactories) return
        
        eventProviderFactories[key] = { eventProcessor, components, applicationConfiguration ->
            val configuration = eventProviderConfigurations[key]!!
            eventProviderFactory.create(eventProcessor, components, applicationConfiguration, configuration)
        }
        
    }
    
    /**
     * 添加一个bot注册函数。
     */
    override fun bots(registrar: suspend BotRegistrar.() -> Unit) {
        botRegisterConfig.also { old ->
            botRegisterConfig = {
                old()
                registrar()
            }
        }
    }
    
    
    private fun <Config : Any> newConfigurator(
        key: Attribute<*>,
        configurations: Map<Attribute<*>, Any.() -> Unit>,
        configurator: Config.(builder: ApplicationBuilder<A>) -> Unit,
    ): (Any.() -> Unit) {
        val oldConfig = configurations[key]
        @Suppress("UNCHECKED_CAST")
        return if (oldConfig != null) {
            {
                oldConfig.invoke(this)
                (this as Config).configurator(this@BaseApplicationBuilder)
            }
        } else {
            {
                (this as Config).configurator(this@BaseApplicationBuilder)
            }
        }
    }
    
    protected fun componentFactoriesSize(): Int = componentFactories.size
    
    
    protected suspend fun buildComponents(): List<Component> {
        return componentFactories.values.map { it() }
    }
    
    protected fun eventProviderFactoriesSize(): Int = eventProviderFactories.size
    
    
    protected suspend fun buildProviders(
        eventProcessor: EventProcessor,
        components: List<Component>,
        applicationConfiguration: ApplicationConfiguration,
    ): List<EventProvider> {
        return eventProviderFactories.values.map { it(eventProcessor, components, applicationConfiguration) }
    }
    
    /**
     * 提供botManager列表并执行它们的注册逻辑。
     *
     * @return 被注册的bot数量
     */
    protected suspend fun registerBots(providers: List<EventProvider>): List<Bot> {
        val registrar = BotRegistrarImpl(providers)
        botRegisterConfig(registrar)
        return providers.flatMap { if (it is BotManager<*>) it.all() else emptyList() }
    }
    
    
    /**
     * 当构建完成时统一执行的函数列表。
     */
    private val onCompletions = ConcurrentLinkedQueue<suspend (A) -> Unit>()
    
    private suspend fun doOnCompletion(application: A) {
        while (onCompletions.isNotEmpty()) {
            onCompletions.poll().invoke(application)
        }
    }
    
    override fun onCompletion(handle: suspend (application: A) -> Unit) {
        var app: A? = null
        applicationLock.read {
            if (::applicationInstance.isInitialized) {
                app = applicationInstance
            } else {
                onCompletions.add(handle)
            }
        }
        app?.also {
            it.launch {
                doOnCompletion(it)
                handle(it)
            }
        }
    }
    
    
    /**
     * 当 [Application] 构建完毕，则执行此函数来执行所有的回调函数。
     */
    protected suspend fun complete(application: A) {
        applicationLock.write {
            if (::applicationInstance.isInitialized) {
                throw SimbotIllegalStateException("Application has been initialized: $applicationInstance")
            }
            applicationInstance = application
        }
        doOnCompletion(application)
    }
    
    
    private class BotRegistrarImpl(providers: List<EventProvider>) : BotRegistrar {
        override val providers: List<EventProvider> = providers.view()
        
        override fun register(botVerifyInfo: BotVerifyInfo): Bot? {
            logger.info("Registering bot with verify info [{}]", botVerifyInfo)
            for (manager in providers) {
                if (manager !is love.forte.simbot.bot.BotRegistrar) {
                    continue
                }
                
                try {
                    return manager.register(botVerifyInfo).also { bot ->
                        logger.debug(
                            "Bot verify info [{}] is registered as [{}] via manager [{}]",
                            botVerifyInfo,
                            bot,
                            manager
                        )
                    }
                } catch (ignore: ComponentMismatchException) {
                    logger.debug("Bot verify info [{}] is not matched by manager {}, try next.", botVerifyInfo, manager)
                    // ignore this.
                }
            }
            return null
        }
        
        override fun toString(): String {
            return "BotRegistrarImpl(providers=$providers)"
        }
        
        private companion object {
            private val logger =
                LoggerFactory.getLogger("love.forte.simbot.core.application.BaseApplicationBuilder.BotRegistrarImpl")
        }
    }
}
