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
 * [Getter.Def] factory. Realized by component.
 */
public interface DefaultGetterFactory {
    /**
     * 根据一个msg构建一个 [Getter]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgGetter(msg: MsgGet): Getter.Def

    /**
     * 根据一个bot信息构建一个 [Getter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotGetter(bot: BotContainer): Getter.Def
}


/**
 * [Setter.Def] factory. Realized by component.
 */
public interface DefaultSetterFactory {
    /**
     * 根据一个msg构建一个 [Setter]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgSetter(msg: MsgGet): Setter.Def
    /**
     * 根据一个bot信息构建一个 [Setter]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotSetter(bot: BotContainer): Setter.Def
}




/**
 * [Sender.Def] factory. Realized by component.
 */
public interface DefaultSenderFactory {
    /**
     * 根据一个msg构建一个 [Sender]. 用于在触发监听消息的时候构建其信息。
     */
    fun getOnMsgSender(msg: MsgGet): Sender.Def
    /**
     * 根据一个bot信息构建一个 [Sender]. 用于构建 [love.forte.simbot.core.bot.Bot] 实例。
     */
    fun getOnBotSender(bot: BotContainer): Sender.Def
}


/**
 * 上述三个送信器的合并数据类。
 * 由核心实现后，也没什么必要修改。
 */
public interface DefaultMsgSenderFactories {
    val defaultSenderFactory: DefaultSenderFactory
    val defaultSetterFactory: DefaultSetterFactory
    val defaultGetterFactory: DefaultGetterFactory
}


