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

package love.forte.simbot.core.listener

import love.forte.common.ioc.DependCenter
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.bot.Bot
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerFunctionInvokeData
import love.forte.simbot.listener.ListenerInterceptorChain

/**
 * 监听函数触发所携带的参数接口默认数据实现。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public data class ListenerFunctionInvokeDataImpl @OptIn(SimbotExperimentalApi::class) constructor(
    private val dependCenter: DependCenter,
    override val msgGet: MsgGet,
    override val context: ListenerContext,
    override val atDetection: AtDetection,
    override val bot: Bot,
    override val msgSender: MsgSender,
    override val listenerInterceptorChain: ListenerInterceptorChain
) : ListenerFunctionInvokeData {
    @OptIn(SimbotExperimentalApi::class)
    override fun get(type: Class<*>): Any? = when {
        type.isAssignableFrom(msgSender::class.java) -> msgSender
        type.isAssignableFrom(msgSender.SENDER::class.java) -> msgSender.SENDER
        type.isAssignableFrom(msgSender.SETTER::class.java) -> msgSender.SETTER
        type.isAssignableFrom(msgSender.GETTER::class.java) -> msgSender.GETTER
        type.isAssignableFrom(bot::class.java) -> bot
        type.isAssignableFrom(atDetection::class.java) -> atDetection
        type.isAssignableFrom(context::class.java) -> context
        type.isAssignableFrom(msgGet::class.java) -> msgGet
        else -> dependCenter[type]
    }
}




@Deprecated("会存在类型匹配错误的问题")
public class ListenerFunctionInvokeDataLazyImpl @OptIn(SimbotExperimentalApi::class) constructor(
    mode: LazyThreadSafetyMode,
    _msgGet: () -> MsgGet,
    _context: () -> ListenerContext,
    _atDetection: () -> AtDetection,
    _bot: () -> Bot,
    _msgSender: () -> MsgSender,
    _listenerInterceptorChain: () -> ListenerInterceptorChain
): ListenerFunctionInvokeData {
    override val msgGet: MsgGet by lazy(mode, _msgGet)
    @OptIn(SimbotExperimentalApi::class)
    override val context: ListenerContext by lazy(mode, _context)
    override val atDetection: AtDetection by lazy(mode, _atDetection)
    override val bot: Bot by lazy(mode, _bot)
    override val msgSender: MsgSender by lazy(mode, _msgSender)
    override val listenerInterceptorChain: ListenerInterceptorChain by lazy(mode, _listenerInterceptorChain)

    @OptIn(SimbotExperimentalApi::class)
    override fun get(type: Class<*>): Any? = when {
        MsgSender::class.java.isAssignableFrom(type) -> msgSender.takeTypeIf(type)
        Sender::class.java.isAssignableFrom(type) -> msgSender.SENDER.takeTypeIf(type)
        Setter::class.java.isAssignableFrom(type) -> msgSender.SETTER.takeTypeIf(type)
        Getter::class.java.isAssignableFrom(type) -> msgSender.GETTER.takeTypeIf(type)
        Bot::class.java.isAssignableFrom(type) -> bot.takeTypeIf(type)
        AtDetection::class.java.isAssignableFrom(type) -> atDetection.takeTypeIf(type)
        ListenerContext::class.java.isAssignableFrom(type) -> context.takeTypeIf(type)
        MsgGet::class.java.isAssignableFrom(type) -> msgGet.takeTypeIf(type)
        else -> null
    }
}

private inline fun <reified T> T.takeTypeIf(type: Class<*>): T? = takeIf { t -> type.isAssignableFrom(t!!::class.java) }
