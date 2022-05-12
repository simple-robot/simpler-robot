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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.Future
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 * 应用程序启动器。
 *
 * @see applicationLauncher
 *
 * @author ForteScarlet
 */
public interface ApplicationLauncher<out A : Application> {
    
    /**
     * 开始启动当前应用程序，并在内部应用程序启动成功后得到目标应用程序实例 [A].
     */
    @JvmSynthetic
    public suspend fun launch(): A
    
    /**
     * 阻塞的启动当前应用程序，并在内部应用程序启动成功后得到目标应用程序实例 [A].
     */
    @Api4J
    public fun launchBlocking(): A
    
    /**
     * 异步的启动当前应用程序，并在内部应用程序启动成功后将目标应用程序实例 [A] 应用于回调函数 [onCompletion] .
     *
     */
    @Api4J
    public fun launchAsync(onCompletion: OnCompletion<A>)
    
    /**
     * 异步的启动当前应用程序，并得到一个异步的 [Future].
     *
     */
    @Api4J
    public fun launchAsync(): Future<out A>
    
}

/**
 * 使用于 [ApplicationLauncher.launchAsync] 中的回调函数。
 */
@Api4J
public fun interface OnCompletion<in A : Application> {
    public operator fun invoke(application: A)
}


/**
 * 通过一个协程作用域并提供构建函数来得到一个 [ApplicationLauncher] 实例。
 */
public fun <A : Application> CoroutineScope.applicationLauncher(create: suspend () -> A): ApplicationLauncher<A> {
    return ApplicationLauncherImpl(create, coroutineContext)
}


/**
 * 提供一个协程上下文并提供构建函数来得到一个 [ApplicationLauncher] 实例。
 */
public fun <A : Application> applicationLauncher(
    context: CoroutineContext = EmptyCoroutineContext,
    create: suspend () -> A,
): ApplicationLauncher<A> {
    return ApplicationLauncherImpl(create, context)
}



private class ApplicationLauncherImpl<out A : Application>(
    private val create: suspend () -> A,
    override val coroutineContext: CoroutineContext,
) : ApplicationLauncher<A>, CoroutineScope {
    override suspend fun launch(): A = create()
    
    @Api4J
    override fun launchBlocking(): A = runInBlocking(coroutineContext) { launch() }
    
    @Api4J
    override fun launchAsync(onCompletion: OnCompletion<A>) {
        launch {
            val app = launch()
            onCompletion.invoke(app)
        }
    }
    
    @Api4J
    override fun launchAsync(): Future<out A> {
        return async { launch() }.asCompletableFuture()
    }
}