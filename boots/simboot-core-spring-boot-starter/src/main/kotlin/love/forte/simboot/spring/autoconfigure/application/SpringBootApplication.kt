/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simboot.spring.autoconfigure.application

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.application.*
import love.forte.simbot.core.application.*
import love.forte.simbot.core.event.SimpleEventListenerManager
import love.forte.simbot.core.event.SimpleListenerManagerConfiguration
import love.forte.simbot.core.event.simpleListenerManager
import love.forte.simbot.set
import love.forte.simbot.utils.view
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.nanoseconds


/**
 * 兼容 [Spring Boot](https://spring.io) 的 [Application] 工厂。
 *
 *
 * ## [SpringBootApplication]
 * [SpringBoot] 提供的 [Application] 实现为 [SpringBootApplication].
 *
 * ## 扩展函数
 * [SpringBoot] 工厂提供了 [springBootApplication] 等扩展了 [simbotApplication] 的函数来简化操作。
 *
 *
 * @see SpringBootApplication
 *
 */
public object SpringBoot :
    ApplicationFactory<SpringBootApplicationConfiguration, SpringBootApplicationBuilder, SpringBootApplication> {
    override suspend fun create(
        configurator: SpringBootApplicationConfiguration.() -> Unit,
        builder: suspend SpringBootApplicationBuilder.(SpringBootApplicationConfiguration) -> Unit,
    ): SpringBootApplication {
        val configuration = SpringBootApplicationConfiguration().also(configurator)
        return create(configuration, builder)
    }
    
    /**
     * 直接提供配置类进行构建。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public suspend fun create(
        configuration: SpringBootApplicationConfiguration,
        builder: suspend SpringBootApplicationBuilder.(SpringBootApplicationConfiguration) -> Unit,
    ): SpringBootApplication {
        val logger = configuration.logger
        val startTime = System.nanoTime()
        return SpringBootApplicationBuilderImpl().apply {
            builder(configuration)
        }.build(configuration).also {
            logger.info(
                "Simbot Spring Boot Application built in {}",
                (System.nanoTime() - startTime).nanoseconds.toString()
            )
        }
    }
}


/**
 * 使用 [SpringBoot] 作为工厂构建一个 [SpringBootApplication] 为目标的 [launcher][ApplicationLauncher].
 *
 * [springBootApplication] 并非是 [simbotApplication] 的直接扩展。与 [simbotApplication] 不同的是,
 * [springBootApplication] 可以提供一个 [初始化配置][initialConfiguration] 实例。
 *
 */
public fun springBootApplication(
    initialConfiguration: SpringBootApplicationConfiguration = SpringBootApplicationConfiguration(),
    configurator: SpringBootApplicationConfiguration.() -> Unit = {},
    builder: SpringBootApplicationBuilder.(SpringBootApplicationConfiguration) -> Unit = {},
): ApplicationLauncher<SpringBootApplication> {
    val configuration = initialConfiguration.also(configurator)
    return applicationLauncher { SpringBoot.create(configuration, builder) }
}


/**
 * 作为 `Spring Boot Starter` 的 [Application] 实现。
 *
 * [SpringBootApplication] 用于作为一个 [Spring Boot Starter](https://spring.io/projects/spring-boot#overview) 来使用的 [Application] 工厂，
 * 此工厂通常在 Spring Boot 环境下配合 [love.forte.simboot.spring.autoconfigure.EnableSimbot] 注解来一键开箱，
 * 而不需要通过 [simbotApplication] 等启动函数来手动开启 ———— 也不应该这么做。
 *
 * 因此, [SpringBootApplication] 是Java友好的。 相对于基础的 [love.forte.simboot.core.application.Boot] 模块而言，
 * [SpringBootApplication] 将内部的依赖管理以及各种配置的实现全部交给了 `Spring Boot` ，因此会与 `Spring Boot` 之间有着更紧密的交融。
 *
 *
 * @see SpringBoot
 */
public interface SpringBootApplication : Application


/**
 * 用于 [SpringBoot] 进行构建的Builder。行为与 [SimpleApplicationBuilder] 类似。
 */
public interface SpringBootApplicationBuilder : ApplicationBuilder<SpringBootApplication>,
    EventProcessableApplicationBuilder<SpringBootApplication> {
    
    /**
     * 配置内部的 core listener manager.
     *
     */
    @ApplicationBuilderDsl
    override fun eventProcessor(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit)
}


// region Impls


private class SpringBootApplicationBuilderImpl : SpringBootApplicationBuilder,
    BaseApplicationBuilder<SpringBootApplication>() {
    private var listenerManagerConfigurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit =
        {}
    
    
    /**
     * 配置内部的 listener manager.
     */
    override fun eventProcessor(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit) {
        val old = listenerManagerConfigurator
        listenerManagerConfigurator = { env -> old(env); configurator(env) }
    }
    
    private fun buildListenerManager(
        appConfig: SpringBootApplicationConfiguration,
        environment: Application.Environment,
    ): SimpleEventListenerManager {
        val initial = SimpleListenerManagerConfiguration {
            // TODO job?
            coroutineContext = appConfig.coroutineContext
        }
        
        return simpleListenerManager(initial = initial, block = fun SimpleListenerManagerConfiguration.() {
            listenerManagerConfigurator(environment)
        })
    }
    
    
    @OptIn(ExperimentalSimbotApi::class)
    @Suppress("DuplicatedCode")
    suspend fun build(configuration: SpringBootApplicationConfiguration): SpringBootApplication {
        val components = buildComponents()
        
        val logger = configuration.logger
        
        val environment = SpringBootEnvironment(
            components,
            logger,
            configuration.coroutineContext
        )
        
        logger.debug("Building listener manager...")
        val listenerManager = buildListenerManager(configuration, environment)
        logger.debug("Listener manager is built: {}", listenerManager)
        
        
        logger.debug("Building providers...")
        val providers = buildProviders(listenerManager, components, configuration)
        logger.info("The size of providers built is {}", providers.size)
        if (providers.isNotEmpty()) {
            logger.debug("The built providers: {}", providers)
        }
        
        val application = SpringBootApplicationImpl(configuration, environment, listenerManager, providers)
        // set application attribute
        listenerManager.globalScopeContext[ApplicationAttributes.Application] = application
    
        // complete.
        complete(application)
    
        //region register bots
        // after complete.
        logger.debug("Registering bots...")
        val bots = registerBots(providers)
    
        logger.info("Bots all registered. The size of bots: {}", bots.size)
        if (bots.isNotEmpty()) {
            logger.debug("The all registered bots: {}", bots)
        }
        val isAutoStartBots = configuration.isAutoStartBots
        logger.debug("Auto start bots: {}", isAutoStartBots)
        if (isAutoStartBots && bots.isNotEmpty()) {
            onCompletion {
                bots.forEach { bot ->
                    logger.info("Starting bot {}", bot)
                    val started = bot.start()
                    logger.info("Bot [{}] started: {}", bot, started)
                }
            }
            logger.debug("Registered on completion function for start bots.")
        }
        if (isAutoStartBots && bots.isEmpty()) {
            logger.debug("But the registered bots are empty.")
        }
        //endregion
        
        return application
    }
    
}


private class SpringBootApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val environment: SpringBootEnvironment,
    override val eventListenerManager: SimpleEventListenerManager,
    providerList: List<EventProvider>,
) : SpringBootApplication, BaseApplication() {
    override val providers: List<EventProvider> = providerList.view()
    
    override val coroutineContext: CoroutineContext
    override val job: CompletableJob
    override val logger: Logger
    
    init {
        val currentCoroutineContext = environment.coroutineContext
        job = SupervisorJob(currentCoroutineContext[Job])
        coroutineContext = currentCoroutineContext + job
        logger = environment.logger
    }
}
// endregion

