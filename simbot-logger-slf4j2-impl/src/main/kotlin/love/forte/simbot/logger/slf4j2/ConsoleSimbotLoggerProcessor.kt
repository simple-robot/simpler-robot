/*
 *     Copyright (c) 2022-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.logger.slf4j2

import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.slf4j2.ConsoleSimbotLoggerProcessor.Companion.SIMBOT_LEVEL_PROPERTY_KEY
import love.forte.simbot.logger.slf4j2.color.Color
import love.forte.simbot.logger.slf4j2.color.FontColor
import love.forte.simbot.logger.slf4j2.color.appendColor
import org.slf4j.Marker
import java.io.PrintStream
import java.time.Instant

/**
 * 将日志直接打印到控制台上的处理器。
 *
 * ## 使用配置
 * ### 日志级别
 *
 * 当参数 [defaultLevel] 未指定日志等级的时候，会尝试加载系统参数 [`simbot.logger.level`][SIMBOT_LEVEL_PROPERTY_KEY], 如果系统参数也找不到，则默认为 [LogLevel.INFO][love.forte.simbot.logger.LogLevel.INFO] 级别。
 *
 * 你可以通过JVM参数 [`simbot.logger.level`][SIMBOT_LEVEL_PROPERTY_KEY]
 * 来指定一个控制台的日志等级而不需要直接提供一个新的 [SimbotLoggerProcessorsFactory] 实现,
 * 例如
 * ```
 * -Dsimbot.logger.level=DEBUG
 * ```
 *
 *
 */
public class ConsoleSimbotLoggerProcessor(configuration: SimbotLoggerConfiguration) : SimbotLoggerProcessor {
    // package prefix support?
    private val defaultLevel: LogLevel = loadLevel(configuration)
    private val prefixLevel: List<Pair<String, LogLevel>>? = loadPrefixLevel(configuration).takeIf { it.isNotEmpty() }


    override fun isLevelEnabled(name: String?, level: LogLevel, marker: Marker?): Boolean {
        if (name == null) {
            return defaultLevel.toInt() <= level.toInt()
        }

        val level0 = prefixLevel?.firstOrNull { (prefix, _) ->
            name.startsWith(prefix)
        }?.second ?: this.defaultLevel
        return level0.toInt() <= level.toInt()
    }

    private fun printLog(info: LogInfo) {
        val printer: PrintStream = if (info.level == LogLevel.ERROR) System.err else System.out

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
        } else if (info.args.lastOrNull() is Throwable) {
            val lastErr = info.args.last() as Throwable
            lastErr.printStackTrace(printer)
        }
    }

    override fun doHandle(info: LogInfo) {
        printLog(info)
    }


    private fun loadPrefixLevel(configuration: SimbotLoggerConfiguration): List<Pair<String, LogLevel>> {
        val prefixSet = mutableSetOf<String>()
        val prefixLevelList = mutableListOf<Pair<String, LogLevel>>()
        val properties = configuration.properties
        properties.forEach { (k, v) ->
            if (k.length > SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY.length &&
                k.startsWith(SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY)
            ) {
                val prefix = k.substringAfter("${SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY}.").takeIf { it.isNotBlank() }
                    ?: return@forEach
                if (prefixSet.add(prefix)) {
                    val level = LogLevel.valueOf(v.stringValue.uppercase())
                    prefixLevelList.add(prefix to level)
                }
            }
        }

        configuration.prefixLevelList.forEach { preLog ->
            if (prefixSet.add(preLog.prefix)) {
                prefixLevelList.add(preLog.prefix to preLog.level)
            }
        }

        return prefixLevelList
    }

    private fun loadLevel(configuration: SimbotLoggerConfiguration): LogLevel {
        val prop = configuration.properties
        val levelName =
            (prop[SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY] ?: prop[SIMBOT_LEVEL_PROPERTY_KEY])?.stringValue?.uppercase()
                ?: return configuration.defaultLevel ?: LogLevel.INFO
        return LogLevel.valueOf(levelName)
    }


    public companion object {
        private const val SIMBOT_LEVEL_PROPERTY_KEY = "level"
        private const val SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY = "console.level"
    }
}


internal fun String.getOnMax(max: Int): String {
    return if (length <= max) {
        this
    } else {
        this.substring(length - max)
    }
}

internal val LogLevel.color: Color
    get() = when (this) {
        LogLevel.INFO -> FontColor.GREEN
        LogLevel.ERROR -> FontColor.RED
        LogLevel.WARN -> FontColor.YELLOW
        LogLevel.DEBUG -> FontColor.PURPLE
        LogLevel.TRACE -> FontColor.BLUE
    }
