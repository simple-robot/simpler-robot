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

package love.forte.simbot.logger

/**
 * 日志工厂。
 *
 */
public expect object LoggerFactory {

    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public fun getLogger(name: String): Logger

}

/**
 * 使用 [LoggerFactory] 通过类型 [T] 构建 [Logger]。
 *
 * 通常这会使得结果 [Logger] 的 [name][Logger.getName]
 * 与目标类型 [T] 有关，比如为 [T] 的全限定名称。
 */
@Suppress("unused")
public expect inline fun <reified T> LoggerFactory.logger(): Logger
