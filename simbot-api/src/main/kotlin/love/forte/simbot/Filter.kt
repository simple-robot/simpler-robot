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

package love.forte.simbot

import love.forte.simbot.utils.runWithInterruptible


/**
 *
 * 过滤器。 对一个指定的目标进行过滤匹配, 并得到最终的匹配结果。
 *
 * 此过滤器的匹配函数 [test] 是可挂起的。
 *
 * @see BlockingFilter
 *
 * @author ForteScarlet
 */
public interface Filter<T> {
    
    /**
     * 通过匹配目标进行检测，得到匹配结果。
     *
     */
    @JvmSynthetic
    public suspend fun test(t: T): Boolean
    
}


/**
 * 过滤器。 对一个指定的目标进行过滤匹配, 并得到最终的匹配结果。
 *
 * 使用非挂起函数的 [testBlocking] 来实现 [Filter] 的 [test].
 */
@Api4J
public interface BlockingFilter<T> : Filter<T> {
    
    /**
     * 通过匹配目标进行检测，得到匹配结果。
     *
     */
    @JvmSynthetic
    override suspend fun test(t: T): Boolean {
        return runWithInterruptible { testBlocking(t) }
    }
    
    /**
     * 通过匹配目标进行阻塞的检测，得到匹配结果。
     *
     */
    @Api4J
    public fun testBlocking(t: T): Boolean
    
    
}
