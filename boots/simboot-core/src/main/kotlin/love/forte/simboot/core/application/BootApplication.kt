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

package love.forte.simboot.core.application

import kotlinx.coroutines.*
import love.forte.di.Bean
import love.forte.di.BeanContainer
import love.forte.simboot.SimbootContext
import love.forte.simboot.annotation.Listener
import love.forte.simbot.Api4J
import love.forte.simbot.application.*
import love.forte.simbot.core.application.*
import love.forte.simbot.core.event.CoreListenerManager
import love.forte.simbot.core.event.CoreListenerManagerConfiguration
import love.forte.simbot.core.event.EventListenersGenerator
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.utils.view
import org.slf4j.Logger
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

/**
 * boot-core 模块所提供的 [ApplicationFactory] 实现，基于 [SimpleApplication] 的拓展。
 *
 */
public object Boot : ApplicationFactory<BootApplicationConfiguration, BootApplicationBuilder, BootApplication> {
    override fun create(
        configurator: BootApplicationConfiguration.() -> Unit,
        builder: BootApplicationBuilder.(BootApplicationConfiguration) -> Unit
    ): BootApplication {
        // init configurator
        val config = BootApplicationConfiguration().also(configurator)
        val appBuilder = BootApplicationBuilderImpl().apply {
            builder(config)
        }
        return appBuilder.build(config)
    }
}


/**
 * [Boot] 所使用的配置类型。
 */
public open class BootApplicationConfiguration : SimpleApplicationConfiguration() {
    /**
     * 提供额外参数，例如命令行参数。
     */
    public var args: List<String> = emptyList()

    /**
     * 需要进行依赖扫描的所有包路径。
     *
     */
    public var classesScanScope: List<String> = emptyList()

    /**
     * 是否在bot注册后，在 application 构建完毕的时候自动执行 `Bot.start`。
     *
     * 这一行为将会是阻塞的 （ `runBlocking { bot.start() }` ）。
     *
     * 如果设置为 `true`，效果类似于：
     * ```kotlin
     * simbotApplication(...) {
     *     bots {
     *         register(...)?.also { bot ->
     *             onCompletion {
     *                 runBlocking { bot.start() }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * 或者
     * ```kotlin
     * simbotApplication(...) {
     *    // install a component
     *    install(FooComponent)
     *    // install bot manager by 'foo component'.
     *    install(FooBotManager) {
     *      register(code, pass).also { bot ->
     *          onCompletion {
     *              runBlocking { bot.start() }
     *          }
     *      }
     *    }
     * }
     * ```
     *
     * 默认为`true`。
     *
     */
    public var isAutoStartBots: Boolean = true

}


/**
 * 用于构建 [BootApplication] 的构建器。
 */
public interface BootApplicationBuilder : ApplicationBuilder<BootApplication>,
    CoreEventProcessableApplicationBuilder<BootApplication> {

    /**
     * 事件处理器。
     */
    override fun eventProcessor(configurator: CoreListenerManagerConfiguration.(environment: Application.Environment) -> Unit)


    /**
     *
     * 配置当前环境中所使用的依赖管理器。在进行构建的时候会自动扫描所有加载的bean中标记了 [Listener] 注解的函数并将它们解析为监听函数。
     *
     * e.g.
     * ```kotlin
     *  beans {
     *      bean(name, instance)
     *      beanBy(name) { Foo() }
     *      bean(name, Foo::class) { Foo() }
     *      scan("com.example1", "com.example2")
     *  }
     * ```
     *
     */
    public fun beans(beanContainerBuilder: BeanContainerBuilder.() -> Unit)


    // TODO binder


}


/**
 * [Boot] 所得到的最终的 [Application] 实现, 基于 [SimpleApplication].
 */
public interface BootApplication : SimpleApplication, SimbootContext {

    /**
     * 当前环境中的 [Bean容器][BeanContainer].
     */
    public val beanContainer: BeanContainer

    /**
     * [BootApplication] 不需要执行 [start].
     */
    override suspend fun start(): Boolean = false

    /**
     * [BootApplication] 不需要 `start`.
     */
    @OptIn(Api4J::class)
    override fun startBlocking(): Boolean = false

    /**
     * [BootApplication] 不需要 `start`.
     */
    @OptIn(Api4J::class)
    override fun startAsync() {
    }

    /**
     * [BootApplication] 从一开始就是启用状态。
     */
    override val isStarted: Boolean
        get() = true


}


