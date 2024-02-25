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

@file:JvmName("Collectables")
@file:JvmMultifileClass

package love.forte.simbot.common.collectable

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.asFlux
import love.forte.simbot.common.async.Async
import love.forte.simbot.common.async.asAsync
import love.forte.simbot.common.collection.asIterator
import love.forte.simbot.common.function.Action
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import reactor.core.publisher.Flux
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
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
 * 将一个 [Collectable] 转化为 [Stream]。
 * 如果是 [IterableCollectable] 或 [SequenceCollectable]，
 * 则会使用它们的 `asSequence` 进行转化，
 * 否则会使用 [Collectable] 中合适的方法进行适当的**阻塞转化**。
 *
 * 如果 collectable 不是 [IterableCollectable] 或 [SequenceCollectable]，
 * 则需要提供 [produceScope] 来将异步的收集器转化为 stream。默认使用 [GlobalScope]。
 * 注意：请参考并了解有关 [GlobalScope] 的各种注意事项，避免出现预期外的结果或错误。
 *
 * @param produceScope 如果 collectable 不是 [IterableCollectable] 或 [SequenceCollectable]，
 * 则需要提供一个作用域来将异步的收集器转化为 stream。默认使用 [GlobalScope]。
 * 注意：请参考并了解有关 [GlobalScope] 的各种注意事项，避免出现预期外的结果或错误。
 */
