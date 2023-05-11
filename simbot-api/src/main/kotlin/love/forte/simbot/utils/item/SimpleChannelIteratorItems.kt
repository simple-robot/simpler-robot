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

import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 *
 * [Items] 的基础实现，通过一个构建 [ChannelIterator] 的函数实现 [Items] 约定的功能。
 *
 * @author ForteScarlet
 */
public class SimpleChannelIteratorItems<out T>(
    private val channelIteratorFactory: (Items.PreprocessingProperties) -> ChannelIterator<T>,
) : BaseItems<T, SimpleChannelIteratorItems<T>>() {
    override val self: SimpleChannelIteratorItems<T>
        get() = this
    
    private val iterator: ChannelIterator<T> get() = channelIteratorFactory(preprocessingProperties)
    
    /**
     * 收集当前数据序列中的元素. [collectTo] 可能会产生挂起，会直到当前序列中的所有可能产生的元素收集完毕后结束挂起。
     */
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val iter = iterator
        while (iter.hasNext()) {
            collector(iter.next())
        }
    }
    
    /**
     * 将当前元素序列转化为 [Flow] 。
     */
    override fun asFlow(): Flow<T> {
        val iter = iterator
        return flow {
            while (iter.hasNext()) {
                emit(iter.next())
            }
        }
    }
    
    /**
     * 将当前元素序列转化为 [Sequence].
     */
    override fun asSequence(): Sequence<T> {
        val iter = iterator
        return sequence {
            while (runInNoScopeBlocking { iter.hasNext() }) {
                yield(iter.next())
            }
        }
    }
    
    /**
     * 将当前元素序列转化为 [Stream].
     */
    @Api4J
    override fun asStream(): Stream<out T> {
        val iter = BlockingIterator(iterator)
        // see Sequence.asStream
        return StreamSupport.stream(
            { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
            Spliterator.ORDERED,
            false
        )
    }
    
    
}
