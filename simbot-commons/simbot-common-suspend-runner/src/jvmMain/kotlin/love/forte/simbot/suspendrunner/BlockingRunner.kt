/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

@file:JvmName("BlockingRunnerKt")
@file:Suppress("FunctionName")
@file:OptIn(DelicateCoroutinesApi::class)

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * 使用 `Executors.newVirtualThreadPerTaskExecutor` 构建的全局虚拟线程“线程池”。
 * 如果不支持虚拟线程则会抛出 [UnsupportedOperationException]。
 *
 * @since 3.3.0
 * @see DefaultBlockingDispatcherOrNull
 * @throws UnsupportedOperationException 不支持虚拟线程时
 */
@ExperimentalSimbotAPI
public val VirtualThreadDispatcher: CoroutineDispatcher by lazy {
    runCatching {
        val handle = MethodHandles.publicLookup().findStatic(
            Executors::class.java,
            "newVirtualThreadPerTaskExecutor",
            MethodType.methodType(ExecutorService::class.java)
        )
        (handle.invoke() as Executor).asCoroutineDispatcher()
    }.getOrElse { e ->
        throw UnsupportedOperationException(
            "Virtual thread dispatcher is not support. " +
                "Mark sure you are using JDK 21+ now or try to provide the dispatcher via " +
                "'love.forte.simbot.utils.CustomBlockingDispatcherProvider'",
            e
        )
    }
}

private const val DEFAULT_KEEP_ALIVE_TIME = 60_000L // 60s

private const val LOGGER_NAME = "love.forte.simbot.utils.BlockingRunner"

private val logger by lazy { LoggerFactory.getLogger(LOGGER_NAME) }

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
    // cpu / 2 or 8
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
public class DefaultBlockingDispatcherTaskRejectedExecutionException(
    public val runnable: java.lang.Runnable, public val executor: Executor,
) : RejectedExecutionException("The task $runnable is rejected by default blocking task executor $executor")

private const val BLOCKING_DISPATCHER_BASE_PROPERTY = "simbot.runInBlocking.dispatcher"
private const val ASYNC_DISPATCHER_BASE_PROPERTY = "simbot.runInAsync.dispatcher"

private const val DISPATCHER_USE_IO_PROPERTY_VALUE = "io"
private const val DISPATCHER_USE_DEFAULT_PROPERTY_VALUE = "default"
private const val DISPATCHER_USE_MAIN_PROPERTY_VALUE = "main"
private const val DISPATCHER_USE_UNCONFINED_PROPERTY_VALUE = "unconfined"
private const val DISPATCHER_USE_FORK_JOIN_POOL_PROPERTY_VALUE = "forkJoinPool"
private const val DISPATCHER_USE_VIRTUAL_PROPERTY_VALUE = "virtual"
private const val DISPATCHER_USE_VIRTUAL_OR_IO_PROPERTY_VALUE = "virtualOrIo"
private const val DISPATCHER_USE_CUSTOM_PROPERTY_VALUE = "custom"

private const val DISPATCHER_LIMITED_PARALLELISM = "limitedParallelism"

// region blocking properties
private const val BLOCKING_DISPATCHER_LIMITED_PARALLELISM_PROPERTY =
    "$BLOCKING_DISPATCHER_BASE_PROPERTY.$DISPATCHER_LIMITED_PARALLELISM"
private const val BLOCKING_DISPATCHER_CORE_SIZE_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.coreSize"
private const val BLOCKING_DISPATCHER_MAX_SIZE_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.maxSize"
private const val BLOCKING_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY = "$BLOCKING_DISPATCHER_BASE_PROPERTY.keepAliveTime"
// endregion

// region async properties
private const val ASYNC_DISPATCHER_LIMITED_PARALLELISM_PROPERTY =
    "$ASYNC_DISPATCHER_BASE_PROPERTY.$DISPATCHER_LIMITED_PARALLELISM"
private const val ASYNC_DISPATCHER_CORE_SIZE_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.coreSize"
private const val ASYNC_DISPATCHER_MAX_SIZE_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.maxSize"
private const val ASYNC_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY = "$ASYNC_DISPATCHER_BASE_PROPERTY.keepAliveTime"
// endregion


