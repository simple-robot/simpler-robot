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

package love.forte.simbot.component.mirai

import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.component.mirai.utils.BotLevelUtil
import love.forte.simbot.http.template.HttpTemplate
import net.mamoe.mirai.Bot
import java.util.concurrent.ConcurrentHashMap


/**
 * 基于mirai的 [Bot] 的 [BotInfo] 实现。
 *
 * 此实例属性动态委托于 [bot].
 *
 */
public data class MiraiBotInfo internal constructor(private val bot: Bot, private val http: HttpTemplate? = null) : BotInfo {

    /**
     * 缓存部分bot信息
     */
    companion object INS {
        /** MiraiBotInfo实例缓存 */
        private val instances: MutableMap<Long, MiraiBotInfo> = ConcurrentHashMap<Long, MiraiBotInfo>()

        internal fun destroyBotInfo(id: Long): MiraiBotInfo? = instances.remove(id)

        fun getInstance(bot: Bot, http: HttpTemplate? = null): MiraiBotInfo {
            val instance = instances[bot.id]
            if (instance != null) {
                return instance
            }
            return instances.computeIfAbsent(bot.id) { MiraiBotInfo(bot, http) }
        }
    }

    /** 当前的bot的账号 */
    override val botCode: String
        get() = bot.id.toString()

    /** 当前的bot的账号 */
    override val botCodeNumber: Long
        get() = bot.id

    /** 机器人的名称 */
    override val botName: String
        get() = bot.nick

    /** 机器人的头像 */
    override val botAvatar: String
        get() = bot.avatarUrl

    override val botLevel: Long
        get() = http?.let { BotLevelUtil.level(bot, it).toLong() } ?: -1

    override fun toString(): String = "Bot(bot=$bot, code=$botCode, name=$botName${if (botLevel >= 0) ", level=$botLevel" else ""})"
}


