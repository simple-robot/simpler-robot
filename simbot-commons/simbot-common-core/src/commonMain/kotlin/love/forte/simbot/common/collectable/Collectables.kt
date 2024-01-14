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

@file:JvmName("Collectables")
@file:JvmMultifileClass

package love.forte.simbot.common.collectable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import love.forte.simbot.common.async.Async
import love.forte.simbot.common.async.completedAsync
import love.forte.simbot.common.function.Action
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 得到一个没有元素的 [Collectable]。
 */
public fun <T> emptyCollectable(): Collectable<T> = EmptyCollectable

private data object EmptyCollectable : Collectable<Nothing> {
    override suspend fun collect(collector: Action<Nothing>) {
    }

    override fun collectAsync(scope: CoroutineScope, collector: Action<Nothing>): Async<Unit> {
        return completedAsync(Unit)
    }

    override fun asFlow(): Flow<Nothing> = emptyFlow()
}

/**
 * Checks if the collectable is [EmptyCollectable].
 */
internal fun Collectable<*>.isEmptyCollectable(): Boolean = this === EmptyCollectable

/**
 * 将一个 [Flow] 转化为 [Collectable]。
 *
 */
@JvmName("valueOf")
public fun <T> Flow<T>.asCollectable(): Collectable<T> = FlowCollectable(this)

/**
 * 构建一个基于 [Flow] 的 [Collectable]。
 */
public inline fun <T> flowCollectable(crossinline block: suspend FlowCollector<T>.() -> Unit): Flow<T> =
    flow { block() }

private class FlowCollectable<T>(private val flow: Flow<T>) : Collectable<T> {
    override fun asFlow(): Flow<T> = flow
    override suspend fun collect(collector: Action<T>) {
        flow.collect { collector(it) }
    }
}

/**
 * 将 [Iterable] 转换为 [IterableCollectable] 的函数.
 */
@JvmName("valueOf")
public fun <T> Iterable<T>.asCollectable(): IterableCollectable<T> = IterableCollectableImpl(this)


private class IterableCollectableImpl<T>(private val iterable: Iterable<T>) : IterableCollectable<T> {
    override fun asFlow(): Flow<T> = iterable.asFlow()

    override fun forEach(action: Action<T>): Unit = iterable.forEach(action::invoke)

    override fun iterator(): Iterator<T> = iterable.iterator()

    override fun toList(): List<T> = iterable.toList()
}

/**
 * 将 [Sequence] 转换为 [Collectable] 的函数.
 */
@JvmName("valueOf")
public fun <T> Sequence<T>.asCollectable(): SequenceCollectable<T> = SequenceCollectableImpl(this)


private class SequenceCollectableImpl<T>(private val sequence: Sequence<T>) : SequenceCollectable<T> {
    override fun asSequence(): Sequence<T> = sequence
    override fun forEach(action: Action<T>): Unit = sequence.forEach(action::invoke)
    override fun toList(): List<T> = sequence.toList()
}
