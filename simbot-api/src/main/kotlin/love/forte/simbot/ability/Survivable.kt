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

import kotlinx.coroutines.CompletionHandler
import love.forte.simbot.Api4J
import love.forte.simbot.JST
import love.forte.simbot.utils.runInNoScopeBlocking

/**
 * 可存活的。
 * 此接口提供 [join]、[invokeOnCompletion] 等函数来对生命周期提供一定操作。
 *
 * @author ForteScarlet
 */
public interface Survivable : Switchable {
    
    /**
     * 挂起, 直到当前实例被 [cancel] 或完成.
     */
    @JST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()
    
    /**
     * 当完成（或被cancel）时执行一段处理。
     */
    public fun invokeOnCompletion(handler: CompletionHandler)
    
    /**
     * 阻塞当前线程并等待 [join] 的挂起结束。
     *
     * 等同于 `joinBlocking`。目前来看唯一的区别是 [waiting] 显示通过 [Throws] 指定了受检异常 [InterruptedException],
     * 而 joinBlocking 目前不会产生受检异常。
     */
    @Api4J
    @Throws(InterruptedException::class)
    public fun waiting() {
        runInNoScopeBlocking { join() }
    }
}

