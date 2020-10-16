/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-mirai
 * File     SimbotMiraiLogger.kt
 *
 * You can contact the author through the following channels:
 *  github https://github.com/ForteScarlet
 *  gitee  https://gitee.com/ForteScarlet
 *  email  ForteScarlet@163.com
 *  QQ     1149159218
 *  The Mirai code is copyrighted by mamoe-mirai
 *  you can see mirai at https://github.com/mamoe/mirai
 *
 *
 */

package love.forte.simbot.component.mirai

import net.mamoe.mirai.utils.MiraiLoggerPlatformBase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.reflect.CallerSensitive

/**
 * 默认使用的普通日志实现，不会区分bot
 */
object SimbotMiraiLogger : MiraiLoggerPlatformBase() {
    override val identity: String = "Simbot-Mirai"
    private val logger: Logger = LoggerFactory.getLogger(identity)

    override fun debug0(message: String?, e: Throwable?) {
        e?.let { logger.debug(message, it) } ?: logger.debug("{}", message)
    }

    override fun error0(message: String?, e: Throwable?) {
        e?.let { logger.error(message, it) } ?: logger.error("{}", message)
    }

    override fun info0(message: String?, e: Throwable?) {
        e?.let { logger.info(message, it) } ?: logger.info("{}", message)
    }

    override fun verbose0(message: String?, e: Throwable?) {
        e?.let { logger.trace(message, it) } ?: logger.trace("{}", message)
    }

    override fun warning0(message: String?, e: Throwable?) {
        e?.let { logger.warn(message, it) } ?: logger.warn("{}", message)
    }
}