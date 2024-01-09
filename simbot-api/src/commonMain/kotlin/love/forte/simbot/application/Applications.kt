/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

@file:JvmMultifileClass
@file:JvmName("Applications")

package love.forte.simbot.application

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import love.forte.simbot.common.async.Async
import love.forte.simbot.common.async.asAsync
import love.forte.simbot.common.async.toAsync
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.function.toConfigurerFunction
import love.forte.simbot.event.EventDispatcherConfiguration
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmSynthetic


/**
 * 构建一个 [Application] 并启动它。
 *
 * ```kotlin
 * val app = launchApplication(Simple) {
 *     // ...
 * }
 * ```
 *
 */
@JvmSynthetic
public suspend inline fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplication(
    factory: ApplicationFactory<A, C, L, AER, DC>,
    crossinline configurer: ApplicationFactoryConfigurer<C, AER, DC>.() -> Unit = {}
): A {
    return factory.create(toConfigurerFunction(configurer)).launch()
}

/**
 * 构建一个 [Application] 并异步地启动它。
 */
@JvmOverloads
public fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplicationAsync(
    scope: CoroutineScope,
    factory: ApplicationFactory<A, C, L, AER, DC>,
    configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>? = null
): Async<A> {
    val launcher = runCatching {
        factory.create(configurer?.let { c ->
            toConfigurerFunction {
                c.invokeWith(this)
            }
        })
    }.getOrElse { e ->
        return CompletableDeferred<A>().apply {
            completeExceptionally(e)
        }.asAsync()
    }

    return scope.toAsync { launcher.launch() }
}


/**
 * 构建一个 [Application] 并异步地启动它。
 */
@OptIn(DelicateCoroutinesApi::class)
@JvmOverloads
public fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplicationAsync(
    factory: ApplicationFactory<A, C, L, AER, DC>,
    configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>? = null
): Async<A> = launchApplicationAsync(scope = GlobalScope, factory = factory, configurer = configurer)
