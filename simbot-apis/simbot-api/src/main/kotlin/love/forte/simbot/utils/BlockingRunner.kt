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

@file:JvmName("BlockingRunnerKt")
@file:Suppress("FunctionName")

package love.forte.simbot.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.logger.LoggerFactory
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine


private const val DEFAULT_KEEP_ALIVE_TIME = 60_000L // 60s

private val logger by lazy { LoggerFactory.getLogger("love.forte.simbot.utils.BlockingRunner") }

@Suppress("NAME_SHADOWING")
private fun createDefaultDispatcher(
    coreSize: Int?,
    maxSize: Int?,
    keepAliveTime: Long?,
    threadGroupName: String,
    threadNamePrefix: String,
): ExecutorCoroutineDispatcher? {
    if (coreSize == null && maxSize == null && keepAliveTime == null) {
        return null
    }
    // cpu / 2 or 1
    val availableProcessors = Runtime.getRuntime().availableProcessors()
    val coreSize = (coreSize ?: (availableProcessors / 2)).coerceAtLeast(8)
    val maxSize =
        maxSize?.coerceAtLeast(coreSize) ?: (availableProcessors * 4).coerceAtLeast(coreSize * 2)
    val keepAliveTime = keepAliveTime?.takeIf { it >= 0L } ?: DEFAULT_KEEP_ALIVE_TIME
    
    val num = AtomicLong(0)
    val group = ThreadGroup(threadGroupName)
    val executor = ThreadPoolExecutor(
        coreSize,
        maxSize,
        keepAliveTime,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(),
        { r ->
            Thread(group, r, "$threadNamePrefix-${num.getAndIncrement()}").also {
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

private const val BLOCKING_DISPATCHER_BASE_PROPERTY = "simbot.runInBlocking.dispatcher"
private const val ASYNC_DISPATCHER_BASE_PROPERTY = "simbot.runInAsync.dispatcher"

private const val DISPATCHER_USE_IO_PROPERTY_VALUE = "io"
private const val DISPATCHER_USE_DEFAULT_PROPERTY_VALUE = "default"
private const val DISPATCHER_USE_MAIN_PROPERTY_VALUE = "main"
private const val DISPATCHER_USE_UNCONFINED_PROPERTY_VALUE = "unconfined"
private const val DISPATCHER_USE_FORK_JOIN_POOL_PROPERTY_VALUE = "forkJoinPool"

private const val DISPATCHER_LIMITED_PARALLELISM = "limitedParallelism"

// region blocking properties
private const val BLOCKING_DISPATCHER_LIMITED_PARALLELISM_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.$DISPATCHER_LIMITED_PARALLELISM"
private const val BLOCKING_DISPATCHER_CORE_SIZE_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.coreSize"
private const val BLOCKING_DISPATCHER_MAX_SIZE_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.maxSize"
private const val BLOCKING_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.keepAliveTime"
// endregion

// region async properties
private const val ASYNC_DISPATCHER_LIMITED_PARALLELISM_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.$DISPATCHER_LIMITED_PARALLELISM"
private const val ASYNC_DISPATCHER_CORE_SIZE_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.coreSize"
private const val ASYNC_DISPATCHER_MAX_SIZE_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.maxSize"
private const val ASYNC_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.keepAliveTime"
// endregion


/**
 * 使用在阻塞API（例如 [runInBlocking] ）或非Java协程环境中的默认调度器。
 * 会在首次被获取的时候进行实例化。
 *
 * 默认情况下，[DefaultBlockingDispatcherOrNull] 为 null，即不使用特别的调度器。
 *
 * 存在部分可配置内容：
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
 * | `simbot.runInBlocking.dispatcher=main` | [Dispatchers.Main] | 使用 [Dispatchers.Main] 作为默认调度器. |
 * | `simbot.runInBlocking.dispatcher=unconfined` | [Dispatchers.Unconfined] | 使用 [Dispatchers.Unconfined] 作为默认调度器. |
 * | `simbot.runInBlocking.dispatcher=forkJoinPool` | [ForkJoinPool] | 使用 [ForkJoinPool] 作为默认调度器. |
 *
 * 如果选择了使用某个具体的调度器，那么你可以额外指定属性 `simbot.runInBlocking.dispatcher.limitedParallelism` 来通过 [CoroutineDispatcher.limitedParallelism]
 * 来限制使用的最大并发数。更多说明（和警告）参考 [CoroutineDispatcher.limitedParallelism]。
 *
 *
 */
@InternalSimbotApi
public val DefaultBlockingDispatcherOrNull: CoroutineDispatcher? by lazy {
    initDefaultBlockingDispatcher(
        BLOCKING_DISPATCHER_BASE_PROPERTY,
        BLOCKING_DISPATCHER_LIMITED_PARALLELISM_PROPERTY,
        BLOCKING_DISPATCHER_CORE_SIZE_PROPERTY,
        BLOCKING_DISPATCHER_MAX_SIZE_PROPERTY,
        BLOCKING_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY,
        "defaultBlockingDispatcherThreadGroup",
        "defaultBlocking"
    )
}

/**
 * 如果 [DefaultBlockingDispatcherOrNull] 为 null，得到 [Dispatchers.Default], 否则得到 [DefaultBlockingDispatcherOrNull]的值。
 * @see DefaultBlockingDispatcherOrNull
 */
@InternalSimbotApi
public val DefaultBlockingDispatcher: CoroutineDispatcher
    get() = DefaultBlockingDispatcherOrNull ?: Dispatchers.Default

private infix fun String.eq(other: String): Boolean = equals(other = other, ignoreCase = true)

private inline fun initDefaultBlockingDispatcher(
    dispatcherPropertyName: String,
    dispatcherLimitedParallelismPropertyName: String,
    coreSizePropertyName: String,
    maxSizePropertyName: String,
    keepAliveTimePropertyName: String,
    threadGroupName: String,
    threadNamePrefix: String,
    onDefault: (
        coreSize: Int?, maxSize: Int?, keepAliveTime: Long?,
        threadGroupName: String, threadNamePrefix: String,
        cause: Throwable?,
    ) -> CoroutineDispatcher? =
        { coreSize, maxSize, keepAliveTime, threadGroupName0, threadNamePrefix0, _ ->
            createDefaultDispatcher(coreSize, maxSize, keepAliveTime, threadGroupName0, threadNamePrefix0)
        },
): CoroutineDispatcher? {
    return runCatching {
        val dispatcher: String? = System.getProperty(dispatcherPropertyName)
        if (dispatcher != null) {
            logger.debug("Dispatcher runner: {}={}", dispatcherPropertyName, dispatcher)
    
            var useDispatcher: CoroutineDispatcher? = when {
                dispatcher eq DISPATCHER_USE_IO_PROPERTY_VALUE -> Dispatchers.IO
                dispatcher eq DISPATCHER_USE_DEFAULT_PROPERTY_VALUE -> Dispatchers.Default
                dispatcher eq DISPATCHER_USE_MAIN_PROPERTY_VALUE -> Dispatchers.Main
                dispatcher eq DISPATCHER_USE_UNCONFINED_PROPERTY_VALUE -> Dispatchers.Unconfined
                dispatcher eq DISPATCHER_USE_FORK_JOIN_POOL_PROPERTY_VALUE -> ForkJoinPool.commonPool()
                    .asCoroutineDispatcher()
                
                else -> null
            }
    
            if (useDispatcher != null) {
                val limitedParallelism = System.getProperty(dispatcherLimitedParallelismPropertyName).toIntOrNull()
            
                @OptIn(ExperimentalCoroutinesApi::class)
                if (limitedParallelism != null) {
                    logger.debug("Dispatcher runner limited parallelism: {}={}", dispatcherLimitedParallelismPropertyName, limitedParallelism)
                    useDispatcher = useDispatcher.limitedParallelism(limitedParallelism)
                }
                
                return useDispatcher
            } else {
                logger.debug("Unknown dispatcher runner: {}, ignore.", dispatcher)
                
            }
        }
        
        val coreSize = System.getProperty(coreSizePropertyName)?.toIntOrNull()
        val maxSize = System.getProperty(maxSizePropertyName)?.toIntOrNull()
        val keepAliveTime = System.getProperty(keepAliveTimePropertyName)?.toLongOrNull()
        
        logger.debug("Dispatcher properties: coreSize={}, maxSize={}, keepAliveTime={}", coreSize, maxSize, keepAliveTime)
        onDefault(coreSize, maxSize, keepAliveTime, threadGroupName, threadNamePrefix, null)
    }.getOrElse {
        logger.debug("Dispatcher properties: coreSize=null, maxSize=null, keepAliveTime=null")
        onDefault(null, null, null, threadGroupName, threadNamePrefix, it)
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
public val DefaultBlockingContext: CoroutineContext by lazy {
    CoroutineName("defaultBlocking").let { name ->
        if (DefaultBlockingDispatcherOrNull == null) name else DefaultBlockingDispatcher + name
    }
}

/**
 * 使用在非协程环境下的异步API（例如 [runInAsync] ）中的默认调度器。
 * 会在首次被获取的时候进行实例化。
 *
 * 默认情况下，[DefaultAsyncDispatcherOrNull] 等同于 [DefaultBlockingDispatcherOrNull].
 *
 * 存在部分可配置内容：
 *
 * | 属性 | JVM参数 | 默认值 |
 * | --- | :----: | -----: |
 * | [核心线程数][ThreadPoolExecutor.corePoolSize] | `simbot.runInAsync.dispatcher.coreSize` | [availableProcessors][Runtime.availableProcessors] / 2 |
 * | [最大线程数][ThreadPoolExecutor.maximumPoolSize] | `simbot.runInAsync.dispatcher.maxSize` | [availableProcessors][Runtime.availableProcessors] * 4 |
 * |[ 维持时间][ThreadPoolExecutor.keepAliveTime]（毫秒） | `simbot.runInAsync.dispatcher.keepAliveTime` | `60000` |
 *
 * 除了提供调度器的使用，你也可以指定一个从 [Dispatchers] 中存在的属性。使用如下JVM参数可以覆盖调度器的使用：
 * _(参数值不区分大小写)_
 *
 * | JVM参数 | 对应值 | 描述 |
 * | ------ | -----: | ---- |
 * | `simbot.runInAsync.dispatcher=io` | [Dispatchers.IO] | 使用 [Dispatchers.IO] 作为默认调度器. |
 * | `simbot.runInAsync.dispatcher=default` | [Dispatchers.Default] | 使用 [Dispatchers.Default] 作为默认调度器. |
 * | `simbot.runInAsync.dispatcher=main` | [Dispatchers.Main] | 使用 [Dispatchers.Main] 作为默认调度器. |
 * | `simbot.runInAsync.dispatcher=unconfined` | [Dispatchers.Unconfined] | 使用 [Dispatchers.Unconfined] 作为默认调度器. |
 * | `simbot.runInAsync.dispatcher=forkJoinPool` | [ForkJoinPool] | 使用 [ForkJoinPool] 作为默认调度器. |
 *
 */
@InternalSimbotApi
public val DefaultAsyncDispatcherOrNull: CoroutineDispatcher? by lazy {
    initDefaultBlockingDispatcher(
        ASYNC_DISPATCHER_BASE_PROPERTY,
        ASYNC_DISPATCHER_LIMITED_PARALLELISM_PROPERTY,
        ASYNC_DISPATCHER_CORE_SIZE_PROPERTY,
        ASYNC_DISPATCHER_MAX_SIZE_PROPERTY,
        ASYNC_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY,
        "defaultAsyncDispatcherThreadGroup",
        "defaultAsync"
    ) { coreSize, maxSize, keepAliveTime, threadGroupName, threadNamePrefix, cause ->
        if (cause != null || (coreSize == null && maxSize == null && keepAliveTime == null)) {
            // default.
            DefaultBlockingDispatcherOrNull
        } else {
            createDefaultDispatcher(coreSize, maxSize, keepAliveTime, threadGroupName, threadNamePrefix)
        }
    }
}

/**
 * 如果 [DefaultAsyncDispatcherOrNull] 为 null，得到 [Dispatchers.Default], 否则得到 [DefaultAsyncDispatcherOrNull]的值。
 * @see DefaultAsyncDispatcherOrNull
 */
@InternalSimbotApi
public val DefaultAsyncDispatcher: CoroutineDispatcher
    get() = DefaultAsyncDispatcherOrNull ?: Dispatchers.Default

/**
 * 默认的异步调用（Java异步，例如 [CompletableFuture] 或 [runInAsync]）上下文。
 *
 * 使用的上下文与 [DefaultBlockingContext] 一致。
 *
 */
@InternalSimbotApi
public val DefaultAsyncContext: CoroutineContext by lazy {
    val asyncDispatcher = DefaultAsyncDispatcherOrNull
    if (asyncDispatcher == null) {
        CoroutineName("defaultAsync")
    } else {
        CoroutineName("defaultAsync") + asyncDispatcher
    }
}


@Suppress("unused", "ObjectPropertyName")
@InternalSimbotApi
private val `$$DefaultScope`: CoroutineScope by lazy {
    CoroutineScope(DefaultAsyncContext)
}


// region run in blocking strategy
@ExperimentalSimbotApi
public interface RunInBlockingStrategy {
    @kotlin.jvm.Throws(Exception::class)
    public operator fun <T> invoke(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T
}

@OptIn(ExperimentalSimbotApi::class)
private var runInBlockingStrategy: RunInBlockingStrategy = DefaultRunInBlockingStrategy


@OptIn(ExperimentalSimbotApi::class)
private object DefaultRunInBlockingStrategy : RunInBlockingStrategy {
    override fun <T> invoke(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
        return runBlocking(context, block)
    }
}

/**
 * 设置一个 [runInBlocking] 函数的实际调度逻辑。
 */
@ExperimentalSimbotApi
public fun setRunInBlockingStrategy(strategy: RunInBlockingStrategy) {
    runInBlockingStrategy = strategy
}
// endregion

// region run in no scope blocking strategy
@ExperimentalSimbotApi
public interface RunInNoScopeBlockingStrategy {
    @kotlin.jvm.Throws(Exception::class)
    public operator fun <T> invoke(context: CoroutineContext, block: suspend () -> T): T
}

@OptIn(ExperimentalSimbotApi::class)
private var runInNoScopeBlockingStrategy: RunInNoScopeBlockingStrategy = DefaultRunInNoScopeBlockingStrategy

@OptIn(ExperimentalSimbotApi::class)
private object DefaultRunInNoScopeBlockingStrategy : RunInNoScopeBlockingStrategy {
    override fun <T> invoke(context: CoroutineContext, block: suspend () -> T): T {
        val runner = RunBlocking<T>(context)
        block.startCoroutine(runner)
        return runner.await()
    }
}

/**
 * 设置一个 [runInNoScopeBlocking] 函数的实际调度逻辑。
 */
@OptIn(ExperimentalSimbotApi::class)
public fun setRunInNoScopeBlockingStrategy(strategy: RunInNoScopeBlockingStrategy) {
    runInNoScopeBlockingStrategy = strategy
}

// endregion

/**
 *
 * 在simbot中提供的 [runBlocking] 包装。
 *
 * 在默认未提供上下文的情况下，[runInBlocking] 所使用的 [context] 为 [DefaultBlockingContext].
 *
 * @see DefaultBlockingContext
 * @see runBlocking
 */
@OptIn(ExperimentalSimbotApi::class, InternalSimbotApi::class)
@Throws(InterruptedException::class)
public fun <T> runInBlocking(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T,
): T = runInBlockingStrategy(context, block)

/**
 * 如果超时，则抛出 [TimeoutException].
 * @see runInBlocking
 * @see withTimeout
 */
@OptIn(InternalSimbotApi::class, ExperimentalSimbotApi::class)
@Throws(InterruptedException::class, TimeoutException::class)
public fun <T> runInTimeoutBlocking(
    timeout: Long,
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T,
): T = runInBlockingStrategy(context) {
    try {
        withTimeout(timeout, block)
    } catch (timeout: TimeoutCancellationException) {
        throw TimeoutException(timeout.localizedMessage).initCause(timeout)
    }
}

/**
 *
 * 在simbot中提供的 [runBlocking] 包装。
 *
 * 在默认未提供上下文的情况下，[runInBlocking] 所使用的 [context] 为 [DefaultBlockingContext].
 *
 * @see DefaultBlockingContext
 * @see runBlocking
 */
@OptIn(InternalSimbotApi::class, ExperimentalSimbotApi::class)
@Throws(InterruptedException::class)
public fun <T> runInNoScopeBlocking(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend () -> T,
): T = runInNoScopeBlockingStrategy(context, block)

/**
 * 如果超时，则抛出 [TimeoutException].
 * @see runInBlocking
 * @see withTimeout
 */
@OptIn(InternalSimbotApi::class, ExperimentalSimbotApi::class)
@Throws(InterruptedException::class, TimeoutException::class)
public fun <T> runInNoScopeTimeoutBlocking(
    timeout: Long,
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend () -> T,
): T = runInNoScopeBlockingStrategy(context) {
    try {
        withTimeout(timeout) { block() }
    } catch (timeout: TimeoutCancellationException) {
        throw TimeoutException(timeout.localizedMessage).initCause(timeout)
    }
}

/**
 * 执行一个异步函数，得到 [CompletableFuture].
 */
@InternalSimbotApi
public fun <T> runInAsync(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): CompletableFuture<T> =
    scope.future(context) { block() }

/**
 * 执行一个异步函数，得到 [CompletableFuture].
 */
@InternalSimbotApi
public fun <T> runInAsync(block: suspend CoroutineScope.() -> T): CompletableFuture<T> =
    runInAsync(scope = `$$DefaultScope`, context = EmptyCoroutineContext, block = block)

@InternalSimbotApi
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInBlocking`(block: suspend () -> T): T = runInNoScopeBlocking(block = block)

@InternalSimbotApi
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInAsync`(block: suspend () -> T): CompletableFuture<T> {
    return runInAsync {
        block()
    }
}


@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private class RunBlocking<T>(override val context: CoroutineContext = EmptyCoroutineContext) : Continuation<T> {
    var result: Result<T>? = null
    
    override fun resumeWith(result: Result<T>) {
        synchronized(this) {
            this.result = result
            (this as Object).notifyAll()
        }
    }
    
    @Suppress("BlockingMethodInNonBlockingContext")
    fun await(): T {
        synchronized(this) {
            while (true) {
                when (val result = this.result) {
                    null -> (this as Object).wait()
                    else -> {
                        return result.getOrThrow()
                    }
                }
            }
        }
    }
}