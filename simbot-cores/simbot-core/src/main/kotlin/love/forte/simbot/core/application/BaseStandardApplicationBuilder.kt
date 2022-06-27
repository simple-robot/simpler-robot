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
        
        return simpleListenerManager(initial = initial, block = fun SimpleListenerManagerConfiguration.() {
            listenerManagerConfig(environment)
        })
    }
    
    
}