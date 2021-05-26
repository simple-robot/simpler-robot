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
    fun getOnMsgGetter(msg: MsgGet, def: Getter.Def): Getter

    /**
     * 根据一个bot信息构建一个 [Getter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotGetter(bot: BotContainer, def: Getter.Def): Getter
}


/**
 * [Setter] factory. Realized by component.
 */
public interface SetterFactory {
    /**
     * 根据一个msg构建一个 [Setter]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgSetter(msg: MsgGet, def: Setter.Def): Setter
    /**
     * 根据一个bot信息构建一个 [Setter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotSetter(bot: BotContainer, def: Setter.Def): Setter
}




/**
 * [Sender] factory. Realized by component.
 */
public interface SenderFactory {
    /**
     * 根据一个msg构建一个 [Sender]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgSender(msg: MsgGet, def: Sender.Def): Sender
    /**
     * 根据一个bot信息构建一个 [Sender]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotSender(bot: BotContainer, def: Sender.Def): Sender
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


@JvmInline
public value class GetterFacOnBot(private val getterFactory: GetterFactory) {
    operator fun invoke(bot: BotContainer, def: Getter.Def): Getter = getterFactory.getOnBotGetter(bot, def)
}

public fun GetterFactory.onBot(): GetterFacOnBot = GetterFacOnBot(this)
public fun GetterFactory.onBot(bot: BotContainer, def: Getter.Def) = this.onBot()(bot, def)

@JvmInline
public value class SetterFacOnBot(private val setterFactory: SetterFactory) {
    operator fun invoke(bot: BotContainer, def: Setter.Def): Setter = setterFactory.getOnBotSetter(bot, def)
}

public fun SetterFactory.onBot(): SetterFacOnBot = SetterFacOnBot(this)
public fun SetterFactory.onBot(bot: BotContainer, def: Setter.Def) = this.onBot()(bot, def)

@JvmInline
public value class SenderFacOnBot(private val senderFactory: SenderFactory) {
    operator fun invoke(bot: BotContainer, def: Sender.Def): Sender = senderFactory.getOnBotSender(bot, def)
}

public fun SenderFactory.onBot(): SenderFacOnBot = SenderFacOnBot(this)
public fun SenderFactory.onBot(bot: BotContainer, def: Sender.Def) = this.onBot()(bot, def)


/**
 * 通过 [MsgSenderFactories] 构建 BotSender。
 */
public fun MsgSenderFactories.toBotSender(
    bot: BotContainer, defFactories: DefaultMsgSenderFactories
): BotSender {
    return BotSender(
        senderFactory.onBot(bot, defFactories.defaultSenderFactory.getOnBotSender(bot)),
        setterFactory.onBot(bot, defFactories.defaultSetterFactory.getOnBotSetter(bot)),
        getterFactory.onBot(bot, defFactories.defaultGetterFactory.getOnBotGetter(bot)),
        bot.botInfo
    )
}