/**
 * 提供一个用于阻塞调用的调度器的供应商。
 *
 * 当JVM参数 `simbot.runInBlocking.dispatcher` 的值为 `custom` 时：
 * ```
 * -Dsimbot.runInBlocking.dispatcher=custom
 * ```
 * 会通过 SPI 加载 [CustomBlockingDispatcherProvider] 来作为阻塞调用时的默认调度器。
 *
 * 如果 SPI 检测到环境种存在多个 `CustomBlockingExecutorProvider`，会输出警告并就近选择其中一个。
 *
 * 对于不是非常容易得到 [CoroutineDispatcher] 类型的情况（例如使用Java）或希望单纯提供一个 [Executor] 时，
 * 可以选择使用 [CustomBlockingExecutorProvider] 类型。
 *
 * @see CustomBlockingDispatcherProvider
 * @since 3.3.0
 */
public abstract class CustomBlockingDispatcherProvider {
    /**
     * 得到用于阻塞调用的调度器。
     */
    public abstract fun blockingDispatcher(): CoroutineDispatcher
}

/**
 * 提供一个用于阻塞调用的调度器的供应商。
 *
 * @see CustomBlockingDispatcherProvider
 * @since 3.3.0
 */
public abstract class CustomBlockingExecutorProvider : CustomBlockingDispatcherProvider() {
    final override fun blockingDispatcher(): CoroutineDispatcher = blockingExecutor().asCoroutineDispatcher()

    /**
     * 得到用于阻塞调用的 [Executor]。
     * 会通过 [asCoroutineDispatcher] 转化为 [CoroutineDispatcher]。
     */
    public abstract fun blockingExecutor(): Executor
}


private class CustomBlockingDispatcherProviderNotFoundException(
    classLoader: ClassLoader?
) : RuntimeException(
    "System property 'simbot.runInBlocking.dispatcher' is 'custom', " +
        "but there is no provider loaded via classLoader $classLoader"
)


private fun loadCustomBlockingDispatcher(loader: ClassLoader?): CoroutineDispatcher {
    val serviceLoader = ServiceLoader.load(CustomBlockingDispatcherProvider::class.java, loader)
    val services = serviceLoader.toList()

    if (services.isEmpty()) {
        throw CustomBlockingDispatcherProviderNotFoundException(loader)
    }

    val first = services.first()
    val dis = first.blockingDispatcher()

    if (services.size > 1) {
        // log
        logger.warn(
            "System property 'simbot.runInBlocking.dispatcher' is 'custom', " +
                "and the size of providers are more than 1: {}",
            services.size
        )
        for ((index, provider) in services.withIndex()) {
            logger.warn("index: {}, provider: {}", index, provider)
        }

        logger.warn("Will choose the first (index=0) dispatcher {} of provider {}", dis, first)
    }

    return dis
}


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
 * | `simbot.runInBlocking.dispatcher=virtual` | [VirtualThreadDispatcher] | (since 3.3.0) 使用 [VirtualThreadDispatcher] 作为默认调度器. |
 * | `simbot.runInBlocking.dispatcher=virtualOrIo` | [VirtualThreadDispatcher] or [Dispatchers.IO] if virtual thread not support | (since 3.3.0) 使用 [VirtualThreadDispatcher] 作为默认调度器，如果虚拟线程不支持，则退化为 [Dispatchers.IO] |
 * | `simbot.runInBlocking.dispatcher=custom` | dispatcher from [CustomBlockingDispatcherProvider] | (since 3.3.0) 通过 SPI 加载 [CustomBlockingDispatcherProvider] 并通过其构建 [CoroutineDispatcher] |
 *
 * 如果选择了使用某个具体的调度器，那么你可以额外指定属性 `simbot.runInBlocking.dispatcher.limitedParallelism` 来通过 [CoroutineDispatcher.limitedParallelism]
 * 来限制使用的最大并发数。更多说明（和警告）参考 [CoroutineDispatcher.limitedParallelism]。
 *
 *
 */
