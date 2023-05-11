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
import kotlinx.coroutines.flow.asFlow
import love.forte.simbot.Api4J
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * [Items] 的基础阻塞实现，通过提供 [Iterator] 的工厂函数实现 [Items] 的基本约定。
 * @author ForteScarlet
 */
public class SimpleIteratorItems<out T>(
    private val iteratorFactory: (Items.PreprocessingProperties) -> Iterator<T>,
) : BaseItems<T, SimpleIteratorItems<T>>() {
    override val self: SimpleIteratorItems<T>
        get() = this
    
    private val iterator: Iterator<T> get() = iteratorFactory(preprocessingProperties)
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val iter = iterator
        for (v in iter) {
            collector(v)
        }
        
        
    }
    
    override fun asFlow(): Flow<T> {
        return iterator.asFlow()
    }
    
    override fun asSequence(): Sequence<T> {
        return iterator.asSequence()
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        val iter = iterator
        return StreamSupport.stream(
            { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
            Spliterator.ORDERED,
            false
        )
    }
}
