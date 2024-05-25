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

package love.forte.simbot.spring2

import love.forte.simbot.spring2.configuration.*
import love.forte.simbot.spring2.configuration.application.*
import love.forte.simbot.spring2.configuration.binder.DefaultBinderManagerProvidersConfiguration
import love.forte.simbot.spring2.configuration.binder.ResolveBinderManagerProcessor
import love.forte.simbot.spring2.configuration.config.DefaultSimbotApplicationConfigurationProcessorConfiguration
import love.forte.simbot.spring2.configuration.listener.SimbotEventListenerFunctionProcessor
import love.forte.simbot.spring2.warn.MigratingSpringBootVersion
import love.forte.simbot.spring2.warn.MigratingSpringBootVersionWarningPrinter
import org.springframework.context.annotation.Import

/**
 * 启用 simbot 的各项配置。
 *
 * 将其标记在你的启动类或某个配置类上。
 *
 * Kotlin
 *
 * ```kotlin
 * @EnableSimbot
 * @SpringBootApplication
 * open class MyApplication {
 *  // ...
 * }
 * ```
 *
 * ```kotlin
 * @EnableSimbot
 * @Configuration
 * open class MyConfig {
 *  // ...
 * }
 * ```
 *
 * Java
 *
 * ```java
 * @EnableSimbot
 * @SpringBootApplication
 * public class MyApplication {
 *  // ...
 * }
 * ```
 *
 * ```java
 * @EnableSimbot
 * @Configuration
 * public class MyConfig {
 *  // ...
 * }
 * ```
 *
 */
@Target(AnnotationTarget.CLASS)
@Import(
    MigratingSpringBootVersionWarningPrinter::class,
    SimbotSpringPropertiesConfiguration::class,
    // defaults
    DefaultSimbotApplicationConfigurationProcessorConfiguration::class,
    DefaultSimbotDispatcherProcessorConfiguration::class,
    DefaultSimbotComponentInstallProcessorConfiguration::class,
    DefaultSimbotPluginInstallProcessorConfiguration::class,
    DefaultSimbotEventDispatcherProcessorConfiguration::class,
    DefaultBinderManagerProvidersConfiguration::class,
    // listeners directly
    DefaultSimbotEventListenerRegistrarProcessorConfiguration::class,
    // launcher factory & launcher
    DefaultSimbotSpringApplicationLauncherFactoryConfiguration::class,
    DefaultSimbotApplicationLauncherFactoryProcessorConfiguration::class,
    SimbotApplicationConfiguration::class,
    // app
    DefaultSimbotSpringApplicationProcessorConfiguration::class,
    // binders
    ResolveBinderManagerProcessor::class,
    // post listeners
    SimbotEventListenerFunctionProcessor::class,
    SimbotApplicationRunner::class
)
@MigratingSpringBootVersion
public annotation class EnableSimbot
