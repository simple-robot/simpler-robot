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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.produceIn
import love.forte.simbot.Api4J
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.utils.runInAsync
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 *
 * 使用 [Flow] 实现 [Items] 的基本约定。
 *
 * 需要提供一个 [produceScope] 来作为当 flow 转化为 [ChannelIterator] 时的作用域。
 *
 * @author ForteScarlet
 */
public class FlowItems<T>(
    private val produceScope: CoroutineScope,
    private val flowFactory: (Items.PreprocessingProperties) -> Flow<T>,
) : BaseItems<T, FlowItems<T>>() {
    override val self: FlowItems<T>
        get() = this
    
    private val flow: Flow<T> get() = flowFactory(preprocessingProperties)
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val flow = flow
        flow.collect { collector(it) }
    }
    
    override fun asFlow(): Flow<T> {
        return flow
    }
    
    @OptIn(FlowPreview::class)
    override fun asSequence(): Sequence<T> {
        val iter = flow.produceIn(produceScope).iterator()
        return sequence {
            while (runInNoScopeBlocking { iter.hasNext() }) {
                yield(iter.next())
            }
        }
    }
    
    @Api4J
    @OptIn(FlowPreview::class)
    override fun asStream(): Stream<out T> {
        val iter = BlockingIterator(flow.produceIn(produceScope).iterator())
        return StreamSupport.stream(
            { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
            Spliterator.ORDERED,
            false
        )
    }
    
    @OptIn(InternalSimbotApi::class)
    @Api4J
    override fun collectAsync(collector: Consumer<in T>): CompletableFuture<Unit> {
        return runInAsync(produceScope) {
            flow.collect {
                collector.accept(it)
            }
        }
    }
    
    @Api4J
    @OptIn(InternalSimbotApi::class)
    override fun <C : MutableCollection<in T>> collectToAsync(collector: C): CompletableFuture<out C> {
        return runInAsync(produceScope) {
            flow.collect {
                collector.add(it)
            }
        }.thenApply { collector }
    }
}
