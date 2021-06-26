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

@file:JvmName("ErrorSenderFactories")

package love.forte.simbot.api.sender

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.Result

/**
 * [Sender] 的 无效化实现，所有的方法均会抛出异常。
 */
object ErrorSender : Sender.Def {
    override fun sendGroupMsg(parent: String?, group: String, msg: String): Nothing =
        NO("Sender.sendGroupMsg")

    override fun sendPrivateMsg(code: String, group: String?, msg: String): Nothing =
        NO("Sender.sendPrivateMsg")

    override fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Nothing =
        NO("Sender.sendGroupNotice")

    override fun sendGroupSign(group: String, title: String, message: String): Nothing =
        NO("Sender.sendGroupSign")

    override fun <R : Result> additionalExecute(additionalApi: AdditionalApi<R>): Nothing =
        NO("Sender.additionalApi.${additionalApi.additionalApiName}")
}



/**
 * [ErrorGetter] 的构建工厂，得到的 [Getter] 实例的所有方法均会抛出异常。
 */
@get:JvmName("getErrorSenderFactory")
public val ErrorSenderFactory : DefaultSenderFactory = object : DefaultSenderFactory {
    override fun getOnMsgSender(msg: MsgGet): Sender.Def = ErrorSender
    override fun getOnBotSender(bot: BotContainer): Sender.Def = ErrorSender
}