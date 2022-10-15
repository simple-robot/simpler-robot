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

import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Component
import love.forte.simbot.ComponentFactory
import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotVerifyInfo
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
        Builder : ApplicationBuilder<A>,
        A : Application,
        > {
    
    /**
     * 提供配置函数和构建器函数，构建一个 [Application] 实例。
     */
    @JvmBlocking
    public suspend fun create(configurator: Config.() -> Unit, builder: suspend Builder.(Config) -> Unit): A
}


/**
 * [Application] 的构建器.
 *
 * 在 [ApplicationBuilder] 中，所有相关的函数如果没做特殊说明的话均为 **非线程安全的**。
 * 因此请避免并行的使用未作特殊说明的函数。
 *
 * @param A 目标 [Application] 类型
 */
public interface ApplicationBuilder<A : Application> : CompletionPerceivable<A> {
    
    /**
     * 注册一个 [组件][Component].
     */
    @ApplicationBuilderDsl
    public fun <C : Component, Config : Any> install(
        componentFactory: ComponentFactory<C, Config>,
        configurator: Config.(perceivable: CompletionPerceivable<A>) -> Unit = {},
    )
    
    /**
     * 注册一个事件提供者。
     */
    @ApplicationBuilderDsl
    public fun <P : EventProvider, Config : Any> install(
        eventProviderFactory: EventProviderFactory<P, Config>,
        configurator: Config.(perceivable: CompletionPerceivable<A>) -> Unit = {},
    )
    
    
    /**
     * 提供一个可以使用 [BotVerifyInfo] 进行通用性bot注册的配置方式。
     */
    @ApplicationBuilderDsl
    public fun bots(registrar: suspend BotRegistrar.() -> Unit)
    
    
    /**
     * 注册一个当 [Application] 构建完成后的回调函数。
     *
     * [onCompletion] 的实现应当是**线程安全**的。因此你可以安全的在 [ApplicationBuilder]
     * 内的一些其他函数中并行的注册或使用 [handle] 函数。
     *
     * [onCompletion] 在并行注册的情况下，不保证其最终地执行顺序。
     *
     * 假如当前builder已经构建完毕，再调用此函数则会**异步的**立刻执行 [handle] 函数。
     * 此时执行 [handle] 使用的异步协程作用域为已经构建完毕的 [Application].
     */
    @ApplicationBuilderDsl
    override fun onCompletion(handle: suspend (application: A) -> Unit)
    
}


/**
 * 应用于 [ApplicationBuilder.bots] 中的bot注册函数,
 * 提供一个通过 [BotVerifyInfo] 注册的通用bot注册函数。
 *
 * [BotRegistrar] 会通过 [BotVerifyInfo] 中的 [组件id][BotVerifyInfo.componentId]
 * 去当前环境中寻找对应组件的、实现了 [Bot注册器][love.forte.simbot.bot.BotRegistrar] 的 [事件提供者][EventProvider],
 * 并尝试注册此bot。
 *
 */
public interface BotRegistrar {
    
    /**
     * 当前环境中的所有事件提供者。
     *
     * 你可以通过 [providers] 寻找你所需要的指定 [Bot注册器][love.forte.simbot.bot.BotRegistrar]。
     *
     * ```kotlin
     * providers.filterIsInstance<FooBotRegistrar>().forEach {
     *    // ...
     * }
     * ```
     *
     * 当然，根据组件和事件提供者的注册机制来讲，通常情况下同一个类型的注册器环境中只会存在一个。
     * ```kotlin
     * providers.firstOrNull { it is FooBotRegistrar } as FooBotRegistrar?
     * ```
     *
     */
    public val providers: List<EventProvider>
    
    
    /**
     * 通过 [BotVerifyInfo] 中的 [组件信息][BotVerifyInfo.componentId]
     * 去当前环境中寻找对应组件的、实现了 [Bot注册器][love.forte.simbot.bot.BotRegistrar] 的 [事件提供者][EventProvider],
     * 并尝试注册此bot。
     *
     * 如果没有找到符合组件id的 [Bot注册器][love.forte.simbot.bot.BotRegistrar] 存在，则返回null。
     */
    public fun register(botVerifyInfo: BotVerifyInfo): Bot?
}


/**
 * 标记为用于 [ApplicationBuilder] 的 dsl api.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class ApplicationBuilderDsl


/**
 * 整个应用程序进行构建所需的基本配置信息。
 */
public open class ApplicationConfiguration {
    
    /**
     * 当前application内所使用的协程上下文。
     *
     * [ApplicationFactory] 应当考虑 [ApplicationConfiguration]
     * 配置完成后依旧不存在 [kotlinx.coroutines.Job] 时候进行主动分配。
     *
     */
    public open var coroutineContext: CoroutineContext = EmptyCoroutineContext
    
    /**
     * 提供一个用于Application内部的日志对象。
     */
    public open var logger: Logger = love.forte.simbot.logger.LoggerFactory.getLogger("love.forte.simbot.application.ApplicationConfiguration")
    
}
