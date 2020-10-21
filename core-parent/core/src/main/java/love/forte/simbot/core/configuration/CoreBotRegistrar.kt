/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreBotRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.annotation.PostPass
import love.forte.simbot.core.CompLogger
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotRegisterInfo


@ConfigBeans
@AsCoreConfig
public class CoreBotRegistrar {
    private companion object : CompLogger("BotRegistrarConfiguration")

    /**
     * bot manager.
     */
    @Depend
    lateinit var botManager: BotManager


    @ConfigInject("bots", orDefault = [""])
    lateinit var bots: List<String>



    /**
     * 注册bot。
     */
    @PostPass
    public fun registerBots(){
        if(bots.isEmpty()) {
            logger.warn("No bot information is configured.")
        } else {
            bots.forEach {
                val info = BotRegisterInfo.splitTo(it)
                logger.debug("Try to verify the bot(${info.code}) information.")
                botManager.registerBot(info)
            }
        }
    }


}