/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.autoconfigure

import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simboot.core.configuration.CoreEventListenerManagerContextFactory
import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simbot.ID
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.event.EventProcessingInterceptor
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import kotlin.coroutines.CoroutineContext

/**
 * 配置 [EventListenerManager]
 * @author ForteScarlet
 */
public open class SpringEventListenerManagerConfiguration {

    @Bean
    @ConditionalOnMissingBean(CoreEventListenerManagerContextFactory::class)
    public open fun defaultCoreEventListenerManagerContextFactory(executor: ThreadPoolTaskExecutor): CoreEventListenerManagerContextFactory {
        return object : CoreEventListenerManagerContextFactory {
            override val managerCoroutineContext: CoroutineContext = executor.asCoroutineDispatcher()
        }
    }

    @Bean
    @ConditionalOnMissingBean(EventListenerManager::class)
    public open fun eventListenerManager(
        beanFactory: ListableBeanFactory,
        contextFactory: CoreEventListenerManagerContextFactory
    ): EventListenerManager {
        val listenerInterceptorClass = EventListenerInterceptor::class.java
        val listenerInterceptors = beanFactory.getBeanNamesForType(listenerInterceptorClass).associate { name ->
            name.ID as ID to beanFactory.getBean(name, listenerInterceptorClass)
        }

        val eventInterceptorClass = EventProcessingInterceptor::class.java
        val processingInterceptors = beanFactory.getBeanNamesForType(eventInterceptorClass).associate { name ->
            name.ID as ID to beanFactory.getBean(name, eventInterceptorClass)
        }

        val context = contextFactory.managerCoroutineContext

        return coreListenerManager {
            if (processingInterceptors.isNotEmpty()) {
                addProcessingInterceptors(processingInterceptors)
            }
            if (listenerInterceptors.isNotEmpty()) {
                addListenerInterceptors(listenerInterceptors.filterValues { it !is AnnotatedEventListenerInterceptor }) // 不追加注解拦截器
            }

            coroutineContext = context
        }
    }

}

