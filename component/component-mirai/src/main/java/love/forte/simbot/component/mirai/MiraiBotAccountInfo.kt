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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.DetailAccountInfo
import love.forte.simbot.api.message.containers.Gender
import net.mamoe.mirai.Bot
import net.mamoe.mirai.data.UserProfile
import java.util.concurrent.ConcurrentHashMap


/**
 * 基于mirai的 [Bot] 的 [BotInfo] 实现。
 *
 * 此实例属性动态委托于 [bot].
 *
 */
public data class MiraiBotAccountInfo internal constructor(private val bot: Bot) :
    BotInfo, DetailAccountInfo {

    /**
     * 缓存部分bot信息
     */
    companion object INS {
        /** MiraiBotInfo实例缓存 */
        private val instances: MutableMap<Long, MiraiBotAccountInfo> = ConcurrentHashMap<Long, MiraiBotAccountInfo>()

        internal fun destroyBotInfo(id: Long): MiraiBotAccountInfo? = instances.remove(id)

        fun getInstance(bot: Bot): MiraiBotAccountInfo {
            val instance = instances[bot.id]
            if (instance != null) {
                return instance
            }
            return instances.computeIfAbsent(bot.id) { MiraiBotAccountInfo(bot) }
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

    private val profile by lazy {
        runBlocking { bot.asStranger.queryProfile() }
    }

    override val botLevel: Long
        get() = profile.qLevel.toLong()

    override val age: Int
        get() = profile.age

    override val email: String
        get() = profile.email.ifBlank { "${bot.id}@qq.com" }

    /** 无法获取手机号 */
    override val phone: String?
        get() = null
    override val signature: String
        get() = profile.sign

    override val gender: Gender
        get() = profile.sex.toGender()

    override fun toString(): String = buildString {
        // "Bot(bot=$bot, code=$botCode, name=$botName${if (botLevel >= 0) ", level=$botLevel" else ""})"
        append("Bot(bot=").append(bot)
        append(", code=").append(botCode)
        append(", name=").append(botName)
        if (botLevel >= 0) {
            append(", level=").append(botLevel)
        }
        if (age >= 0) {
            append(", age=").append(age)
        }
        append(", email=").append(email)
        append(", signature=").append(signature)
        append(", gender=").append(gender)
        phone?.apply { append(", phone=").append(this) }
    }

}


public fun UserProfile.Sex.toGender(): Gender = when (this) {
    UserProfile.Sex.MALE -> Gender.MALE
    UserProfile.Sex.FEMALE -> Gender.FEMALE
    UserProfile.Sex.UNKNOWN -> Gender.UNKNOWN
}


