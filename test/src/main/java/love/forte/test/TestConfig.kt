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
import love.forte.simbot.api.message.events.MessageContent
import love.forte.simbot.api.message.events.TextMessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.flag
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.message.events.PrivateMsgIdFlagContent
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.api.sender.toBotSender
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.bot.BotVerifier
import love.forte.simbot.bot.NoNeedToCloseBot



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


object AccountInfoObj : AccountInfo {
    override val accountCode: String = "666"
    override val accountNickname: String = "forli(nick)"
    override val accountRemark: String = "forli(remark)"
    override val accountAvatar: String = "avatar"
}




object TestPrivateMsg : PrivateMsg {
    override val privateMsgType: PrivateMsg.Type = PrivateMsg.Type.FRIEND
    override val flag: Flag<PrivateMsg.FlagContent> = flag { PrivateMsgIdFlagContent("private") }
    override val id: String = flag.flag.id
    override val time: Long = System.currentTimeMillis()

    override fun toString(): String = "TestPrivateMsg()"

    override var msgContent: MessageContent = TextMessageContent("[at,code=123]")

    override var msg: String?
        get() = msgContent.msg
        set(value) {}

    override val originalData: String = toString()
    override val botInfo: BotInfo = BotInfoObj
    override val accountInfo: AccountInfo = AccountInfoObj
}