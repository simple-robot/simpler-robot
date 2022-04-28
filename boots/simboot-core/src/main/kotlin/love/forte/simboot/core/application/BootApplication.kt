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

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.di.BeanContainer
import love.forte.simboot.SimbootContext
import love.forte.simbot.Api4J
import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationFactory
import love.forte.simbot.application.EventProvider
import love.forte.simbot.core.application.BaseApplication
import love.forte.simbot.core.application.BaseApplicationBuilder
import love.forte.simbot.core.application.SimpleApplication
import love.forte.simbot.core.application.SimpleApplicationConfiguration
import love.forte.simbot.core.event.CoreListenerManager
import love.forte.simbot.utils.view
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

/**
 * boot-core 模块所提供的 [ApplicationFactory] 实现，基于 [SimpleApplication] 的拓展。
 *
 */
public object Boot : ApplicationFactory<BootApplicationConfiguration, BootApplicationBuilder, BootApplication> {
    override fun create(
        configurator: BootApplicationConfiguration.() -> Unit,
        builder: BootApplicationBuilder.() -> Unit
    ): BootApplication {
        // init configurator
        val config = BootApplicationConfiguration().also(configurator)
        val appBuilder = BootApplicationBuilderImpl().also(builder)
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





}


/**
 * 用于构建 [BootApplication] 的构建器。
 */
public interface BootApplicationBuilder : ApplicationBuilder<BootApplication>


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
    override fun startAsync() { }

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


    fun build(configuration: BootApplicationConfiguration): BootApplication {

        TODO()
    }
}