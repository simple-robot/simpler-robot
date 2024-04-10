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

public actual object LoggerFactory {

    /**
     * 配置当前全局使用的默认日志等级。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public var defaultLevel: LogLevel = LogLevel.INFO

    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = getLogger(name, defaultLevel)

    /**
     * 根据名称和日志级别获取一个 [Logger] 实例。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun getLogger(name: String, level: LogLevel): Logger = SimpleConsoleLogger(name = name, level = level)
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    return getLogger(T::class.simpleName ?: T::class.toString())
}
