/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.logger

/**
 * 日志对象类型，用于输出日志到目标处（例如控制台）。
 *
 * ## JVM
 * 在JVM平台目标会将 [Logger] 作为 [org.slf4j.Logger] 使用。
 *
 * ## JS
 * 在JS平台中会通过 `kotlin.js.Console` 向控制台输出日志信息。详见平台实现说明。
 *
 * ## Native
 * 在native平台中则会直接使用 [println] 简单的向控制台输出日志信息。
 *
 * @author ForteScarlet
 */
@Suppress("KDocUnresolvedReference")
public actual interface Logger {
    /**
     * 当前logger的名称。
     */
    public actual fun getName(): String

    /**
     * 是否允许 [LogLevel.TRACE] 级别的日志输出.
     */
    public actual fun isTraceEnabled(): Boolean

    /**
     * 输出 [LogLevel.TRACE] 级别日志。
     */
    public actual fun trace(log: String)

    /**
     * 输出 [LogLevel.TRACE] 级别日志。
     */
    public actual fun trace(log: String, vararg arg: Any?)

    /**
     * 是否允许 [LogLevel.DEBUG] 级别的日志输出.
     */
    public actual fun isDebugEnabled(): Boolean

    /**
     * 输出 [LogLevel.DEBUG] 级别日志。
     */
    public actual fun debug(log: String)

    /**
     * 输出 [LogLevel.DEBUG] 级别日志。
     */
    public actual fun debug(log: String, vararg arg: Any?)

    /**
     * 是否允许 [LogLevel.INFO] 级别的日志输出.
     */
    public actual fun isInfoEnabled(): Boolean

    /**
     * 输出 [LogLevel.INFO] 级别日志。
     */
    public actual fun info(log: String)

    /**
     * 输出 [LogLevel.INFO] 级别日志。
     */
    public actual fun info(log: String, vararg arg: Any?)

    /**
     * 是否允许 [LogLevel.WARN] 级别的日志输出.
     */
    public actual fun isWarnEnabled(): Boolean

    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun warn(log: String)

    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun warn(log: String, vararg arg: Any?)

    /**
     * 是否允许 [LogLevel.ERROR] 级别的日志输出.
     */
    public actual fun isErrorEnabled(): Boolean

    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun error(log: String)

    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun error(log: String, vararg arg: Any?)
}
