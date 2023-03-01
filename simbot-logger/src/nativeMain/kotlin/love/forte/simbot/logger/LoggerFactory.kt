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


private var defaultLoggerLevel0: LogLevel = LogLevel.INFO

/**
 * [LoggerFactory] 所使用的全局默认日志等级。
 */
public var LoggerFactory.defaultLoggerLevel: LogLevel by ::defaultLoggerLevel0

@Suppress("MemberVisibilityCanBePrivate")
public actual object LoggerFactory {
    
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = getLogger(name, defaultLoggerLevel)
    
    /**
     * 根据名称和日志级别获取一个 [Logger] 实例。
     */
    public fun getLogger(name: String, level: LogLevel): Logger = SimpleConsoleLogger(name = name, level = level)
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    return getLogger(T::class.qualifiedName ?: T::class.simpleName ?: T::class.toString())
}
