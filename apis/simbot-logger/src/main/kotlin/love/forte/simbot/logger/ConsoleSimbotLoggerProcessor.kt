package love.forte.simbot.logger

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

    override suspend fun doHandle(info: LogInfo) {
        // time -
        // 2022-02-23 16:52:36.014  INFO 8070 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: createUsingPOST_1
        // 2022-02-23 16:52:36.014  INFO 8070 --- [thread name] full name : msg
        val printer: PrintStream = if (info.level == Level.ERROR) System.err else System.out
        val printMsg = buildString {
            append(Instant.ofEpochMilli(info.timestamp)).append(' ')
            if (info.level.toString().length <= 4) {
                append(' ')
            }
            append(info.level.toString()).append(' ').append(" --- [")
            // TODO thread name format
            append("] ")

        }

        TODO("Not yet implemented")
    }
}


internal fun String.getOnMax(max: Int): String {
    return if (length <= max) this
    else this.substring(lastIndex - (length - max + 1))
}