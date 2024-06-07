/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher

/**
 * 获取 `IO` 调度器，始终返回 `null`。
 */
internal actual fun ioDispatcher(): CoroutineDispatcher? = null

/**
 * 获取自定义调度器。不支持或无法构建时返回 `null`。
 */
internal actual fun customDispatcher(
    coreThreads: Int?,
    maxThreads: Int?,
    keepAliveMillis: Long?,
    name: String?
): CoroutineDispatcher? = null

/**
 * 当平台为 Java21+ 的 JVM平台时得到虚拟线程调度器，否则得到 `null`。
 */
internal actual fun virtualDispatcher(): CoroutineDispatcher? = null
