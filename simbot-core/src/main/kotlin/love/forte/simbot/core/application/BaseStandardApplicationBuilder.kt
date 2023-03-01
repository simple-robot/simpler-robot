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

package love.forte.simbot.core.application

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationBuilderDsl
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.core.event.SimpleEventListenerManager
import love.forte.simbot.core.event.SimpleListenerManagerConfiguration
import love.forte.simbot.core.event.simpleListenerManager

// public interface CoreApplicationBuilder<A : Application>

/**
 * 约定使用 [SimpleEventListenerManager] 作为事件处理器的 [ApplicationBuilder] 类型。
 */
public interface StandardApplicationBuilder<A : Application> : EventProcessableApplicationBuilder<A> {
    
    /**
     * 配置当前的构建器内的事件处理器。
     */
    @ApplicationBuilderDsl
    override fun eventProcessor(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit)
}


/**
 *
 * 提供一个使用 [SimpleEventListenerManager] 作为内部事件处理器的 [ApplicationBuilder] 抽象类。
 *
 * @author ForteScarlet
 */
public abstract class BaseStandardApplicationBuilder<A : Application> : BaseApplicationBuilder<A>(),
    StandardApplicationBuilder<A> {
    
    private var listenerManagerConfig: (SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit) =
        {}
    
    protected open fun addListenerManagerConfig(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit) {
        listenerManagerConfig.also { old ->
            listenerManagerConfig = {
                old(it)
                configurator(it)
            }
        }
    }
    
    /**
     * 配置当前的构建器内的事件处理器。
     */
    override fun eventProcessor(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit) {
        addListenerManagerConfig(configurator)
    }
    
    /**
     * 构建并得到目标 [SimpleEventListenerManager].
     */
    protected open fun buildListenerManager(
        appConfig: ApplicationConfiguration,
        environment: Application.Environment,
    ): SimpleEventListenerManager {
        val initial = SimpleListenerManagerConfiguration {
            coroutineContext = appConfig.coroutineContext
        }
        
        return simpleListenerManager(initial = initial) {
            listenerManagerConfig(environment)
        }
    }
    
    
}
