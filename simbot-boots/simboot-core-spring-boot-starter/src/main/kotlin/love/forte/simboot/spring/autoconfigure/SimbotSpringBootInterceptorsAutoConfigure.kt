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

package love.forte.simboot.spring.autoconfigure

import love.forte.di.BeanContainer
import love.forte.di.all
import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.core.event.EventInterceptorsGenerator
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import kotlin.reflect.full.isSubclassOf

/**
 *
 * 自动扫描并注册所有的全局拦截器的spring boot starter配置类。
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootInterceptorsAutoConfigure : SimbotSpringBootApplicationBuildConfigure,
    ApplicationContextAware {
    
    private lateinit var beanContainer: BeanContainer
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        beanContainer = SpringBeanContainer(applicationContext)
    }
    
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        eventProcessor {
            interceptors {
                processingInterceptors()
                listenerInterceptors()
            }
        }
    }
    
    private fun EventInterceptorsGenerator.processingInterceptors() {
        beanContainer.all<EventProcessingInterceptor>().forEach { name ->
            val instance = beanContainer[name] as EventProcessingInterceptor
            processingIntercept(name, instance)
        }
    }
    
    private fun EventInterceptorsGenerator.listenerInterceptors() {
        beanContainer.all<EventListenerInterceptor>().forEach { name ->
            val type = beanContainer.getType(name)
            if (type.isSubclassOf(AnnotatedEventListenerInterceptor::class)) {
                // 不使用 AnnotatedEventListenerInterceptor
                return@forEach
            }
            val instance = beanContainer[name, type] as EventListenerInterceptor
            listenerIntercept(id = name, instance)
        }
    }
    
}