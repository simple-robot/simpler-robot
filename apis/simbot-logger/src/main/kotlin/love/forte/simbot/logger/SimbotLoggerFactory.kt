package love.forte.simbot.logger

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

/**
 * `simbot-logger` 的 slf4j 日志工厂。
 */
public class SimbotLoggerFactory(
    private val processors: List<SimbotLoggerProcessor>
) : ILoggerFactory, CoroutineScope {

    override val coroutineContext: CoroutineContext =
        CoroutineName("SimbotLoggerProcessScope")+ newFixedThreadPoolContext(2, "simbot-logger-process")

    // private val processScope = CoroutineScope(
    //     CoroutineName("SimbotLoggerProcessScope") +
    //             newFixedThreadPoolContext(1, "simbot-logger-process")
    // )
    //
    // private val sendScope = CoroutineScope(
    //     CoroutineName("SimbotLoggerSendScope") +
    //             newFixedThreadPoolContext(4, "simbot-logger-send")
    // )


    private val processChannel = Channel<LogInfo>()

    init {
        val flow = processChannel.consumeAsFlow()
        val logJob = flow
            .onEach { log ->
            processors.forEach { processor ->
                processor.doHandle(log)
            }
        }.launchIn(this)

        Runtime.getRuntime().addShutdownHook(thread(
            start = false, name = "SimbotLoggerFactoryShutdownHook"
        ) {
            processChannel.close()
            val future = CompletableFuture<Unit>()
            logJob.invokeOnCompletion { e ->
                if (e != null) {
                    future.completeExceptionally(e)
                } else {
                    future.complete(Unit)
                }
            }
            kotlin.runCatching { future.join() }
        })
    }

    /**
     * Return an appropriate [Logger] instance as specified by the
     * `name` parameter.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLogger(name: String?): Logger {
        return SimbotLogger(name ?: "null", processors) { log ->
            launch {
                if (processChannel.isClosedForSend) {
                    launch {
                        processors.forEach { processor ->
                            processor.doHandleClosed(log)
                        }
                    }
                } else {
                    processChannel.send(log)
                }

            }
        }
    }
}