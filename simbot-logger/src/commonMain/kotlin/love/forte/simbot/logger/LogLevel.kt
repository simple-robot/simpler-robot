package love.forte.simbot.logger

import kotlin.jvm.JvmInline


/**
 * 日志级别。
 * @author ForteScarlet
 */
public expect enum class LogLevel {
    ERROR, WARN, INFO, DEBUG, TRACE,
}

@JvmInline
public value class LogLevelCompare(private val level: LogLevel) {
    internal inline val compareValue: Int get() = 5 - level.ordinal
    
}

public inline val LogLevel.level: LogLevelCompare get() = LogLevelCompare(this)

public operator fun LogLevelCompare.compareTo(other: LogLevelCompare): Int = compareValue.compareTo(other.compareValue)