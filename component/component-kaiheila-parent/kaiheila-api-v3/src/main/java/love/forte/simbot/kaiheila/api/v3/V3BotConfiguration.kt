/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.kaiheila.api.v3

import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.ErrorFactories
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.api.ApiConfigurationBuilder
import love.forte.simbot.component.kaiheila.event.EventLocator
import love.forte.simbot.component.kaiheila.event.KhlEventLocator


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class V3BotConfigurationDSL


/**
 *
 * configuration for [V3WsBot].
 *
 * @author ForteScarlet
 */
public class V3BotConfiguration {

    @V3BotConfigurationDSL
    lateinit var apiConfiguration: ApiConfiguration

    @V3BotConfigurationDSL
    var compress: Int = 1

    @V3BotConfigurationDSL
    var senderFactories: DefaultMsgSenderFactories = ErrorFactories

    @V3BotConfigurationDSL
    var eventLocator: EventLocator = KhlEventLocator

    @V3BotConfigurationDSL
    @JvmSynthetic
    var connectRetryTimes: UInt = UInt.MAX_VALUE

    @V3BotConfigurationDSL
    fun setConnectRetryTimes(times: Int) {
        connectRetryTimes = times.toUInt()
    }

    fun getConnectRetryTimes(): Int = connectRetryTimes.toInt()

}

public inline fun v3BotConfiguration(block: V3BotConfiguration.() -> Unit): V3BotConfiguration {
    return V3BotConfiguration().also(block)
}


@V3BotConfigurationDSL
public fun V3BotConfiguration.apiConfiguration(block: ApiConfigurationBuilder.() -> Unit) {
    apiConfiguration = ApiConfigurationBuilder().also(block).build()
}