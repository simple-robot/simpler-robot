/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.logger.slf4j2.dispatcher

import love.forte.simbot.logger.slf4j2.LogInfo
import love.forte.simbot.logger.slf4j2.SimbotLoggerConfiguration
import love.forte.simbot.logger.slf4j2.SimbotLoggerProcessor
import love.forte.simbot.logger.slf4j2.doHandleIfLevelEnabled


/**
 * 同步地调度日志。
 *
 * 没有任何特殊机制。
 *
 * @author ForteScarlet
 */
public class SyncDispatcher(
    private val processors: List<SimbotLoggerProcessor>
) : LogDispatcher {

    override fun onLog(logInfo: LogInfo) {
        processors.forEach { p -> p.doHandleIfLevelEnabled(logInfo) }
    }

    override fun close() {
        // do nothing
    }

    public companion object Factory : LogDispatcherFactory {
        override fun create(
            processors: List<SimbotLoggerProcessor>,
            configuration: SimbotLoggerConfiguration
        ): LogDispatcher = SyncDispatcher(processors)
    }
}
