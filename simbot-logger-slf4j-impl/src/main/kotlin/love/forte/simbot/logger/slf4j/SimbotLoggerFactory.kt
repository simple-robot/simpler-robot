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

import com.lmax.disruptor.*
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import love.forte.simbot.logger.Logger
import org.slf4j.ILoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * `simbot-logger` 的 slf4j 日志工厂。
 *
 * @property processors 对日志的处理器链。会按照顺序处理每一次的日志请求。
 */
public class SimbotLoggerFactory(
    private val processors: List<SimbotLoggerProcessor>
) : ILoggerFactory {

    private val factory = LogInfoDataFactory()
    private val disruptor: Disruptor<LogInfoEvent>
    private val producer: LogInfoDataProducer
    private val processThreadGroup = ThreadGroup("simbot-logger-process")

    init {
        val index = AtomicInteger(0)
        val disruptor = Disruptor(
            factory,
            524288, // 512*1024
            ThreadFactory {
                Thread(processThreadGroup, it, processThreadGroup.name + "-${index.getAndIncrement()}").also { t ->
                    t.isDaemon = true
                }
            },
            ProducerType.SINGLE,
            SleepingWaitStrategy()
        )
        disruptor.handleEventsWith(LogInfoDataEventHandler(processors))
        disruptor.start()
        this.disruptor = disruptor

        val producer = LogInfoDataProducer(disruptor.ringBuffer)
        this.producer = producer

        Runtime.getRuntime().addShutdownHook(thread(
            start = false, name = "SimbotLoggerFactoryShutdownHook"
        ) {
            disruptor.shutdown(5, TimeUnit.SECONDS)
        })
    }

    /**
     * Return an appropriate [Logger] instance as specified by the
     * `name` parameter.
     */
    override fun getLogger(name: String?): Logger {
        return loggerCache.computeIfAbsent(name.toString()) { loggerName ->
            SimbotLogger(loggerName, processors, producer::onLog)
        }
    }


    public companion object {
        private val loggerCache = ConcurrentHashMap<String, Logger>(32)
    }
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
        processors.forEach { processor ->
            val info = event.info
            if (processor.isLevelEnabled(event.info.fullName, info.level, info.marker)) {
                processor.doHandle(info)
            }
        }
    }

    override fun onEvent(event: LogInfoEvent) {
        processors.forEach { processor ->
            val info = event.info
            if (processor.isLevelEnabled(event.info.fullName, info.level, info.marker)) {
                processor.doHandle(info)
            }
        }
    }
}

private class LogInfoDataProducer(private val ringBuffer: RingBuffer<LogInfoEvent>) {
    fun onLog(logInfo: LogInfo) {
        val sequence = ringBuffer.next()
        try {
            val event = ringBuffer[sequence]
            event.info = logInfo
        } finally {
            ringBuffer.publish(sequence)
        }
    }
}
