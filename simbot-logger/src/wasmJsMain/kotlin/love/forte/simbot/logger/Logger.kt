/*
 *     Copyright (c) 2023-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.logger

/**
 * 日志。
 *
 * 在JS平台中会通过 `console` 向控制台输出日志信息,
 * 并会尝试为不同种类的api分配对应的 `console` api.
 * - [Logger.trace] 使用 `console.log`
 * - [Logger.debug] 使用 `console.log`
 * - [Logger.info] 使用 `console.info`
 * - [Logger.warn] 使用 `console.warn`
 * - [Logger.error] 使用 `console.error`
 *
 * @author ForteScarlet
 */
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
