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

package love.forte.simbot.logger.internal

import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.Logger


/**
 * 针对 [Logger] 的简单抽象类，主要服务于JS平台和Native平台进行实现。
 *
 * @author ForteScarlet
 */
public abstract class AbstractSimpleLogger : Logger {
    protected abstract val displayName: String
    protected abstract val level: LogLevel
    
    abstract override fun getName(): String
    
    override fun isTraceEnabled(): Boolean = level >= LogLevel.TRACE
    
    private inline fun ifTrace(block: () -> Unit) {
        if (isTraceEnabled()) {
            block()
        }
    }
    
    protected abstract fun trace0(formattedLog: String, throwable: Throwable? = null)
    
    override fun trace(log: String): Unit = ifTrace {
        trace0(log, null)
    }
    
    override fun trace(log: String, vararg arg: Any?): Unit = ifTrace {
        doLog(log, arg, ::trace0)
    }
    
    override fun isDebugEnabled(): Boolean = level >= LogLevel.DEBUG
    
    private inline fun ifDebug(block: () -> Unit) {
        if (isDebugEnabled()) {
            block()
        }
    }
    
    protected abstract fun debug0(formattedLog: String, throwable: Throwable? = null)
    
    override fun debug(log: String): Unit = ifDebug {
        debug0(log, null)
    }
    
    override fun debug(log: String, vararg arg: Any?): Unit = ifDebug {
        doLog(log, arg, ::debug0)
    }
    
    override fun isInfoEnabled(): Boolean = level >= LogLevel.INFO
    
    private inline fun ifInfo(block: () -> Unit) {
        if (isInfoEnabled()) {
            block()
        }
    }
    
    protected abstract fun info0(formattedLog: String, throwable: Throwable? = null)
    
    override fun info(log: String): Unit = ifInfo {
        info0(log, null)
    }
    
    override fun info(log: String, vararg arg: Any?): Unit = ifInfo {
        doLog(log, arg, ::info0)
    }
    
    override fun isWarnEnabled(): Boolean = level >= LogLevel.WARN
    
    private inline fun ifWarn(block: () -> Unit) {
        if (isWarnEnabled()) {
            block()
        }
    }
    
    protected abstract fun warn0(formattedLog: String, throwable: Throwable? = null)
    
    override fun warn(log: String): Unit = ifWarn {
        warn0(log, null)
    }
    
    override fun warn(log: String, vararg arg: Any?): Unit = ifWarn {
        doLog(log, arg, ::warn0)
    }
    
    override fun isErrorEnabled(): Boolean = level >= LogLevel.ERROR
    
    private inline fun ifError(block: () -> Unit) {
        if (isErrorEnabled()) {
            block()
        }
    }
    
    protected abstract fun error0(formattedLog: String, throwable: Throwable? = null)
    
    override fun error(log: String): Unit = ifError {
        error0(log, null)
    }
    
    override fun error(log: String, vararg arg: Any?): Unit = ifError {
        doLog(log, arg, ::error0)
    }
    
    private inline fun doLog(log: String, args: Array<*>, doLogBlock: (String, Throwable?) -> Unit) {
        var e: Throwable? = null
        val formatted = log.logFormat(args) { r ->
            if (r == 1 && args.last() is Throwable) {
                e = args.last() as Throwable
            }
        }
        doLogBlock(formatted, e)
    }
}


internal const val MAX_LOG_NAME_LENGTH = 35


internal fun String.toDisplayName(): String {
    return when {
        length < MAX_LOG_NAME_LENGTH -> {
            val spaceSize = MAX_LOG_NAME_LENGTH - length
            buildString(MAX_LOG_NAME_LENGTH) {
                repeat(spaceSize) {
                    append(' ')
                }
                append(this@toDisplayName)
            }
        }
        
        length == MAX_LOG_NAME_LENGTH -> {
            this
        }
        
        else -> {
            val splitByPoint = split('.').toMutableList()
            if (splitByPoint.size == 1) {
                val value = splitByPoint.first()
                return value.substring(value.length - MAX_LOG_NAME_LENGTH, value.length)
            }
            
            fun computeTotal() = splitByPoint.sumOf { it.length } + splitByPoint.size - 1
            
            var simplificationIndex = 0
            while (true) {
                if (simplificationIndex == splitByPoint.lastIndex) {
                    break
                } else {
                    val v = splitByPoint[simplificationIndex]
                    splitByPoint[simplificationIndex] = v.firstOrNull()?.toString() ?: ""
                }
                
                val computeTotal = computeTotal()
                
                if (computeTotal <= MAX_LOG_NAME_LENGTH) {
                    break
                }
                
                simplificationIndex++
            }
            
            val joinToString = splitByPoint.joinToString(".")
            
            when {
                joinToString.length < MAX_LOG_NAME_LENGTH -> {
                    return buildString(MAX_LOG_NAME_LENGTH) {
                        repeat(MAX_LOG_NAME_LENGTH - joinToString.length) {
                            append(' ')
                        }
                        append(joinToString)
                    }
                }
                
                joinToString.length > MAX_LOG_NAME_LENGTH -> {
                    joinToString.substring(joinToString.length - MAX_LOG_NAME_LENGTH, joinToString.length)
                }
                
                else -> {
                    joinToString
                }
            }
        }
    }
}
