package love.forte.simbot.logger

import org.slf4j.Marker
import org.slf4j.event.Level

/**
 * 用于作为 [SimbotLogger] 中的日志处理器所使用的，通过 `Java Service Interface` 进行加载，并取第一个有效值。
 *
 * 如果无法加载任何处理器，将会使用默认的处理器于控制台输出相应日志。
 *
 * @author ForteScarlet
 */
public interface SimbotLoggerProcessor {
    /**
     * 检测日志等级是否可用。
     */
    public fun isLevelEnabled(level: Level, marker: Marker?): Boolean

    /**
     * 处理日志。
     */
    public suspend fun doHandle(info: LogInfo)
}


/**
 * 一次日志所记录的信息。
 */
public class LogInfo(
    public val level: Level,
    public val marker: Marker?,
    public val msg: String,
    public val args: Array<Any?>,
    public val error: Throwable?,
    public val thread: Thread,
    public val timestamp: Long,
) {
    /**
     * 获取格式化之后的消息文本。
     */
    public val formattedMsg: String by lazy {
        var index = 0
        FORMAT_REGEX.replace(msg) { result ->
            if (index > args.lastIndex) {
                result.value
            } else {
                args[index++].toString()
            }
        }
    }

    public companion object {
        private val FORMAT_REGEX = Regex("\\{}")
    }
}