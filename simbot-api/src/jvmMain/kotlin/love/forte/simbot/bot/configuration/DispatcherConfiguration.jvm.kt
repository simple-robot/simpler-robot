/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 获取 [Dispatchers.IO] 调度器。
 */
internal actual fun ioDispatcher(): CoroutineDispatcher? = Dispatchers.IO

/**
 * 获取自定义调度器。不支持的情况下返回 `null`。
 */
internal actual fun customDispatcher(
    coreThreads: Int?,
    maxThreads: Int?,
    keepAliveMillis: Long?,
    name: String?,
): CoroutineDispatcher? {
    val core = coreThreads ?: return null
    require(core >= 1) { "'coreThreads' must >= 1, but $core" }

    val max = maxThreads ?: core
    require(max >= core) { "'maxThreads' must >= 'coreThreads', but max: $max, core: $core" }

    // Create ThreadGroup
    fun tg() = ThreadGroup(name?.let { "-group" } ?: "CustomDP-STPE-group")

    if (core == max && (keepAliveMillis == null || keepAliveMillis == 0L)) {
        val counter = AtomicInteger(1)
        val group = tg()

        val executor = ScheduledThreadPoolExecutor(core) { r ->
            Thread(
                group,
                r,
                name?.let { "$it-${counter.getAndIncrement()}" } ?: "CustomDP-STPE-${counter.getAndIncrement()}"
            ).also {
                it.isDaemon = true
            }
        }

        return executor.asCoroutineDispatcher()
    }

    val aliveMillis: Long = keepAliveMillis ?: (60L * 1000L)
    require(aliveMillis >= 0L) { "'keepAliveMillis' must >= 0, but $aliveMillis" }

    val counter = AtomicInteger(1)
    val group = tg()

    val executor = ThreadPoolExecutor(
        core,
        max,
        aliveMillis,
        TimeUnit.MILLISECONDS,
        LinkedBlockingDeque()
    ) { r ->
        Thread(
            group,
            r,
            name?.let { "$it-${counter.getAndIncrement()}" } ?: "CustomDP-TPE-${counter.getAndIncrement()}"
        ).also {
            it.isDaemon = true
        }
    }

    return executor.asCoroutineDispatcher()
}

private val VirtualCreator: (() -> Executor)? by lazy {
    runCatching {
        val handle = MethodHandles.publicLookup().findStatic(
            Executors::class.java,
            "newVirtualThreadPerTaskExecutor",
            MethodType.methodType(ExecutorService::class.java)
        )

        return@runCatching { handle.invoke() as Executor }
    }.getOrElse { null }
}

/**
 * 当平台为 Java21+ 的 JVM平台时得到虚拟线程调度器，否则得到 `null`。
 */
internal actual fun virtualDispatcher(): CoroutineDispatcher? =
    VirtualCreator?.invoke()?.asCoroutineDispatcher()
