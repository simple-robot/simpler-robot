/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.utils.item

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.stream.Stream

/**
 *
 * 允许为一个 [Items] 提供一个转化函数的 [Items] 代理实现。
 *
 * @author ForteScarlet
 */
public class TransformItems<B, T>(
    private val baseItems: Items<B>,
    private val transform: suspend (B) -> T,
) : Items<T> {
    override fun limit(count: Int): Items<T> {
        baseItems.limit(count)
        return this
    }
    
    override fun offset(count: Int): Items<T> {
        baseItems.offset(count)
        return this
    }
    
    override fun batch(size: Int): Items<T> {
        baseItems.batch(size)
        return this
    }
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        baseItems.collect { collector(transform(it)) }
    }
    
    override fun asFlow(): Flow<T> {
        return baseItems.asFlow().map(transform)
    }
    
    override fun asSequence(): Sequence<T> {
        return baseItems.asSequence().map { runInNoScopeBlocking { transform(it) } }
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return baseItems.asStream().map { runInNoScopeBlocking { transform(it) } }
    }
}
