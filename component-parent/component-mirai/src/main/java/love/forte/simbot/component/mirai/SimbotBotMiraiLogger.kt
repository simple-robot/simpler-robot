/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-mirai
 * File     SimbotBotMiraiLogger.kt
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


/**
 * 通过bot参数在日志中输出不同的bot账号
 * 可能会导致日志过长.
 * @author ForteScarlet <ForteScarlet@163.com>
 * 2020/8/11
 */
public class SimbotBotMiraiLogger(botCode: Long) : MiraiLoggerPlatformBase() {

    /**
     * 日志的标记. 在 Mirai 中, identity 可为
     * - "Bot"
     * - "BotNetworkHandler"
     * 等.
     *
     * 它只用于帮助调试或统计. 十分建议清晰定义 identity
     */
    override val identity: String = "${SimbotBotMiraiLogger::class.java.name}-$botCode"

    private val logger: Logger = LoggerFactory.getLogger(identity)


    override fun debug0(message: String?, e: Throwable?) {
        e?.let { logger.debug(message, it) } ?: logger.debug(message)
    }

    override fun error0(message: String?, e: Throwable?) {
        e?.let { logger.error(message, it) } ?: logger.error(message)
    }

    override fun info0(message: String?, e: Throwable?) {
        e?.let { logger.info(message, it) } ?: logger.info(message)
    }

    override fun verbose0(message: String?, e: Throwable?) {
        e?.let { logger.trace(message, it) } ?: logger.trace(message)
    }

    override fun warning0(message: String?, e: Throwable?) {
        e?.let { logger.warn(message, it) } ?: logger.warn(message)
    }

}