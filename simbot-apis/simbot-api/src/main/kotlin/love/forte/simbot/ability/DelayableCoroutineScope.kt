/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */
package love.forte.simbot.ability

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.await
import kotlinx.coroutines.selects.select
import love.forte.simbot.Api4J
import love.forte.simbot.JavaDuration
import java.util.concurrent.*
import java.util.function.*
import java.util.function.Function
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 * 可延迟的 [CoroutineScope]。
 * [DelayableCoroutineScope] 继承并扩展 [CoroutineScope], 对外提供部分服务于 Java 开发者的“延迟”函数。
 *
 * 对外提供的这些延时函数本质上是通过当前作用域所构建的异步函数，并通过 [delay] 实现非阻塞的延时效果。
 * Java 开发者可以通过这些延时函数来更高效的通过异步回调手段类似于 [Thread.sleep] 的效果。
 *
 * 需要注意的是，本质上这些延迟任务都是 **异步** 的，所以需要依靠回调函数进行进一步的逻辑。
 *
 * Java 开发者可以通过链式风格使用这些延时函数：
 * ```java
 * public void foo(Bot bot) { // Bot 间接实现了 DelayableCoroutineScope
 * DelayableCompletableFuture<LocalTime> whole = bot
 *         // (1). 延时5秒，打印当前时间
 *         .delay(Duration.ofSeconds(5), () -> {
 *             System.out.println(LocalTime.now());
 *         })
 *         // (2). 流程(1)结束后，再延时5秒，打印当前时间
 *         .delay(Duration.ofSeconds(5), () -> {
 *             System.out.println(LocalTime.now());
 *         })
 *         // (3). 流程(2)结束后，再延时5秒，返回当前时间
 *         .delayAndCompute(Duration.ofSeconds(5), (v) -> LocalTime.now());
 * }
 * ```
 * 上述示例中，函数内通过 `bot` 总共创建了3个延时函数，他们将在 **异步** 中按照顺序分别延时5秒。
 * 内部异步的延时任务会在创建时立刻执行。示例中的 `foo` 函数会立刻返回，而延时任务不会收到影响。
 *
 * 当然，延时任务的最后会返回 [DelayableCompletableFuture], 它实现 [Future], 你可以通过此 future 获取整体延时任务的最终结果，
 * 或者控制延时任务的流程。
 *
 * 如果流程中某个节点出现了异常，则后续延时任务会受到上游任务的影响而无法抵达。
 *
 * @see DelayableCompletableFuture
 * @author ForteScarlet
 */
public interface DelayableCoroutineScope : CoroutineScope {
    
    /**
     * 延迟时间单位为 [timeUnit] 的 [time] 时长后执行 [runnable]，得到一个 [DelayableCompletableFuture]。
     *
     * 这个 [DelayableCompletableFuture] 的计算结果**永远为null**。
     */
    @Api4J
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<Void?> =
        delay0(timeUnit.toMillis(time), runnable)
    
    /**
     * 延迟 [millis] 毫秒后执行 [runnable]，得到一个 [DelayableCompletableFuture]。
     *
     * 这个 [DelayableCompletableFuture] 的计算结果**永远为null**。
     *
     * @param millis 毫秒级延迟时长
     */
    @Api4J
    public fun delay(millis: Long, runnable: Runnable): DelayableCompletableFuture<Void?> = delay0(millis, runnable)
    
    /**
     * 延时 [duration] 时间后执行回调函数 [runnable]，得到一个 [DelayableCompletableFuture]。
     *
     * 这个 [DelayableCompletableFuture] 的计算结果**永远为null**。
     */
    @Api4J
    public fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<Void?> =
        delay0(duration.toMillis(), runnable)
    
