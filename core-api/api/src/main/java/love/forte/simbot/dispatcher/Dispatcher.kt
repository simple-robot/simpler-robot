/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("DispatcherUtils")
@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Runnable
import love.forte.common.ioc.annotation.Depend
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext


/**
 *
 * 调度器工厂, 用于获取调度器实例。
 *
 * @see EventDispatcherFactory
 *
 * @author ForteScarlet
 */
public interface DispatcherFactory {

    /**
     * 得到一个 [调度器][CoroutineDispatcher]. 此 [CoroutineDispatcher] 对于当前的 [DispatcherFactory] 是唯一的，
     * 一个 [DispatcherFactory] 实例仅对应一个 [dispatcher].
     */
    val dispatcher: CoroutineDispatcher


    /**
     * 基于当前的 [DispatcherFactory] 得到一个新的 [调度器][CoroutineDispatcher].
     *
     * 通过 [newDispatcher] 得到的 [CoroutineDispatcher] 实例总是不同于 [dispatcher] 的实例。
     *
     * 在首次加载 [dispatcher] 的时候同样会调用 [newDispatcher].
     *
     */
    fun newDispatcher(): CoroutineDispatcher
}


/**
 *
 * [DispatcherFactory] 的配置类抽象，通过 [Executor] 实现。
 *
 */
public abstract class AbstractDispatcherFactory(threadGroupName: String) : DispatcherFactory {
    protected val threadGroup = ThreadGroup(threadGroupName).also {
        it.isDaemon = true
    }
    private val threadNo = AtomicInteger(1)

    @Depend(value = "corePoolSize", orIgnore = true)
    var corePoolSize: Int = Runtime.getRuntime().availableProcessors() * 2 + 2

    @Depend(value = "maximumPoolSize", orIgnore = true)
    var maximumPoolSize: Int = Runtime.getRuntime().availableProcessors() * 4

    @Depend(value = "keepAliveTime", orIgnore = true)
    var keepAliveTime: Long = 0L

    @Depend(value = "timeUnit", orIgnore = true)
    var timeUnit: TimeUnit = TimeUnit.MILLISECONDS

    override val dispatcher: CoroutineDispatcher by lazy(::newDispatcher)

    override fun newDispatcher(): CoroutineDispatcher = executorDispatcher(
        corePoolSize, maximumPoolSize, keepAliveTime, timeUnit
    ) { r ->
        Thread(threadGroup, r, "${threadGroup.name}-${threadNo.getAndIncrement()}").also {
            it.isDaemon = true
        }
    }

    override fun toString(): String = "DispatcherFactory(group=${threadGroup.name}, core=$corePoolSize, maximum=$maximumPoolSize, keepAliveTime=$keepAliveTime, timeUnit=$timeUnit)"
}


internal class ExecutorDispatcher(executorFactory: () -> ExecutorService) : ExecutorCoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }

    override val executor: ExecutorService by lazy(executorFactory)

    override fun close() {
        executor.shutdown()
    }
}


internal fun executorDispatcher(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    timeUnit: TimeUnit,
    threadFactory: ThreadFactory,
): ExecutorDispatcher = ExecutorDispatcher {
    ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        timeUnit,
        LinkedBlockingQueue(),
        threadFactory,
    )
}