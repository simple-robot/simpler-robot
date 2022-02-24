/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.logger

import love.forte.simbot.logger.ConsoleSimbotLoggerProcessor.Companion.SIMBOT_LEVEL_PROPERTY_KEY
import love.forte.simbot.logger.color.Color
import love.forte.simbot.logger.color.FontColor
import love.forte.simbot.logger.color.appendColor
import org.slf4j.Marker
import org.slf4j.event.Level
import java.io.PrintStream
import java.time.Instant

/**
 * 将日志直接打印到控制台上的处理器。
 * 当参数 [level] 未指定日志等级的时候，会尝试加载系统参数 [SIMBOT_LEVEL_PROPERTY_KEY], 如果系统参数也找不到，则默认为 [Level.INFO] 级别。
 *
 * 你可以通过参数
 * ```
 * -Dsimbot.logger.level=DEBUG
 * ```
 * 来指定一个控制台的日志等级而不需要直接提供一个新的 [SimbotLoggerProcessorsFactory] 实现。
 *
 *
 */
public class ConsoleSimbotLoggerProcessor(level: Level?) : SimbotLoggerProcessor {
    private val level: Level = level ?: loadLevel()

    override fun isLevelEnabled(level: Level, marker: Marker?): Boolean {
        return this.level.toInt() <= level.toInt()
    }

    private fun printLog(info: LogInfo) {
        val printer: PrintStream = if (info.level == Level.ERROR) System.err else System.out

        val printMsg = buildString(info.formattedMsg.length + 80) {
            appendColor(FontColor.BLUE, Instant.ofEpochMilli(info.timestamp).toString()).append(' ')

            if (info.level.toString().length <= 4) {
                append(' ')
            }
            appendColor(info.level.color, info.level.toString()).append(' ').append(" --- [")
            val threadName = info.thread.name.getOnMax(20)
            if (threadName.length < 20) {
                repeat(20 - threadName.length) {
                    append(' ')
                }
            }
            append(threadName).append("] ")
            appendColor(FontColor.BLUE, info.name).append("  : ").append(info.formattedMsg)
        }

        printer.println(printMsg)
        if (info.error != null) {
            info.error.printStackTrace(printer)
        }
    }

    override fun doHandle(info: LogInfo) {
        printLog(info)
    }


    private fun loadLevel(): Level {
        val levelName = System.getProperty(SIMBOT_LEVEL_PROPERTY_KEY) ?: return Level.INFO
        return Level.values().find { it.name.equals(levelName, true) } ?: Level.INFO
    }


    public companion object {
        private const val SIMBOT_LEVEL_PROPERTY_KEY = "simbot.logger.level"
    }
}



internal fun String.getOnMax(max: Int): String {
    return if (length <= max) this
    else this.substring(length - max)
}

internal val Level.color: Color
    get() = when (this) {
        Level.INFO -> FontColor.GREEN
        Level.ERROR -> FontColor.RED
        Level.WARN -> FontColor.YELLOW
        Level.DEBUG -> FontColor.PURPLE
        Level.TRACE -> FontColor.BLUE
    }