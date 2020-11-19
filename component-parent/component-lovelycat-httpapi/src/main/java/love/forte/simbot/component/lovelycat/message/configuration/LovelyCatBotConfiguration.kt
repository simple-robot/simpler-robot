/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatBotVerifierConfiguration.kt
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

import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.bot.BotVerifier
import love.forte.simbot.core.configuration.ComponentBeans

/**
 * lovely cat 验证器。
 */
@ComponentBeans
public class LovelyCatBotVerifier : BotVerifier {
    /** 验证一个bot的注册信息，并转化为一个该组件对应的 [Bot] 实例。 */
    override fun verity(botInfo: BotRegisterInfo, msgSenderFactories: MsgSenderFactories): Bot {
        TODO("Not yet implemented")
    }
}


