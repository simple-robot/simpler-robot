/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MsgSender.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MsgSenderUtil")
package love.forte.simbot.api.sender

import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.events.MessageEventGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo

/**
 *
 * 我们的老朋友，送信器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public open class MsgSender(
    @JvmField val SENDER: Sender,
    @JvmField val SETTER: Setter,
    @JvmField val GETTER: Getter
) {
    /** 撤回消息。 */
    public open fun recall(flag: Flag<MessageEventGet.MessageFlagContent>): Carrier<Boolean> = SETTER.setMsgRecall(flag)



}


public fun MsgSender(msgGet: MsgGet, factories: MsgSenderFactories): MsgSender = with(msgGet) {
    val sender = factories.senderFactory.getOnMsgSender(this)
    val setter = factories.setterFactory.getOnMsgSetter(this)
    val getter = factories.getterFactory.getOnMsgGetter(this)
    MsgSender(sender, setter, getter)
}



/**
 * 一个bot对应的送信器。
 */
public open class BotSender(
    sender: Sender,
    setter: Setter,
    getter: Getter,
    override val botInfo: BotInfo
) : MsgSender(sender, setter, getter), BotContainer