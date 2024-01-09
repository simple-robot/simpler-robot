/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.logger.slf4j2

import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.Logger
import org.slf4j.Marker


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

    override fun isTraceEnabled(): Boolean = isLevelEnabled(LogLevel.TRACE)
    override fun isTraceEnabled(marker: Marker?): Boolean = isLevelEnabled(LogLevel.TRACE, marker)
    override fun isDebugEnabled(): Boolean = isLevelEnabled(LogLevel.DEBUG)
    override fun isDebugEnabled(marker: Marker?): Boolean = isLevelEnabled(LogLevel.DEBUG, marker)
    override fun isInfoEnabled(): Boolean = isLevelEnabled(LogLevel.INFO)
    override fun isInfoEnabled(marker: Marker?): Boolean = isLevelEnabled(LogLevel.INFO, marker)
    override fun isWarnEnabled(): Boolean = isLevelEnabled(LogLevel.WARN)
    override fun isWarnEnabled(marker: Marker?): Boolean = isLevelEnabled(LogLevel.WARN, marker)
    override fun isErrorEnabled(): Boolean = isLevelEnabled(LogLevel.ERROR)
    override fun isErrorEnabled(marker: Marker?): Boolean = isLevelEnabled(LogLevel.ERROR, marker)

    private fun isLevelEnabled(level: LogLevel, marker: Marker? = null): Boolean {
        return processors.any { it.isLevelEnabled(fullyQualifiedCallerName, level, marker) }
    }

    public fun getFullyQualifiedCallerName(): String = fullyQualifiedCallerName
    private fun doLog(
        level: LogLevel?,
        marker: Marker?,
        msg: String?,
        arguments: Array<out Any?>?,
        throwable: Throwable?
    ) {
        val thread = Thread.currentThread()
        val timestamp = System.currentTimeMillis()
        sendLog(
            LogInfo(
                level ?: LogLevel.INFO,
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
        doLog(LogLevel.TRACE, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun trace(format: String?, arg: Any?) {
        doLog(LogLevel.TRACE, null, format, arg.toArgArray(), null)
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.TRACE, null, format, arrayOf(arg1, arg2), null)
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        doLog(LogLevel.TRACE, null, format, arguments, null)
    }

    override fun trace(msg: String?, t: Throwable?) {
        doLog(LogLevel.TRACE, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun trace(marker: Marker?, msg: String?) {
        doLog(LogLevel.TRACE, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun trace(marker: Marker?, format: String?, arg: Any?) {
        doLog(LogLevel.TRACE, marker, format, arg.toArgArray(), null)
    }

    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.TRACE, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun trace(marker: Marker?, format: String?, vararg argArray: Any?) {
        doLog(LogLevel.TRACE, marker, format, argArray, null)
    }

    override fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(LogLevel.TRACE, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun debug(msg: String?) {
        doLog(LogLevel.DEBUG, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun debug(format: String?, arg: Any?) {
        doLog(LogLevel.DEBUG, null, format, arg.toArgArray(), null)
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.DEBUG, null, format, arrayOf(arg1, arg2), null)
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        doLog(LogLevel.DEBUG, null, format, arguments, null)
    }

    override fun debug(msg: String?, t: Throwable?) {
        doLog(LogLevel.DEBUG, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun debug(marker: Marker?, msg: String?) {
        doLog(LogLevel.DEBUG, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun debug(marker: Marker?, format: String?, arg: Any?) {
        doLog(LogLevel.DEBUG, marker, format, arg.toArgArray(), null)
    }

    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.DEBUG, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(LogLevel.DEBUG, marker, format, arguments, null)
    }

    override fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(LogLevel.DEBUG, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun info(msg: String?) {
        doLog(LogLevel.INFO, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun info(format: String?, arg: Any?) {
        doLog(LogLevel.INFO, null, format, arg.toArgArray(), null)
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.INFO, null, format, arrayOf(arg1, arg2), null)
    }

    override fun info(format: String?, vararg arguments: Any?) {
        doLog(LogLevel.INFO, null, format, arguments, null)
    }

    override fun info(msg: String?, t: Throwable?) {
        doLog(LogLevel.INFO, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun info(marker: Marker?, msg: String?) {
        doLog(LogLevel.INFO, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun info(marker: Marker?, format: String?, arg: Any?) {
        doLog(LogLevel.INFO, marker, format, arg.toArgArray(), null)
    }

    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.INFO, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(LogLevel.INFO, marker, format, arguments, null)
    }

    override fun info(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(LogLevel.INFO, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun warn(msg: String?) {
        doLog(LogLevel.WARN, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun warn(format: String?, arg: Any?) {
        doLog(LogLevel.WARN, null, format, arg.toArgArray(), null)
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        doLog(LogLevel.WARN, null, format, arguments, null)
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.WARN, null, format, arrayOf(arg1, arg2), null)
    }

    override fun warn(msg: String?, t: Throwable?) {
        doLog(LogLevel.WARN, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun warn(marker: Marker?, msg: String?) {
        doLog(LogLevel.WARN, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun warn(marker: Marker?, format: String?, arg: Any?) {
        doLog(LogLevel.WARN, marker, format, arg.toArgArray(), null)
    }

    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.WARN, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(LogLevel.WARN, marker, format, arguments, null)
    }

    override fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(LogLevel.WARN, marker, msg, EMPTY_ARGUMENTS, t)
    }

    override fun error(msg: String?) {
        doLog(LogLevel.ERROR, null, msg, EMPTY_ARGUMENTS, null)
    }

    override fun error(format: String?, arg: Any?) {
        doLog(LogLevel.ERROR, null, format, arg.toArgArray(), null)
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.ERROR, null, format, arrayOf(arg1, arg2), null)
    }

    override fun error(format: String?, vararg arguments: Any?) {
        doLog(LogLevel.ERROR, null, format, arguments, null)
    }

    override fun error(msg: String?, t: Throwable?) {
        doLog(LogLevel.ERROR, null, msg, EMPTY_ARGUMENTS, t)
    }

    override fun error(marker: Marker?, msg: String?) {
        doLog(LogLevel.ERROR, marker, msg, EMPTY_ARGUMENTS, null)
    }

    override fun error(marker: Marker?, format: String?, arg: Any?) {
        doLog(LogLevel.ERROR, marker, format, arg.toArgArray(), null)
    }

    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        doLog(LogLevel.ERROR, marker, format, arrayOf(arg1, arg2), null)
    }

    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        doLog(LogLevel.ERROR, marker, format, arguments, null)
    }

    override fun error(marker: Marker?, msg: String?, t: Throwable?) {
        doLog(LogLevel.ERROR, marker, msg, EMPTY_ARGUMENTS, t)
    }
}
