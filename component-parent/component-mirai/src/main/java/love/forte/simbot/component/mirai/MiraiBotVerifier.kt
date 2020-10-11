/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiBotVerifer.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai

import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.containers.BotInfo
import love.forte.simbot.core.api.sender.BotSender
import love.forte.simbot.core.api.sender.MsgSenderFactories
import love.forte.simbot.core.bot.Bot
import love.forte.simbot.core.bot.BotRegisterInfo
import love.forte.simbot.core.bot.BotVerifier


/**
 * mirai bot验证器。
 */
public class MiraiBotVerifier : BotVerifier {



    override fun verity(botInfo: BotRegisterInfo, msgSenderFactories: MsgSenderFactories): Bot {
        TODO("Not yet implemented")
    }

}





internal class MiraiBot(
    private val bot: net.mamoe.mirai.Bot,
    override val sender: BotSender,
    override val botInfo: BotInfo
) : Bot {
    override fun close() {
        runCatching {
            bot.close()
        }
    }
}