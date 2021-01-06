/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.mirai.configuration

import love.forte.common.ioc.DependCenter
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.component.mirai.DefaultMiraiBotConfigurationFactory
import love.forte.simbot.component.mirai.MiraiBotConfigurationFactory
import love.forte.simbot.component.mirai.MiraiBotVerifier
import love.forte.simbot.component.mirai.utils.MiraiBotEventRegistrar
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.listener.MsgGetProcessor

/**
 *
 * 配置mirai的bot验证器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("miraiBotVerifierConfiguration")
public class MiraiBotVerifierConfiguration {


    /**
     * inject default [MiraiBotConfigurationFactory].
     */
    @ComponentBeans("miraiMiraiBotConfigurationFactory")
    fun miraiMiraiBotConfigurationFactory(): MiraiBotConfigurationFactory = DefaultMiraiBotConfigurationFactory


    /**
     * bot verifier.
     */
    @ComponentBeans("miraiMiraiBotVerifier")
    fun miraiMiraiBotVerifier(
        configurationFactory: MiraiBotConfigurationFactory,
        miraiConfiguration: MiraiConfiguration,
        httpTemplate: HttpTemplate,
        miraiBotEventRegistrar: MiraiBotEventRegistrar,
        dependCenter: DependCenter,
    ): MiraiBotVerifier = MiraiBotVerifier(
        configurationFactory,
        miraiConfiguration,
        httpTemplate,
        miraiBotEventRegistrar,
        dependCenter
    )


}