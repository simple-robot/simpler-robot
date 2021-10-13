package love.forte.simbot.kaiheila.v3.comfiguration

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.SimbotIllegalArgumentException
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.*
import love.forte.simbot.kaiheila.KhlBot
import love.forte.simbot.kaiheila.api.v3.sender.KhlV3Getter
import love.forte.simbot.kaiheila.api.v3.sender.KhlV3Sender
import love.forte.simbot.kaiheila.api.v3.sender.KhlV3Setter
import love.forte.simbot.kaiheila.event.BotInitialized


/**
 *
 * V3 khl msg sender 工厂配置类。
 *
 * @author ForteScarlet
 */
@ConfigBeans
class KhlV3MsgSenderConfiguration {
    @Beans
    fun msgSenderFactories() = KhlV3MsgSenderFactories
}




public object KhlV3MsgSenderFactories : MsgSenderFactories {
    override val senderFactory: SenderFactory get() = KhlV3SenderFactory
    override val setterFactory: SetterFactory get() = KhlV3SetterFactory
    override val getterFactory: GetterFactory get() = KhlV3GetterFactory
}


private object KhlV3SenderFactory : SenderFactory {
    override fun getOnMsgSender(msg: MsgGet, def: Sender.Def): Sender {
        if (msg !is BotInitialized) {
            throw SimbotIllegalArgumentException("msg $msg  was not a khl event.")
        }
        // TODO find bot?
        return msg.bot.sender.SENDER //.asMsgSender(msg)
    }

    override fun getOnBotSender(bot: BotContainer, def: Sender.Def): Sender {
        if (bot !is KhlBot) {
            throw SimbotIllegalArgumentException("Bot $bot was not a khl bot.")
        }
        return KhlV3Sender(bot, def)
    }

}


private object KhlV3SetterFactory : SetterFactory {
    override fun getOnMsgSetter(msg: MsgGet, def: Setter.Def): Setter {
        if (msg !is BotInitialized) {
            throw SimbotIllegalArgumentException("msg $msg  was not a khl event.")
        }
        // TODO find bot?
        return msg.bot.sender.SETTER
    }

    override fun getOnBotSetter(bot: BotContainer, def: Setter.Def): Setter {
        if (bot !is KhlBot) {
            throw SimbotIllegalArgumentException("Bot $bot was not a khl bot.")
        }
        return KhlV3Setter(bot, def)
    }
}

private object KhlV3GetterFactory : GetterFactory {
    override fun getOnMsgGetter(msg: MsgGet, def: Getter.Def): Getter {
        if (msg !is BotInitialized) {
            throw SimbotIllegalArgumentException("msg $msg  was not a khl event.")
        }
        // TODO find bot?
        return msg.bot.sender.GETTER
    }

    override fun getOnBotGetter(bot: BotContainer, def: Getter.Def): Getter {
        if (bot !is KhlBot) {
            throw SimbotIllegalArgumentException("Bot $bot was not a khl bot.")
        }
        return KhlV3Getter(bot, def)
    }
}