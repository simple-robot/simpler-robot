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

package love.forte.simbot.core.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.constant.PriorityConstant

/**
 *
 * 核心提供三个无效化的送信器工厂实例。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreDefaultMsgSenderConfiguration")
@AsConfig(prefix = "simbot.core.sender.default")
public class CoreDefaultMsgSenderConfiguration {

    /**
     * 默认送信器的类型。是 [DefaultSenderType] 枚举元素。
     * 默认为 ERROR
     */
    @ConfigInject("type", orDefault = ["ERROR"])
    lateinit var type: DefaultSenderType


    @CoreBeans(value = "coreDefaultFactories", priority = PriorityConstant.LAST)
    public fun coreDefaultFactories(): DefaultMsgSenderFactories = type.factories()


}


/**
 * 默认（当出现不支持API时使用的）送信器类型
 */
public enum class DefaultSenderType(val factories: () -> DefaultMsgSenderFactories) {
    /** 总是会抛出异常的默认送信器。 */
    ERROR({ love.forte.simbot.api.sender.ErrorFactories }),

    /** 总是会返回一个失败默认值的默认送信器。 */
    FAILED({ love.forte.simbot.api.sender.FailedFactories }),

    /**
     * 提供一个返回默认值且会提供一个警告日志的默认送信器。
     */
    WARN({ love.forte.simbot.api.sender.WarnFactories })

}