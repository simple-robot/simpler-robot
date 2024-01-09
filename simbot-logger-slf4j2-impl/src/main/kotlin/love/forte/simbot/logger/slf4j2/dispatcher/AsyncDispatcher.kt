/*
 *     Copyright (c) 2023-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import love.forte.simbot.logger.slf4j2.DEBUG.debug
import love.forte.simbot.logger.slf4j2.LogInfo
import love.forte.simbot.logger.slf4j2.SimbotLoggerConfiguration
import love.forte.simbot.logger.slf4j2.SimbotLoggerProcessor
import love.forte.simbot.logger.slf4j2.doHandleIfLevelEnabled
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * 异步地调度日志。
 *
 * 纯粹地使用线程池作为调度器。
 *
 * ## 配置项
 *
 * - `dispatcher.async.corePoolSize` 调度使用的线程池的核心线程数，默认为 `0`。
 * - `dispatcher.async.maximumPoolSize` 调度使用的线程池的最大线程数，默认为 CPU数量 / 2 ，至少为 `1`。
 * - `dispatcher.async.keepAliveTimeMs` 调度使用的线程池的线程存活时间，单位毫秒。默认为 60_000 （60s），至少为 `0`。（不可为负）
 * - `dispatcher.async.daemon` 调度使用的线程池中产生的线程是否为守护线程。默认为 `false`
 * - `dispatcher.async.threadGroupName` 调度使用的线程池中产生的线程的线程组名称。
 *
 * 注意：调度器默认使用无界任务队列 [LinkedBlockingQueue]。
 *
 * @author ForteScarlet
 */
public class AsyncDispatcher(
    private val processors: List<SimbotLoggerProcessor>,
    configuration: SimbotLoggerConfiguration
) : LogDispatcher {

    private val executor: ExecutorService

    init {
        val config = Config()
        resolveConfig(configuration, config)
        configuration.debug("AsyncDispatcher") {
            "Config: $config"
        }

        val threadGroup = ThreadGroup(config.threadGroupName)
        val num = AtomicInteger(0)

        executor = ThreadPoolExecutor(
            config.corePoolSize,
            config.maximumPoolSize,
            config.keepAliveTimeMs,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue()
        ) { target ->
            Thread(threadGroup, target, "${threadGroup.name}-${num.incrementAndGet()}").apply {
                if (config.daemon) {
                    isDaemon = true
                }
            }
        }
    }

    override fun onLog(logInfo: LogInfo) {
        executor.execute {
            processors.forEach { p -> p.doHandleIfLevelEnabled(logInfo) }
        }
    }

    override fun close() {
        executor.shutdown()
    }

    public companion object Factory : LogDispatcherFactory {
        public const val PREFIX_KEY: String = "dispatcher.async"
        public const val CORE_POOL_SIZE_KEY: String = "$PREFIX_KEY.corePoolSize"
        public const val MAXIMUM_POOL_SIZE_KEY: String = "$PREFIX_KEY.maximumPoolSize"
        public const val KEEP_ALIVE_TIME_MS_KEY: String = "$PREFIX_KEY.keepAliveTimeMs"
        public const val TIME_UNIT_KEY: String = "$PREFIX_KEY.timeUnit"
        public const val DAEMON_KEY: String = "$PREFIX_KEY.daemon"
        public const val THREAD_GROUP_NAME_KEY: String = "$PREFIX_KEY.threadGroupName"

        private fun resolveConfig(configuration: SimbotLoggerConfiguration, config: Config) {
            configuration[CORE_POOL_SIZE_KEY]?.stringValue?.toIntOrNull()?.coerceAtLeast(0)?.also {
                config.corePoolSize = it
            }
            configuration[MAXIMUM_POOL_SIZE_KEY]?.stringValue?.toIntOrNull()?.coerceAtLeast(1)?.also {
                config.maximumPoolSize = it
            }
            configuration[KEEP_ALIVE_TIME_MS_KEY]?.stringValue?.toLong()?.coerceAtLeast(0)?.also {
                config.keepAliveTimeMs = it
            }
            configuration[DAEMON_KEY]?.stringValue.toBoolean().also {
                config.daemon = it
            }
            configuration[THREAD_GROUP_NAME_KEY]?.stringValue?.takeIf { it.isNotBlank() }?.also {
                config.threadGroupName = it
            }
        }

        override fun create(
            processors: List<SimbotLoggerProcessor>,
            configuration: SimbotLoggerConfiguration
        ): LogDispatcher = AsyncDispatcher(processors, configuration)
    }

    private data class Config(
        var corePoolSize: Int = 0,
        var maximumPoolSize: Int = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1),
        var keepAliveTimeMs: Long = 60L,
        var daemon: Boolean = false,
        var threadGroupName: String = "simbot-logger-dispatcher-async",
    )
}
