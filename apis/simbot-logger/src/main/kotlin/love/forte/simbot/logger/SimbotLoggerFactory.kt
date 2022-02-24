package love.forte.simbot.logger

import com.lmax.disruptor.*
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * `simbot-logger` 的 slf4j 日志工厂。
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
            1024 * 1024,
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
        return SimbotLogger(name ?: "null", processors) { log ->
            producer.onLog(log)
        }
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
            processor.doHandle(event.info)
        }
    }

    override fun onEvent(event: LogInfoEvent) {
        processors.forEach { processor ->
            processor.doHandle(event.info)
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