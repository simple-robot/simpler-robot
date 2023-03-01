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

package love.forte.simbot.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import love.forte.simbot.Api4J
import love.forte.simbot.JST
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 * 应用程序启动器。
 *
 * [ApplicationLauncher] 用在 [simbotApplication] 或与之相关的函数中，用来实现在配置application之后而不直接启动的情况。
 *
 * @see applicationLauncher
 *
 * @author ForteScarlet
 */
public interface ApplicationLauncher<out A : Application> {
    
    /**
     * 开始启动当前应用程序，并在内部应用程序启动成功后得到目标应用程序实例 [A].
     */
    @JST
    public suspend fun launch(): A
    
    
    /**
     * @suppress 直接使用 `launchAsync()` 得到 [java.util.concurrent.CompletableFuture] 并使用即可。
     */
    @Api4J
    @Deprecated("Just use launch")
    public fun launchAsync(onCompletion: OnCompletion<A>)
    
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
    @Deprecated("Just use launch", ReplaceWith("launch { onCompletion(launch()) }", "kotlinx.coroutines.launch"))
    override fun launchAsync(onCompletion: OnCompletion<A>) {
        launch {
            onCompletion(launch())
        }
    }
}
