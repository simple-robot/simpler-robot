/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BotManager.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.bot

import love.forte.simbot.core.api.message.containers.BotCodeContainer
import love.forte.simbot.core.api.message.containers.BotContainer


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
}






/**
 * bot进行注册的时候使用的数据类。
 * @property code 一般指账号信息。
 * @property verification 验证信息。一般可以代表账号的密码或者上报路径的链接。
 */
public data class BotRegisterInfo(val code: String, val verification: String)


/**
 * bot注册器。
 */
public interface BotRegistrar {
    /**
     * 验证或登录一个bot。
     * @throws BotVerifyException 验证失败则会抛出此异常。
     */
    fun registerBot(botRegisterInfo: BotRegisterInfo): Bot
}


/**
 * bot注销器。
 */
public interface BotDestroyer {
    fun destroyBot(code: String)
}
