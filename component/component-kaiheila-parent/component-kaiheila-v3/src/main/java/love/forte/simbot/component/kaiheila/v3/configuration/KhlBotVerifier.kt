package love.forte.simbot.component.kaiheila.v3.configuration

import love.forte.common.ioc.DependCenter
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.*
import love.forte.simbot.core.SimbotApp
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.kaiheila.*
import love.forte.simbot.kaiheila.api.v3.channel.ChannelListReq
import love.forte.simbot.kaiheila.api.v3.v3WsBot
import love.forte.simbot.listener.ListenerManager
import love.forte.simbot.listener.ListenerRegistered
import love.forte.simbot.listener.MsgGetProcessor

/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
public class KhlBotVerifier(
    private val dependCenter: DependCenter,
) : BotVerifier, ListenerRegistered {

    private val msgGetProcessor: MsgGetProcessor
        get() = dependCenter[MsgGetProcessor::class.java]

    internal companion object : TypedCompLogger(KhlBotVerifier::class.java)

    @Volatile
    private var isOnRegistered = false
    private var waitForRegisterEvents: MutableList<KhlBot>? = mutableListOf()

    override fun verity(
        botInfo: BotVerifyInfo,
        msgSenderFactories: MsgSenderFactories,
        defFactories: DefaultMsgSenderFactories,
    ): Bot {
        var bot: KhlBot? = null
        val clientId = botInfo.find("code", "id", "clientId")
        val token = botInfo.find("token", "verification", "password")
        val clientSecret = botInfo.find("clientSecret")

        kotlin.runCatching {
            val kb = v3WsBot(clientId, token, clientSecret)
            bot = kb
            logger.info("Verifier bot: {}#{}", kb.botName, kb.botCode)
            registerEvent(kb)
            kb.startBot().also {
                SimbotApp.onJoined { kb.join() }
            }
            logger.info("Bot started. {}", kb)
        }.getOrElse {
            logger.error("Verifier bot({}) failed.", clientId)
            bot?.closeBot()
            throw BotVerifyException("Cannot verifier bot clientId: $$clientId", it)
        }

        return bot!!
    }


    private fun registerEvent(bot: KhlBot) {
        if (isOnRegistered) {
            bot.listenMsgGet()
        } else {
            waitForRegisterEvents!!.add(bot)
        }
    }

    override fun onRegistered(manager: ListenerManager) {
        isOnRegistered = true
        waitForRegisterEvents!!.forEach { bot ->
            bot.listenMsgGet()
        }.also {
            waitForRegisterEvents = null
        }
    }

    private fun KhlBot.listenMsgGet() {
        val msgGetProcessor0 = msgGetProcessor
        listenPrecise<MsgGet> { msgGet ->
            if (msgGet.accountInfo.accountCodeNumber != this.botCodeNumber) {
                msgGetProcessor0.onMsg(msgGet)
            }
        }
        logger.debug("Bot {} simbot listener registered.", this)
    }
}