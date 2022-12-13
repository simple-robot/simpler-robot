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
import org.slf4j.Marker

/**
 * 用于作为 [SimbotLogger] 中的日志处理器所使用的，通过 [SimbotLoggerProcessorsFactory] 进行加载，并取第一个有效工厂。
 *
 * 如果无法加载任何处理器，将会使用默认的处理器 [ConsoleSimbotLoggerProcessor] 于控制台输出相应的日志。
 *
 * @author ForteScarlet
 */
public interface SimbotLoggerProcessor {
    /**
     * 检测日志等级是否可用。
     */
    @Deprecated("use isLevelEnabled(name, level, marker)", ReplaceWith("isLevelEnabled(null, level, marker)"))
    public fun isLevelEnabled(level: LogLevel, marker: Marker?): Boolean = isLevelEnabled(null, level, marker)

    /**
     * 检测日志等级是否可用。
     */
    public fun isLevelEnabled(name: String?, level: LogLevel, marker: Marker?): Boolean

    /**
     * 处理日志。 [doHandle] 是当 [SimbotLoggerFactory] 中的异步处理通道尚未关闭的时候进行的处理函数。
     */
    public fun doHandle(info: LogInfo)
}

/**
 * [SimbotLoggerProcessor] 的工厂接口， 通过 `Java Service Loader` ([java.util.ServiceLoader]) 进行加载。
 */
public interface SimbotLoggerProcessorsFactory {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use getProcessors(SimbotLoggerConfiguration)")
    public fun getProcessors(): List<SimbotLoggerProcessor> {
        return getProcessors(createSimbotLoggerConfiguration(false))
    }

    public fun getProcessors(configuration: SimbotLoggerConfiguration): List<SimbotLoggerProcessor>
}


/**
 * 一次日志所记录的信息。
 */
public class LogInfo(
    public val level: LogLevel,
    public val marker: Marker?,
    public val msg: String,
    public val args: Array<out Any?>,
    public val error: Throwable?,
    public val name: String,
    public val fullName: String,
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
