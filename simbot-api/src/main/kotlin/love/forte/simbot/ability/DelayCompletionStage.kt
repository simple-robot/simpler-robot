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

package love.forte.simbot.ability

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.JavaDuration
import java.util.concurrent.*
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

/**
 * 存在 **延时函数** 的 [CompletionStage] 扩展, 为 [DelayableCompletableFuture] 提供类型支持.
 *
 * [DelayCompletionStage] 由 [DelayableCoroutineScope]
 * 的相关api以 [DelayableCompletableFuture] 类型的形式得到，实现 [Future] 和 [CompletionStage]
 *
 * @see DelayableCoroutineScope
 * @see DelayableCompletableFuture
 *
 */
public interface DelayCompletionStage<T> : CompletionStage<T> {
    /**
     * 得到 [CompletableFuture] 对象。
     */
    override fun toCompletableFuture(): DelayableCompletableFuture<T>
    
    // region delay stage
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间周期 [duration]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    public fun delay(duration: JavaDuration, runnable: Runnable): DelayCompletionStage<T>
    
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [timeUnit] 的时间周期 [time]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayCompletionStage<T>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    public fun delay(time: Long, runnable: Runnable): DelayCompletionStage<T>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间周期 [duration]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayCompletionStage] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    public fun <V> delayAndCompute(
        duration: JavaDuration,
        function: Function<T, V>,
    ): DelayCompletionStage<V>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayCompletionStage] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    public fun <V> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<T, V>,
    ): DelayCompletionStage<V>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayCompletionStage] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    public fun <V> delayAndCompute(
        time: Long,
        function: Function<T, V>,
    ): DelayCompletionStage<V>
    // endregion
    
    // region CompletionStage
    override fun <U : Any?> thenApply(fn: Function<in T, out U>): DelayCompletionStage<U>
    
    override fun <U : Any?> thenApplyAsync(fn: Function<in T, out U>): DelayCompletionStage<U>
    
    override fun <U : Any?> thenApplyAsync(
        fn: Function<in T, out U>,
        executor: Executor,
    ): DelayCompletionStage<U>
    
    override fun thenAccept(action: Consumer<in T>): DelayCompletionStage<Void?>
    
    override fun thenAcceptAsync(action: Consumer<in T>): DelayCompletionStage<Void?>
    
    override fun thenAcceptAsync(action: Consumer<in T>, executor: Executor): DelayCompletionStage<Void?>
    
    override fun thenRun(action: Runnable): DelayCompletionStage<Void?>
    
    override fun thenRunAsync(action: Runnable): DelayCompletionStage<Void?>
    
    override fun thenRunAsync(action: Runnable, executor: Executor): DelayCompletionStage<Void?>
    
    
    override fun <U : Any?> thenAcceptBoth(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayCompletionStage<Void?>
    
    override fun <U : Any?> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayCompletionStage<Void?>
    
    override fun <U : Any?> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
        executor: Executor,
    ): DelayCompletionStage<Void?>
    
    override fun <U : Any?, V : Any?> thenCombine(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayCompletionStage<V>
    
    override fun <U : Any?, V : Any?> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayCompletionStage<V>
    
    override fun <U : Any?, V : Any?> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
        executor: Executor,
    ): DelayCompletionStage<V>
    
    override fun runAfterBoth(other: CompletionStage<*>, action: Runnable): DelayCompletionStage<Void?>
    
    override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: Runnable,
    ): DelayCompletionStage<Void?>
    
    override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor,
    ): DelayCompletionStage<Void?>
    
    override fun <U : Any?> applyToEither(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayCompletionStage<U>
    
    override fun <U : Any?> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayCompletionStage<U>
    
    override fun <U : Any?> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
        executor: Executor,
    ): DelayCompletionStage<U>
    
    override fun acceptEither(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayCompletionStage<Void?>
    
    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayCompletionStage<Void?>
    
    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
        executor: Executor,
    ): DelayCompletionStage<Void?>
    
    override fun runAfterEither(other: CompletionStage<*>, action: Runnable): DelayCompletionStage<Void?>
    
    override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: Runnable,
    ): DelayCompletionStage<Void?>
    
    override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor,
    ): DelayCompletionStage<Void?>
    
    override fun <U : Any?> thenCompose(fn: Function<in T, out CompletionStage<U>>): DelayCompletionStage<U>
    
    override fun <U : Any?> thenComposeAsync(fn: Function<in T, out CompletionStage<U>>): DelayCompletionStage<U>
    
    override fun <U : Any?> thenComposeAsync(
        fn: Function<in T, out CompletionStage<U>>,
        executor: Executor,
    ): DelayCompletionStage<U>
    
    override fun exceptionally(fn: Function<Throwable, out T>): DelayCompletionStage<T>
    
    override fun whenComplete(action: BiConsumer<in T?, in Throwable?>): DelayCompletionStage<T>
    
    override fun whenCompleteAsync(action: BiConsumer<in T?, in Throwable?>): DelayCompletionStage<T>
    
    override fun whenCompleteAsync(
        action: BiConsumer<in T?, in Throwable?>,
        executor: Executor,
    ): DelayCompletionStage<T>
    
    override fun <U : Any?> handle(fn: BiFunction<in T?, Throwable?, out U>): DelayCompletionStage<U>
    
    override fun <U : Any?> handleAsync(fn: BiFunction<in T?, Throwable?, out U>): DelayCompletionStage<U>
    
    override fun <U : Any?> handleAsync(
        fn: BiFunction<in T?, Throwable?, out U>,
        executor: Executor,
    ): DelayCompletionStage<U>
    // endregion
    
    
}

