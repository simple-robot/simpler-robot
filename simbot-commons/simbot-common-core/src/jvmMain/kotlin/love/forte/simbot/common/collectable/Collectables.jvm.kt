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

@file:JvmName("Collectables")
@file:JvmMultifileClass

package love.forte.simbot.common.collectable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import love.forte.simbot.common.async.Async
import love.forte.simbot.common.async.asAsync
import love.forte.simbot.common.collection.asIterator
import love.forte.simbot.common.function.Action
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import reactor.core.publisher.Flux
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.streams.asStream


/**
 * 将一个 [Collectable] 转化为可以提供同步迭代能力的迭代器 [SynchronouslyIterateCollectable]。
 *
 * 如果本身就属于 [SynchronouslyIterateCollectable] 类型则会得到自身，否则会尝试通过阻塞函数转化。
 * 这种情况下得到的 [SynchronouslyIterateCollectable] 是借助 [Flow] 实现的 [FlowSynchronouslyIterateCollectable] 类型。
 * 这其中可能会有一定的性能损耗，因为这其中隐含了将挂起函数转化为阻塞函数的行为。
 *
 * [produceScope] 用于当 [Collectable] 需要通过 [Flow] 以 [FlowSynchronouslyIterateCollectable] 类型实现时，
 * 使用在 [FlowSynchronouslyIterateCollectable.iterator] 中，将 [Flow] 转化为 [Iterator]。
 *
 * 如果最终的类型不是 [FlowSynchronouslyIterateCollectable]，那么 [produceScope] 将不会被使用。
 *
 * 如果不希望进行过多的校验和优化，直接转化为 [FlowSynchronouslyIterateCollectable]，可以考虑通过 [Collectable.asFlow] 得到 [Flow]
 * 后使用 [asFlowSynchronouslyIterateCollectable]。
 *
 * @see FlowSynchronouslyIterateCollectable
 */
public fun <T> Collectable<T>.asSynchronouslyIterateCollectable(produceScope: CoroutineScope): SynchronouslyIterateCollectable<T> {
    if (this is SynchronouslyIterateCollectable<T>) {
        return this
    }

    if (isEmptyCollectable()) {
        return EmptySynchronouslyIterateCollectable
    }

    return FlowSynchronouslyIterateCollectableImpl(produceScope, asFlow())
}

/**
 * Converts the given [Flow] into a synchronously iterable and collectable [FlowSynchronouslyIterateCollectable].
 *
 * @param produceScope The [CoroutineScope] to use for producing values from the [Flow].
 * @see asSynchronouslyIterateCollectable
 */
public fun <T> Flow<T>.asFlowSynchronouslyIterateCollectable(produceScope: CoroutineScope): FlowSynchronouslyIterateCollectable<T> {
    return FlowSynchronouslyIterateCollectableImpl(produceScope, this)
}

/**
 * [FlowSynchronouslyIterateCollectable] 接口，为处理流数据提供了一套方法。
 * 扩展自 [SynchronouslyIterateCollectable] 接口。
 *
 * [FlowSynchronouslyIterateCollectable] 直接引用操作 [Flow] ，
 * 请尽可能避免对 [FlowSynchronouslyIterateCollectable] 的重复操作。
 *
 * 注意：接口实现中，一些与挂起函数相关的异步操作会通过 [runInNoScopeBlocking] 函数转化为同步操作，这可能会造成一定的性能损耗。
 *
 * @param T 泛型参数，表示集合中元素的类型。
 */
public interface FlowSynchronouslyIterateCollectable<T> : SynchronouslyIterateCollectable<T> {
    /**
     * 对每个元素执行指定的操作 ([Action])。
     *
     * 注意：本函数可能会通过 [runInNoScopeBlocking] 函数对相关的异步操作进行同步，可能会引起一定的性能损耗。
     *
     * @param action 一个接收集合成员的操作函数。
     */
    override fun forEach(action: Action<T>)

    /**
     * 对每个元素执行指定的操作 ([Action])。
     *
     * @param collector 一个接收集合成员的操作函数。
     */
    override suspend fun collect(collector: Action<T>)

