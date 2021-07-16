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

package love.forte.simbot.component.mirai.utils

import love.forte.simbot.component.mirai.sender.cookies
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.http.template.assertBody
import net.mamoe.mirai.Bot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern


/**
 *
 * 通过得到[Bot]的cookies信息以获取Bot的等级信息。
 *
 * Created by lcy on 2020/8/25.
 */
@Deprecated("Unused.")
object BotLevelUtil {

    private val warnings by lazy { TreeSet<Long>() }

    private val logger: Logger = LoggerFactory.getLogger(BotLevelUtil::class.java)

    private val levelCache: MutableMap<Long, Int> = ConcurrentHashMap()

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
        val id = bot.id
        val level = levelCache[id]
        if (level != null) {
            return level
        }
        return kotlin.runCatching {
            levelCache.computeIfAbsent(id) {
                val cookies = bot.cookies ?: return@computeIfAbsent DEFAULT_VALUE
                val vipHtml = http.get(VIP_URL, null, cookies.cookiesMap, null, String::class.java)
                val message = vipHtml.assertBody()!!
                val matcher = levelPattern.matcher(message)
                val getLevel = if (matcher.find()) {
                    matcher.group(1).toInt()
                } else DEFAULT_VALUE
                getLevel
            }
        }.getOrElse { e ->
            if (logger.isWarnEnabled) {
                if (!warnings.contains(id)) {
                    logger.warn("Cannot get bot($id) level, will return default level: $DEFAULT_VALUE.", e)
                    warnings.add(id)
                }
            }
            DEFAULT_VALUE
        }
    }

}
