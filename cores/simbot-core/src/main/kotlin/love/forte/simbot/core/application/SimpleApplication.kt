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

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.application.*
import love.forte.simbot.core.event.CoreListenerManager
import love.forte.simbot.core.event.CoreListenerManagerConfiguration
import love.forte.simbot.core.event.coreListenerManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

/**
 * 由核心所提供的最基础的 [ApplicationFactory] 实现。
 */
public object Simple : ApplicationFactory<SimpleApplicationConfiguration, SimpleApplicationBuilder, SimpleApplication> {
    override fun create(
        configurator: SimpleApplicationConfiguration.() -> Unit,
        builder: SimpleApplicationBuilder.() -> Unit,
    ): SimpleApplication {
        // init configurator
        val config = SimpleApplicationConfiguration().also(configurator)
        val appBuilder = SimpleApplicationBuilder().also(builder)
        return appBuilder.build(config)
    }
}


public class SimpleApplication internal constructor(
    private val simpleEnvironment: SimpleEnvironment,
) : Application {
    override val coroutineContext: CoroutineContext
    private val job: CompletableJob
    private val logger: Logger

    init {
        val properties = simpleEnvironment.properties
        val currentCoroutineContext = properties.coroutineContext
        job = SupervisorJob(currentCoroutineContext[Job])
        coroutineContext = currentCoroutineContext + job
        logger = properties.logger


    }


    override val environment: Application.Environment get() = simpleEnvironment

    override suspend fun join() {
        job.join()
    }

    override suspend fun shutdown() {
        job.cancel()
        stopAll()
    }

    private suspend fun stopAll() {
        environment.providers.forEach {
            kotlin.runCatching {
                it.cancel()
            }.getOrElse { }
        }
        // TODO

    }
}


/**
 * [SimpleApplication]所使用的构建器。
 */
public class SimpleApplicationBuilder : BaseApplicationBuilder() {
    private var listenerManagerConfigurator: CoreListenerManagerConfiguration.() -> Unit = {}


    /**
     * 配置内部的 listener manager.
     *
     */
    @ApplicationBuildDsl
    public fun listenerManager(configurator: CoreListenerManagerConfiguration.() -> Unit) {
        val old = listenerManagerConfigurator
        listenerManagerConfigurator = { old(); configurator() }
    }


    private fun buildListenerManager(appConfig: SimpleApplicationConfiguration): CoreListenerManager {
        val initial = CoreListenerManagerConfiguration {
            coroutineContext = appConfig.coroutineContext.minusKey(Job)
        }

        return coreListenerManager(initial = initial, block = listenerManagerConfigurator)
    }


    internal fun build(appConfig: SimpleApplicationConfiguration): SimpleApplication {
        val components = buildComponents()
        val listenerManager = buildListenerManager(appConfig)
        val providers = buildProviders(listenerManager, components, appConfig)
        val environment = SimpleEnvironment(
            components,
            listenerManager,
            providers,
            appConfig.toProperties()
        )

        return SimpleApplication(environment)
    }


    private fun SimpleApplicationConfiguration.toProperties(): SimpleApplicationProperties {
        val logger = logger ?: LoggerFactory.getLogger("love.forte.simbot.core.application.Simple")
        return SimpleApplicationProperties(
            logger = logger,
            coroutineContext = coroutineContext
        )
    }

}

/**
 * [SimpleApplication] 的配置类。
 */
public open class SimpleApplicationConfiguration : ApplicationConfiguration()


/**
 * 使用 [Simple] 作为应用工厂来构建一个 [Application].
 */
public fun simpleApplication(
    configurator: SimpleApplicationConfiguration.() -> Unit = {},
    builder: SimpleApplicationBuilder.() -> Unit = {},
): SimpleApplication = simbotApplication(Simple, configurator, builder)