@InternalSimbotAPI
public val DefaultBlockingDispatcherOrNull: CoroutineDispatcher? by lazy {
    initDispatcher(
        BLOCKING_DISPATCHER_BASE_PROPERTY,
        BLOCKING_DISPATCHER_LIMITED_PARALLELISM_PROPERTY,
        BLOCKING_DISPATCHER_CORE_SIZE_PROPERTY,
        BLOCKING_DISPATCHER_MAX_SIZE_PROPERTY,
        BLOCKING_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY,
        "defaultBlockingDispatcherThreadGroup",
        "defaultBlocking"
    ).also {
        if (it == null) {
            logger.debug("Initialized default blocking dispatcher is null")
        } else {
            logger.debug("Initialized default blocking dispatcher: {}", it)
        }
    }
}

/**
 * 如果 [DefaultBlockingDispatcherOrNull] 为 null，
 * 得到 [Dispatchers.IO], 否则使用 [DefaultBlockingDispatcherOrNull] 的值。
 * @see DefaultBlockingDispatcherOrNull
 */
@InternalSimbotAPI
public val DefaultBlockingDispatcher: CoroutineDispatcher
    get() = DefaultBlockingDispatcherOrNull ?: Dispatchers.IO

private infix fun String.eq(other: String): Boolean = equals(other = other, ignoreCase = true)

@OptIn(ExperimentalSimbotAPI::class)
private inline fun initDispatcher(
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

                dispatcher eq DISPATCHER_USE_VIRTUAL_PROPERTY_VALUE -> VirtualThreadDispatcher
                dispatcher eq DISPATCHER_USE_VIRTUAL_OR_IO_PROPERTY_VALUE -> runCatching {
                    VirtualThreadDispatcher
                }.getOrElse { e ->
                    logger.warn("Virtual thread dispatcher is not supported.", e)
                    Dispatchers.IO
                }

                dispatcher eq DISPATCHER_USE_CUSTOM_PROPERTY_VALUE -> loadCustomBlockingDispatcher(
                    Thread.currentThread().contextClassLoader
                )

                else -> null
            }

            if (useDispatcher != null) {
                val limitedParallelism = systemInt(dispatcherLimitedParallelismPropertyName)

                @OptIn(ExperimentalCoroutinesApi::class)
                if (limitedParallelism != null) {
                    logger.debug(
                        "Dispatcher runner limited parallelism: {}={}",
                        dispatcherLimitedParallelismPropertyName,
                        limitedParallelism
                    )
                    useDispatcher = useDispatcher.limitedParallelism(limitedParallelism)
                }

                return useDispatcher
            } else {
                logger.debug("Unknown dispatcher runner: {}, ignore.", dispatcher)

            }
        }

        val coreSize = systemInt(coreSizePropertyName)
        val maxSize = systemInt(maxSizePropertyName)
        val keepAliveTime = systemLong(keepAliveTimePropertyName)

        logger.debug(
            "Dispatcher properties: coreSize={}, maxSize={}, keepAliveTime={}",
            coreSize,
            maxSize,
            keepAliveTime
        )
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
 * - 名称为 `"def-block"` 的 [CoroutineName].
 * - 默认调度器 [DefaultBlockingDispatcher].
 *
 * @see DefaultBlockingDispatcher
 */
@InternalSimbotAPI
public val DefaultBlockingContext: CoroutineContext by lazy {
    DefaultBlockingDispatcher + CoroutineName("def-block")
    // CoroutineName("def-block").let { name ->
    //     if (DefaultBlockingDispatcherOrNull == null) name else DefaultBlockingDispatcher + name
    // }
}

/**
 * 使用在非协程环境下的异步API（例如 [runInAsync] ）中的默认调度器。
 * 会在首次被获取的时候进行实例化。
 *
 * 默认情况下，[DefaultAsyncDispatcherOrNull] 等于 `null`.
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
 * | `simbot.runInAsync.dispatcher=virtual` | [VirtualThreadDispatcher] | 使用 [VirtualThreadDispatcher] 作为默认调度器. |
 * | `simbot.runInAsync.dispatcher=virtualOrIo` | [VirtualThreadDispatcher] or [Dispatchers.IO] if virtual thread not supported. | 使用 [VirtualThreadDispatcher] 作为默认调度器，并在虚拟线程不支持的情况下退化为 [Dispatchers.IO] |
 * | `simbot.runInAsync.dispatcher=forkJoinPool` | [ForkJoinPool] | 使用 [ForkJoinPool] 作为默认调度器. |
 * | `simbot.runInAsync.dispatcher=custom` | [CustomBlockingDispatcherProvider] | (since 3.3.0) 通过 SPI 加载 [CustomBlockingDispatcherProvider] 并通过其构建 [CoroutineDispatcher] |
 */
