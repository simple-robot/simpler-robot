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

import kotlin.reflect.KClass
import org.slf4j.LoggerFactory as Slf4jLoggerFactory

/**
 * [Logger] 的构建工厂.
 *
 * 委托给 [Slf4jLoggerFactory].
 *
 * @see Slf4jLoggerFactory
 */
public actual object LoggerFactory {
    /**
     * 根据名称获取一个 [Logger] 实例。
     *
     * @see Slf4jLoggerFactory.getLogger
     */
    @JvmStatic
    public actual fun getLogger(name: String): Logger = Slf4jLoggerFactory.getLogger(name)

    /**
     *
     * @see Slf4jLoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(type: KClass<*>): Logger = getLogger(type.java)

    /**
     *
     * @see Slf4jLoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(type: Class<*>): Logger = Slf4jLoggerFactory.getLogger(type)
}

/**
 * just like `LoggerFactory.getLogger(T::class)`
 */
public actual inline fun <reified T> LoggerFactory.logger(): Logger = getLogger(T::class.java)
