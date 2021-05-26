/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatBotVerifierConfiguration.kt
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

package love.forte.simbot.component.lovelycat.configuration

import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.api.message.containers.botContainer
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.api.sender.toBotSender
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotVerifier
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.component.lovelycat.LovelyCatApiManager
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplateImpl
import love.forte.simbot.component.lovelycat.LovelyCatBot
import love.forte.simbot.component.lovelycat.message.LovelyCatApiCache
import love.forte.simbot.component.lovelycat.message.event.lovelyCatBotInfo
import love.forte.simbot.component.lovelycat.set
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory

/**
 * lovely cat 验证器。
 */
@ComponentBeans("lovelyCatBotVerifier")
public class LovelyCatBotVerifier : BotVerifier {

    @Depend
    lateinit var httpTemplate: HttpTemplate
    @Depend
    lateinit var jsonSerializerFactory: JsonSerializerFactory
    @Depend
    lateinit var apiManager: LovelyCatApiManager
    @Depend
    lateinit var lovelyCatApiCache: LovelyCatApiCache


    /** 验证一个bot的注册信息，并转化为一个该组件对应的 [Bot] 实例。 */
    override fun verity(botInfo: BotVerifyInfo, msgSenderFactories: MsgSenderFactories, defFactories: DefaultMsgSenderFactories): Bot {
        val code = botInfo.code
        val path = botInfo.verification!!
        val api = LovelyCatApiTemplateImpl(httpTemplate, path, jsonSerializerFactory, lovelyCatApiCache)
        // 寻找登录bot中是否存在此code
        val accountList = api.getLoggedAccountList()
        val contains = accountList.accountList.any { code == it.wxid }

        if (contains) {
            // save api info.
            apiManager[code] = api
            val botContainer = botContainer(lovelyCatBotInfo(code, api))
            val botSender = msgSenderFactories.toBotSender(botContainer, defFactories)
            return LovelyCatBot(code, api, botSender)
        } else {
            throw IllegalStateException("cannot found bot id '$code' in ${accountList.accountList.map { it.wxid }}")
        }
    }
}




