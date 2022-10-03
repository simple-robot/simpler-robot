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

package love.forte.simbot.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import love.forte.simbot.InternalSimbotApi
import java.util.concurrent.*
import kotlin.coroutines.CoroutineContext


private const val DEFAULT_KEEP_ALIVE_TIME = 60_000L // 60s


@Suppress("NAME_SHADOWING")
private fun createDefaultDispatcher(
    coreSize: Int?,
    maxSize: Int?,
    keepAliveTime: Long?,
): ExecutorCoroutineDispatcher {
    // cpu / 2 or 1
    val coreSize = (coreSize ?: (Runtime.getRuntime().availableProcessors() / 2)).coerceAtLeast(1)
    val maxSize =
        maxSize?.coerceAtLeast(coreSize) ?: (Runtime.getRuntime().availableProcessors() * 4).coerceAtLeast(coreSize)
    val keepAliveTime = keepAliveTime?.takeIf { it >= 0L } ?: DEFAULT_KEEP_ALIVE_TIME
    
    // val num = AtomicLong(0)
    val group = ThreadGroup("defaultBlockingDispatcherThread")
    val executor = ThreadPoolExecutor(
        coreSize,
        maxSize,
        keepAliveTime,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(),
        { r ->
            Thread(group, r, group.name).also {
                it.isDaemon = true
            }
        }
    ) { runnable, executor ->
        throw DefaultBlockingDispatcherTaskRejectedExecutionException(runnable, executor)
    }
    
    return executor.asCoroutineDispatcher()
}

/**
 * 对 [RejectedExecutionException] 的扩展, 当 [DefaultBlockingDispatcher] 将会在追加任务被拒绝时抛出此异常并携带 [runnable] 和 [executor] 信息供于外部用户捕获并处理.
 */
@Suppress("MemberVisibilityCanBePrivate")
public open class DefaultBlockingDispatcherTaskRejectedExecutionException(
    public val runnable: java.lang.Runnable, public val executor: Executor,
) : RejectedExecutionException("The task $runnable is rejected by default blocking task executor $executor")

private const val DISPATCHER_BASE_PROPERTY = "simbot.runInBlocking.dispatcher"

private const val DISPATCHER_USE_IO_PROPERTY_VALUE = "io"
private const val DISPATCHER_USE_DEFAULT_PROPERTY_VALUE = "default"
private const val DISPATCHER_USE_FORK_JOIN_POOL_PROPERTY_VALUE = "forkJoinPool"

private const val DISPATCHER_CORE_SIZE_PROPERTY = "$DISPATCHER_BASE_PROPERTY.coreSize"
private const val DISPATCHER_MAX_SIZE_PROPERTY = "$DISPATCHER_BASE_PROPERTY.maxSize"
private const val DISPATCHER_KEEP_ALIVE_TIME_PROPERTY = "$DISPATCHER_BASE_PROPERTY.keepAliveTime"


/**
 * 使用在阻塞环境或非Java协程环境中的默认调度器实现。
 * 会在首次被获取的时候进行实例化。
 *
 * 默认情况下，[DefaultBlockingDispatcher] 会构建一个默认的线程池作为调度器：
 * ```kotlin
 * ThreadPoolExecutor(
 *     coreSize,
 *     maxSize,
 *     keepAliveTime,
 *     TimeUnit.MILLISECONDS,
 *     LinkedBlockingQueue(),
 *     { r -> ... } // isDaemon = true
 * ) { runnable, executor ->
 *        throw DefaultBlockingDispatcherTaskRejectedExecutionException(runnable, executor)
 *    }
 * ```
 * 其中存在部分可配置内容：
 *
 * | 属性 | JVM参数 | 默认值 |
 * | --- | :----: | -----: |
 * | [核心线程数][ThreadPoolExecutor.corePoolSize] | `simbot.runInBlocking.dispatcher.coreSize` | [availableProcessors][Runtime.availableProcessors] / 2 |
 * | [最大线程数][ThreadPoolExecutor.maximumPoolSize] | `simbot.runInBlocking.dispatcher.maxSize` | [availableProcessors][Runtime.availableProcessors] * 4 |
 * |[ 维持时间][ThreadPoolExecutor.keepAliveTime]（毫秒） | `simbot.runInBlocking.dispatcher.keepAliveTime` | `60000` |
 *
 * 除了提供调度器的使用，你也可以指定一个从 [Dispatchers] 中存在的属性。使用如下JVM参数可以覆盖调度器的使用：
 * _(参数值不区分大小写)_
 *
 * | JVM参数 | 对应值 | 描述 |
 * | ------ | -----: | ---- |
 * | `simbot.runInBlocking.dispatcher=io` | [Dispatchers.IO] | 使用 [Dispatchers.IO] 作为默认调度器. |
 * | `simbot.runInBlocking.dispatcher=default` | [Dispatchers.Default] | 使用 [Dispatchers.Default] 作为默认调度器. |
 * | `simbot.runInBlocking.dispatcher=forkJoinPool` | [ForkJoinPool] | 使用 [ForkJoinPool] 作为默认调度器. |
 *
 *
 */
