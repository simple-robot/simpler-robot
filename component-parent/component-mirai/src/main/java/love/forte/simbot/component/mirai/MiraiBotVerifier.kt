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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.mirai.configuration.MiraiConfiguration
import love.forte.simbot.component.mirai.message.MiraiBotInfo
import love.forte.simbot.core.CompLogger
import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.containers.BotInfo
import love.forte.simbot.core.api.sender.BotSender
import love.forte.simbot.core.api.sender.MsgSenderFactories
import love.forte.simbot.core.api.sender.toBotSender
import love.forte.simbot.core.bot.Bot
import love.forte.simbot.core.bot.BotRegisterInfo
import love.forte.simbot.core.bot.BotVerifier
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch
import net.mamoe.mirai.Bot as MBot

/**
 * mirai bot验证器。
 */
public class MiraiBotVerifier(
    private val configurationFactory: MiraiBotConfigurationFactory,
    private val miraiConfiguration: MiraiConfiguration
) : BotVerifier {
    private companion object : CompLogger("MiraiBotVerifier")


    /**
     * 验证（登录）信息并得到要给 [Bot] 实例。
     */
    override fun verity(botInfo: BotRegisterInfo, msgSenderFactories: MsgSenderFactories): Bot {
        // try to login bot.
        logger.debug("verify bot code: {}", botInfo.code)

        val mBot = MBot(
            botInfo.code.toLong(),
            botInfo.verification,
            configurationFactory.getMiraiBotConfiguration(botInfo, miraiConfiguration)
        )
        runCatching {

            with(mBot.logger) {
                if (this is MiraiLoggerWithSwitch) {
                    // 临时关闭logger.
                    this.disable()
                }
            }


            runBlocking {
                mBot.alsoLogin()
            }

            val botContainer = BotContainer { MiraiBotInfo(mBot) }

            val sender = msgSenderFactories.toBotSender(botContainer)

            return MiraiBot(mBot, sender, botContainer.botInfo)
        }.getOrElse {
            mBot.close(it)
            throw IllegalStateException("cannot login bot code: ${botInfo.code}", it)
        }
    }

}


/**
 * Mirai[Bot].
 */
internal class MiraiBot(
    private val bot: MBot,
    override val sender: BotSender,
    override val botInfo: BotInfo
) : Bot {
    override fun close() {
        runCatching {
            bot.close()
        }
    }
}