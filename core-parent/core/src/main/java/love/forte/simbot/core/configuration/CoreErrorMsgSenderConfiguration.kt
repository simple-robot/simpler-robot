/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreErrorMsgSenderConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.sender.*
import love.forte.simbot.api.sender.ErrorSenderFactory
import love.forte.simbot.constant.PriorityConstant

/**
 *
 * 核心提供三个无效化的送信器工厂实例。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreErrorMsgSenderConfiguration {


    @CoreBeans(priority = PriorityConstant.LAST)
    public fun coreErrorSenderFactory(): SenderFactory = ErrorSenderFactory

    @CoreBeans(priority = PriorityConstant.LAST)
    public fun coreErrorSetterFactory(): SetterFactory = ErrorSetterFactory

    @CoreBeans(priority = PriorityConstant.LAST)
    public fun coreErrorGetterFactory(): GetterFactory = ErrorGetterFactory


}