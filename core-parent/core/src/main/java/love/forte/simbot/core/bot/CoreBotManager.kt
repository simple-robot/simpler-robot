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

package love.forte.simbot.core.bot

import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.*
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * 核心对 [BotManager] 的实现，使用 [Map] 储存bot信息。
 *
 * @property verifier bot验证器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class CoreBotManager(
    private val verifier: BotVerifier,
    private val msgSenderFactories: MsgSenderFactories,
    private val defSenderFactories: DefaultMsgSenderFactories,
) : BotManager {

    /**
     * bot map.
     */
    private val botsMap = ConcurrentHashMap<String, Bot>()


    /**
     *
     * 获取一个默认的bot。一般可能是第一个被注册的bot。
     * 默认bot有可能是一个随机的bot，不保证获取的bot每次都是相同的。
     *
     * @throws NoSuchBotException 如果没有注册任何bot或者全部bot都被移除，则可能抛出此异常。
     */
    override val defaultBot: Bot
        get() = with(botsMap.entries) {
            if(isEmpty()) throw NoSuchBotException("No bot can be acquired.")
            else first().value
        }

    /**
     *
     * 根据bot的id获取一个bot。
     *
     * @param id bot的账号。
     * @throws NoSuchBotException 如果找不到则会出现此异常。
     */
    override fun getBot(id: String): Bot = botsMap[id] ?: throw NoSuchBotException(id)

    /**
     * 根据bot的id获取一个bot。获取不到则返回null。
     * @param id bot的账号。
     */
    override fun getBotOrNull(id: String): Bot? = botsMap[id]

    /**
     * 获取所有的bot。
     */
    override val bots: List<Bot>
        get() = botsMap.values.toMutableList()


    /**
     * 验证或登录一个bot。
     * @throws BotVerifyException 验证失败则会抛出此异常。
     */
    override fun registerBot(botRegisterInfo: BotRegisterInfo): Bot {

        return synchronized(botsMap) {
            removeAndClose(botRegisterInfo.code)
            verifier.verity(botRegisterInfo, msgSenderFactories, defSenderFactories).apply {
                botsMap[botRegisterInfo.code] = this
            }
        }
    }



    /**
     * 关闭并移除。
     */
    override fun destroyBot(code: String) {
        removeAndClose(code)
    }

    private fun removeAndClose(code: String) {
        botsMap.remove(code)?.runCatching { close() }
    }
}