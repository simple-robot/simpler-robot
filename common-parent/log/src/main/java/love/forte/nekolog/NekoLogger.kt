/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NekoLogger.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.nekolog

import org.slf4j.event.Level
import org.slf4j.helpers.MarkerIgnoringBase
import java.io.PrintStream


/**
 * 将不会对带有Marker的进行实现。
 */
open class NekoLogger(private val logName: String,
                      private val colorBuilderFactory: ColorBuilderFactory,
                      private val level: Int,
                      private val msgFormatter: LoggerFormatter) : MarkerIgnoringBase() {


    protected open val tracePrint : PrintStream = System.out
    protected open val debugPrint : PrintStream = System.out
    protected open val infoPrint : PrintStream = System.out
    protected open val warnPrint : PrintStream = System.out
    protected open val errPrint : PrintStream = System.err

    private fun isEnable(level: Level): Boolean {
        return isEnable(level.toInt())
    }

    private fun isEnable(level: Int): Boolean {
        return this.level <= level
    }

    /**
     * Return the name of this `Logger` instance.
     * @return name of this logger instance
     */
    override fun getName(): String = logName

    private fun log(msg: String?, level: Level, printStream: PrintStream, err: Throwable?, vararg args: Any?) {
        val th: Thread = Thread.currentThread()
        val stack: StackTraceElement = th.stackTrace[3]
        if(isEnable(level)) {
            val formatInfo = FormatterInfo(msg, level, logName, th, stack, colorBuilderFactory.getColorBuilder(), args)
            printStream.println(msgFormatter.format(formatInfo))
            err?.printStackTrace(printStream)
        }
    }



    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level,
     * false otherwise.
     * @since 1.4
     */
    override fun isTraceEnabled(): Boolean = isEnable(Level.TRACE)

    /**
     * Log a message at the TRACE level.
     *
     * @param msg the message string to be logged
     * @since 1.4
     */
    override fun trace(msg: String?) {
        log(msg, Level.TRACE, tracePrint, null)
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the TRACE level.
     *
     * @param format the format string
     * @param arg    the argument
     * @since 1.4
     */
    override fun trace(format: String?, arg: Any?) {
        log(format, Level.TRACE, tracePrint, null, arg)
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the TRACE level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     * @since 1.4
     */
    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        log(format, Level.TRACE, tracePrint, null, arg1, arg2)
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the TRACE level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for TRACE. The variants taking [one][.trace] and
     * [two][.trace] arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     * @since 1.4
     */
    override fun trace(format: String?, vararg arguments: Any?) {
        log(format, Level.TRACE, tracePrint, null, arguments)
    }

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     * @since 1.4
     */
    override fun trace(msg: String?, t: Throwable?) {
        log(msg, Level.TRACE, tracePrint, t)
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     * false otherwise.
     */
    override fun isDebugEnabled(): Boolean = isEnable(Level.DEBUG)

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    override fun debug(msg: String?) {
        log(msg, Level.DEBUG, debugPrint, null)
    }
    override fun debug(format: String?, arg: Any?) {
        log(format, Level.DEBUG, debugPrint, null, arg)
    }
    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        log(format, Level.DEBUG, debugPrint, null, arg1, arg2)
    }
    override fun debug(format: String?, vararg arguments: Any?) {
        log(format, Level.DEBUG, debugPrint, null, arguments)
    }
    override fun debug(msg: String?, t: Throwable?) {
        log(msg, Level.DEBUG, debugPrint, t)
    }

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level,
     * false otherwise.
     */
    override fun isInfoEnabled(): Boolean = isEnable(Level.INFO)

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    override fun info(msg: String?) {
        log(msg, Level.INFO, infoPrint, null)
    }
    override fun info(format: String?, arg: Any?) {
        log(format, Level.INFO, infoPrint, null, arg)
    }
    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        log(format, Level.INFO, infoPrint, null, arg1, arg2)
    }
    override fun info(format: String?, vararg arguments: Any?) {
        log(format, Level.INFO, infoPrint, null, arguments)
    }
    override fun info(msg: String?, t: Throwable?) {
        log(msg, Level.INFO, infoPrint, t)
    }

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level,
     * false otherwise.
     */
    override fun isWarnEnabled(): Boolean = isEnable(Level.WARN)

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    override fun warn(msg: String?) {
        log(msg, Level.WARN, warnPrint, null)
    }
    override fun warn(format: String?, arg: Any?) {
        log(format, Level.WARN, warnPrint, null, arg)
    }
    override fun warn(format: String?, vararg arguments: Any?) {
        log(format, Level.WARN, warnPrint, null, arguments)
    }
    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        log(format, Level.WARN, warnPrint, null, arg1, arg2)
    }
    override fun warn(msg: String?, t: Throwable?) {
        log(msg, Level.WARN, warnPrint, t)
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     * false otherwise.
     */
    override fun isErrorEnabled(): Boolean = isEnable(Level.ERROR)

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    override fun error(msg: String?) {
        log(msg, Level.ERROR, errPrint, null)
    }
    override fun error(format: String?, arg: Any?) {
        log(format, Level.ERROR, errPrint, null, arg)
    }
    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        log(format, Level.ERROR, errPrint, null, arg1, arg2)
    }
    override fun error(format: String?, vararg arguments: Any?) {
        log(format, Level.ERROR, errPrint, null, arguments)
    }
    override fun error(msg: String?, t: Throwable?) {
        log(msg, Level.ERROR, errPrint, t)
    }
}
