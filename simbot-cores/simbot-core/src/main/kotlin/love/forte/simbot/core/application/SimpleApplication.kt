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

package love.forte.simbot.core.application

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.application.*
import love.forte.simbot.core.event.SimpleEventListenerManager
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.set
import love.forte.simbot.utils.view
import kotlin.time.Duration.Companion.nanoseconds

/**
 * 由核心所提供的最基础的 [ApplicationFactory] 实现。
 */
public object Simple : ApplicationFactory<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> {
    private val logger = LoggerFactory.logger<Simple>()

    override suspend fun create(
        configurator: SimpleApplicationConfiguration.() -> Unit,
        builder: suspend SimpleApplicationBuilder.(SimpleApplicationConfiguration) -> Unit,
    ): SimpleApplication {
        // init configurator
        val config = SimpleApplicationConfiguration().also {
            it.logger = this.logger
        }.also(configurator).also {
            it.initJob()
        }

        val logger = config.logger
        logger.debug("Configuration init: {}", config)
        val startTime = System.nanoTime()
        val appBuilder = SimpleApplicationBuilderImpl().apply {
            builder(config)
        }
        return appBuilder.build(config).also {
            val duration = (System.nanoTime() - startTime).nanoseconds
            if (logger.isInfoEnabled) {
                logger.info("Simple Application built in {}", duration.toString())
            }
        }
    }
}


/**
 * 使用 [Simple] 作为应用工厂来构建一个 [Application].
 */
public fun simpleApplication(
    configurator: SimpleApplicationConfiguration.() -> Unit = {},
    builder: SimpleApplicationBuilder.(SimpleApplicationConfiguration) -> Unit = {},
): ApplicationLauncher<SimpleApplication> = simbotApplication(Simple, configurator, builder)


/**
 * 使用 [Simple] 作为应用工厂来构建一个 [Application].
 */
public suspend fun createSimpleApplication(
    configurator: SimpleApplicationConfiguration.() -> Unit = {},
    builder: SimpleApplicationBuilder.(SimpleApplicationConfiguration) -> Unit = {},
): SimpleApplication = createSimbotApplication(Simple, configurator, builder)


/**
 * [SimpleApplication] 的配置类。
 */
public open class SimpleApplicationConfiguration : ApplicationConfiguration() {
    public open fun initJob() {
        if (coroutineContext[Job] == null) {
            coroutineContext += SupervisorJob()
        }
    }
}


/**
 * 通过 [Simple] 构建而得到的 [Application] 实例。
 */
public interface SimpleApplication : Application {

    /**
     * [SimpleApplication] 使用 [SimpleEventListenerManager] 作为事件管理器。
     */
    override val eventListenerManager: SimpleEventListenerManager


    /**
     * 所有的事件提供者。
     */
    override val providers: List<EventProvider>
}


/**
 * 用于构建 [SimpleApplication] 的构建器类型。
 */
public interface SimpleApplicationBuilder : StandardApplicationBuilder<SimpleApplication>


/**
 * 通过 [Simple] 构建而得到的 [Application] 实例。
 */
private class SimpleApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val environment: SimpleEnvironment,
    override val eventListenerManager: SimpleEventListenerManager,
    providerList: List<EventProvider>,
) : SimpleApplication, BaseApplication() {
    override val providers: List<EventProvider> = providerList.view()

    override val coroutineContext = environment.coroutineContext
    override val logger = environment.logger
}


/**
 * [SimpleApplication]所使用的构建器。
 */
private class SimpleApplicationBuilderImpl : SimpleApplicationBuilder,
    BaseStandardApplicationBuilder<SimpleApplication>() {


    @OptIn(ExperimentalSimbotApi::class)
    suspend fun build(appConfig: SimpleApplicationConfiguration): SimpleApplication {
        val logger = appConfig.logger

        logger.debug("Building components...")
        val components = buildComponents()
        logger.debug("Components are built: {}", components)
        logger.info("The size of components built is {}", components.size)

        val environment = SimpleEnvironment(
            components, logger, appConfig.coroutineContext
        )
        logger.debug("Init SimpleEnvironment: {}", environment)

        logger.debug("Building listeners...")
        val listenerManager = buildListenerManager(appConfig, environment)
        logger.debug("Listeners are built by listener manager: {}", listenerManager)

        logger.debug("Building providers...")
        val providers = buildProviders(listenerManager, components, appConfig)
        logger.debug("Providers are built: {}", providers)
        logger.info("The size of providers built is {}", providers.size)

        val application = SimpleApplicationImpl(appConfig, environment, listenerManager, providers)

        // set application attribute
        listenerManager.globalScopeContext[ApplicationAttributes.Application] = application

        // complete.
        complete(application)
        logger.info("Application [{}] is built and completed.", application)

        // region register bots
        // registering bot after complete.

        logger.debug("Registering bots...")
        val bots = registerBots(providers)
        logger.debug("All bot registers: {}", bots)
        logger.info("The size of bots registered: {}", bots.size)
        // endregion


        return application
    }
}


