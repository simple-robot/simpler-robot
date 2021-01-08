/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatSenderFactory.kt
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

package love.forte.simbot.component.lovelycat.sender

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.api.sender.SetterFactory
import love.forte.simbot.component.lovelycat.LovelyCatApiManager
import love.forte.simbot.component.lovelycat.get


/**
 * Sender Factory
 */
public class LovelyCatSetterFactory(private val apiManager: LovelyCatApiManager) : SetterFactory {

    /**
     * 根据一个msg构建一个 [Sender]. 用于在触发监听消息的时候构建其信息。
     */
    override fun getOnMsgSetter(msg: MsgGet, def: Setter.Def): Setter {
        val botCode = msg.botInfo.botCode
        val api = apiManager[botCode] ?: throw IllegalStateException("cannot found bot($botCode)'s api.")
        return LovelyCatSetter(botCode, api, def)
    }

    /**
     * 根据一个bot信息构建一个 [Sender]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    override fun getOnBotSetter(bot: BotContainer, def: Setter.Def): Setter {
        val botCode = bot.botInfo.botCode
        val api = apiManager[botCode] ?: throw IllegalStateException("cannot found bot($botCode)'s api.")
        return LovelyCatSetter(botCode, api, def)
    }
}