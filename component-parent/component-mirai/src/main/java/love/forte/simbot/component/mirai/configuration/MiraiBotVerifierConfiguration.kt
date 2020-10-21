/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiBotVerifierConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.mirai.DefaultMiraiBotConfigurationFactory
import love.forte.simbot.component.mirai.MiraiBotConfigurationFactory
import love.forte.simbot.component.mirai.MiraiBotVerifier
import love.forte.simbot.constant.PriorityConstant

/**
 *
 * 配置mirai的bot验证器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class MiraiBotVerifierConfiguration {


    /**
     * inject default [MiraiBotConfigurationFactory].
     */
    @ComponentBeans
    fun miraiMiraiBotConfigurationFactory(): MiraiBotConfigurationFactory = DefaultMiraiBotConfigurationFactory


    /**
     * bot verifier.
     */
    @ComponentBeans
    fun miraiMiraiBotVerifier(
        configurationFactory: MiraiBotConfigurationFactory,
        miraiConfiguration: MiraiConfiguration
    ): MiraiBotVerifier = MiraiBotVerifier(configurationFactory, miraiConfiguration)


}