    /**
     * 延迟时间单位为 [timeUnit] 的 [time] 时长后执行 [supplier]，
     * 并将 [supplier] 得到结果通过得到的 [DelayableCompletableFuture]
     * 向下传递。
     */
    @Api4J
    public fun <V> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        supplier: Supplier<V>,
    ): DelayableCompletableFuture<V> = delayAndCompute0(timeUnit.toMillis(time), supplier)
    
    /**
     * 延迟 [millis] 毫秒的时长后执行 [supplier]，
     * 并将 [supplier] 得到结果通过得到的 [DelayableCompletableFuture]
     * 向下传递。
     */
    @Api4J
    public fun <V> delayAndCompute(millis: Long, supplier: Supplier<V>): DelayableCompletableFuture<V> =
        delayAndCompute0(millis, supplier)
    
    /**
     * 延迟 [duration] 时间后，执行 [supplier]，
     * 并将 [supplier] 得到结果通过得到的 [DelayableCompletableFuture]
     * 向下传递。
     */
    @Api4J
    public fun <V> delayAndCompute(duration: JavaDuration, supplier: Supplier<V>): DelayableCompletableFuture<V> =
        delayAndCompute0(duration.toMillis(), supplier)
    
    
}


/**
 * 提供一个 [CoroutineScope], 将一个 [Deferred] 转化为 [DelayableCompletableFuture]。
 */
public fun <T> Deferred<T>.asDelayableFuture(scope: CoroutineScope): DelayableCompletableFuture<T> =
    DelayableCompletableFutureImpl(this, scope)


private fun CoroutineScope.delay0(
    millis: Long,
    runnable: Runnable,
): DelayableCompletableFuture<Void?> {
    return DelayableCompletableFutureImpl(
        async {
            delay(millis)
            runInterruptible { runnable.run() }
            null
        }, this
    )
}

private fun <V> CoroutineScope.delayAndCompute0(
    millis: Long,
    supplier: Supplier<V>,
): DelayableCompletableFuture<V> {
    return DelayableCompletableFutureImpl(
        async {
            delay(millis)
            runInterruptible { supplier.get() }
        }, this
    )
}

