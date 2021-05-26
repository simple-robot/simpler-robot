/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai

import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.component.mirai.configuration.MiraiConfiguration
import net.mamoe.mirai.utils.BotConfiguration

/**
 * 用于获取一个根据bot对应的 [BotConfiguration] 实例。
 */
public interface MiraiBotConfigurationFactory {
    fun getMiraiBotConfiguration(botInfo: BotVerifyInfo, simbotMiraiConfig: MiraiConfiguration): BotConfiguration
}


/**
 * 默认的 [MiraiBotConfigurationFactory] 实现。
 */
public object DefaultMiraiBotConfigurationFactory : MiraiBotConfigurationFactory {
    override fun getMiraiBotConfiguration(botInfo: BotVerifyInfo, simbotMiraiConfig: MiraiConfiguration): BotConfiguration {
        return simbotMiraiConfig.botConfiguration(botInfo.code)
    }
}



