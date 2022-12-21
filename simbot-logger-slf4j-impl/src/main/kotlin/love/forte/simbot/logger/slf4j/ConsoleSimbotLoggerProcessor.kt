/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.logger.slf4j

import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.slf4j.ConsoleSimbotLoggerProcessor.Companion.SIMBOT_LEVEL_PROPERTY_KEY
import love.forte.simbot.logger.slf4j.color.Color
import love.forte.simbot.logger.slf4j.color.FontColor
import love.forte.simbot.logger.slf4j.color.appendColor
import org.slf4j.Marker
import java.io.PrintStream
import java.time.Instant

/**
 * 将日志直接打印到控制台上的处理器。
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
            if (k.length > SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY.length && k.startsWith(SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY)) {
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
                ?: return (configuration.defaultLevel ?: LogLevel.INFO)
        return LogLevel.valueOf(levelName)
    }


    public companion object {
        private const val SIMBOT_LEVEL_PROPERTY_KEY = "level"
        private const val SIMBOT_LEVEL_CONSOLE_PROPERTY_KEY = "console.level"
    }
}


internal fun String.getOnMax(max: Int): String {
    return if (length <= max) this
    else this.substring(length - max)
}

internal val LogLevel.color: Color
    get() = when (this) {
        LogLevel.INFO -> FontColor.GREEN
        LogLevel.ERROR -> FontColor.RED
        LogLevel.WARN -> FontColor.YELLOW
        LogLevel.DEBUG -> FontColor.PURPLE
        LogLevel.TRACE -> FontColor.BLUE
    }
