/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
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
 *
 */
package love.forte.simbot.ability

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.Api4J
import love.forte.simbot.JavaDuration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.function.Supplier


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
    public fun delay(millis: Long, runnable: Runnable): DelayableCompletableFuture<Void?> =
        delay0(millis, runnable)
    
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
    ): DelayableCompletableFuture<V> =
        delayAndCompute0(timeUnit.toMillis(time), supplier)
    
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
 * 可以链式调用延迟函数的 [CompletableFuture] 函数实现。
 *
 * [DelayableCompletableFuture] 由 [DelayableCoroutineScope]
 * 的相关api得到，实现 [Future] 和 [CompletionStage]，提供与 [CompletableFuture]
 * 基本一致的使用方式（但不直接实现 [CompletableFuture]），且允许通过 [DelayableCompletableFuture.asCompletableFuture]
 * 得到一个行为一致的 [CompletableFuture] 对象。
 *
 *
 */
public interface DelayableCompletableFuture<V> : Future<V>, CompletionStage<V> {
    
    /**
     * 得到用于描述当前 [DelayableCompletableFuture] 的 [CompletableFuture] 对象。
     */
    public fun asCompletableFuture(): CompletableFuture<V>
    
    /**
     * 当前 [DelayableCompletableFuture] 中等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间周期 [duration]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    @Api4J
    public fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<V>
    
    
    /**
     * 当前 [DelayableCompletableFuture] 中等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [timeUnit] 的时间周期 [time]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    @Api4J
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<V>
    
    /**
     * 当前 [DelayableCompletableFuture] 中等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    @Api4J
    public fun delay(time: Long, runnable: Runnable): DelayableCompletableFuture<V>
    
    /**
     * 当前 [DelayableCompletableFuture] 中等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间周期 [duration]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayableCompletableFuture] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    @Api4J
    public fun <T> delayAndCompute(
        duration: JavaDuration,
        function: Function<V, T>,
    ): DelayableCompletableFuture<T>
    
    /**
     * 当前 [DelayableCompletableFuture] 中等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayableCompletableFuture] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    @Api4J
    public fun <T> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<V, T>,
    ): DelayableCompletableFuture<T>
    
    /**
     * 当前 [DelayableCompletableFuture] 中等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayableCompletableFuture] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    @Api4J
    public fun <T> delayAndCompute(time: Long, function: Function<V, T>): DelayableCompletableFuture<T>
    
    
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


private class DelayableCompletableFutureImpl<V> private constructor(
    private val deferred: Deferred<V>,
    private val future: CompletableFuture<V>,
    private val scope: CoroutineScope,
) : DelayableCompletableFuture<V>, Future<V> by future, CompletionStage<V> by future {
    constructor(deferred: Deferred<V>, scope: CoroutineScope) : this(deferred, deferred.asCompletableFuture(), scope)
    
    override fun asCompletableFuture(): CompletableFuture<V> {
        return future
    }
    
    @Api4J
    override fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<V> =
        delay0(duration.toMillis(), runnable)
    
    @Api4J
    override fun delay(time: Long, runnable: Runnable): DelayableCompletableFuture<V> =
        delay0(time, runnable)
    
    @Api4J
    override fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<V> =
        delay0(timeUnit.toMillis(time), runnable)
    
    
    @Api4J
    override fun <T> delayAndCompute(duration: JavaDuration, function: Function<V, T>): DelayableCompletableFuture<T> =
        delayAndCompute0(duration.toMillis(), function)
    
    @Api4J
    override fun <T> delayAndCompute(time: Long, function: Function<V, T>): DelayableCompletableFuture<T> =
        delayAndCompute0(time, function)
    
    @Api4J
    override fun <T> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<V, T>,
    ): DelayableCompletableFuture<T> = delayAndCompute0(timeUnit.toMillis(time), function)
    
    
    private fun delay0(millis: Long, runnable: Runnable) =
        DelayableCompletableFutureImpl(
            scope.async {
                deferred.await().also {
                    delay(millis)
                    runInterruptible { runnable.run() }
                }
            }, scope
        )
    
    private fun <T> delayAndCompute0(
        millis: Long,
        function: Function<V, T>,
    ): DelayableCompletableFuture<T> = DelayableCompletableFutureImpl(
        scope.async {
            deferred.await().let { v ->
                delay(millis)
                runInterruptible { function.apply(v) }
            }
        }, scope
    )
}


