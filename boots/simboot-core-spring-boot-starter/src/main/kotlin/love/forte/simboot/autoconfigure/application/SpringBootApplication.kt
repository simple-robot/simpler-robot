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

package love.forte.simboot.autoconfigure.application

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.application.*
import love.forte.simbot.core.application.BaseApplication
import love.forte.simbot.core.application.BaseApplicationBuilder
import love.forte.simbot.core.application.CoreEventProcessableApplicationBuilder
import love.forte.simbot.core.application.SimpleApplicationBuilder
import love.forte.simbot.core.event.CoreListenerManager
import love.forte.simbot.core.event.CoreListenerManagerConfiguration
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.utils.view
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext


/**
 * 兼容 SpringBoot 的 [Application] 工厂。
 */
public object SpringBoot :
    ApplicationFactory<SpringBootApplicationConfiguration, SpringBootApplicationBuilder, SpringBootApplication> {
    override fun create(
        configurator: SpringBootApplicationConfiguration.() -> Unit,
        builder: SpringBootApplicationBuilder.(SpringBootApplicationConfiguration) -> Unit
    ): SpringBootApplication {
        val configuration = SpringBootApplicationConfiguration().also(configurator)
        return create(configuration, builder)
    }

    /**
     * 直接提供配置类进行构建。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun create(
        configuration: SpringBootApplicationConfiguration,
        builder: SpringBootApplicationBuilder.(SpringBootApplicationConfiguration) -> Unit
    ): SpringBootApplication {
        return SpringBootApplicationBuilderImpl().apply {
            builder(configuration)
        }.build(configuration)
    }
}


/**
 * 使用 [SpringBoot] 作为工厂构建一个 [SpringBootApplication].
 */
@JvmOverloads
public fun springBootApplication(
    initialConfiguration: SpringBootApplicationConfiguration = SpringBootApplicationConfiguration(),
    configurator: SpringBootApplicationConfiguration.() -> Unit = {},
    builder: SpringBootApplicationBuilder.(SpringBootApplicationConfiguration) -> Unit = {},
): SpringBootApplication {
    val configuration = initialConfiguration.also(configurator)
    return SpringBoot.create(configuration, builder)
}


/**
 * 兼容 `Spring Boot` 的 `Application` 实现。
 */
public interface SpringBootApplication : Application


/**
 * 用于 [SpringBoot] 进行构建的Builder。行为与 [SimpleApplicationBuilder] 类似。
 */
public interface SpringBootApplicationBuilder : ApplicationBuilder<SpringBootApplication>,
    CoreEventProcessableApplicationBuilder<SpringBootApplication> {

    /**
     * 配置内部的 core listener manager.
     *
     */
    @ApplicationBuilderDsl
    override fun eventProcessor(configurator: CoreListenerManagerConfiguration.(environment: Application.Environment) -> Unit)
}


//region Impls


private class SpringBootApplicationBuilderImpl : SpringBootApplicationBuilder,
    BaseApplicationBuilder<SpringBootApplication>() {
    private var listenerManagerConfigurator: CoreListenerManagerConfiguration.(environment: Application.Environment) -> Unit =
        {}


    /**
     * 配置内部的 listener manager.
     */
    override fun eventProcessor(configurator: CoreListenerManagerConfiguration.(environment: Application.Environment) -> Unit) {
        val old = listenerManagerConfigurator
        listenerManagerConfigurator = { env -> old(env); configurator(env) }
    }

    private fun buildListenerManager(
        appConfig: SpringBootApplicationConfiguration,
        environment: Application.Environment
    ): CoreListenerManager {
        val initial = CoreListenerManagerConfiguration {
            // TODO job?
            coroutineContext = appConfig.coroutineContext
        }

        return coreListenerManager(initial = initial, block = { listenerManagerConfigurator(environment) })
    }


    // TODO
    fun build(appConfig: SpringBootApplicationConfiguration): SpringBootApplication {
        // TODO

        val components = buildComponents()

        val logger = appConfig.logger

        val environment = SpringBootEnvironment(
            components,
            logger,
            appConfig.coroutineContext
        )

        val listenerManager = buildListenerManager(appConfig, environment)
        val providers = buildProviders(listenerManager, components, appConfig)

        registerBots(providers.filterIsInstance<love.forte.simbot.BotRegistrar>())

        val application = SpringBootApplicationImpl(appConfig, environment, listenerManager, providers)

        // complete.
        complete(application)

        return application
    }

}


// TODO
private class SpringBootApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val environment: SpringBootEnvironment,
    override val eventListenerManager: CoreListenerManager,
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
//endregion

