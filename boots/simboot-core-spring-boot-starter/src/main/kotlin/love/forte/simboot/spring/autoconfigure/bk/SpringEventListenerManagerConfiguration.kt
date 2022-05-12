// /*
//  *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
//  *
//  *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
//  *
//  *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
//  *
//  *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
//  *
//  *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
//  *  https://www.gnu.org/licenses
//  *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
//  *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
//  *
//  */
//
// package love.forte.simboot.spring.autoconfigure.bk
//
// import kotlinx.coroutines.CoroutineDispatcher
// import kotlinx.coroutines.asCoroutineDispatcher
// import love.forte.simboot.config.ComponentRegistryConfigure
// import love.forte.simboot.config.InstallAllComponentRegistryConfigure
// import love.forte.simboot.core.configuration.CoreEventListenerManagerContextFactory
// import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
// import love.forte.simboot.spring.autoconfigure.SimbotEventDispatcherContainer
// import love.forte.simbot.ID
// import love.forte.simbot.core.event.coreListenerManager
// import love.forte.simbot.event.EventListenerInterceptor
// import love.forte.simbot.event.EventListenerManager
// import love.forte.simbot.event.EventProcessingInterceptor
// import org.springframework.beans.factory.ListableBeanFactory
// import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
// import org.springframework.context.annotation.Bean
// import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
// import kotlin.coroutines.CoroutineContext
//
//
//
// /**
//  * 配置 [EventListenerManager]
//  * @author ForteScarlet
//  */
// public open class SpringEventListenerManagerConfiguration {
//
//     @Bean
//     @ConditionalOnMissingBean(SimbotEventDispatcherContainer::class)
//     public open fun defaultSimbotEventDispatcher(executor: ThreadPoolTaskExecutor): SimbotEventDispatcherContainer =
//         SimbotEventDispatcherContainer(executor.asCoroutineDispatcher())
//
//
//     @Bean
//     @ConditionalOnMissingBean(CoreEventListenerManagerContextFactory::class)
//     public open fun defaultCoreEventListenerManagerContextFactory(dispatcherContainer: SimbotEventDispatcherContainer): CoreEventListenerManagerContextFactory {
//         val dispatcher = dispatcherContainer.dispatcher
//         return object : CoreEventListenerManagerContextFactory {
//             override val managerCoroutineContext: CoroutineContext = dispatcher
//         }
//     }
//
//     @Bean
//     @ConditionalOnMissingBean(ComponentRegistryConfigure::class)
//     public open fun defaultComponentRegistryConfigure(): InstallAllComponentRegistryConfigure =
//         InstallAllComponentRegistryConfigure
//
//     @Bean
//     @ConditionalOnMissingBean(EventListenerManager::class)
//     public open fun eventListenerManager(
//         beanFactory: ListableBeanFactory,
//         contextFactory: CoreEventListenerManagerContextFactory,
//         componentRegistryConfigures: List<ComponentRegistryConfigure>,
//     ): EventListenerManager {
//         val listenerInterceptorClass = EventListenerInterceptor::class.java
//         val listenerInterceptors = beanFactory.getBeanNamesForType(listenerInterceptorClass).associate { name ->
//             name.ID as ID to beanFactory.getBean(name, listenerInterceptorClass)
//         }
//
//         val eventInterceptorClass = EventProcessingInterceptor::class.java
//         val processingInterceptors = beanFactory.getBeanNamesForType(eventInterceptorClass).associate { name ->
//             name.ID as ID to beanFactory.getBean(name, eventInterceptorClass)
//         }
//
//         val context = contextFactory.managerCoroutineContext
//
//         return coreListenerManager {
//             componentRegistryConfigures.forEach {
//                 // TODO
//                 // it.registerComponent(this)
//             }
//
//             if (processingInterceptors.isNotEmpty()) {
//                 addProcessingInterceptors(processingInterceptors)
//             }
//             if (listenerInterceptors.isNotEmpty()) {
//                 addListenerInterceptors(listenerInterceptors.filterValues { it !is AnnotatedEventListenerInterceptor }) // 不追加注解拦截器
//             }
//
//             coroutineContext = context
//         }
//     }
//
// }
//
