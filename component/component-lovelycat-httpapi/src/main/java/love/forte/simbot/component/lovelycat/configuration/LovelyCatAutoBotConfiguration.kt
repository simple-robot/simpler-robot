/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     LovelyCatBotConfiguration.kt
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

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.annotation.PrePass
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.component.lovelycat.LovelyCatApiManager
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplateImpl
import love.forte.simbot.component.lovelycat.message.LovelyCatApiCache
import love.forte.simbot.component.lovelycat.set
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


/**
 * 可爱猫bot自动配置器。
 */
@ComponentBeans("lovelyCatBotConfiguration")
@AsConfig(prefix = "simbot.component.lovelycat.bot.autoConfig")
public class LovelyCatAutoBotConfiguration {

    private companion object : TypedCompLogger(LovelyCatAutoBotConfiguration::class.java)

    /**
     * 是否启用自动配置。
     */
    @ConfigInject
    var enable: Boolean = true

    /**
     * 如果启动自动配置，则此处为自动配置的可爱猫上报地址列表。
     */
    @ConfigInject(orIgnore = true)
    var paths: List<String> = emptyList()

    @Depend
    lateinit var httpTemplate: HttpTemplate

    @Depend
    lateinit var jsonSerializerFactory: JsonSerializerFactory

    @Depend
    lateinit var apiManager: LovelyCatApiManager

    @Depend
    lateinit var lovelyCatApiCache: LovelyCatApiCache

    @Depend
    lateinit var botManager: BotManager


    @PrePass
    public fun preRegisterBot() {
        if (enable) {
            if (paths.isEmpty()) {
                logger.warn("Auto register bot is enabled, but paths is empty.")
                return
            }

            paths.forEach { path ->
                logger.debug("Auto registrar path {}", path)
                val api = LovelyCatApiTemplateImpl(httpTemplate, path, jsonSerializerFactory, lovelyCatApiCache)
                val loggedList = api.getLoggedAccountList().accountList
                if (loggedList.isEmpty()) {
                    logger.debug("Path {} logged account empty.", path)
                } else {
                    loggedList.forEach { botInfo ->
                        val botCode = botInfo.botCode
                        if (botManager.getBotOrNull(botCode) == null) {
                            apiManager[botCode] = api
                            botManager.registerBot(BotRegisterInfo(botCode, path))
                        }
                    }

                }

            }


        }
    }


}