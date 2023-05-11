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
