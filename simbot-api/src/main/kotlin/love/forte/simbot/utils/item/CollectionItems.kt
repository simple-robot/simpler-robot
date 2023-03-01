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
import java.util.stream.Stream

/**
 *
 * 使用 [List] 作为数据来源实现 [Items] 的基础约定。
 *
 * @author ForteScarlet
 */
public class CollectionItems<T>(private val collection: Collection<T>) : BaseItems<T, CollectionItems<T>>() {
    
    override val self: CollectionItems<T>
        get() = this
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        if (collection.isEmpty()) {
            return
        }
        
        val (limit, offset, _) = preprocessingProperties
        if (offset >= collection.size) return
        
        val iter: Iterator<T>
        if (collection is List) {
            iter = if (offset > 0) collection.listIterator(offset) else collection.listIterator()
        } else {
            iter = collection.iterator()
            if (offset > 0) {
                var count = 0
                while (iter.hasNext() && count++ < offset) {
                    iter.next()
                }
            }
        }
        
        if (limit > 0) {
            var count = 0
            while (iter.hasNext() && count++ < limit) {
                collector(iter.next())
            }
        } else {
            while (iter.hasNext()) {
                collector(iter.next())
            }
        }
        
        
    }
    
    override fun asFlow(): Flow<T> {
        return preprocessingProperties.effectOn(collection.asFlow())
    }
    
    override fun asSequence(): Sequence<T> {
        return preprocessingProperties.effectOn(collection.asSequence())
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return preprocessingProperties.effectOn(collection.stream())
    }
}
