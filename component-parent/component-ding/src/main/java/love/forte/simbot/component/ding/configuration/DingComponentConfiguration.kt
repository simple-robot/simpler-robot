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
import love.forte.simbot.component.ding.sceret.DefaultDingSecretCalculator
import love.forte.simbot.component.ding.sceret.DingSecretCalculator
import love.forte.simbot.component.ding.sender.*
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


/**
 * 钉钉的组件配置类
 * @author ForteScarlet <ForteScarlet@163.com>
 * 2020/8/8
 */
@Beans(value = "dingComponentConfiguration", priority = PriorityConstant.COMPONENT_TENTH)
class DingComponentConfiguration {


    /**
     * 签名计算器
     * @see DingSecretCalculator
     * @see DefaultDingSecretCalculator
     */
    @Beans(value = "defaultDingSecretCalculator", priority = PriorityConstant.COMPONENT_TENTH)
    fun defaultDingSecretCalculator(): DefaultDingSecretCalculator = DefaultDingSecretCalculator

    /**
     * sender builder
     */
    @Beans(value = "dingSenderBuilder", priority = PriorityConstant.COMPONENT_TENTH)
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
    @Beans(value = "getDingSenderManager", priority = PriorityConstant.COMPONENT_TENTH)
    fun getDingSenderManager(config: ComponentDingProperties,
                             dingSecretCalculator: DingSecretCalculator,
                             senderBuilder: DingSenderBuilder,
                             jsonSerializerFactory: JsonSerializerFactory
    ): DingSenderManager {
        val senders: MutableMap<String, DingSender> = config.bots.asSequence().map {sp ->
            val bot = BotRegisterInfo.splitTo(sp)
            val secret: String? = bot.code.takeIf { it.isNotBlank() }
            val accessToken: String = bot.verification
            val dingSenderInfo = DingSenderInfo(accessToken, config.webhook, secret, dingSecretCalculator, httpTemplate, jsonSerializerFactory)
            val dingSender = senderBuilder.getDingSender(dingSenderInfo)
            accessToken to dingSender
        }.toMap(mutableMapOf())
        return DingSenderManagerImpl(senders)
    }


}


