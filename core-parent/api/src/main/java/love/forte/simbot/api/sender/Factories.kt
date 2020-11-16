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

@file:JvmName("MsgSenderFactoriesUtil")
package love.forte.simbot.api.sender

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.MsgGet


/**
 * [Getter] factory. Realized by component.
 */
public interface GetterFactory {
    /**
     * 根据一个msg构建一个 [Getter]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgGetter(msg: MsgGet): Getter

    /**
     * 根据一个bot信息构建一个 [Getter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotGetter(bot: BotContainer): Getter
}


/**
 * [Setter] factory. Realized by component.
 */
public interface SetterFactory {
    /**
     * 根据一个msg构建一个 [Setter]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgSetter(msg: MsgGet): Setter
    /**
     * 根据一个bot信息构建一个 [Setter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotSetter(bot: BotContainer): Setter
}




/**
 * [Sender] factory. Realized by component.
 */
public interface SenderFactory {
    /**
     * 根据一个msg构建一个 [Sender]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgSender(msg: MsgGet): Sender
    /**
     * 根据一个bot信息构建一个 [Sender]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotSender(bot: BotContainer): Sender
}


/**
 * 上述三个送信器的合并数据类。
 * 由核心实现后，也没什么必要修改。
 */
public interface MsgSenderFactories {
    val senderFactory: SenderFactory
    val setterFactory: SetterFactory
    val getterFactory: GetterFactory
}



public inline class GetterFacOnMsg(private val getterFactory: GetterFactory) {
    operator fun invoke(msg: MsgGet): Getter = getterFactory.getOnMsgGetter(msg)
}
public inline class GetterFacOnBot(private val getterFactory: GetterFactory) {
    operator fun invoke(bot: BotContainer): Getter = getterFactory.getOnBotGetter(bot)
}

public fun GetterFactory.onBot(): GetterFacOnBot = GetterFacOnBot(this)
public fun GetterFactory.onMsg(): GetterFacOnMsg = GetterFacOnMsg(this)
public fun GetterFactory.onBot(bot: BotContainer) = this.onBot()(bot)
public fun GetterFactory.onMsg(msg: MsgGet) = this.onMsg()(msg)

public inline class SetterFacOnMsg(private val setterFactory: SetterFactory) {
    operator fun invoke(msg: MsgGet): Setter = setterFactory.getOnMsgSetter(msg)
}
public inline class SetterFacOnBot(private val setterFactory: SetterFactory) {
    operator fun invoke(bot: BotContainer): Setter = setterFactory.getOnBotSetter(bot)
}

public fun SetterFactory.onBot(): SetterFacOnBot = SetterFacOnBot(this)
public fun SetterFactory.onMsg(): SetterFacOnMsg = SetterFacOnMsg(this)
public fun SetterFactory.onBot(bot: BotContainer) = this.onBot()(bot)
public fun SetterFactory.onMsg(msg: MsgGet) = this.onMsg()(msg)

public inline class SenderFacOnMsg(private val senderFactory: SenderFactory) {
    operator fun invoke(msg: MsgGet): Sender = senderFactory.getOnMsgSender(msg)
}
public inline class SenderFacOnBot(private val senderFactory: SenderFactory) {
    operator fun invoke(bot: BotContainer): Sender = senderFactory.getOnBotSender(bot)
}

public fun SenderFactory.onBot(): SenderFacOnBot = SenderFacOnBot(this)
public fun SenderFactory.onMsg(): SenderFacOnMsg = SenderFacOnMsg(this)
public fun SenderFactory.onBot(bot: BotContainer) = this.onBot()(bot)
public fun SenderFactory.onMsg(msg: MsgGet) = this.onMsg()(msg)



/**
 * 通过 [MsgSenderFactories] 构建 BotSender。
 */
public fun MsgSenderFactories.toBotSender(bot: BotContainer): BotSender {
    return BotSender(
        senderFactory.onBot(bot),
        setterFactory.onBot(bot),
        getterFactory.onBot(bot),
        bot.botInfo
    )
}

