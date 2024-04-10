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

package love.forte.simbot.core.application

import love.forte.simbot.application.*
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration

/**
 * 通过 [Simple] 工厂构建可得的 [Application] 实现类型。
 *
 */
public interface SimpleApplication : Application {
    override val configuration: SimpleApplicationConfiguration
}

/**
 * 使用 [Simple] 构建 [SimpleApplication] 时与之对应的 [ApplicationConfiguration] 类型扩展。
 *
 */
public interface SimpleApplicationConfiguration : ApplicationConfiguration {
    // properties?
}

/**
 * 针对 [Simple] 工厂构建 [SimpleApplication] 的 [ApplicationLauncher] 实现。
 */
public interface SimpleApplicationLauncher : ApplicationLauncher<SimpleApplication>


/**
 * 构建一个 [SimpleApplication] 并启动它。
 *
 * ```kotlin
 * val app = launchSimpleApplication {
 *     // ...
 * }
 * ```
 *
 */
public suspend inline fun launchSimpleApplication(
    crossinline configurer: ApplicationFactoryConfigurer<
        SimpleApplicationBuilder,
        ApplicationEventRegistrar,
        SimpleEventDispatcherConfiguration
        >.() -> Unit = {}
): SimpleApplication {
    return launchApplication(Simple, configurer)
}
