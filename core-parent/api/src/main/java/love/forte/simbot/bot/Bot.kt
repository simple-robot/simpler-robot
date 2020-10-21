/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Bot.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.bot

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.sender.BotSender
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
public class NoNeedToCloseBot(sender: BotSender, botInfo: BotInfo): BaseBot(sender, botInfo) {
    override fun close() { }
}
