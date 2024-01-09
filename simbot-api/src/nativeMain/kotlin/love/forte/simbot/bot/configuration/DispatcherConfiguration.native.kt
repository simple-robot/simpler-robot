/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.newFixedThreadPoolContext

/**
 * 获取 [Dispatchers.IO] 调度器。
 */
internal actual fun ioDispatcher(): CoroutineDispatcher? = Dispatchers.IO

/**
 * 获取自定义调度器。
 *
 * native 平台下 [maxThreads] 和 [keepAliveMillis] 无效
 *
 */
internal actual fun customDispatcher(
    coreThreads: Int?,
    maxThreads: Int?,
    keepAliveMillis: Long?,
    name: String?,
): CoroutineDispatcher? {
    val core = coreThreads ?: return null
    require(core <= 1) { "'coreThreads' must >= 1, but $core" }

    return newFixedThreadPoolContext(core, name ?: "Custom-DP.FT.$core")
}

/**
 * 得到 `null`。
 */
internal actual fun virtualDispatcher(): CoroutineDispatcher? = null
