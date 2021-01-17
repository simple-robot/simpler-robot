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

package love.forte.simbot.core.listener

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.bot.Bot
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerFunctionInvokeData
import love.forte.simbot.listener.ListenerInterceptorChain

/**
 * 监听函数触发所携带的参数接口默认数据实现。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public data class ListenerFunctionInvokeDataImpl(
    override val msgGet: MsgGet,
    override val context: ListenerContext,
    override val atDetection: AtDetection,
    override val bot: Bot,
    override val msgSender: MsgSender,
    override val listenerInterceptorChain: ListenerInterceptorChain
) : ListenerFunctionInvokeData {
    override fun get(type: Class<*>): Any? = when {
        type.isAssignableFrom(msgSender::class.java) -> msgSender
        type.isAssignableFrom(msgSender.SENDER::class.java) -> msgSender.SENDER
        type.isAssignableFrom(msgSender.SETTER::class.java) -> msgSender.SETTER
        type.isAssignableFrom(msgSender.GETTER::class.java) -> msgSender.GETTER
        type.isAssignableFrom(bot::class.java) -> bot
        type.isAssignableFrom(atDetection::class.java) -> atDetection
        type.isAssignableFrom(context::class.java) -> context
        type.isAssignableFrom(msgGet::class.java) -> msgGet
        else -> null
    }
}