/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.ability

import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.Api4J
import love.forte.simbot.JST
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

/**
 *
 * 可开关的。
 *
 * 此接口提供 `start`、`cancel` 等相关开始、关闭的操作。
 *
 * 对于开关操作，在执行了 [cancel] 之后将不允许二次开启。
 *
 * @author ForteScarlet
 */
@JST
public interface Switchable : DelayableCoroutineScope {
    
    /**
     * 启动操作.
     * @return 从未启动且尚未关闭的情况下启动成功则返回true。
     */
    public suspend fun start(): Boolean
    
    /**
     * 关闭操作.
     *
     * @return 尚未关闭且关闭成功时返回true。
     */
    public suspend fun cancel(reason: Throwable? = null): Boolean
    
    @Api4J
    @Throws(InterruptedException::class)
    public fun cancelBlocking(): Boolean = runInNoScopeBlocking { cancel() }
    
    @Api4J
    public fun cancelAsync(): CompletableFuture<Boolean> {
        return async { cancel() }.asCompletableFuture()
    }
    
    /**
     * 是否已经启动过了。
     */
    public val isStarted: Boolean
    
    /**
     * 是否正在运行，即启动后尚未关闭。
     */
    public val isActive: Boolean
    
    /**
     * 是否已经被取消。
     */
    public val isCancelled: Boolean
    
}
