/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.logger.slf4j.dispatcher

import love.forte.simbot.logger.slf4j.LogInfo
import love.forte.simbot.logger.slf4j.SimbotLoggerConfiguration
import love.forte.simbot.logger.slf4j.SimbotLoggerProcessor
import java.io.Closeable
import java.io.IOException


/**
 * 日志调度器，用于调度收集到的日志信息 [LogInfo]。
 *
 * @author ForteScarlet
 */
public interface LogDispatcher : Closeable {

    /**
     * 接收到并处理日志信息。
     *
     */
    public fun onLog(logInfo: LogInfo)

    /**
     * 终止并关闭调度器。
     *
     * 关闭前应当处理完未完成的任务，但是此行为无法被保证。
     */
    @Throws(IOException::class)
    public override fun close()
}

/**
 * [LogDispatcher] 的工厂。
 *
 * _内部使用的API_
 *
 */
public interface LogDispatcherFactory {

    /**
     * 使用提供的参数构建一个 [LogDispatcher].
     */
    public fun create(
        processors: List<SimbotLoggerProcessor>,
        configuration: SimbotLoggerConfiguration
    ): LogDispatcher
}
