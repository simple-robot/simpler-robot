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

package love.forte.simbot.spring

import love.forte.simbot.spring.configuration.*
import love.forte.simbot.spring.configuration.application.*
import love.forte.simbot.spring.configuration.binder.DefaultBinderManagerProvidersConfiguration
import love.forte.simbot.spring.configuration.binder.ResolveBinderManagerProcessor
import love.forte.simbot.spring.configuration.config.DefaultSimbotApplicationConfigurationProcessorConfiguration
import love.forte.simbot.spring.configuration.listener.SimbotEventListenerFunctionProcessor
import org.springframework.context.annotation.Import


@Target(AnnotationTarget.CLASS)
@Import(
    SimbotSpringPropertiesConfiguration::class,
    // defaults
    DefaultSimbotApplicationConfigurationProcessorConfiguration::class,
    DefaultSimbotDispatcherProcessorConfiguration::class,
    DefaultSimbotComponentInstallProcessorConfiguration::class,
    DefaultSimbotPluginInstallProcessorConfiguration::class,
    DefaultSimbotSpringApplicationLauncherFactoryConfiguration::class,
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
public annotation class EnableSimbot
