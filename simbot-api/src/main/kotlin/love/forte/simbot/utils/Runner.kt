/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.InternalSimbotApi
import kotlin.coroutines.CoroutineContext

@OptIn(InternalSimbotApi::class)
@PublishedApi
internal val RunWithInterruptibleDefaultCoroutineContext: CoroutineContext =
    DefaultBlockingContext + CoroutineName("runWithInterruptible")

/**
 * 默认使用 [DefaultBlockingDispatcher] 作为调度器的可中断执行函数。
 *
 * @throws CancellationException May be thrown by [runInterruptible]
 * @see runInterruptible
 */
public suspend inline fun <T> runWithInterruptible(
    context: CoroutineContext = RunWithInterruptibleDefaultCoroutineContext,
    crossinline block: () -> T,
): T {
    return runInterruptible(context) { block() }
}
