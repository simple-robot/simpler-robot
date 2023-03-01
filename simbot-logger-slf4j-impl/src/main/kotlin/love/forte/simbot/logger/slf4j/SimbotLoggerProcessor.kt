/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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
