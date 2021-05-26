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

package love.forte.simbot.component.mirai.configuration

import kotlinx.coroutines.runBlocking
import love.forte.common.ioc.DependCenter
import love.forte.simbot.LogAble
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.botContainer
import love.forte.simbot.api.sender.BotSender
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.api.sender.toBotSender
import love.forte.simbot.bot.*
import love.forte.simbot.component.mirai.MiraiBotConfigurationFactory
import love.forte.simbot.component.mirai.MiraiBotInfo
import love.forte.simbot.component.mirai.utils.MiraiBotEventRegistrar
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.listener.MsgGetProcessor
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch
import org.slf4j.Logger
import net.mamoe.mirai.Bot as MBot
import net.mamoe.mirai.Bot.Companion as MiraiBot




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
    internal companion object : TypedCompLogger(MiraiBotVerifier::class.java) {
        fun MBot.toContainer(httpTemplate: HttpTemplate?): BotContainer = botContainer { MiraiBotInfo.getInstance(this, httpTemplate) }
        fun MsgSenderFactories.getBotSender(botContainer: BotContainer,  defFactories: DefaultMsgSenderFactories): BotSender = this.toBotSender(botContainer, defFactories)
    }


    /**
     * 验证（登录）信息并得到要给 [Bot] 实例。
     */
    override fun verity(
        botInfo: BotVerifyInfo,
        msgSenderFactories: MsgSenderFactories,
        defFactories: DefaultMsgSenderFactories,
    ): Bot {
        // try to login bot.
        logger.debug("verify bot code: {}", botInfo.code)


        var mBot: net.mamoe.mirai.Bot? = null



        runCatching {
            mBot = MiraiBot.getInstanceOrNull(botInfo.code.toLong())

            // 如果此bot尚未登录，则登录。
            if (mBot == null) {
                mBot = BotFactory.newBot(botInfo.code.toLong(),
                    requireNotNull(botInfo.verification) { "Bot verification (password) was null." },
                    configurationFactory.getMiraiBotConfiguration(botInfo, miraiConfiguration)
                )

                runBlocking {
                    mBot!!.alsoLogin()
                }

                // 只有从未登录过的时候才会临时关闭logger.
                with(mBot!!.logger) {
                    if (this is MiraiLoggerWithSwitch) {
                        // 临时关闭logger.
                        this.disable()
                    }
                }
            }


            val botContainer = mBot!!.toContainer(httpTemplate)

            // val botContainer = botContainer { MiraiBotInfo.getInstance(mBot!!, httpTemplate) }

            // val sender = msgSenderFactories.toBotSender(botContainer, defFactories)
            val sender = msgSenderFactories.getBotSender(botContainer, defFactories)

            // if started
            if (miraiBotEventRegistrar.started) {
                val msgGetProcessor = dependCenter[MsgGetProcessor::class.java]
                miraiBotEventRegistrar.registerSimbotEvents(mBot!!, msgGetProcessor)
            }

            return MiraiBot(mBot!!, sender, botContainer.botInfo)
        }.getOrElse {
            logger.error("Verifier bot(${botInfo.code}) failed.")
            mBot?.close(it)
            throw BotVerifyException("Cannot verifier bot code: ${botInfo.code}", it)
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
) : Bot, LogAble {
    override val log: Logger = miraiBotLogger(bot.id)

    override fun close() {
        runCatching {
            val id = bot.id
            bot.close()
            // Destroy bot self cache.
            MiraiBotInfo.destroyBotInfo(id)
        }.getOrElse {  }
    }

    override fun toString(): String = bot.toString()

    override fun equals(other: Any?): Boolean = bot == other
    override fun hashCode(): Int = bot.hashCode()
}