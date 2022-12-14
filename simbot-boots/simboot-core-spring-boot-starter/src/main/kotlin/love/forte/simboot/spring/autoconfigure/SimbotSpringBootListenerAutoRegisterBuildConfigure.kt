/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simboot.spring.autoconfigure

// import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.application.Application
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import love.forte.simbot.event.EventListenerRegistrationDescription
import love.forte.simbot.event.EventListenerRegistrationDescriptionBuilder
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order


public open class SimbotSpringBootListenerAutoRegisterBuildConfigure {
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 2) // Higher than bot auto start
    public fun simbotSpringBootListenerAutoRegisterPostProcessor(
        @Autowired(required = false) listeners: Map<String, EventListener>? = null,
        @Autowired(required = false) listenerDescriptions: Map<String, EventListenerRegistrationDescription>? = null,
        @Autowired(required = false) listenerBuilders: Map<String, EventListenerBuilder>? = null,
    ): ApplicationPostProcessor {
        return SimbotSpringBootListenerAutoRegisterPostProcessor(
            listeners ?: emptyMap(),
            listenerDescriptions ?: emptyMap(),
            listenerBuilders ?: emptyMap()
        )
    }
}

private class SimbotSpringBootListenerAutoRegisterPostProcessor(
    private val listeners: Map<String, EventListener>,
    private val listenerDescriptions: Map<String, EventListenerRegistrationDescription>,
    private val listenerBuilders: Map<String, EventListenerBuilder>,
) : ApplicationPostProcessor {

    @OptIn(ExperimentalSimbotApi::class)
    override suspend fun processApplication(application: Application) {
        logger.debug("Start listener auto register.")
        logger.debug("The size of resolved listeners is {}", listeners.size)
        logger.debug(
            "The size of resolved listenerRegistrationDescriptions is {}",
            listenerDescriptions.size
        )
        logger.debug("The size of resolved listener builders is {}", listenerBuilders.size)

        listeners.forEach { (name, listener) ->
            application.eventListenerManager.register(listener)
            logger.debug("Registered listener [{}] named {}", listener, name)
        }
        listenerDescriptions.forEach { (name, listener) ->
            application.eventListenerManager.register(listener)
            logger.debug("Registered listener description [{}] named {}", listener, name)
        }
        listenerBuilders.forEach { (name, builder) ->
            if (builder is EventListenerRegistrationDescriptionBuilder) {
                val description = builder.buildDescription()
                application.eventListenerManager.register(description)
                logger.debug(
                    "Registered listener registration description [{}] by builder [{}] named {}",
                    description,
                    builder,
                    name
                )
            } else {
                val listener = builder.build()
                application.eventListenerManager.register(listener)
                logger.debug("Registered listener [{}] by builder [{}] named {}", listener, builder, name)
            }
        }
    }

    private companion object {
        private val logger = LoggerFactory.logger<SimbotSpringBootListenerAutoRegisterPostProcessor>()
    }
}

//
//
///**
// *
// * 自动扫描、解析、注册当前SpringBoot依赖环境中存在的所有 [EventListener] 的配置器。
// *
// * 这其中包括了标记 [Listener] 注解的函数以及直接对 [EventListener] 进行实现的实现类。
// *
// *
// * @author ForteScarlet
// */
//@Suppress("SpringJavaAutowiredMembersInspection")
//@AutoConfigureAfter(SimbotSpringBootApplicationConfiguration::class)
//public open class SimbotSpringBootListenerAutoRegisterBuildConfigure0 : BeanFactoryPostProcessor {
//    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
//        logger.debug("Start listener auto register")
//        try {
//            val application = beanFactory.getBean<Application>()
//            config(application, beanFactory)
//        } catch (nsbEx: NoSuchBeanDefinitionException) {
//            // ignore?
//            logger.warn("No such bean (Application) definition, skip listener register.", nsbEx)
//        }
//    }
//
//    @OptIn(ExperimentalSimbotApi::class)
//    private fun config(application: Application, beanFactory: ConfigurableListableBeanFactory) {
//        val listeners = beanFactory.getBeansOfType(EventListener::class.java)
//        val listenerRegistrationDescriptions =
//            beanFactory.getBeansOfType(EventListenerRegistrationDescription::class.java)
//        val listenerBuilders = beanFactory.getBeansOfType(EventListenerBuilder::class.java)
//        logger.debug("The size of resolved listeners is {}", listeners.size)
//        logger.debug(
//            "The size of resolved listenerRegistrationDescriptions is {}",
//            listenerRegistrationDescriptions.size
//        )
//        logger.debug("The size of resolved listener builders is {}", listenerBuilders.size)
//
//        // listeners {
//        listeners.forEach { (name, listener) ->
//            application.eventListenerManager.register(listener)
//            logger.debug("Registered listener [{}] named {}", listener, name)
//        }
//        listenerRegistrationDescriptions.forEach { (name, listener) ->
//            application.eventListenerManager.register(listener)
//            logger.debug("Registered listener description [{}] named {}", listener, name)
//        }
//        listenerBuilders.forEach { (name, builder) ->
//            if (builder is EventListenerRegistrationDescriptionBuilder) {
//                val description = builder.buildDescription()
//                application.eventListenerManager.register(description)
//                logger.debug(
//                    "Registered listener registration description [{}] by builder [{}] named {}",
//                    description,
//                    builder,
//                    name
//                )
//
//            } else {
//                val listener = builder.build()
//                application.eventListenerManager.register(listener)
//                logger.debug("Registered listener [{}] by builder [{}] named {}", listener, builder, name)
//            }
//        }
//        // }
//    }
//
//    private companion object {
//        private val logger = LoggerFactory.getLogger(SimbotSpringBootListenerAutoRegisterBuildConfigure::class.java)
//    }
//}
