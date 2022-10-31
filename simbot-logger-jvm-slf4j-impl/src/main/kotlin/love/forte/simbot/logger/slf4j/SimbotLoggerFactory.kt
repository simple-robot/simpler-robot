/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
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
            if (processor.isLevelEnabled(info.level, info.marker)) {
                processor.doHandle(info)
            }
        }
    }

    override fun onEvent(event: LogInfoEvent) {
        processors.forEach { processor ->
            val info = event.info
            if (processor.isLevelEnabled(info.level, info.marker)) {
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