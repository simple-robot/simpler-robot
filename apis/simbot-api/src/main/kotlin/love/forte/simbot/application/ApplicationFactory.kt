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

package love.forte.simbot.application

import love.forte.simbot.Component
import love.forte.simbot.ComponentFactory
import love.forte.simbot.application.Application.Environment
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于构建 [Application.Environment] 的工厂。
 *
 * @author ForteScarlet
 */
public interface ApplicationFactory<
        Config : ApplicationConfiguration,
        Builder : ApplicationBuilder,
        A : Application
        > {

    /**
     * 提供配置函数和构建器函数，构建一个 [Application] 实例。
     */
    public fun create(configurator: Config.() -> Unit, builder: Builder.() -> Unit): A

}


/**
 * [Environment] 的构建器.
 * @param CBuilder 组件构建器的实例。
 */
public interface ApplicationBuilder {

    /**
     * 注册一个 [组件][Component].
     */
    @ApplicationBuildDsl
    public fun <C : Component, Config : Any> install(
        componentFactory: ComponentFactory<C, Config>,
        configurator: Config.() -> Unit = {},
    )

    /**
     * 注册一个事件提供者。
     */
    @ApplicationBuildDsl
    public fun <P : EventProvider, Config : Any> install(
        eventProviderFactory: EventProviderFactory<P, Config>,
        configurator: Config.() -> Unit = {},
    )

}

/**
 * 标记为用于 [ApplicationBuilder] 的 dsl api.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class ApplicationBuildDsl


/**
 * 整个应用程序进行构建所需的基本配置信息。
 */
public open class ApplicationConfiguration {

    /**
     * 当前application内所使用的协程上下文。
     *
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 提供一个用于Application内部的日志对象。
     */
    public var logger: Logger? = null

}
