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
import love.forte.simbot.Api4J
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
 * @author ForteScarlet
 */
public interface DelayableCoroutineScope : CoroutineScope {
    
    @Api4J
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableFuture<Void?> =
        delay0(time, timeUnit, runnable)
    
    @Api4J
    public fun delay(time: Long, runnable: Runnable): DelayableFuture<Void?> =
        delay(time, TimeUnit.MILLISECONDS, runnable)
    
    
    @Api4J
    public fun <V> delayAndCompute(time: Long, timeUnit: TimeUnit, supplier: Supplier<V>): DelayableFuture<V> =
        delayAndCompute0(time, timeUnit, supplier)
    
    @Api4J
    public fun <V> delayAndCompute(time: Long, supplier: Supplier<V>): DelayableFuture<V> =
        delayAndCompute(time, TimeUnit.MILLISECONDS, supplier)
    
}


/**
 * 可以链式调用延迟函数的 [Future] 函数实现。
 *
 */
public interface DelayableFuture<V> : Future<V> {
    
    @Api4J
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableFuture<V>
    
    @Api4J
    public fun delay(time: Long, runnable: Runnable): DelayableFuture<V> = delay(time, TimeUnit.MILLISECONDS, runnable)
    
    @Api4J
    public fun <T> delayAndCompute(time: Long, timeUnit: TimeUnit, function: Function<V, T>): DelayableFuture<T>
    
    @Api4J
    public fun <T> delayAndCompute(time: Long, function: Function<V, T>): DelayableFuture<T> =
        delayAndCompute(time, TimeUnit.MILLISECONDS, function)
    
    @Api4J
    public fun <T> map(function: Function<V, T>): DelayableFuture<T>
}


public fun <T> Deferred<T>.asDelayableFuture(scope: CoroutineScope): DelayableFuture<T> =
    DelayableFutureImpl(this, scope)


private fun CoroutineScope.delay0(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableFuture<Void?> {
    return DelayableFutureImpl(
        async {
            delay(timeUnit.toMillis(time))
            runInterruptible { runnable.run() }
            null
        }, this
    )
}

private fun <V> CoroutineScope.delayAndCompute0(
    time: Long,
    timeUnit: TimeUnit,
    supplier: Supplier<V>,
): DelayableFuture<V> {
    return DelayableFutureImpl(
        async {
            delay(timeUnit.toMillis(time))
            runInterruptible { supplier.get() }
        }, this
    )
}


private class DelayableFutureImpl<V>(private val deferred: Deferred<V>, private val scope: CoroutineScope) :
    DelayableFuture<V> {
    private val future = deferred.asCompletableFuture()
    
    @Api4J
    override fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableFuture<V> {
        return DelayableFutureImpl(
            scope.async {
                deferred.await().also {
                    delay(timeUnit.toMillis(time))
                    runInterruptible { runnable.run() }
                }
            }, scope
        )
    }
    
    @Api4J
    override fun <T> delayAndCompute(time: Long, timeUnit: TimeUnit, function: Function<V, T>): DelayableFuture<T> {
        return DelayableFutureImpl(
            scope.async {
                deferred.await().let { v ->
                    delay(timeUnit.toMillis(time))
                    runInterruptible { function.apply(v) }
                }
            }, scope
        )
    }
    
    @Api4J
    override fun <T> map(function: Function<V, T>): DelayableFuture<T> {
        return TransformDelayableFutureImpl(this, function)
    }
    
    override fun isCancelled(): Boolean {
        return future.isCancelled
    }
    
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return future.cancel(mayInterruptIfRunning)
    }
    
    override fun isDone(): Boolean {
        return future.isDone
    }
    
    override fun get(): V {
        return future.get()
    }
    
    override fun get(timeout: Long, unit: TimeUnit): V {
        return future.get(timeout, unit)
    }
}


private class TransformDelayableFutureImpl<From, To>(
    private val future: DelayableFuture<From>,
    private val mapper: Function<From, To>,
) : DelayableFuture<To> {
    
    @Api4J
    override fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableFuture<To> {
        return TransformDelayableFutureImpl(future.delay(time, timeUnit, runnable), mapper)
    }
    
    @Api4J
    override fun <T> delayAndCompute(time: Long, timeUnit: TimeUnit, function: Function<To, T>): DelayableFuture<T> {
        return future.delayAndCompute(time, timeUnit) { v ->
            function.apply(mapper.apply(v))
        }
    }
    
    @Api4J
    override fun <T> map(function: Function<To, T>): DelayableFuture<T> {
        return TransformDelayableFutureImpl(this, function)
    }
    
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return future.cancel(mayInterruptIfRunning)
    }
    
    override fun isCancelled(): Boolean {
        return future.isCancelled
    }
    
    override fun isDone(): Boolean {
        return future.isDone
    }
    
    override fun get(): To {
        val value = future.get()
        return mapper.apply(value)
    }
    
    override fun get(timeout: Long, unit: TimeUnit): To {
        val value = future.get(timeout, unit)
        return mapper.apply(value)
    }
}