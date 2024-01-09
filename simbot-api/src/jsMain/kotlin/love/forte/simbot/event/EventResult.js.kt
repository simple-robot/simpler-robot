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

package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlin.js.Promise

/**
 * 收集 [StandardEventResult.CollectableReactivelyResult.content] 的结果并返回。
 * 如果结果不可收集或不支持收集，则得到原值。
 *
 * native 平台下支持 Kotlin Coroutines 本身的可挂起类型 [Deferred] 和 [Flow] 和 [Promise]。
 * 可收集类型参考 [StandardEventResult.CollectableReactivelyResult.content] 说明。
 *
 * @see StandardEventResult.CollectableReactivelyResult.content
 * @return The collected result.
 */
public actual suspend fun StandardEventResult.CollectableReactivelyResult.collectCollectableReactively(): Any? {
    return when (val c = content) {
        null -> null
        is Deferred<*> -> c.await()
        is Flow<*> -> c.toList()
        is Promise<*> -> c.await()
        else -> content
    }
}
