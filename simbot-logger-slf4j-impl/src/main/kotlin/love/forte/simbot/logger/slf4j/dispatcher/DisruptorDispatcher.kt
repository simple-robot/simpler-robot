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

import com.lmax.disruptor.*
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import love.forte.simbot.logger.slf4j.*
import love.forte.simbot.logger.slf4j.DEBUG.debug
import java.io.IOException
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * 基于 [Disruptor] 的 [LogDispatcher] 实现。
 *
 * ## 配置项
 * - `dispatcher.disruptor.ringBufferSize`: 用于指定 [Disruptor.ringBuffer] 的大小。
 *
 * @author ForteScarlet
 */
public class DisruptorDispatcher internal constructor(
    processors: List<SimbotLoggerProcessor>,
    configuration: SimbotLoggerConfiguration
) : LogDispatcher {
    private val factory = LogInfoDataFactory()
    private val disruptor: Disruptor<LogInfoEvent>
    private val processThreadGroup: ThreadGroup

    init {
        val config = Config()
        resolveConfig(configuration, config)

        configuration.debug("DisruptorDispatcher") {
            "Config: $config"
        }

        this.processThreadGroup = ThreadGroup(config.threadGroupName)

        val index = AtomicInteger(0)
        val disruptor = Disruptor(
            factory,
            config.ringBufferSize, // 512*1024
            ThreadFactory {
                Thread(processThreadGroup, it, processThreadGroup.name + "-${index.getAndIncrement()}").also { t ->
                    t.isDaemon = true
                }
            },
            ProducerType.SINGLE,
            SleepingWaitStrategy()
        )
        disruptor.handleEventsWith(LogInfoDataEventHandler(processors))
        this.disruptor = disruptor

        disruptor.start()
    }

    override fun onLog(logInfo: LogInfo) {
        val ringBuffer = disruptor.ringBuffer
        val sequence = ringBuffer.next()
        try {
            val event = ringBuffer[sequence]
            event.info = logInfo
        } finally {
            ringBuffer.publish(sequence)
        }
    }

    /**
     * 关闭当前的调度器。
     */
    @Throws(IOException::class)
    override fun close() {
        try {
            disruptor.shutdown(5, TimeUnit.SECONDS)
        } catch (te: TimeoutException) {
            System.err.println("Logger disruptor $disruptor shutdown timeout: ${te.localizedMessage}")
            te.printStackTrace(System.err)
        }
    }

    public companion object : LogDispatcherFactory {
        public const val PREFIX_KEY: String = "dispatcher.disruptor"
        public const val RING_BUFFER_SIZE_KEY: String = "$PREFIX_KEY.ringBufferSize"
        public const val THREAD_GROUP_NAME_KEY: String = "$PREFIX_KEY.threadGroupName"

        private fun resolveConfig(configuration: SimbotLoggerConfiguration, config: Config) {
            configuration[RING_BUFFER_SIZE_KEY]?.also { p ->
                val value = p.stringValue.trim().toIntOrNull() ?: return@also
                config.ringBufferSize = value
            }

            configuration[THREAD_GROUP_NAME_KEY]?.also { p ->
                val value = p.stringValue.trim().takeIf { it.isNotBlank() } ?: return@also
                config.threadGroupName = value
            }
        }

        override fun create(
            processors: List<SimbotLoggerProcessor>,
            configuration: SimbotLoggerConfiguration
        ): LogDispatcher = DisruptorDispatcher(processors, configuration)
    }

    private data class Config(
        var ringBufferSize: Int = 512 * 1024,
        var threadGroupName: String = "simbot-logger-dispatcher-disruptor",
    )


}


private class LogInfoEvent {
    lateinit var info: LogInfo
}

private class LogInfoDataFactory : EventFactory<LogInfoEvent> {
    override fun newInstance(): LogInfoEvent = LogInfoEvent()
}

private class LogInfoDataEventHandler(
    private val processors: List<SimbotLoggerProcessor>
) : EventHandler<LogInfoEvent>, WorkHandler<LogInfoEvent> {
    override fun onEvent(event: LogInfoEvent, sequence: Long, endOfBatch: Boolean) {
        onEvent(event)
    }

    override fun onEvent(event: LogInfoEvent) {
        processors.forEach { processor ->
            val info = event.info
            processor.doHandleIfLevelEnabled(info)
        }
    }
}