    /**
     * 异步地执行收集操作。
     *
     * @param scope 用来定义 [CoroutineScope]， 提供上下文环境
     * @param collector 一个接收集合成员的操作函数。
     * @return 返回 [Async] 结果, 用于表达异步操作结果。
     */
    override fun collectAsync(scope: CoroutineScope, collector: Action<T>): Async<Unit> =
        scope.async { collect(collector) }.asAsync()

    /**
     * 将数据转化为 [Flow]流。
     *
     * @return 返回 Flow<T> 对象。
     */
    override fun asFlow(): Flow<T>

    /**
     * 获取集合的迭代器。
     *
     * 注意：本函数会使用构建时提供的 [`productScope`][CoroutineScope] 进行转化操作，
     * 且会使用 [runInNoScopeBlocking] 来进行挂起函数的同步转化。
     *
     * @return 返回 [Iterator] 对象。
     */
    override fun iterator(): Iterator<T>

    /**
     * 获取集合的迭代器。
     *
     * 注意：本函数会使用 [produceScope] 进行转化操作，
     * 且会使用 [runInNoScopeBlocking] 来进行挂起函数的同步转化。
     *
     * @return 返回 [Iterator] 对象。
     */
    public fun iterator(produceScope: CoroutineScope): Iterator<T>
}


private class FlowSynchronouslyIterateCollectableImpl<T>(
    private val produceScope: CoroutineScope,
    private val flow: Flow<T>
) : FlowSynchronouslyIterateCollectable<T> {
    override fun forEach(action: Action<T>) {
        runInNoScopeBlocking {
            flow.collect {
                action.invoke(it)
            }
        }
    }

    override suspend fun collect(collector: Action<T>) {
        flow.collect {
            collector.invoke(it)
        }
    }

    override fun asFlow(): Flow<T> = flow

    override fun iterator(): Iterator<T> = iterator(produceScope)

    override fun iterator(produceScope: CoroutineScope): Iterator<T> {
        return flow.asIterator(
            produceScope,
            hasNext = { runInNoScopeBlocking { hasNext() } },
            next = { runInNoScopeBlocking { next() } })
    }

    override fun toList(): List<T> = runInNoScopeBlocking { flow.toList() }
}

private data object EmptySynchronouslyIterateCollectable : SynchronouslyIterateCollectable<Nothing> {
    override fun forEach(action: Action<Nothing>) {
    }

    override fun asFlow(): Flow<Nothing> = emptyFlow()
    override fun iterator(): Iterator<Nothing> = emptyList<Nothing>().iterator()
    override fun toList(): List<Nothing> = emptyList()
}

/**
 * Converts an [IterableCollectable] to a [Stream].
 *
 * @see Sequence.asStream
 * @return the Stream representation of the [IterableCollectable].
 */
public fun <T> IterableCollectable<T>.asStream(): Stream<T> = asSequence().asStream()

/**
 * Converts an [SequenceCollectable] to a [Stream].
 *
 * @see Sequence.asStream
 * @return the Stream representation of the [SequenceCollectable].
 */
public fun <T> SequenceCollectable<T>.asStream(): Stream<T> = asSequence().asStream()


/**
 * 将 [Stream] 转换为 [Collectable] 的函数.
 */
@JvmName("valueOf")
public fun <T> Stream<T>.asCollectable(): SequenceCollectable<T> = StreamCollectableImpl(this)

private class StreamCollectableImpl<T>(private val stream: Stream<T>) : SequenceCollectable<T> {
    override fun asSequence(): Sequence<T> = stream.asSequence()
    override fun forEach(action: Action<T>): Unit = stream.forEach(action::invoke)
    override fun toList(): List<T> = stream.collect(Collectors.toUnmodifiableList())
}


// reactor

/**
 * 将 [Collectable] 转化为 [Flux]。
 * 需要环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 */
public fun <T : Any> Collectable<T>.asFlux(): Flux<T> =
    asFlow().asFlux()


