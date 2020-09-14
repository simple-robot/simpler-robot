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

import org.slf4j.helpers.MarkerIgnoringBase


/**
 * 将不会对带有Marker的进行实现。
 */
class NekoLogger(private val logName: String, val msgFormatter: LoggerFormatter) : MarkerIgnoringBase() {


    /**
     * Return the name of this `Logger` instance.
     * @return name of this logger instance
     */
    override fun getName(): String = logName

    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level,
     * false otherwise.
     * @since 1.4
     */
    override fun isTraceEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the TRACE level.
     *
     * @param msg the message string to be logged
     * @since 1.4
     */
    override fun trace(msg: String?) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     * false otherwise.
     */
    override fun isDebugEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    override fun debug(msg: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun debug(format: String?, arg: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the DEBUG level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for DEBUG. The variants taking
     * [one][.debug] and [two][.debug]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun debug(format: String?, vararg arguments: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun debug(msg: String?, t: Throwable?) {
        TODO("Not yet implemented")
    }

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level,
     * false otherwise.
     */
    override fun isInfoEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    override fun info(msg: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the INFO level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun info(format: String?, arg: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the INFO level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the INFO level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for INFO. The variants taking
     * [one][.info] and [two][.info]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun info(format: String?, vararg arguments: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun info(msg: String?, t: Throwable?) {
        TODO("Not yet implemented")
    }

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level,
     * false otherwise.
     */
    override fun isWarnEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    override fun warn(msg: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the WARN level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun warn(format: String?, arg: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the WARN level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for WARN. The variants taking
     * [one][.warn] and [two][.warn]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun warn(format: String?, vararg arguments: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the WARN level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun warn(msg: String?, t: Throwable?) {
        TODO("Not yet implemented")
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     * false otherwise.
     */
    override fun isErrorEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    override fun error(msg: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun error(format: String?, arg: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the ERROR level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for ERROR. The variants taking
     * [one][.error] and [two][.error]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun error(format: String?, vararg arguments: Any?) {
        TODO("Not yet implemented")
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun error(msg: String?, t: Throwable?) {
        TODO("Not yet implemented")
    }
}
