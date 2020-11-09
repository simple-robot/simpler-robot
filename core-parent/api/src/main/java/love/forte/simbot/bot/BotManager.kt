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
 * bot进行注册的时候使用的数据类。
 * [code] 与 [verification] 都可能根据组件的变化而可能存在为空的情况。
 *
 * [code] 和 [verification] 出现以下内容需要进行转义：
 *
 * - `&` -> `&nbsp;`
 * - `,` -> `&#44;`
 * - `:` -> `&#58;`
 *
 * @property code 一般指账号信息。
 * @property verification 验证信息。一般可以代表账号的密码或者上报路径的链接。
 */
public data class BotRegisterInfo(val code: String, val verification: String) {
    companion object {

        private val SPLIT_REGEX = Regex(":")

        /**
         * 转义经过逗号切割的字符串，其中应为 “xxx:xxx”的格式。
         */
        @JvmStatic
        public fun splitTo(configTextPair: String): BotRegisterInfo {
            // 切割后转义
            val split = configTextPair.split(SPLIT_REGEX, 2)
            return BotRegisterInfo(
                CoreBotsDecoder.decoder(split[0]),
                if (split.size > 1) {
                    CoreBotsDecoder.decoder(split[1])
                } else ""
            )
        }
    }
}


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
    /**
     * 销毁一个bot，或者说关闭并移除一个bot。
     */
    fun destroyBot(code: String)
}
