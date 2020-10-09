/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreMsgSenderFactoriesConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.core.api.sender.GetterFactory
import love.forte.simbot.core.api.sender.MsgSenderFactories
import love.forte.simbot.core.api.sender.SenderFactory
import love.forte.simbot.core.api.sender.SetterFactory
import love.forte.simbot.core.sender.CoreMsgSenderFactories

/**
 * 配置 [MsgSenderFactories].
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreMsgSenderFactoriesConfiguration {


    /**
     * config [MsgSenderFactories].
     */
    @CoreBeans
    fun coreMsgSenderFactories(
        senderFactory: SenderFactory,
        setterFactory: SetterFactory,
        getterFactory: GetterFactory
    ): MsgSenderFactories = CoreMsgSenderFactories(senderFactory, setterFactory, getterFactory)


}