/**
 * 继承 [CompletableFuture] 并实现 [DelayCompletionStage], 提供 [CompletableFuture] 与 [DelayCompletionStage] 的能力.
 *
 * @see DelayCompletionStage
 * @see CompletableFuture
 *
 * @author ForteScarlet
 */
public abstract class DelayableCompletableFuture<T> : DelayCompletionStage<T>, CompletableFuture<T>() {
    abstract override fun toCompletableFuture(): DelayableCompletableFuture<T>
    
    abstract override fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<T>
    
    abstract override fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<T>
    
    abstract override fun delay(time: Long, runnable: Runnable): DelayableCompletableFuture<T>
    
    abstract override fun <V> delayAndCompute(
        duration: JavaDuration,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V>
    
    abstract override fun <V> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V>
    
    abstract override fun <V> delayAndCompute(time: Long, function: Function<T, V>): DelayableCompletableFuture<V>
    
    abstract override fun <U> thenApply(fn: Function<in T, out U>): DelayableCompletableFuture<U>
    
    abstract override fun <U> thenApplyAsync(fn: Function<in T, out U>): DelayableCompletableFuture<U>
    
    abstract override fun <U> thenApplyAsync(
        fn: Function<in T, out U>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    
    abstract override fun thenAccept(action: Consumer<in T>): DelayableCompletableFuture<Void?>
    
    abstract override fun thenAcceptAsync(action: Consumer<in T>): DelayableCompletableFuture<Void?>
    
    abstract override fun thenAcceptAsync(action: Consumer<in T>, executor: Executor): DelayableCompletableFuture<Void?>
    
    abstract override fun thenRun(action: Runnable): DelayableCompletableFuture<Void?>
    
    abstract override fun thenRunAsync(action: Runnable): DelayableCompletableFuture<Void?>
    
    abstract override fun thenRunAsync(action: Runnable, executor: Executor): DelayableCompletableFuture<Void?>
    
    abstract override fun <U> thenAcceptBoth(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun <U> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun <U> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun <U, V> thenCombine(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayableCompletableFuture<V>
    
    abstract override fun <U, V> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayableCompletableFuture<V>
    
    abstract override fun <U, V> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
        executor: Executor,
    ): DelayableCompletableFuture<V>
    
    abstract override fun runAfterBoth(other: CompletionStage<*>, action: Runnable): DelayableCompletableFuture<Void?>
    
    abstract override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: Runnable,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun <U> applyToEither(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayableCompletableFuture<U>
    
    abstract override fun <U> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayableCompletableFuture<U>
    
    abstract override fun <U> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    
    abstract override fun acceptEither(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun runAfterEither(other: CompletionStage<*>, action: Runnable): DelayableCompletableFuture<Void?>
    
    abstract override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: Runnable,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    abstract override fun <U> thenCompose(fn: Function<in T, out CompletionStage<U>>): DelayableCompletableFuture<U>
    
    abstract override fun <U> thenComposeAsync(fn: Function<in T, out CompletionStage<U>>): DelayableCompletableFuture<U>
    
    abstract override fun <U> thenComposeAsync(
        fn: Function<in T, out CompletionStage<U>>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    
    abstract override fun exceptionally(fn: Function<Throwable, out T>): DelayableCompletableFuture<T>
    
    abstract override fun whenComplete(action: BiConsumer<in T?, in Throwable?>): DelayableCompletableFuture<T>
    
    abstract override fun whenCompleteAsync(action: BiConsumer<in T?, in Throwable?>): DelayableCompletableFuture<T>
    
    abstract override fun whenCompleteAsync(
        action: BiConsumer<in T?, in Throwable?>,
        executor: Executor,
    ): DelayableCompletableFuture<T>
    
    abstract override fun <U> handle(fn: BiFunction<in T?, Throwable?, out U>): DelayableCompletableFuture<U>
    
    abstract override fun <U> handleAsync(fn: BiFunction<in T?, Throwable?, out U>): DelayableCompletableFuture<U>
    
    abstract override fun <U> handleAsync(
        fn: BiFunction<in T?, Throwable?, out U>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
}