private class DelayableCompletableFutureImpl<T> constructor(
    private val deferred: Deferred<T>,
    // private val future000: CompletableFuture<T>,
    private val scope: CoroutineScope,
) : DelayableCompletableFuture<T>, Future<T>, CompletionStage<T> { //  by future
    // constructor(deferred: Deferred<T>, scope: CoroutineScope) : this(deferred, deferred.asCompletableFuture(), scope)
    
    companion object {
        private val useCommonPool = ForkJoinPool.getCommonPoolParallelism() > 1
        
        /**
         * Default executor -- ForkJoinPool.commonPool() unless it cannot
         * support parallelism.
         *
         * @see CompletableFuture
         */
        private val asyncPool = (if (useCommonPool) ForkJoinPool.commonPool() else ThreadPerTaskExecutor())
        
        private val asyncPoolDispatcher = asyncPool.asCoroutineDispatcher()
        
        private val asyncPoolScope =
            CoroutineScope(asyncPoolDispatcher + CoroutineName("DelayableCompletableFutureCommon"))
        
        private class ThreadPerTaskExecutor : Executor {
            override fun execute(r: java.lang.Runnable) {
                Thread(r).apply {
                    isDaemon = true
                    start()
                }
            }
        }
    }
    
    override fun toCompletableFuture(): CompletableFuture<T> = deferred.asCompletableFuture()
    
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = toCompletableFuture().cancel(mayInterruptIfRunning)
    
    override fun isCancelled(): Boolean = deferred.isCancelled
    
    override fun isDone(): Boolean = deferred.isCompleted
    
    override fun get(): T = toCompletableFuture().get()
    
    override fun get(timeout: Long, unit: TimeUnit): T = toCompletableFuture().get(timeout, unit)
    
    // region delay
    override fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<T> =
        delay0(duration.toMillis(), runnable)
    
    override fun delay(time: Long, runnable: Runnable): DelayableCompletableFuture<T> = delay0(time, runnable)
    
    override fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<T> =
        delay0(timeUnit.toMillis(time), runnable)
    
    override fun <V> delayAndCompute(
        duration: JavaDuration,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V> = delayAndCompute0(duration.toMillis(), function)
    
    override fun <V> delayAndCompute(time: Long, function: Function<T, V>): DelayableCompletableFuture<V> =
        delayAndCompute0(time, function)
    
    override fun <V> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V> = delayAndCompute0(timeUnit.toMillis(time), function)
    
    
    private fun delay0(millis: Long, runnable: Runnable) = DelayableCompletableFutureImpl(
        scope.async {
            deferred.await().also {
                delay(millis)
                runInterruptible { runnable.run() }
            }
        }, scope
    )
    
    
    private fun <V> delayAndCompute0(
        millis: Long,
        function: Function<T, V>,
    ) = DelayableCompletableFutureImpl(
        scope.async {
            deferred.await().let {
                delay(millis)
                runInterruptible { function.apply(it) }
            }
        }, scope
    )
    // endregion
    
    private fun <V> CoroutineScope.then(deferred: Deferred<V>): DelayableCompletableFutureImpl<V> {
        return DelayableCompletableFutureImpl(deferred, this)
    }
    
    private fun <V> CoroutineScope.then(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> V,
    ): DelayableCompletableFutureImpl<V> {
        return then(async(context = context, block = block))
    }
    
    private fun <V> then(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> V,
    ): DelayableCompletableFutureImpl<V> {
        return scope.then(context, block)
    }
    
    // region CompletionStage
    
    private suspend inline fun <T, U> Function<in T, out U>.applyInterruptible(t: T): U {
        return runInterruptible { apply(t) }
    }
    
    override fun <U> thenApply(fn: Function<in T, out U>): DelayableCompletableFuture<U> {
        return then {
            fn.applyInterruptible(deferred.await())
        }
    }
    
    override fun <U> thenApplyAsync(fn: Function<in T, out U>): DelayableCompletableFuture<U> {
        return asyncPoolScope.then {
            fn.applyInterruptible(deferred.await())
        }
    }
    
    override fun <U> thenApplyAsync(
        fn: Function<in T, out U>,
        executor: Executor,
    ): DelayableCompletableFuture<U> {
        return asyncPoolScope.then(executor.asCoroutineDispatcher()) {
            fn.applyInterruptible(deferred.await())
        }
    }
    
    private suspend inline fun <T> Consumer<in T>.acceptInterruptible(v: T): Void? {
        runInterruptible { accept(v) }
        return null
    }
    
    
    override fun thenAccept(action: Consumer<in T>): DelayableCompletableFuture<Void?> {
        return then {
            action.acceptInterruptible(deferred.await())
        }
    }
    
    override fun thenAcceptAsync(action: Consumer<in T>): DelayableCompletableFuture<Void?> {
        return asyncPoolScope.then {
            action.acceptInterruptible(deferred.await())
        }
    }
    
    override fun thenAcceptAsync(action: Consumer<in T>, executor: Executor): DelayableCompletableFuture<Void?> {
        return asyncPoolScope.then(executor.asCoroutineDispatcher()) {
            action.acceptInterruptible(deferred.await())
        }
    }
    
    private suspend inline fun java.lang.Runnable.runInterruptible(): Void? {
        runInterruptible { run() }
        return null
    }
    
    override fun thenRun(action: java.lang.Runnable): DelayableCompletableFuture<Void?> {
        return then {
            deferred.await()
            action.runInterruptible()
        }
    }
    
    override fun thenRunAsync(action: java.lang.Runnable): DelayableCompletableFuture<Void?> {
        return asyncPoolScope.then {
            deferred.await()
            action.runInterruptible()
        }
        
    }
    
    override fun thenRunAsync(action: java.lang.Runnable, executor: Executor): DelayableCompletableFuture<Void?> {
        return asyncPoolScope.then(executor.asCoroutineDispatcher()) {
            deferred.await()
            action.runInterruptible()
        }
        
    }
    
    private suspend inline fun <T, U> BiConsumer<in T, in U>.acceptInterruptible(t: T, u: U): Void? {
        runInterruptible { accept(t, u) }
        return null
    }
    
    override fun <U> thenAcceptBoth(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayableCompletableFuture<Void?> {
        return then {
            thenAcceptBoth0(other, action)
        }
    }
    
    
    override fun <U> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayableCompletableFuture<Void?> {
        return asyncPoolScope.then {
            thenAcceptBoth0(other, action)
        }
    }
    
    override fun <U> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
        executor: Executor,
    ): DelayableCompletableFuture<Void?> {
        return asyncPoolScope.then(executor.asCoroutineDispatcher()) {
            thenAcceptBoth0(other, action)
        }
    }
    
    
    private suspend fun <U> thenAcceptBoth0(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): Void? {
        val u = other.await()
        val t = deferred.await()
        return action.acceptInterruptible(t, u)
    }
    
    private suspend inline fun <T, U, V> BiFunction<in T, in U, out V>.applyInterruptible(t: T, u: U): V {
        return runInterruptible { apply(t, u) }
    }
    
    override fun <U, V> thenCombine(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayableCompletableFuture<V> = then {
        thenCombine0(other, fn)
    }
    
    override fun <U, V> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayableCompletableFuture<V> = asyncPoolScope.then {
        thenCombine0(other, fn)
    }
    
    override fun <U, V> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
        executor: Executor,
    ): DelayableCompletableFuture<V> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        thenCombine0(other, fn)
    }
    
    private suspend fun <U, V> thenCombine0(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): V {
        val u = other.await()
        val t = deferred.await()
        return fn.applyInterruptible(t, u)
    }
    
    
    override fun runAfterBoth(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
    ): DelayableCompletableFuture<Void?> = then {
        runAfterBoth0(other, action)
    }
    
    override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
    ): DelayableCompletableFuture<Void?> = asyncPoolScope.then {
        runAfterBoth0(other, action)
    }
    
    override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
        executor: Executor,
    ): DelayableCompletableFuture<Void?> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        runAfterBoth0(other, action)
    }
    
    private suspend fun runAfterBoth0(other: CompletionStage<*>, action: java.lang.Runnable): Void? {
        other.await()
        deferred.await()
        return action.runInterruptible()
    }
    
    override fun <U> applyToEither(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayableCompletableFuture<U> = then {
        applyToEither0(fn, other)
    }
    
    override fun <U> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayableCompletableFuture<U> = asyncPoolScope.then {
        applyToEither0(fn, other)
    }
    
    override fun <U> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
        executor: Executor,
    ): DelayableCompletableFuture<U> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        applyToEither0(fn, other)
    }
    
    private suspend fun <U> applyToEither0(
        fn: Function<in T, U>,
        other: CompletionStage<out T>,
    ) = fn.applyInterruptible(select {
        deferred.onAwait { it }
        other.asDeferred().onAwait { it }
    })
    
    override fun acceptEither(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayableCompletableFuture<Void?> = then {
        acceptEither0(action, other)
    }
    
    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayableCompletableFuture<Void?> = asyncPoolScope.then {
        acceptEither0(action, other)
    }
    
    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
        executor: Executor,
    ): DelayableCompletableFuture<Void?> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        acceptEither0(action, other)
    }
    
    private suspend fun acceptEither0(
        action: Consumer<in T>,
        other: CompletionStage<out T>,
    ) = action.acceptInterruptible(select {
        deferred.onAwait { it }
        other.asDeferred().onAwait { it }
    })
    
    override fun runAfterEither(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
    ): DelayableCompletableFuture<Void?> = then {
        runAfterEither0(other, action)
    }
    
    override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
    ): DelayableCompletableFuture<Void?> = asyncPoolScope.then {
        runAfterEither0(other, action)
    }
    
    override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
        executor: Executor,
    ): DelayableCompletableFuture<Void?> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        runAfterEither0(other, action)
    }
    
    private suspend fun runAfterEither0(
        other: CompletionStage<*>,
        action: java.lang.Runnable,
    ): Void? {
        select<Unit> {
            deferred.onJoin { }
            other.asDeferred().onJoin { }
        }
        return action.runInterruptible()
    }
    
    override fun <U> thenCompose(fn: Function<in T, out CompletionStage<U>>): DelayableCompletableFuture<U> = then {
        thenCompose0(fn)
    }
    
    override fun <U> thenComposeAsync(fn: Function<in T, out CompletionStage<U>>): DelayableCompletableFuture<U> =
        asyncPoolScope.then {
            thenCompose0(fn)
        }
    
    override fun <U> thenComposeAsync(
        fn: Function<in T, out CompletionStage<U>>,
        executor: Executor,
    ): DelayableCompletableFuture<U> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        thenCompose0(fn)
    }
    
    private suspend fun <U> thenCompose0(fn: Function<in T, out CompletionStage<U>>) =
        fn.applyInterruptible(deferred.await()).await()
    
    override fun exceptionally(fn: Function<Throwable, out T>): DelayableCompletableFuture<T> = then {
        kotlin.runCatching { deferred.await() }.getOrElse { e -> fn.applyInterruptible(e) }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun whenComplete(action: BiConsumer<in T?, in Throwable?>): DelayableCompletableFuture<T> {
        deferred.invokeOnCompletion { e ->
            if (e != null) {
                action.accept(null, e)
            } else {
                try {
                    action.accept(deferred.getCompleted(), null)
                } catch (e: Throwable) {
                    action.accept(null, e)
                }
            }
        }
        return this
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun whenCompleteAsync(action: BiConsumer<in T?, in Throwable?>): DelayableCompletableFuture<T> {
        deferred.invokeOnCompletion { e ->
            asyncPoolScope.launch {
                if (e != null) {
                    action.accept(null, e)
                } else {
                    try {
                        action.accept(deferred.getCompleted(), null)
                    } catch (e: Throwable) {
                        action.accept(null, e)
                    }
                }
            }
        }
        return this
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun whenCompleteAsync(
        action: BiConsumer<in T?, in Throwable?>,
        executor: Executor,
    ): DelayableCompletableFuture<T> {
        deferred.invokeOnCompletion { e ->
            asyncPoolScope.launch(executor.asCoroutineDispatcher()) {
                if (e != null) {
                    action.accept(null, e)
                } else {
                    try {
                        action.accept(deferred.getCompleted(), null)
                    } catch (e: Throwable) {
                        action.accept(null, e)
                    }
                }
            }
        }
        return this
    }
    
    override fun <U> handle(fn: BiFunction<in T?, Throwable?, out U>): DelayableCompletableFuture<U> = then {
        handle0(fn)
    }
    
    override fun <U> handleAsync(fn: BiFunction<in T?, Throwable?, out U>): DelayableCompletableFuture<U> =
        asyncPoolScope.then {
            handle0(fn)
        }
    
    override fun <U> handleAsync(
        fn: BiFunction<in T?, Throwable?, out U>,
        executor: Executor,
    ): DelayableCompletableFuture<U> = asyncPoolScope.then(executor.asCoroutineDispatcher()) {
        handle0(fn)
    }
    
    private suspend fun <U> handle0(fn: BiFunction<in T?, Throwable?, out U>): U {
        var t: T? = null
        var e: Throwable? = null
        
        try {
            t = deferred.await()
        } catch (e0: Throwable) {
            e = e0
        }
        
        return fn.applyInterruptible(t, e)
    }
    // endregion
}

