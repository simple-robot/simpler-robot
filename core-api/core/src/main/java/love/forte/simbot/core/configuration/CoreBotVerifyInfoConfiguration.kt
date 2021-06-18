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

import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.bot.BotResourceType
import love.forte.simbot.bot.SimpleBotVerifyInfoConfiguration
import love.forte.simbot.bot.botVerifyInfoBySplit


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
@AsCoreConfig
class CoreBotVerifyInfoConfiguration {

    @ConfigInject("botResourceType")
    private var botResourceType: BotResourceType = BotResourceType.FILE_FIRST

    @ConfigInject("bots", orDefault = [""])
    lateinit var bots: List<String>

    @ConfigInject("actionBots", orDefault = ["*"])
    lateinit var actionBots: List<String>


    @SpareBeans
    fun botVerifyInfoConfiguration(): SimpleBotVerifyInfoConfiguration {
        val otherBots = if (bots.isNotEmpty()) {
            bots.asSequence().distinct().map { botVerifyInfoBySplit(it) }.toList()
        } else emptyList()

        return SimpleBotVerifyInfoConfiguration(botResourceType, actionBots = actionBots, other = otherBots)
    }

}