@InternalSimbotAPI
public val DefaultAsyncDispatcherOrNull: CoroutineDispatcher? by lazy {
    initDispatcher(
        ASYNC_DISPATCHER_BASE_PROPERTY,
        ASYNC_DISPATCHER_LIMITED_PARALLELISM_PROPERTY,
        ASYNC_DISPATCHER_CORE_SIZE_PROPERTY,
        ASYNC_DISPATCHER_MAX_SIZE_PROPERTY,
        ASYNC_DISPATCHER_KEEP_ALIVE_TIME_PROPERTY,
        "defaultAsyncDispatcherThreadGroup",
        "defaultAsync"
    ) { coreSize, maxSize, keepAliveTime, threadGroupName, threadNamePrefix, cause ->
        val hasCause = cause != null
        val useDefault = hasCause || (coreSize == null && maxSize == null && keepAliveTime == null)
        val dispatcher = if (useDefault) {
            // default.
            if (hasCause) {
                cause as Throwable
                logger.debug(
                    "Default async dispatcher will use the default blocking dispatcher " +
                        "because an exception thrown duration initialization: {}",
                    cause.localizedMessage,
                    cause
                )
            } else {
                logger.debug(
                    "Default async dispatcher will use the default blocking dispatcher " +
                        "because all initialization parameters are null"
                )
            }
            // default: null
            null
        } else {
            createDefaultDispatcher(coreSize, maxSize, keepAliveTime, threadGroupName, threadNamePrefix)
        }

        if (dispatcher == null) {
            logger.debug("Initialized default async dispatcher is null")
        } else {
            logger.debug("Initialized default async dispatcher: {}", dispatcher)
        }

        dispatcher
    }
}

/**
 * 如果 [DefaultAsyncDispatcherOrNull] 为 null，得到 [Dispatchers.Default], 否则得到 [DefaultAsyncDispatcherOrNull]的值。
 * @see DefaultAsyncDispatcherOrNull
 */
@InternalSimbotAPI
public val DefaultAsyncDispatcher: CoroutineDispatcher
    get() = DefaultAsyncDispatcherOrNull ?: Dispatchers.Default


// @Suppress("ObjectPropertyName")
// private val `$$DefaultScopeJob` = SupervisorJob()

/**
 * 默认的异步调用（Java异步，例如 [CompletableFuture] 或 [runInAsync]）上下文。
 *
 * [DefaultAsyncContext] 的基本内容如下：
 * - 一个 [CoroutineName]
 * - 如果 [DefaultAsyncDispatcherOrNull] 不为null，则使用它。
 *
 */
@InternalSimbotAPI
public val DefaultAsyncContext: CoroutineContext by lazy {
    val asyncDispatcher = DefaultAsyncDispatcherOrNull
    if (asyncDispatcher == null) {
        CoroutineName("def-async")
    } else {
        CoroutineName("def-async") + asyncDispatcher
    }
}

@Suppress("unused", "ObjectPropertyName", "TopLevelPropertyNaming")
@InternalSimbotAPI
@Deprecated("Unused property", level = DeprecationLevel.ERROR)
private val `$$DefaultScope`: CoroutineScope by lazy {
    CoroutineScope(DefaultAsyncContext)
}

// region run in blocking strategy

/**
 * 阻塞API所使用的执行策略。
 *
 * 可以通过 [setRunInBlockingStrategy]
 * 来自定义一个**全局**的阻塞函数执行策略。
 *
 */
