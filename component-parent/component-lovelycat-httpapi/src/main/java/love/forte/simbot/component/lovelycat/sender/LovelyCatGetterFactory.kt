/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatGetterFactory.kt
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
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.GetterFactory
import love.forte.simbot.component.lovelycat.LovelyCatApiManager
import love.forte.simbot.component.lovelycat.get


public class LovelyCatGetterFactory(private val apiManager: LovelyCatApiManager) : GetterFactory {
    /**
     * 根据一个msg构建一个 [Getter]. 用于在触发监听消息的时候构建其信息。
     */
    override fun getOnMsgGetter(msg: MsgGet): Getter {
        val botCode = msg.botInfo.botCode
        val api = apiManager[botCode] ?: throw IllegalStateException("cannot found bot($botCode)'s api.")
        return LovelyCatGetter(botCode, api)
    }

    /**
     * 根据一个bot信息构建一个 [Getter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    override fun getOnBotGetter(bot: BotContainer): Getter {
        val botCode = bot.botInfo.botCode
        val api = apiManager[botCode] ?: throw IllegalStateException("cannot found bot($botCode)'s api.")
        return LovelyCatGetter(botCode, api)
    }
}