@OptIn(DelicateCoroutinesApi::class)
@JvmOverloads
public fun <T> Collectable<T>.asStream(produceScope: CoroutineScope? = null): Stream<T> {
    return when (this) {
        is IterableCollectable -> asStream()
        is SequenceCollectable -> asStream()
        else -> {
            val scope = produceScope ?: GlobalScope
            val iter = asFlow().asIterator(
                producerScope = scope,
                hasNext = { runInNoScopeBlocking { hasNext() } },
                next = { runInNoScopeBlocking { next() } })

            StreamSupport.stream(
                { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
                Spliterator.ORDERED,
                false
            )
        }
    }
}


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

/// List

/**
 * 将 [Collectable] 阻塞地收集为 [List]。
 * 会根据类型适当地优化与避免阻塞挂起操作。
 *
 * @see runInNoScopeBlocking
 */
public fun <T> Collectable<T>.toList(): List<T> = when (this) {
    is SynchronouslyIterateCollectable -> toList()
    else -> runInNoScopeBlocking { asFlow().toList() }
}

/**
 * 将 [Collectable] 异步地收集为 [List]。
 * 如果 [scope] 为 `null`，则会视情况使用 [GlobalScope]
 * 或使用 [CompletableFuture.supplyAsync]。
 *
 * **注意：如果没有指定 [scope] 且在可能会使用 [GlobalScope] 的情况下，**
 * **你应当了解 [GlobalScope] 的特性与注意事项。**
 *
 * @see GlobalScope
 * @see CompletableFuture.supplyAsync
 */
@OptIn(DelicateCoroutinesApi::class)
@JvmOverloads
public fun <T> Collectable<T>.toListAsync(scope: CoroutineScope? = null): CompletableFuture<List<T>> = when (this) {
    is SynchronouslyIterateCollectable -> scope?.future { toList() }
        ?: CompletableFuture.supplyAsync { toList() }

    else -> (scope ?: GlobalScope).future { asFlow().toList() }
}

/// collector

/**
 * 使用 [Collector] **阻塞地**收集 [Collectable] 中的元素。
 */
public fun <T, R> Collectable<T>.collect(collector: Collector<T, *, R>): R {
    return when (this) {
        is SynchronouslyIterateCollectable -> when (this) {
            is SequenceCollectable -> asSequence().asStream().collect(collector)
            else -> asSequence().asStream().collect(collector)
        }

        else -> {
            runInNoScopeBlocking { asFlow().collectBy(collector) }
        }
    }
}

/**
 * 使用 [Collector] **异步地**收集 [Collectable] 中的元素。
 * 如果 [scope] 为 `null`，则会视情况使用 [GlobalScope]
 * 或使用 [CompletableFuture.supplyAsync]。
 *
 * **注意：如果没有指定 [scope] 且在可能会使用 [GlobalScope] 的情况下，**
 * **你应当了解 [GlobalScope] 的特性与注意事项。**
 *
 * @see GlobalScope
 * @see CompletableFuture.supplyAsync
 */
@OptIn(DelicateCoroutinesApi::class)
@JvmOverloads
public fun <T, R> Collectable<T>.collectAsync(
    scope: CoroutineScope? = null,
    collector: Collector<T, *, R>
): CompletableFuture<R> {
    return when (this) {
        is SynchronouslyIterateCollectable -> when (this) {
            is SequenceCollectable -> scope?.future { asSequence().asStream().collect(collector) }
                ?: CompletableFuture.supplyAsync { asSequence().asStream().collect(collector) }

            else -> scope?.future { asSequence().asStream().collect(collector) }
                ?: CompletableFuture.supplyAsync { asSequence().asStream().collect(collector) }
        }

        else -> (scope ?: GlobalScope).let { s ->
            s.future { asFlow().collectBy(scope = s, collector = collector) }
        }
    }
}

/// transform

/**
 * 使用 [SuspendReserve.Transformer] 对 [Collectable.asFlow] 的结果进行转化，
 * 例如可以使用 `SuspendReserves.flux()` 转化为 [Flux] 或 `SuspendReserves.list()`
 * 转化为 [List]。
 * 注意：部分转化器可能会要求运行时存在一些依赖，请注意参考它们的注释与说明。
 *
 * 建议主要使用 [transform] 转化为其他响应式类型，例如 [Flux]。
 * 对列表等普通的集合类型可以选择其他可能有更多判断与优化的API，
 * 例如 [Collectable.toList]。
 *
 * @see Collectable.transform
 */
@OptIn(DelicateCoroutinesApi::class)
@JvmOverloads
@Deprecated(
    "Just use Collectable.transform", ReplaceWith(
        "transform(scope, EmptyCoroutineContext, transformer)",
        "kotlin.coroutines.EmptyCoroutineContext"
    ), level = DeprecationLevel.ERROR
)
public fun <T, R> Collectable<T>.transform(
    scope: CoroutineScope = GlobalScope,
    transformer: SuspendReserve.Transformer<Flow<T>, R>
): R = transform(scope, EmptyCoroutineContext, transformer)

/// reactor

/**
 * 将 [Collectable] 转化为 [Flux]。
 * 需要环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 */
public fun <T : Any> Collectable<T>.asFlux(): Flux<T> =
    asFlow().asFlux()


/**
 * 使用 [Collector] 收集 [Flow] 中的元素。
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public suspend fun <T, R> Flow<T>.collectBy(
    scope: CoroutineScope,
    launchContext: CoroutineContext = EmptyCoroutineContext,
    collector: Collector<T, *, R>
): R {
    val container = collector.supplier().get()
    val accumulator = collector.accumulator() as java.util.function.BiConsumer<Any?, T>
    val characteristics = collector.characteristics()

    if (Collector.Characteristics.CONCURRENT in characteristics && Collector.Characteristics.UNORDERED in characteristics) {
        // collect in launch
        collect { result ->
            scope.launch(launchContext) { accumulator.accept(container, result) }
        }
    } else {
        collect { result ->
            accumulator.accept(container, result)
        }
    }

    return if (Collector.Characteristics.IDENTITY_FINISH in characteristics) {
        container as R
    } else {
        (collector.finisher() as Function<Any?, R>).apply(container)
    }
}

/**
 * 使用 [Collector] 收集 [Flow] 中的元素。
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public suspend fun <T, R> Flow<T>.collectBy(collector: Collector<T, *, R>): R {
    val container = collector.supplier().get()
    val accumulator = collector.accumulator() as java.util.function.BiConsumer<Any?, T>
    val characteristics = collector.characteristics()

    collect { result ->
        accumulator.accept(container, result)
    }

    return if (Collector.Characteristics.IDENTITY_FINISH in characteristics) {
        container as R
    } else {
        (collector.finisher() as Function<Any?, R>).apply(container)
    }
}
