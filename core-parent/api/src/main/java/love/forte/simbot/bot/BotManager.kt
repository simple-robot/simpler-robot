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

package love.forte.simbot.bot

import love.forte.simbot.api.message.containers.BotCodeContainer
import love.forte.simbot.api.message.containers.BotContainer


/**
 *
 * Bot管理器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface BotManager : BotRegistrar, BotDestroyer {

    /**
     *
     * 获取一个默认的bot。一般可能是第一个被注册的bot。
     * 默认bot有可能是一个随机的bot，不保证获取的bot每次都是相同的。
     *
     * @throws NoSuchBotException 如果没有注册任何bot或者全部bot都被移除，则可能抛出此异常。
     */
    val defaultBot: Bot

    /**
     *
     * 根据bot的id获取一个bot。
     *
     * @param id bot的账号。
     * @throws NoSuchBotException 如果找不到则会出现此异常。
     */
    fun getBot(id: String): Bot


    /**
     * override for [getBot].
     */
    @JvmDefault
    fun getBot(bot: BotCodeContainer): Bot = getBot(bot.botCode)

    /**
     * override for [getBot].
     */
    @JvmDefault
    fun getBot(bot: BotContainer): Bot = getBot(bot.botInfo)

    /**
     * 根据bot的id获取一个bot。获取不到则返回null。
     * @param id bot的账号。
     */
    fun getBotOrNull(id: String): Bot?


    /**
     * override for [getBot].
     */
    @JvmDefault
    fun getBotOrNull(bot: BotCodeContainer): Bot? = getBotOrNull(bot.botCode)

    /**
     * override for [getBot].
     */
    @JvmDefault
    fun getBotOrNull(bot: BotContainer): Bot? = getBotOrNull(bot.botInfo)


    /**
     * 获取所有的bot。
     *
     */
    val bots: List<Bot>

    /**
     * 判断是否为空。
     */
    fun isEmpty(): Boolean = bots.isEmpty()

}


public object CoreBotsEncoder {
    fun encoder(text: String): String =
        if (text.isEmpty()) text else {
            text.replace("&", "&nbsp;")
                .replace(",", "&#44;")
                .replace(":", "&#58;")
        }

}


public object CoreBotsDecoder {
    fun decoder(text: String): String =
        if (text.isEmpty()) text else {
            text.replace("&#58;", ":")
                .replace("&#44;", ",")
                .replace("&nbsp;", "&")
        }

}


/**
 * bot注册器。
 */
public interface BotRegistrar {
    /**
     * 验证或登录一个bot。如果账号已经存在，则在非必要情况下不会实质进行登录，但或许会存在验证。
     * @throws BotVerifyException 验证失败则会抛出此异常。
     */
    fun registerBot(botRegisterInfo: BotVerifyInfo): Bot
}


/**
 * bot注销器。
 */
public interface BotDestroyer {
    /**
     * 销毁一个bot，或者说关闭并移除一个bot。
     */
    fun destroyBot(code: String)
}
