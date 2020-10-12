/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreListenerRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.annotation.PostPass
import love.forte.simbot.core.CompLogger
import love.forte.simbot.core.api.message.containers.BotInfo
import love.forte.simbot.core.bot.BotManager
import love.forte.simbot.core.listener.ListenerManager
import love.forte.simbot.core.listener.ListenerRegistered
import love.forte.simbot.core.listener.PostListenerRegistrar


@ConfigBeans
public class CoreListenerRegistrar {
    private companion object : CompLogger("CoreListenerRegistrars")

    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory

    @Depend
    private lateinit var listenerManager: ListenerManager

    @Depend
    private lateinit var botManager: BotManager


    @PostPass
    public fun registerListeners(){

        logger.debug("Start register listeners.")

        // do register.
        dependBeanFactory.allBeans.mapNotNull {
            if (PostListenerRegistrar::class.java.isAssignableFrom(dependBeanFactory.getType(it))) {
                dependBeanFactory[it] as PostListenerRegistrar
            } else null
        }.forEach {
            logger.debug("Starter register listeners by $it.")
            it.registerListenerFunctions(listenerManager)
        }

        logger.debug("Listeners registered.")

        // do done.
        dependBeanFactory.allBeans.mapNotNull {
            if (ListenerRegistered::class.java.isAssignableFrom(dependBeanFactory.getType(it))) {
                dependBeanFactory[it] as ListenerRegistered
            } else null
        }.forEach {
            logger.debug("Do registered by $it.")
            it.onRegistered(listenerManager)
        }


        // show bots.
        botManager.bots.forEach {
            val info: BotInfo = it.botInfo
            if (info.botLevel >= 0) {
                logger.info("Start the registration Bot: code={}, name={}, level={}", info.botCode, info.botName, info.botLevel)
            } else {
                logger.info("Start the registration Bot: code={}, name={}", info.botCode, info.botName)
            }

        }

    }
}