@ExperimentalSimbotAPI
public interface RunInBlockingStrategy {
    @kotlin.jvm.Throws(Exception::class)
    public operator fun <T> invoke(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T
}

@OptIn(ExperimentalSimbotAPI::class)
private var runInBlockingStrategy: RunInBlockingStrategy = DefaultRunInBlockingStrategy


@OptIn(ExperimentalSimbotAPI::class)
private object DefaultRunInBlockingStrategy : RunInBlockingStrategy {
    override fun <T> invoke(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
        return runBlocking(context, block)
    }
}

/**
 * 设置一个 [runInBlocking] 函数的实际调度逻辑。
 *
 * 默认情况下的调度策略与 [runBlocking] 一致。
 */
@ExperimentalSimbotAPI
public fun setRunInBlockingStrategy(strategy: RunInBlockingStrategy) {
    runInBlockingStrategy = strategy
}
// endregion

// region run in no scope blocking strategy

/**
 * 无作用域的阻塞API所使用的执行策略。
 *
 * 可以通过 [setRunInNoScopeBlockingStrategy]
 * 来自定义一个**全局**的无作用域阻塞函数执行策略。
 *
 */
@ExperimentalSimbotAPI
public interface RunInNoScopeBlockingStrategy {
    @kotlin.jvm.Throws(Exception::class)
    public operator fun <T> invoke(context: CoroutineContext, block: suspend () -> T): T
}

@OptIn(ExperimentalSimbotAPI::class)
private var runInNoScopeBlockingStrategy: RunInNoScopeBlockingStrategy = DefaultRunInNoScopeBlockingStrategy

@OptIn(ExperimentalSimbotAPI::class)
private object DefaultRunInNoScopeBlockingStrategy : RunInNoScopeBlockingStrategy {
    @kotlin.jvm.Throws(Exception::class)
    override fun <T> invoke(context: CoroutineContext, block: suspend () -> T): T {
        val runner = SuspendRunner<T>(context)
        block.startCoroutine(runner)
        return runner.await(SuspendRunner.isWaitTimeoutEnabled)
    }

