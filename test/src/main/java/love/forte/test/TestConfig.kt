/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     TestConfig.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.test

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.containers.BotInfo
import love.forte.simbot.core.api.sender.MsgSenderFactories
import love.forte.simbot.core.api.sender.toBotSender
import love.forte.simbot.core.bot.*


@ConfigBeans
public class TestConfig {


    @Beans
    fun botVerify(): BotVerifier = object : BotVerifier {
        override fun verity(botInfo: BotRegisterInfo, msgSenderFactories: MsgSenderFactories): Bot {
            return NoNeedToCloseBot(msgSenderFactories.toBotSender(BotContainerObj), BotInfoObj)
        }

    }

}


object BotInfoObj : BotInfo {
    override val botCode: String = "123"
    override val botName: String = "forte"
    override val botAvatar: String = "head"
}

object BotContainerObj : BotContainer {
    override val botInfo: BotInfo = BotInfoObj
}


