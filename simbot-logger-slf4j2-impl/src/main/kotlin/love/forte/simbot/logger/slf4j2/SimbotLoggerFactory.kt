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

import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.slf4j2.DEBUG.debug
import love.forte.simbot.logger.slf4j2.dispatcher.LogDispatcher
import org.slf4j.ILoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

/**
 * `simbot-logger` 的 slf4j 日志工厂。
 *
 * @property processors 对日志的处理器链。会按照顺序处理每一次的日志请求。
 */
public class SimbotLoggerFactory(
    private val processors: List<SimbotLoggerProcessor>,
    configuration: SimbotLoggerConfiguration
) : ILoggerFactory {

    private val dispatcher: LogDispatcher

    init {
        val mode = configuration.dispatcherMode ?: DispatchMode.DISRUPTOR
        configuration.debug("SimbotLoggerFactory") { "Dispatch mode: $mode" }

        this.dispatcher = mode.factory.create(processors, configuration)
        configuration.debug("SimbotLoggerFactory") { "dispatcher: $dispatcher" }

        Runtime.getRuntime().addShutdownHook(
            thread(
                start = false,
                name = "SimbotLoggerDispatcherShutdownHook"
            ) {
                runCatching {
                    dispatcher.close()
                }.onFailure { e ->
                    System.err.println("Logger dispatcher $dispatcher close failed: ${e.localizedMessage}")
                    e.printStackTrace(System.err)
                }
            }
        )
    }

    /**
     * Return an appropriate [Logger] instance as specified by the
     * `name` parameter.
     */
    override fun getLogger(name: String?): Logger {
        return loggerCache.computeIfAbsent(name.toString()) { loggerName ->
            SimbotLogger(loggerName, processors, dispatcher::onLog)
        }
    }

    public companion object {
        private val loggerCache = ConcurrentHashMap<String, Logger>(32)
    }
}

