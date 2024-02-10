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

package love.forte.simbot.common.collectable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import love.forte.simbot.common.async.Async
import love.forte.simbot.common.async.asAsync
import love.forte.simbot.common.function.Action
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmSynthetic

/**
 * 一个可收集序列器。
 *
 * [Collectable] 应当支持将自身（或其中实际的元素收集方式）
 * 转化为可挂起的流（[Flow]）或同步序列（[Sequence], see [IterableCollectable]）。
 *
 * [Collectable] 不保证可以多次使用，也不建议多次调用 [Collectable] 中的API。
 * 无论是对于收集还是转化，同一个实例都应仅调用其中某API的一个。
 * 如果需要多次使用，请考虑将其转化为可安全重复使用的类型（例如 [List]）后再使用。
 *
 * 当 [Collectable] 中实际没有需要被挂起的类型时，考虑对外提供普通可迭代的收集器类型 [IterableCollectable]。
 *
 * 在 JVM 中，会使用 `Collectables` 静态类提供更多辅助API。
 *
 * @see IterableCollectable
 *
 * @author ForteScarlet
 */
public interface Collectable<out T> {

    /**
     * 挂起并收集其中的元素。
     *
     * [collector] 函数体内不可挂起，如果希望行为函数内部可挂起请使用 [asFlow]
     * 转化为 [Flow] 后进行操作。
     *
     */
    @JvmSynthetic
    public suspend fun collect(collector: Action<T>)

    /**
     * 异步收集其中的元素。
     *
     * [collector] 函数体内不可挂起，如果希望行为函数内部可挂起请使用 [asFlow]
     * 转化为 [Flow] 后进行操作。
     *
     */
    public fun collectAsync(scope: CoroutineScope, collector: Action<T>): Async<Unit> =
        scope.async { collect(collector) }.asAsync()

    /**
     * 将自身中的元素（或收集器）转化为 [Flow]。
     */
    public fun asFlow(): Flow<T>

    /**
     * 使用 [SuspendReserve.Transformer] 对 [flow][asFlow] 的值进行转化处理。
     * 例如在 JVM 中，可以使用 `SuspendReserves.flux()` 转化为 `reactor.core.publisher.Flux`
     * 或使用 `SuspendReserves.list()` 转化为 [List]。
     *
     * ```Kotlin
     * val flux: Flow<Int> = ...
     * val list = flux.transform(transformer = flux())
     * ```
     *
     * **注意：部分转化器可能会要求运行时存在一些依赖，请注意参考它们的注释与说明。**
     *
     * 建议主要使用 [transform] 转化为其他响应式类型，例如 `reactor.core.publisher.Flux`。
     * 对列表等普通的集合类型可以选择其他可能有更多判断与优化的API，
     * 例如 Java 使用者可选择 `Collectables.toList`。
     *
     * @param scope 提供给 [SuspendReserve.Transformer] 的作用域。默认会使用 [GlobalScope]。
     * @param context 提供给 [SuspendReserve.Transformer] 的作用域。默认会使用 [EmptyCoroutineContext]。
     * @param transformer 转化器实现。
     */
    public fun <R> transform(scope: CoroutineScope, context: CoroutineContext, transformer: SuspendReserve.Transformer<Flow<T>, R>): R =
        transformer.invoke(scope, context) { asFlow() }

    /**
     * 使用 [SuspendReserve.Transformer] 对 [flow][asFlow] 的值进行转化处理。
     * 更多说明参考全量参数的重载函数。
     *
     * 默认使用 [GlobalScope] 作为协程作用域、使用 [EmptyCoroutineContext] 作为协程上下文。
     *
     * _Java 友好的接口重载函数_
     *
     * @see transformer
      */
    @OptIn(DelicateCoroutinesApi::class)
    public fun <R> transform(transformer: SuspendReserve.Transformer<Flow<T>, R>): R =
        transform(scope = GlobalScope, context = EmptyCoroutineContext, transformer = transformer)
}

/**
 * [SynchronouslyIterateCollectable] 包含迭代、收集和异步收集元素的相关方法。
 * 它是 [Collectable] 接口的一个具体化，为我们提供了对可同步迭代集合元素进行操作的能力。
 * 类型参数 [T] 定义了集合元素的类型。
 *
 * 注意：此接口下的方法有一些是不支持挂起的，如果你希望使用协程处理集合中的元素，可以使用 [asFlow] 方法将集合转化为Flow对象
 */
public interface SynchronouslyIterateCollectable<out T> : Collectable<T>, Iterable<T> {
    /**
     * 普通地迭代当前收集器中的元素。
     */
    public fun forEach(action: Action<T>)

    /**
     * 收集当前元素。等同于 [forEach]，无实际的挂起行为。
     */
    @JvmSynthetic
    override suspend fun collect(collector: Action<T>): Unit = forEach(collector)

    /**
     * 异步收集其中的元素。
     *
     * [collector] 函数体内不可挂起，如果希望行为函数内部可挂起请使用 [asFlow]
     * 转化为 [Flow] 后进行操作。
     *
     * 相当于异步地执行 [forEach]。
     *
     */
    override fun collectAsync(scope: CoroutineScope, collector: Action<T>): Async<Unit> =
        scope.async { forEach(collector) }.asAsync()

    /**
     * 转化或收集为一个 [List]。
     */
    public fun toList(): List<T>
}


/**
 * 一个可迭代的 [Collectable] 实现。实现 [Iterable]，可作为一个普通迭代器使用。
 *
 * @see SynchronouslyIterateCollectable
 */
public interface IterableCollectable<out T> : SynchronouslyIterateCollectable<T> {
    /**
     * 将自身中的元素（或收集器）转化为 [Iterator]。
     */
    override fun iterator(): Iterator<T>

    /**
     * 转化为 [Flow]。
     *
     * 默认实现会通过 [iterator] 转化为一个冷流。
     * 具体实现中可能会重写此函数来提供更好的方案。
     */
    override fun asFlow(): Flow<T> = iterator().asFlow()
}

/**
 * 一个序列化的 [Collectable] 实现。实现 [Iterable]，可转化为为一个 [Sequence] 使用。
 *
 * @see SynchronouslyIterateCollectable
 */
public interface SequenceCollectable<out T> : SynchronouslyIterateCollectable<T>, Iterable<T> {
    /**
     * 将自身中的元素（或收集器）转化为 [Sequence]。
     */
    public fun asSequence(): Sequence<T>

    /**
     * Returns an iterator over the elements in this collection.
     *
     * @see Sequence.iterator
     */
    override fun iterator(): Iterator<T> = asSequence().iterator()

    /**
     * 转化为 [Flow]。
     *
     * 默认实现会通过 [asSequence] 转化为一个冷流。
     * 具体实现中可能会重写此函数来提供更好的方案。
     */
    override fun asFlow(): Flow<T> = asSequence().asFlow()
}


