/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("Shutdown")
package love.forte.simbot.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread


private val shutdownLogger: Logger = LoggerFactory.getLogger("love.forte.simbot.shutdown")


/**
 * 注册一个shutdown hook。
 */
public fun onShutdown(name: String? = null, logger: Logger = shutdownLogger, block: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "$name-shutdown-hook") {
        logger.debug("Try shutdown {}...", name)
        val s = System.currentTimeMillis()
        block()
        logger.debug("$name shutdown successfully on {} ms.", System.currentTimeMillis() - s)
    })
}





