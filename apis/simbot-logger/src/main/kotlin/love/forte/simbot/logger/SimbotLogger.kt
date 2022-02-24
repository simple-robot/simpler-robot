/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
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

package love.forte.simbot.logger

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
    private val simpleName: String // = fullyQualifiedCallerName.getOnMax(20)

    init {
        var simpleName0 = if (fullyQualifiedCallerName.length > MAX_NAME_SIZE) {
            val split = fullyQualifiedCallerName.split('.')
            buildString(MAX_NAME_SIZE) {
                split.forEachIndexed { index, s ->
                    if (index != split.lastIndex) {
                        if (s.isNotEmpty()) {
                            append(s.first())
                        } else {
                            append('?')
                        }
                        append('.')
                    } else {
                        append(s)
                    }
                }
            }
        } else {
            fullyQualifiedCallerName
        }

        if (simpleName0.length < MAX_NAME_SIZE) {
            simpleName0 = " ".repeat(MAX_NAME_SIZE - simpleName0.length) + simpleName0
        }
        simpleName = simpleName0
    }

    public companion object {
        private const val MAX_NAME_SIZE = 35
        private val EMPTY_ARGUMENTS = emptyArray<Any?>()
        private fun Any?.toArgArray(): Array<out Any?> {
            (return if (this is Array<*>) this
            else arrayOf(this))
        }
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

