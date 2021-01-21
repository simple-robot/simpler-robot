/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-mirai
 * File     BotLevelUtil.kt
 *
 * You can contact the author through the following channels:
 *  github https://github.com/ForteScarlet
 *  gitee  https://gitee.com/ForteScarlet
 *  email  ForteScarlet@163.com
 *  QQ     1149159218
 *  The Mirai code is copyrighted by mamoe-mirai
 *  you can see mirai at https://github.com/mamoe/mirai
 *
 *
 */

package love.forte.simbot.component.mirai.utils

import love.forte.simbot.component.mirai.sender.cookies
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.http.template.assertBody
import net.mamoe.mirai.Bot
import java.util.*
import java.util.regex.Pattern


internal data class BotKey(private val id: Long)

/**
 *
 * 通过得到[Bot]的cookies信息以获取Bot的等级信息。
 *
 * Created by lcy on 2020/8/25.
 * @author lcy
 */
object BotLevelUtil {

    private val levelCache: MutableMap<BotKey, Int> = WeakHashMap()

    private val levelPattern: Pattern = Pattern.compile("<em class=\"levelimg\">(\\d+)</em>")
    private const val VIP_URL = "https://vip.qq.com/client/level"

    /**
     * 获取不到的情况下使用的默认值
     */
    private const val DEFAULT_VALUE = -1

    /**
     * 获取当前bot的等级。
     * @return [bot]的level. 如果获取不到/接口变更/cookie失效等，就会得到-1.
     */
    fun level(bot: Bot, http: HttpTemplate): Int {
        val botKey = BotKey(bot.id)
        val level = levelCache[botKey]
        if (level != null) {
            return level
        }
        return synchronized(bot) {
            levelCache[botKey] ?: try {
                val cookies = bot.cookies ?: return@synchronized DEFAULT_VALUE
                val vipHtml = http.get(VIP_URL, null, cookies.cookiesMap, null, String::class.java)
                val message = vipHtml.assertBody()!!
                val matcher = levelPattern.matcher(message)
                val getLevel = if (matcher.find()) {
                    matcher.group(1).toInt()
                } else DEFAULT_VALUE
                levelCache[botKey] = getLevel
                getLevel
            } catch (e: Throwable) {
                DEFAULT_VALUE
            }
        }

    }

}