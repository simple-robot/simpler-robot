/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.core.application

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationBuilderDsl
import love.forte.simbot.core.event.EventListenerRegistrationDescriptionsGenerator
import love.forte.simbot.core.event.SimpleListenerManagerConfiguration


/**
 * 允许进行事件处理器的配置的 [ApplicationBuilder].
 * @author ForteScarlet
 */
public interface EventProcessableApplicationBuilder<A : Application> :
    ApplicationBuilder<A> {
    
    /**
     * 配置内部的 core listener manager.
     *
     */
    @ApplicationBuilderDsl
    public fun eventProcessor(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit)
    
}


/**
 * 配置 [EventProcessableApplicationBuilder.eventProcessor] 的 `listeners`.
 *
 * 相当于
 * ```kotlin
 * eventProcessor { env ->
 *    listeners {
 *       block(env)
 *    }
 * }
 * ```
 */
@ApplicationBuilderDsl
public inline fun EventProcessableApplicationBuilder<*>.listeners(crossinline block: EventListenerRegistrationDescriptionsGenerator.(environment: Application.Environment) -> Unit) {
    eventProcessor { env ->
        listeners {
            block(env)
        }
    }
}
