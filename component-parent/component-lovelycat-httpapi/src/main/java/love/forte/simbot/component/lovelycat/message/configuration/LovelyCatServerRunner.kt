/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatServerRunner.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.lovelycat.message.configuration

import love.forte.common.ioc.DependCenter
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.component.lovelycat.LovelyCatHttpServer
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.listener.ListenerManager
import love.forte.simbot.listener.ListenerRegistered
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ComponentBeans
public class LovelyCatServerRunner : ListenerRegistered {

    @Depend
    lateinit var lovelyCatServerProperties: LovelyCatServerProperties

    @Depend
    lateinit var dependCenter: DependCenter

    /**
     * 监听函数注册结束。
     * 启动http服务（如果需要）
     */
    override fun onRegistered(manager: ListenerManager) {
        if (lovelyCatServerProperties.enableHttpServer) {
            val lovelyCatHttpServer: LovelyCatHttpServer = dependCenter[LovelyCatHttpServer::class.java]
            val port: Int = lovelyCatServerProperties.port
            logger.debug("try to start lovely cat http server on port $port")
            lovelyCatHttpServer.start()
            logger.info("lovely cat http server started. port: $port")
        } else {
            logger.info("lovely cat http server is disabled.")
        }
    }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LovelyCatServerRunner::class.java)
    }
}