public val DefaultBlockingDispatcher: CoroutineDispatcher = initDefaultBlockingDispatcher()


private fun initDefaultBlockingDispatcher(): CoroutineDispatcher {
    infix fun String.eq(other: String): Boolean = equals(other = other, ignoreCase = true)
    
    return runCatching {
        val dispatcher: String? = System.getProperty(DISPATCHER_BASE_PROPERTY)
        if (dispatcher != null) {
            when {
                dispatcher eq DISPATCHER_USE_IO_PROPERTY_VALUE -> return Dispatchers.IO
                dispatcher eq DISPATCHER_USE_DEFAULT_PROPERTY_VALUE -> return Dispatchers.Default
                dispatcher eq DISPATCHER_USE_FORK_JOIN_POOL_PROPERTY_VALUE -> return ForkJoinPool.commonPool().asCoroutineDispatcher()
            }
        }
        
        val coreSize = System.getProperty(DISPATCHER_CORE_SIZE_PROPERTY)?.toIntOrNull()
        val maxSize = System.getProperty(DISPATCHER_MAX_SIZE_PROPERTY)?.toIntOrNull()
        val keepAliveTime = System.getProperty(DISPATCHER_KEEP_ALIVE_TIME_PROPERTY)?.toLongOrNull()
        
        createDefaultDispatcher(coreSize, maxSize, keepAliveTime)
    }.getOrElse {
        createDefaultDispatcher(null, null, null)
    }
}

/**
 * 在 [runInBlocking] 中使用的默认上下文实例。
 *
 * [DefaultBlockingContext] 的内容如下：
 * - 名称为 `"runInBlocking"` 的 [CoroutineName].
 * - 默认调度器 [DefaultBlockingDispatcher].
 *
 */
@InternalSimbotApi
public val DefaultBlockingContext: CoroutineContext = DefaultBlockingDispatcher + CoroutineName("runInBlocking")

/**
 *
 * 在simbot中提供的 [runBlocking] 包装。
 *
 * 在默认未提供上下文的情况下，[runInBlocking] 所使用的 [context] 为 [DefaultBlockingContext].
 *
 * @see DefaultBlockingContext
 * @see runBlocking
 */
@OptIn(InternalSimbotApi::class)
@Throws(InterruptedException::class)
public fun <T> runInBlocking(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T,
): T = runBlocking(context, block)

/**
 * 如果超时，则抛出 [TimeoutException].
 * @see runInBlocking
 * @see withTimeout
 */
@OptIn(InternalSimbotApi::class)
@Throws(InterruptedException::class, TimeoutException::class)
public fun <T> runInTimeoutBlocking(
    timeout: Long,
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T,
): T = runInBlocking(context) {
    try {
        withTimeout(timeout, block)
    } catch (timeout: TimeoutCancellationException) {
        throw TimeoutException(timeout.localizedMessage).initCause(timeout)
    }
}

@InternalSimbotApi
public fun <T> runInAsync(block: suspend () -> T): CompletableFuture<T> =
    `$$DefaultScope`.future(DefaultBlockingContext) { block() }

@Suppress("FunctionName")
@InternalSimbotApi
@Deprecated("Just used by auto-generate", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInBlocking`(block: suspend () -> T): T = runInBlocking { block() }

@Suppress("unused", "ObjectPropertyName")
@InternalSimbotApi
private val `$$DefaultScope` by lazy { CoroutineScope(DefaultBlockingContext) }

@InternalSimbotApi
@Deprecated("Just used by auto-generate", level = DeprecationLevel.HIDDEN)
@Suppress("FunctionName")
public fun <T> `$$runInAsync`(block: suspend () -> T): CompletableFuture<T> {
    return runInAsync(block)
}