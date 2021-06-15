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

@file:JvmName("MiraiBotManagers")
package love.forte.simbot.component.mirai.configuration

import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.*
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.http.template.HttpTemplate
import net.mamoe.mirai.supervisorJob
import java.util.concurrent.ConcurrentHashMap
import net.mamoe.mirai.Bot.Companion as MBot


/**
 *
 * mirai组件下通过 [Mirai-Bot][Bot] 进行Bot管理的 [BotManager].
 *
 * @author ForteScarlet
 */
@ComponentBeans("miraiBotManager")
public class MiraiBotManager(
    private val verifier: BotVerifier,
    private val msgSenderFactories: MsgSenderFactories,
    private val defSenderFactories: DefaultMsgSenderFactories,
    private val httpTemplate: HttpTemplate
) : BotManager {

    private companion object : TypedCompLogger(MiraiBotManager::class.java)

    private val _bots = ConcurrentHashMap<String, MiraiBot>()

    override val defaultBot: Bot
        get() {
            val b = _bots.values.firstOrNull() ?: synchronized(MiraiBotManager) {
                MBot.instancesSequence.firstOrNull()?.let { b ->
                    val botContainer = MiraiBotVerifier.run { b.toContainer() }
                    val botSender = MiraiBotVerifier.run { msgSenderFactories.getBotSender(botContainer, defSenderFactories) }
                    val botInfo = botContainer.botInfo
                    val mb = MiraiBot(b, botSender, botInfo)
                    _bots.putIfAbsent(botInfo.botCode, mb)
                    // remove on.
                    b.supervisorJob.invokeOnCompletion {
                        _bots.remove(b.id.toString())
                    }
                    mb
                }
            }

            return b ?: throw NoSuchBotException("There is no registered bot.")
        }
        // get() = _bots.values.firstOrNull()
        //     // not init,
        //     ?: MBot.instancesSequence.firstOrNull()?.let { b ->
        //     MiraiBot(b, , )
        // } ?: throw NoSuchBotException("There is no registered BOT")

    override fun getBot(id: String): Bot = getBotOrNull(id) ?: throw NoSuchBotException(id)

    override fun getBotOrNull(id: String): Bot? = _bots[id]

    override val bots: List<Bot>
        get() = _bots.values.toList()


    override fun registerBot(botRegisterInfo: BotVerifyInfo): Bot {
        // 如果账号本身就存在，直接返回
        val botCode = botRegisterInfo.code
        val foundBot: Bot? = _bots[botCode]
        if (foundBot != null) {
            // verify mirai bot
            val foundMBot = MBot.getInstanceOrNull(botCode.toLong())
            if (foundMBot == null) {
                // mirai中不存在，则移除此bot并后续进行验证。
                logger.debug("Bot $botCode existed, but cannot found in mirai, register again.")
                _bots.remove(botCode)
            } else {
                // mirai中同样存在，直接返回
                logger.debug("Bot $botCode existed, just return")
                return foundBot
            }
        }

        logger.debug("Verify Bot $botCode.")
        // 账号验证如果通过，查找是否已经存在
        return verifier.verity(botRegisterInfo, msgSenderFactories, defSenderFactories).let { b ->
            val registeredBot = if (b is MiraiBot) b else b.toMiraiBot(msgSenderFactories, defSenderFactories)
            _bots[registeredBot.botInfo.botCode] = registeredBot
            val code = registeredBot.botInfo.botCode
            MBot.getInstance(registeredBot.botInfo.botCodeNumber).supervisorJob.invokeOnCompletion {
                _bots.remove(code)
            }
            registeredBot
        }
    }

    /**
     * 刪除一个bot
     */
    override fun destroyBot(code: String) {
        _bots.remove(code)?.close()
    }
}


/**
 * [Bot] 转化为 [MiraiBot]
 */
private fun Bot.toMiraiBot(msgSenderFactories: MsgSenderFactories, defSenderFactories: DefaultMsgSenderFactories): MiraiBot {
    val info = botInfo
    val mBot = MBot.getInstance(info.botCodeNumber)

    val sender = MiraiBotVerifier.run { msgSenderFactories.getBotSender(this@toMiraiBot, defSenderFactories) }

    return MiraiBot(mBot, sender, info)

}
