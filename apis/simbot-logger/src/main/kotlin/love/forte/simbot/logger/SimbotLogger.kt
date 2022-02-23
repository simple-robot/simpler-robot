package love.forte.simbot.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.AbstractLogger


/**
 * 基础的Simbot Logger类。
 * @author ForteScarlet
 */
public class SimbotLogger(
    private val fullyQualifiedCallerName: String,
    private val processScope: CoroutineScope,
    private val processors: List<SimbotLoggerProcessor>
) : Logger, AbstractLogger() {
    private val processChannel: Channel<LogInfo> = Channel(capacity = 64)

    init {
        processChannel.consumeAsFlow().onEach { logInfo ->
            processors.forEach { processor ->
                processor.doHandle(logInfo)
            }
        }.launchIn(processScope)
    }

    override fun isTraceEnabled(): Boolean = isLevelEnabled(Level.TRACE)
    override fun isTraceEnabled(marker: Marker?): Boolean = isLevelEnabled(Level.TRACE, marker)
    override fun isDebugEnabled(): Boolean = isLevelEnabled(Level.DEBUG)
    override fun isDebugEnabled(marker: Marker?): Boolean = isLevelEnabled(Level.DEBUG, marker)
    override fun isInfoEnabled(): Boolean = isLevelEnabled(Level.INFO)
    override fun isInfoEnabled(marker: Marker?): Boolean = isLevelEnabled(Level.INFO, marker)
    override fun isWarnEnabled(): Boolean = isLevelEnabled(Level.WARN)
    override fun isWarnEnabled(marker: Marker?): Boolean = isLevelEnabled(Level.WARN, marker)
    override fun isErrorEnabled(): Boolean = isLevelEnabled(Level.ERROR)
    override fun isErrorEnabled(marker: Marker?): Boolean = isLevelEnabled(Level.ERROR, marker)

    private fun isLevelEnabled(level: Level, marker: Marker? = null): Boolean {
        return processors.any { it.isLevelEnabled(level, marker) }
    }

    override fun getFullyQualifiedCallerName(): String = fullyQualifiedCallerName
    override fun handleNormalizedLoggingCall(
        level: Level?,
        marker: Marker?,
        msg: String?,
        arguments: Array<Any?>?,
        throwable: Throwable?
    ) {
        val thread = Thread.currentThread()
        val timestamp = System.currentTimeMillis()
        processScope.launch {
            processChannel.send(
                LogInfo(
                    level ?: Level.INFO,
                    marker,
                    msg ?: "null",
                    arguments ?: EMPTY_ARGUMENTS,
                    throwable,
                    thread,
                    timestamp
                )
            )
        }
    }

    public companion object {
        private val EMPTY_ARGUMENTS = emptyArray<Any?>()
    }
}
