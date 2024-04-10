/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("Applications")
@file:JvmMultifileClass

package love.forte.simbot.application

import kotlinx.coroutines.Job
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.function.toConfigurerFunction
import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

/**
 * 将 [Application] 转作 [CompletableFuture]。
 *
 * @see Job.asCompletableFuture
 */
public fun Application.asCompletableFuture(): CompletableFuture<Unit> =
    coroutineContext[Job]?.asCompletableFuture() ?: CompletableFuture.completedFuture(Unit)


/**
 * 构建一个 [Application] 并阻塞地启动它。
 *
 * 启动过程中产生了任何异常，都会被包装在 [ApplicationLaunchBlockingFailureException] 中
 *
 * @throws ApplicationLaunchBlockingFailureException 启动过程中产生了任何异常的包装
 */
@JvmOverloads
public fun <
    A : Application,
    C : AbstractApplicationBuilder,
    L : ApplicationLauncher<A>,
    AER : ApplicationEventRegistrar,
    DC : EventDispatcherConfiguration
    > launchApplicationBlocking(
    factory: ApplicationFactory<A, C, L, AER, DC>,
    configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>? = null
): A {
    runCatching {
        val launcher = factory.create(
            configurer?.let { c ->
                toConfigurerFunction {
                    c.invokeWith(this)
                }
            }
        )

        return runInNoScopeBlocking { launcher.launch() }
    }.getOrElse { e ->
        throw ApplicationLaunchBlockingFailureException(e)
    }
}

/**
 * @see launchApplicationBlocking
 */
public class ApplicationLaunchBlockingFailureException internal constructor(cause: Throwable?) : RuntimeException(cause)

