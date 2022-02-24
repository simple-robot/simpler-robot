package love.forte.simbot.logger

import love.forte.simbot.logger.color.Color
import love.forte.simbot.logger.color.FontColor
import love.forte.simbot.logger.color.appendColor
import org.slf4j.Marker
import org.slf4j.event.Level
import java.io.PrintStream
import java.time.Instant

/**
 * 将日志直接打印到控制台上的处理器。
 */
public class ConsoleSimbotLoggerProcessor(private val level: Level) : SimbotLoggerProcessor {
    override fun isLevelEnabled(level: Level, marker: Marker?): Boolean {
        return this.level.toInt() >= level.toInt()
    }

    private fun printLog(info: LogInfo) {
        // time -
        // 2022-02-23 16:52:36.014  INFO 8070 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: createUsingPOST_1
        // 2022-02-23 16:52:36.014  INFO 8070 --- [thread name] full name : msg
        val printer: PrintStream = if (info.level == Level.ERROR) System.err else System.out

        val printMsg = buildString {
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
            append(info.name).append("  : ").append(info.formattedMsg)
        }

        printer.println(printMsg)
    }

    override suspend fun doHandle(info: LogInfo) {
        printLog(info)
    }

    override fun doHandleClosed(info: LogInfo) {
        printLog(info)
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