/**
 * [BootApplication] 实现。
 */
private class BootApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val environment: BootEnvironment,
    override val eventListenerManager: CoreListenerManager,
    override val beanContainer: BeanContainer,
    providerList: List<EventProvider>,
) : BootApplication, BaseApplication() {
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

    override val isActive: Boolean
        get() = job.isActive

    override val isCancelled: Boolean
        get() = job.isCancelled

    override suspend fun cancel(reason: Throwable?): Boolean {
        shutdown(reason)
        return true
    }

    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }
}


/**
 * [BootApplicationBuilder] 的实现。
 */
private class BootApplicationBuilderImpl : BootApplicationBuilder, BaseApplicationBuilder<BootApplication>() {
    //region listener manager config
    private var listenerManagerConfigurator: CoreListenerManagerConfiguration.(environment: Application.Environment) -> Unit =
        {}


    /**
     * 配置内部的 listener manager.
     */
    @ApplicationBuildDsl
    override fun eventProcessor(configurator: CoreListenerManagerConfiguration.(environment: Application.Environment) -> Unit) {
        val old = listenerManagerConfigurator
        listenerManagerConfigurator = { env -> old(env); configurator(env) }
    }


    private fun buildListenerManager(
        appConfig: SimpleApplicationConfiguration,
        environment: Application.Environment
    ): CoreListenerManager {
        val initial = CoreListenerManagerConfiguration {
            // TODO job?
            coroutineContext = appConfig.coroutineContext
        }

        return coreListenerManager(initial = initial, block = { listenerManagerConfigurator(environment) })
    }
    //endregion

    private val beanContainerBuilderConfigurations = ConcurrentLinkedQueue<BeanContainerBuilder.() -> Unit>()


    override fun beans(beanContainerBuilder: BeanContainerBuilder.() -> Unit) {
        beanContainerBuilderConfigurations.add(beanContainerBuilder)
    }


    fun build(configuration: BootApplicationConfiguration): BootApplication {
        val logger = configuration.logger

        logger.debug("Building components...")
        val components = buildComponents()
        logger.debug("Components are built: {}", components)
        logger.info("The size of components built is {}", components.size)


        logger.debug("Creating boot environment...")
        val environment = BootEnvironment(
            components,
            logger,
            configuration.coroutineContext
        )
        logger.debug("Boot environment created: {}", environment)

        logger.debug("Building listener manager...")
        val listenerManager = buildListenerManager(configuration, environment)
        logger.debug("Listener manager is built: {}", listenerManager)

        logger.debug("Building providers...")
        val providers = buildProviders(listenerManager, components, configuration)
        logger.debug("Providers are built: {}", providers)
        logger.info("The size of providers built is {}", providers.size)

        // bean container
        val beanContainerBuilder = BeanContainerBuilderImpl(configuration)
        logger.debug("Building bean container by builder: {}", beanContainerBuilder)
        beanContainerBuilderConfigurations.forEach { configure ->
            beanContainerBuilder.configure()
        }
        val beanContainer: BeanContainer = beanContainerBuilder.build()
        logger.debug("Bean container is built: {}", beanContainer)

        listeners {

        }

        // register bots
        logger.debug("Registering bots...")
        val bots = registerBots(providers.filterIsInstance<love.forte.simbot.BotRegistrar>())
        logger.info("Bots registered. Size of bots: {}", bots.size)
        val isAutoStartBots = configuration.isAutoStartBots
        logger.debug("Auto start bots: {}", isAutoStartBots)
        if (isAutoStartBots) {
            onCompletion {
                bots.forEach { bot ->
                    logger.info("Blocking start bot {}", bot)
                    val started = runBlocking { bot.start() }
                    logger.info("Bot {} started: {}", bot, started)
                }
            }
        }

        // scan and register listener
        TODO()

        // create application
        val application = BootApplicationImpl(configuration, environment, listenerManager, beanContainer, providers)

        // complete.
        complete(application)

        return application
    }


}


/**
 *
 * 注册一个bean。
 *
 * e.g.
 * ```kotlin
 * beanBy("foo") { Foo() }
 * ```
 */
public inline fun <reified T : Any> BeanContainerBuilder.beanBy(
    name: String,
    crossinline factory: () -> T
): Bean<out T> {
    return bean(name, T::class) { factory() }
}


private fun EventListenersGenerator.autoConfigFromBeanContainer(
    environment: BootEnvironment,
    beanContainer: BeanContainer
) {


    TODO()
}