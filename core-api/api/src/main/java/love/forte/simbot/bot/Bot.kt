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

@file:JvmName("Bots")
package love.forte.simbot.bot

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.sender.BotSender
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.Setter
import java.io.Closeable


/**
 *
 * 一个Bot。
 * 可以获取bot信息，以及bot对应的送信器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface Bot: BotContainer, Closeable {

    /**
     * bot 对应的送信器。
     */
    val sender: BotSender

}


/**
 * [Bot] 的基础抽象类。
 */
public abstract class BaseBot(override val sender: BotSender, override val botInfo: BotInfo) : Bot


/**
 * [BaseBot] 的基础实现，屏蔽了 [close]。
 */
public open class NoNeedToCloseBot(sender: BotSender, botInfo: BotInfo): BaseBot(sender, botInfo) {
    override fun close() { }
}


public inline fun Bot.onSender(block: Sender.() -> Unit) = sender.SENDER.block()
public inline fun Bot.onSetter(block: Setter.() -> Unit) = sender.SETTER.block()
public inline fun Bot.onGetter(block: Getter.() -> Unit) = sender.GETTER.block()



