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

package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simbot.core.event.EventInterceptorsGenerator
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean

/**
 *
 * 自动扫描并注册所有的全局拦截器的spring boot starter配置类。
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootInterceptorsAutoConfigure {

    @Bean
    public fun interceptorAutoConfigure(
        @Autowired(required = false) eventProcessingInterceptors: Map<String, EventProcessingInterceptor>? = null,
        @Autowired(required = false) eventListenerInterceptors: Map<String, EventListenerInterceptor>? = null,
    ): SimbotSpringBootApplicationBuildConfigure {

        fun EventInterceptorsGenerator.processingInterceptors() {
            eventProcessingInterceptors?.forEach { (name, instance) ->
                processingIntercept(name, instance)
            }
        }

        fun EventInterceptorsGenerator.listenerInterceptors() {
            eventListenerInterceptors?.forEach { (name, instance) ->
                if (instance is AnnotatedEventListenerInterceptor) {
                    // 不使用 AnnotatedEventListenerInterceptor
                    return@forEach
                }
                listenerIntercept(name, instance)
            }
        }
        return SimbotSpringBootApplicationBuildConfigure {
            eventProcessor {
                interceptors {
                    processingInterceptors()
                    listenerInterceptors()
                }
            }
        }
    }

}
