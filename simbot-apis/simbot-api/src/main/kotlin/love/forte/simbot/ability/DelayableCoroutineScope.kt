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
 * DelayableFuture<LocalTime> whole = bot
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
 *
 * @author ForteScarlet
 */
public interface DelayableCoroutineScope : CoroutineScope {
    
    /**
     * 延迟 [timeUnit] 的 [time] 时长后执行 [runnable]，得到一个 [DelayableCompletableFuture]。
     * 这个 [DelayableCompletableFuture] 的结果永远为null。
     */
    @Api4J
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<Void?> =
        delay(timeUnit.toMillis(time), runnable)
    
    /**
     * 延迟 [millis] 毫秒后执行 [runnable]，得到一个 [DelayableCompletableFuture]。
     * 这个 [DelayableCompletableFuture] 的结果永远为null。
     *
     * @param millis 毫秒级延迟时长
     */
    @Api4J
    public fun delay(millis: Long, runnable: Runnable): DelayableCompletableFuture<Void?> =
        delay0(TimeUnit.MILLISECONDS.toMillis(millis), runnable)
    
    /**
     * 延时 [duration] 时间后执行回调函数 [runnable]，得到一个 [DelayableCompletableFuture]。
     * 这个 [DelayableCompletableFuture] 的结果永远为null。
     */
    @Api4J
    public fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<Void?> =
        delay(duration.toMillis(), runnable)
    
    
    @Api4J
    public fun <V> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        supplier: Supplier<V>,
    ): DelayableCompletableFuture<V> =
        delayAndCompute0(timeUnit.toMillis(time), supplier)
    
    /**
     * @param time 毫秒时间段
     */
    @Api4J
    public fun <V> delayAndCompute(time: Long, supplier: Supplier<V>): DelayableCompletableFuture<V> =
        delayAndCompute(JavaDuration.ofNanos(TimeUnit.MILLISECONDS.toNanos(time)), supplier)
    
    /**
     * 延迟 [duration] 时间后，执行 [supplier] 并得到 [DelayableCompletableFuture]。
     */
    @Api4J
    public fun <V> delayAndCompute(duration: JavaDuration, supplier: Supplier<V>): DelayableCompletableFuture<V> =
        delayAndCompute0(duration.toMillis(), supplier)
    
    
}


/**
 * 可以链式调用延迟函数的 [Future] 函数实现。
 *
 */
public interface DelayableCompletableFuture<V> : Future<V> {
    // support CompletionStage<V>?
    
    @Api4J
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<V>
    
    @Api4J
    public fun delay(time: Long, runnable: Runnable): DelayableCompletableFuture<V> =
        delay(time, TimeUnit.MILLISECONDS, runnable)
    
    @Api4J
    public fun <T> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<V, T>,
    ): DelayableCompletableFuture<T>
    
    @Api4J
    public fun <T> delayAndCompute(time: Long, function: Function<V, T>): DelayableCompletableFuture<T> =
        delayAndCompute(time, TimeUnit.MILLISECONDS, function)
}


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
) : DelayableCompletableFuture<V>, Future<V> by future {
    constructor(deferred: Deferred<V>, scope: CoroutineScope) : this(deferred, deferred.asCompletableFuture(), scope)
    
    @Api4J
    override fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<V> {
        return DelayableCompletableFutureImpl(
            scope.async {
                deferred.await().also {
                    delay(timeUnit.toMillis(time))
                    runInterruptible { runnable.run() }
                }
            }, scope
        )
    }
    
    @Api4J
    override fun <T> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<V, T>,
    ): DelayableCompletableFuture<T> {
        return DelayableCompletableFutureImpl(
            scope.async {
                deferred.await().let { v ->
                    delay(timeUnit.toMillis(time))
                    runInterruptible { function.apply(v) }
                }
            }, scope
        )
    }
    
}