    @kotlin.jvm.Throws(Exception::class)
    fun <T> invokeWithoutTimeoutLog(context: CoroutineContext, block: suspend () -> T): T {
        val runner = SuspendRunner<T>(context)
        block.startCoroutine(runner)
        return runner.await(isWaitTimeoutEnabled = false)
    }

}

/**
 * 设置一个 [runInNoScopeBlocking] 函数的实际调度逻辑。
 *
 * 默认情况下 [runInNoScopeBlocking] 的策略为在当前线程上阻塞并等待。
 */
@ExperimentalSimbotAPI
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
 * @throws Exception 原函数可能被抛出的任何异常
 * @throws RunInBlockingException 当出现执行 [block] 过程中由于 future 或线程中断等非 [block] 本身产生的异常时被包装为 [RunInBlockingException]
 *
 * @see DefaultBlockingContext
 * @see runBlocking
 */
@OptIn(ExperimentalSimbotAPI::class, InternalSimbotAPI::class)
@Throws(Exception::class)
public fun <T> runInBlocking(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T,
): T = runInBlockingStrategy(context, block)

/**
 * 如果超时，则抛出 [TimeoutCancellationException].
 *
 * @throws TimeoutCancellationException 如果超时
 * @throws Exception 原函数可能被抛出的任何异常
 * @throws RunInBlockingException 当出现执行 [block] 过程中由于 future 或线程中断等非 [block] 本身产生的异常时被包装为 [RunInBlockingException]
 *
 * @see runInBlocking
 * @see withTimeout
 */
@OptIn(ExperimentalSimbotAPI::class, InternalSimbotAPI::class)
@Throws(Exception::class)
public fun <T> runInTimeoutBlocking(
    timeout: Long,
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T,
): T = try {
    runInBlockingStrategy(context) {
        withTimeout(timeout, block)
    }
} catch (timeout: TimeoutCancellationException) {
    throw timeout
} catch (e: Throwable) {
    throw `$RunInBlockingException$`(e)
}

/**
 *
 * 在simbot中提供的 [runBlocking] 包装。
 *
 * 在默认未提供上下文的情况下，[runInBlocking] 所使用的 [context] 为 [DefaultBlockingContext].
 *
 * @throws Exception 原函数可能被抛出的任何异常
 * @throws RunInBlockingException 当出现执行 [block] 过程中由于 future 或线程中断等非 [block] 本身产生的异常时被包装为 [RunInBlockingException]
 * @see DefaultBlockingContext
 * @see runBlocking
 */
@OptIn(ExperimentalSimbotAPI::class, InternalSimbotAPI::class)
@Throws(Exception::class)
public fun <T> runInNoScopeBlocking(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend () -> T,
): T = runInNoScopeBlockingStrategy(context, block)

/**
 * @suppress 内部API
 *
 * @throws Exception 原函数可能被抛出的任何异常
 * @throws RunInBlockingException 当出现执行 [block] 过程中由于 future 或线程中断等非 [block] 本身产生的异常时被包装为 [RunInBlockingException]
 *
 * @see runInNoScopeBlocking
 * @see DefaultBlockingContext
 * @see runBlocking
 */
@OptIn(ExperimentalSimbotAPI::class)
@Throws(Exception::class)
@InternalSimbotAPI
public fun <T> runInNoScopeBlockingWithoutTimeoutDebug(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend () -> T,
): T {
    val strategy = runInNoScopeBlockingStrategy
    if (strategy is DefaultRunInNoScopeBlockingStrategy) {
        return DefaultRunInNoScopeBlockingStrategy.invokeWithoutTimeoutLog(context, block)
    }

    return strategy(context, block)
}

/**
 * 如果超时，则抛出 [TimeoutException].
 * @see runInBlocking
 * @see withTimeout
 */
@OptIn(ExperimentalSimbotAPI::class, InternalSimbotAPI::class)
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
@InternalSimbotAPI
public fun <T> runInAsync(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): CompletableFuture<T> =
    scope.future(DefaultAsyncContext + context) { block() }

/**
 * 执行一个异步函数，得到 [CompletableFuture].
 * 默认情况下会使用 [GlobalScope] 作为调度作用域。
 */
@OptIn(DelicateCoroutinesApi::class)
@InternalSimbotAPI
public fun <T> runInAsync(block: suspend CoroutineScope.() -> T): CompletableFuture<T> =
    runInAsync(scope = GlobalScope, context = EmptyCoroutineContext, block = block)

/**
 * 将 [block] 包装为 [SuspendReserve].
 * [scope] 如果为 `null` 则会使用 [GlobalScope]。
 * @see SuspendReserve
 */
@InternalSimbotAPI
public fun <T> asReserve(
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null,
    block: suspend () -> T
): SuspendReserve<T> =
    suspendReserve(scope = scope ?: GlobalScope, context = context ?: EmptyCoroutineContext, block = block)

/**
 * @param scope 如果为 `null` 则使用 [GlobalScope]。
 * @see asReserve
 */
@InternalSimbotAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$asReserve`(scope: CoroutineScope? = null, block: suspend () -> T): SuspendReserve<T> =
    asReserve(scope = scope, context = EmptyCoroutineContext, block = block)


@InternalSimbotAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
@Throws(Exception::class)
public fun <T> `$$runInBlocking`(block: suspend () -> T): T = runInNoScopeBlocking(block = block)


/**
 *
 * 供编译器插件使用的异步转化函数。
 *
 * [KSTCP#32](https://github.com/ForteScarlet/kotlin-suspend-transform-compiler-plugin/pull/32)
 * 之后对可以为null的 [CoroutineScope] 参数有更好的支持，
 * 因此更建议使用 `$$runInAsyncNullable` 。
 *
 * @since 3.1.0
 *
 * see `$$runInAsyncNullable`
 */
@InternalSimbotAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInAsync`(block: suspend () -> T, scope: CoroutineScope = GlobalScope): CompletableFuture<T> {
    return runInAsync(scope) { block() }
}

/**
 * 供编译器插件使用的异步转化函数。
 *
 * 第二个参数 [CoroutineScope] 可以为null，可以更好的对当前作用域环境进行灵活判断。
 *
 * @param scope 可以为null，可以更好的对当前作用域环境进行灵活判断。
 * 如果为 `null` 则会使用 [GlobalScope]。
 */
@InternalSimbotAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInAsyncNullable`(block: suspend () -> T, scope: CoroutineScope? = null): CompletableFuture<T> {
    return runInAsync(scope ?: GlobalScope) { block() }
}

/**
 * 使用在 `runBlocking` 或相关函数中，用于将运行其中的函数所抛出的函数捕获并包装。
 *
 * [RunInBlockingException] 只会包装那些由 future 或者线程中断导致的异常，
 * 实际执行的blocking函数所抛出的异常会被原样抛出。
 *
 * 通常来讲，[cause] 可能是：
 * - [CancellationException]
 * - [ExecutionException] (概率很低）
 * - [InterruptedException]
 */
public sealed class RunInBlockingException protected constructor(cause: Throwable) : RuntimeException(cause)

@Suppress("unused", "ClassName")
private class `$RunInBlockingException$`(cause: Throwable) : RunInBlockingException(cause)

private fun throwRunInBlockingException(cause: Throwable): Nothing =
    throw `$RunInBlockingException$`(cause)

// Yes. I am the BlockingRunner.
private class SuspendRunner<T>(override val context: CoroutineContext = EmptyCoroutineContext) : Continuation<T> {
    @Suppress("unused")
    @Volatile
    var s: Int = SIGNAL_NONE
    // 1 = resume - success
    // 2 = resume - exception
    // 3 = suspend

    @Volatile
    var value: Any? = null
    // 1 -> value
    // 2 -> ex
    // 3 -> CompletableFuture<T>

    private object NULL

    override fun resumeWith(result: Result<T>) {
        // 先变更信号
        val resumed =
            signalUpdater.compareAndSet(
                this,
                SIGNAL_NONE,
                if (result.isSuccess) SIGNAL_RESUME_SUCCESS else SIGNAL_RESUME_FAILED
            )

        // 信号变更失败，说明现在信号是 SUSPEND
        // 那么 value 就已经有一个 future 值了，或者它应该被初始化为一个 future
        if (!resumed) {
            // value is a Future.
            @Suppress("UNCHECKED_CAST")
            valueUpdater.updateAndGet(this) { curr ->
                (curr as? CompletableFuture<T> ?: CompletableFuture<T>()).also { f ->
                    result.onSuccess { value ->
                        f.complete(value)
                    }.onFailure { e ->
                        f.completeExceptionally(e)
                    }
                }
            }
        } else {
            // resume success, set value
            result.onSuccess { value ->
                valueUpdater.set(this, value ?: NULL)
            }.onFailure { e ->
                valueUpdater.set(this, e)
            }
        }
    }

    /**
     * @param isWaitTimeoutEnabled 是否输出长时间阻塞警告
     *
     * @see CompletableFuture.join
     * @see CompletableFuture.get
     * @throws CancellationException cancellation
     * @throws CompletionException completion
     * @throws InterruptedException
     * @throws RunInBlockingException
     */
    @Suppress("UNCHECKED_CAST", "ReturnCount", "ThrowsCount")
    @Throws(Exception::class)
    fun await(isWaitTimeoutEnabled: Boolean): T {
        val future: CompletableFuture<T>

        // 预期为异步挂起。如果成功，value设置为 CompletableFuture.
        if (signalUpdater.compareAndSet(this, SIGNAL_NONE, SIGNAL_SUSPEND)) {
            future = valueUpdater.updateAndGet(this) { curr ->
                curr as? CompletableFuture<T> ?: CompletableFuture<T>()
            } as CompletableFuture<T>
        } else {
            // 失败，则获取，或等待结果
            var value: Any?
            do {
                value = valueUpdater.get(this)
            } while (value == null)

            if (value == NULL) {
                return null as T
            }

            // success or failed
            if (signalUpdater.get(this) == SIGNAL_RESUME_SUCCESS) {
                return value as T
            }

            throw value as Throwable
        }

        // 不需要检测长时间等待的日志,
        // 或者不需要在virtual时输出日志，且当前是virtual thread
        if (!isWaitTimeoutEnabled || (!logIfVirtual && Thread.currentThread().isVirtualThread())) {
            try {
                return future.get()
            } catch (cancellation: CancellationException) {
                throwRunInBlockingException(cancellation)
            } catch (execution: ExecutionException) {
                // 一般来讲 ExecutionException.cause 不会是 null
                throw execution.cause ?: throwRunInBlockingException(execution)
            }
            // InterruptedException 直接向外传递
        }

        // 需要输出长时间等待日志

        var times = 0
        while (!future.isDone) {
            if (Thread.interrupted()) {
                throw InterruptedException()
            }

            if (times > 0) {
                val duration = (waitTimeout * times).milliseconds
                if (logger.isDebugEnabled) {
                    val durationString = duration.toString()
                    logger.warn("Blocking runner has been blocking for at least {}.", durationString)
                    val e: Throwable = ProlongedBlockingException(durationString)
                    logger.debug(
                        "Prolonged blocking duration at least {}",
                        durationString,
                        e
                    )
                } else {
                    logger.warn(
                        "Blocking runner has been blocking for at least {}. " +
                            "Enable debug logging for '{}' for more stack information.",
                        duration.toString(),
                        LOGGER_NAME
                    )
                }
            }

            try {
                return future.get(waitTimeout, TimeUnit.MILLISECONDS)
            } catch (ignore: TimeoutException) {
                times += 1
            } catch (cancellation: CancellationException) {
                throwRunInBlockingException(cancellation)
            } catch (execution: ExecutionException) {
                throw execution.cause ?: throwRunInBlockingException(execution)
            }
        }

        // Is done, but not in the future.get(timeout)
        try {
            return future.join()
        } catch (cancellation: CancellationException) {
            throwRunInBlockingException(cancellation)
        } catch (execution: CompletionException) {
            throwRunInBlockingException(execution)
        }
    }

    // Used only to show the stack
    private class ProlongedBlockingException(message: String) : RuntimeException(message)

    companion object {
        private const val SIGNAL_NONE = 0
        private const val SIGNAL_RESUME_SUCCESS = 1
        private const val SIGNAL_RESUME_FAILED = 2
        private const val SIGNAL_SUSPEND = 3
        private val signalUpdater = AtomicIntegerFieldUpdater.newUpdater(SuspendRunner::class.java, "s")
        private val valueUpdater =
            AtomicReferenceFieldUpdater.newUpdater(SuspendRunner::class.java, Any::class.java, "value")
//        private val futureUpdater = AtomicReferenceFieldUpdater.newUpdater(SuspendRunner::class.java, CompletableFuture::class.java, "future")

        // ignore for Virtual thread?
        // val threadIsV = MethodHandles.publicLookup().findVirtual(Thread::class.java, "isVirtual", MethodType.methodType(java.lang.Boolean.TYPE))

        private val isVirtualThreadFunc = runCatching<(Thread) -> Boolean> {
            val mh = MethodHandles.publicLookup()
                .findVirtual(Thread::class.java, "isVirtual", MethodType.methodType(java.lang.Boolean.TYPE))
            return@runCatching { t -> mh.invoke(t) as Boolean }
        }.getOrElse {
            return@getOrElse { false }
        }

        private fun Thread.isVirtualThread(): Boolean = isVirtualThreadFunc(this)

        private const val BLOCKING_RUNNER_WAIT_TIME_LOG_IF_VIRTUAL_PROPERTY_NAME =
            "simbot.blockingRunner.waitTimeoutLogIfVirtual"

        private const val BLOCKING_RUNNER_DEFAULT_WAIT_TIME_PROPERTY_NAME =
            "simbot.blockingRunner.waitTimeoutMilliseconds"
        private const val BLOCKING_RUNNER_DISABLE_WAIT_TIME_PROPERTY_NAME = "simbot.blockingRunner.disableWaitTimeout"
        private const val DEFAULT_WAIT_TIME = 60_000L // 60s
        private val waitTimeout =
            systemLong(BLOCKING_RUNNER_DEFAULT_WAIT_TIME_PROPERTY_NAME)?.takeIf { it > 0 } ?: DEFAULT_WAIT_TIME
        internal val isWaitTimeoutEnabled = !systemBool(BLOCKING_RUNNER_DISABLE_WAIT_TIME_PROPERTY_NAME)

        private val logIfVirtual = systemBool(BLOCKING_RUNNER_WAIT_TIME_LOG_IF_VIRTUAL_PROPERTY_NAME)

        init {
            if (isWaitTimeoutEnabled) {
                logger.info(
                    "Blocking runner wait timeout is enabled with wait timeout {}. " +
                        "You can enable debug logging for '$LOGGER_NAME' " +
                        "for more stack information " +
                        "or disable it with the JVM parameter " +
                        "'-D$BLOCKING_RUNNER_DISABLE_WAIT_TIME_PROPERTY_NAME=true'.",
                    waitTimeout.milliseconds.toString(),
                )
            } else {
                logger.debug("Blocking runner wait timeout is disabled.")
            }
        }
    }
}

private fun systemLong(key: String): Long? = System.getProperty(key)?.toLongOrNull()
private fun systemInt(key: String): Int? = System.getProperty(key)?.toIntOrNull()

@Suppress("SameParameterValue")
private fun systemBool(key: String): Boolean = System.getProperty(key).toBoolean()
