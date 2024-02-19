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
@file:JvmName("DispatchersUtil")
@file:JvmMultifileClass

package love.forte.simbot.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simbot.annotations.Api4J
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 得到 [Dispatchers.IO]。
 *
 * @see Dispatchers.IO
 */
public actual inline val Dispatchers.IOOrDefault: CoroutineDispatcher
    get() = IO

/**
 * 在支持虚拟线程调度器时使用虚拟线程调度器 (`Executors.newVirtualThreadPerTaskExecutor`)
 * 作为 [CoroutineDispatcher],
 * 否则得到 `null`。
 *
 */
public val Dispatchers.Virtual: CoroutineDispatcher? by lazy {
    runCatching {
        val handle = MethodHandles.publicLookup().findStatic(
            Executors::class.java,
            "newVirtualThreadPerTaskExecutor",
            MethodType.methodType(ExecutorService::class.java)
        )
        (handle.invoke() as Executor).asCoroutineDispatcher()
    }.getOrNull()
}

/**
 * Friendly API for Java.
 */
@Api4J
public val VirtualDispatcher: CoroutineDispatcher? get() = Dispatchers.Virtual

/**
 * 在支持虚拟线程调度器时使用虚拟线程调度器 (`Executors.newVirtualThreadPerTaskExecutor`)
 * 作为 [CoroutineDispatcher],
 * 否则得到 [Dispatchers.IO]。
 *
 */
public val Dispatchers.VirtualOrIO: CoroutineDispatcher by lazy { Dispatchers.Virtual ?: IO }

/**
 * Friendly API for Java.
 */
@Api4J
public val VirtualOrIODispatcher: CoroutineDispatcher? get() = Dispatchers.VirtualOrIO
