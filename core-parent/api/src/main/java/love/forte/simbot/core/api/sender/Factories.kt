/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Factories.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.sender

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.api.message.containers.BotContainer


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
    fun getOnBotSetter(bot: BotContainer): Getter
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
    fun getOnBotSender(bot: BotContainer): Getter
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





