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

import love.forte.simboot.annotation.Listener
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.LoggerFactory
import love.forte.simbot.core.application.listeners
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import love.forte.simbot.event.EventListenerRegistrationDescription
import love.forte.simbot.event.EventListenerRegistrationDescriptionBuilder
import org.springframework.beans.factory.annotation.Autowired


/**
 *
 * 自动扫描、解析、注册当前SpringBoot依赖环境中存在的所有 [EventListener] 的配置器。
 *
 * 这其中包括了标记 [Listener] 注解的函数以及直接对 [EventListener] 进行实现的实现类。
 *
 *
 * @author ForteScarlet
 */
@Suppress("SpringJavaAutowiredMembersInspection")
public open class SimbotSpringBootListenerAutoRegisterBuildConfigure :
    SimbotSpringBootApplicationBuildConfigure
{
    @Autowired(required = false)
    private var listeners: List<EventListener>? = null
    
    @Autowired(required = false)
    private var listenerRegistrationDescriptions: List<EventListenerRegistrationDescription>? = null
    
    @Autowired(required = false)
    private var listenerBuilders: List<EventListenerBuilder>? = null
    
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        logger.debug("The size of resolved listeners is {}", listeners?.size ?: -1)
        logger.debug("The size of resolved listener builders is {}", listenerBuilders?.size ?: -1)
        
        listeners {
            listeners?.forEach {
                listener(it)
                logger.debug("Registered listener {}", it)
            }
            listenerRegistrationDescriptions?.forEach {
                listener(it)
                logger.debug("Registered listener description {}", it)
            }
            listenerBuilders?.forEach { builder ->
                if (builder is EventListenerRegistrationDescriptionBuilder) {
                    val description = builder.buildDescription()
                    listener(description)
                    logger.debug(
                        "Registered listener registration description [{}] by builder [{}]",
                        description,
                        builder
                    )
                    
                } else {
                    val listener = builder.build()
                    listener(listener)
                    logger.debug("Registered listener [{}] by builder [{}]", listener, builder)
                }
            }
        }
    }
    
    private companion object {
        private val logger = LoggerFactory.getLogger(SimbotSpringBootListenerAutoRegisterBuildConfigure::class)
    }
}
