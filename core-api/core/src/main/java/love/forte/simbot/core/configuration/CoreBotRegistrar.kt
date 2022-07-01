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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.annotation.PostPass
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfoConfiguration
import love.forte.simbot.bot.code
import love.forte.simbot.core.TypedCompLogger


@AsCoreConfig
@ConfigBeans("coreBotRegistrar")
public class CoreBotRegistrar {
    private companion object : TypedCompLogger(CoreBotRegistrar::class.java)

    /**
     * bot manager.
     */
    @Depend
    lateinit var botManager: BotManager


    @Depend
    lateinit var botVerifyConfiguration: BotVerifyInfoConfiguration


    /**
     * 注册bot。
     */
    @PostPass
    public fun registerBots() {
        val bots = botVerifyConfiguration.configuredBotVerifyInfos

        if (bots.isEmpty() && botManager.bots.isEmpty()) {
            logger.warn("No bot information is configured.")
        } else {

            // show logs.
            bots.forEach { info ->
                val showName = info["name"]
                val show = showName?.takeIf { it.isNotBlank() }?.let { "$it-${info.code}" } ?: info.code
                logger.debug("Configured bot: {}", show)
            }

            bots.distinct().forEach { info ->
                // val info = botVerifyInfoBySplit(it)
                val showName = info["name"]
                val show = showName?.takeIf { it.isNotBlank() }?.let { "$it-${info.code}" } ?: info.code
                logger.debug("Try to verify the bot({}) information.", show)
                botManager.registerBot(info)
            }
        }
    }


}