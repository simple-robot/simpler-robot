package love.forte.simbot.logger

import kotlinx.coroutines.channels.Channel
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.event.Level


/**
 * 基础的Simbot Logger类。
 * @author ForteScarlet
 */
public class SimbotLogger(
    private val fullyQualifiedCallerName: String,
    private val processors: List<SimbotLoggerProcessor>,
    private val sendLog: (LogInfo) -> Unit
) : Logger {
    private val processChannel: Channel<LogInfo> = Channel(capacity = 64)
    private val simpleName: String = fullyQualifiedCallerName.getOnMax(20)

    public companion object {
        private val EMPTY_ARGUMENTS = emptyArray<Any?>()
        private fun Any?.toArgArray(): Array<out Any?> {
            (return if (this is Array<*>) this
            else arrayOf(this))
        }
    }

    // init {
    //     processChannel.consumeAsFlow().onEach { logInfo ->
    //         processors.forEach { processor ->
    //             processor.doHandle(logInfo)
    //         }
    //     }.launchIn(processScope)
    // }

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

    public fun getFullyQualifiedCallerName(): String = fullyQualifiedCallerName
    private fun doLog(
        level: Level?,
        marker: Marker?,
        msg: String?,
        arguments: Array<out Any?>?,
        throwable: Throwable?
    ) {
        val thread = Thread.currentThread()
        val timestamp = System.currentTimeMillis()
        sendLog(
            LogInfo(
                level ?: Level.INFO,
                marker,
                msg ?: "null",
                arguments ?: EMPTY_ARGUMENTS,
                throwable,
                name = simpleName,
                fullName = fullyQualifiedCallerName,
                thread,
                timestamp
            )
        )
        // processScope.launch {
        //     processChannel.send(
        //
        //     )
        // }
    }


    override fun getName(): String = fullyQualifiedCallerName

    override fun trace(msg: String?) {
        doLog(Level.TRACE, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun trace(format: String?, arg: Any?) {
        doLog(Level.TRACE, null, format, arg.toArgArray(), null)
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.TRACE, null, format, arrayOf(arg1, arg2), null)
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        doLog(Level.TRACE, null, format, arguments, null)
    }

    override fun trace(msg: String?, t: Throwable?) {
        doLog(Level.TRACE, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun trace(marker: Marker?, msg: String?) {
        doLog(Level.TRACE, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun trace(marker: Marker?, format: String?, arg: Any?) {
        doLog(Level.TRACE, marker, format, arg.toArgArray(), null)
    }

    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.TRACE, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun trace(marker: Marker?, format: String?, vararg argArray: Any?) {
        doLog(Level.TRACE, marker, format, argArray, null)
    }

    override fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(Level.TRACE, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun debug(msg: String?) {
        doLog(Level.DEBUG, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun debug(format: String?, arg: Any?) {
        doLog(Level.DEBUG, null, format, arg.toArgArray(), null)
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.DEBUG, null, format, arrayOf(arg1, arg2), null)
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        doLog(Level.DEBUG, null, format, arguments, null)
    }

    override fun debug(msg: String?, t: Throwable?) {
        doLog(Level.DEBUG, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun debug(marker: Marker?, msg: String?) {
        doLog(Level.DEBUG, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun debug(marker: Marker?, format: String?, arg: Any?) {
        doLog(Level.DEBUG, marker, format, arg.toArgArray(), null)
    }

    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.DEBUG, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(Level.DEBUG, marker, format, arguments, null)
    }

    override fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(Level.DEBUG, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun info(msg: String?) {
        doLog(Level.INFO, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun info(format: String?, arg: Any?) {
        doLog(Level.INFO, null, format, arg.toArgArray(), null)
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.INFO, null, format, arrayOf(arg1, arg2), null)
    }

    override fun info(format: String?, vararg arguments: Any?) {
        doLog(Level.INFO, null, format, arguments, null)
    }

    override fun info(msg: String?, t: Throwable?) {
        doLog(Level.INFO, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun info(marker: Marker?, msg: String?) {
        doLog(Level.INFO, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun info(marker: Marker?, format: String?, arg: Any?) {
        doLog(Level.INFO, marker, format, arg.toArgArray(), null)
    }

    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.INFO, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(Level.INFO, marker, format, arguments, null)
    }

    override fun info(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(Level.INFO, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun warn(msg: String?) {
        doLog(Level.WARN, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun warn(format: String?, arg: Any?) {
        doLog(Level.WARN, null, format, arg.toArgArray(), null)
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        doLog(Level.WARN, null, format, arguments, null)
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.WARN, null, format, arrayOf(arg1, arg2), null)
    }

    override fun warn(msg: String?, t: Throwable?) {
        doLog(Level.WARN, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun warn(marker: Marker?, msg: String?) {
        doLog(Level.WARN, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun warn(marker: Marker?, format: String?, arg: Any?) {
        doLog(Level.WARN, marker, format, arg.toArgArray(), null)
    }

    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.WARN, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(Level.WARN, marker, format, arguments, null)
    }

    override fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(Level.WARN, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun error(msg: String?) {
        doLog(Level.ERROR, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun error(format: String?, arg: Any?) {
        doLog(Level.ERROR, null, format, arg.toArgArray(), null)
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.ERROR, null, format, arrayOf(arg1, arg2), null)
    }

    override fun error(format: String?, vararg arguments: Any?) {
        doLog(Level.ERROR, null, format, arguments, null)
    }

    override fun error(msg: String?, t: Throwable?) {
        doLog(Level.ERROR, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun error(marker: Marker?, msg: String?) {
        doLog(Level.ERROR, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun error(marker: Marker?, format: String?, arg: Any?) {
        doLog(Level.ERROR, marker, format, arg.toArgArray(), null)
    }

    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(Level.ERROR, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(Level.ERROR, marker, format, arguments, null)
    }

    override fun error(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(Level.ERROR, marker, msg, EMPTY_ARGUMENTS, t)
    }
}

