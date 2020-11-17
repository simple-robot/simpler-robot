/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     DingComponentConfiguration.kt
 * Date  2020/8/8 下午6:49
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:Suppress("unused")

package love.forte.simbot.component.ding.configuration

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.component.ding.ComponentDingProperties
import love.forte.simbot.component.ding.sceret.DefaultDingSecretCalculator
import love.forte.simbot.component.ding.sceret.DingSecretCalculator
import love.forte.simbot.component.ding.sender.*
import love.forte.simbot.http.template.HttpTemplate
import java.util.regex.Pattern



/**
 * 钉钉的组件配置类
 * @author ForteScarlet <ForteScarlet@163.com>
 * 2020/8/8
 */
@Beans
class DingComponentConfiguration {


    /**
     * 签名计算器
     * @see DingSecretCalculator
     * @see DefaultDingSecretCalculator
     */
    @Beans
    fun defaultDingSecretCalculator(): DefaultDingSecretCalculator = DefaultDingSecretCalculator

    /**
     * sender builder
     */
    @Beans
    fun dingSenderBuilder(): DingSenderBuilderImpl = DingSenderBuilderImpl

    /**
     * http client able
     */
    @field:Depend
    lateinit var httpTemplate: HttpTemplate


    /**
     * DingSender管理器
     * @param config 配置信息
     * @param dingSecretCalculator 签名计算器
     */
    @Beans
    fun getDingSenderManager(config: ComponentDingProperties,
                             dingSecretCalculator: DingSecretCalculator,
                             senderBuilder: DingSenderBuilder
    ): DingSenderManager {
        val senders: MutableMap<String, DingSender> = config.bots.asSequence().map {sp ->
            val bot = BotRegisterInfo.splitTo(sp)
            val secret: String? = bot.code.takeIf { it.isNotBlank() }
            val accessToken: String = bot.verification
            val dingSenderInfo = DingSenderInfo(accessToken, config.webhook, secret, dingSecretCalculator, httpTemplate, config)
            val dingSender = senderBuilder.getDingSender(dingSenderInfo)
            accessToken to dingSender
        }.toMap(mutableMapOf())
        return DingSenderManagerImpl(senders)
    }


}


