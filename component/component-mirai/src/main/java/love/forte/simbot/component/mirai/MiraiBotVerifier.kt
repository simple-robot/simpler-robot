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

import kotlinx.coroutines.runBlocking
import love.forte.common.ioc.DependCenter
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.sender.BotSender
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.api.sender.toBotSender
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.bot.BotVerifier
import love.forte.simbot.component.mirai.configuration.MiraiConfiguration
import love.forte.simbot.component.mirai.message.result.MiraiBotInfo
import love.forte.simbot.component.mirai.utils.MiraiBotEventRegistrar
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.listener.MsgGetProcessor
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch
import net.mamoe.mirai.Bot as MBot

/**
 * mirai bot验证器。
 */
public class MiraiBotVerifier(
    private val configurationFactory: MiraiBotConfigurationFactory,
    private val miraiConfiguration: MiraiConfiguration,
    private val httpTemplate: HttpTemplate,
    private val miraiBotEventRegistrar: MiraiBotEventRegistrar,
    private val dependCenter: DependCenter,
) : BotVerifier {
    private companion object : TypedCompLogger(MiraiBotVerifier::class.java)


    /**
     * 验证（登录）信息并得到要给 [Bot] 实例。
     */
    override fun verity(
        botInfo: BotRegisterInfo,
        msgSenderFactories: MsgSenderFactories,
        defFactories: DefaultMsgSenderFactories,
    ): Bot {
        // try to login bot.
        logger.debug("verify bot code: {}", botInfo.code)


        var mBot: net.mamoe.mirai.Bot? = null


        runCatching {

            mBot = BotFactory.newBot(botInfo.code.toLong(),
                botInfo.verification,
                configurationFactory.getMiraiBotConfiguration(botInfo, miraiConfiguration)
            )

            runBlocking {
                mBot!!.alsoLogin()
            }

            with(mBot!!.logger) {
                if (this is MiraiLoggerWithSwitch) {
                    // 临时关闭logger.
                    this.disable()
                }
            }

            val botContainer = BotContainer { MiraiBotInfo(mBot!!, httpTemplate) }

            val sender = msgSenderFactories.toBotSender(botContainer, defFactories)

            // if started
            if (miraiBotEventRegistrar.started) {
                val msgGetProcessor = dependCenter[MsgGetProcessor::class.java]
                miraiBotEventRegistrar.registerSimbotEvents(mBot!!, msgGetProcessor)
            }

            return MiraiBot(mBot!!, sender, botContainer.botInfo)
        }.getOrElse {
            logger.error("Verifier bot(${botInfo.code}) failed.")
            mBot?.close(it)
            throw IllegalStateException("Cannot verifier bot code: ${botInfo.code}", it)
        }
    }

}


/**
 * Mirai[Bot].
 */
internal class MiraiBot(
    private val bot: MBot,
    override val sender: BotSender,
    override val botInfo: BotInfo,
) : Bot {
    override fun close() {
        runCatching {
            bot.close()
        }
    }

    override fun toString(): String = bot.toString()
}