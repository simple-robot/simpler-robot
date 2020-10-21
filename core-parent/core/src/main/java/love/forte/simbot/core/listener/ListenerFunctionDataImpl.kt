/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerFunctionDataImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.bot.Bot
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerFunctionInvokeData

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public data class ListenerFunctionInvokeDataImpl(
    override val msgGet: MsgGet,
    override val context: ListenerContext,
    override val atDetection: AtDetection,
    override val bot: Bot,
    override val msgSender: MsgSender
) : ListenerFunctionInvokeData {
    override fun get(type: Class<*>): Any? = when {
        MsgSender::class.java.isAssignableFrom(type) -> msgSender
        Sender::class.java.isAssignableFrom(type) -> msgSender.SENDER
        Setter::class.java.isAssignableFrom(type) -> msgSender.SETTER
        Getter::class.java.isAssignableFrom(type) -> msgSender.GETTER
        Bot::class.java.isAssignableFrom(type) -> bot
        AtDetection::class.java.isAssignableFrom(type) -> atDetection
        ListenerContext::class.java.isAssignableFrom(type) -> context
        MsgGet::class.java.isAssignableFrom(type) -> msgGet
        else -> null
    }
}