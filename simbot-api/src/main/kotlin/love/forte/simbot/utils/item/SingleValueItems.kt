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
import kotlinx.coroutines.flow.flowOf
import love.forte.simbot.Api4J
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.utils.runInAsync
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.stream.Stream

/**
 *
 * 只有一个元素的 [Items] 实例。
 *
 * @author ForteScarlet
 */
public class SingleValueItems<T>(private val value: T) : Items<T> {
    override fun limit(count: Int): Items<T> {
        return this
    }
    
    override fun offset(count: Int): Items<T> {
        if (count > 1) return Items.emptyItems()
        return this
    }
    
    override fun batch(size: Int): Items<T> {
        return this
    }
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        collector(value)
    }
    
    override fun asFlow(): Flow<T> {
        return flowOf(value)
    }
    
    override fun asSequence(): Sequence<T> {
        return sequenceOf(value)
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return Stream.of(value)
    }
    
    @Api4J
    override fun collect(collector: Consumer<in T>) {
        collector.accept(value)
    }
    
    @OptIn(InternalSimbotApi::class)
    @Api4J
    override fun collectAsync(collector: Consumer<in T>): CompletableFuture<Unit> {
        return runInAsync { collect(collector) }
    }
    
    @Api4J
    override fun <C : MutableCollection<in T>> collectTo(collector: C): C {
        collector.add(value)
        return collector
    }
    
    @OptIn(InternalSimbotApi::class)
    @Api4J
    override fun <C : MutableCollection<in T>> collectToAsync(collector: C): CompletableFuture<out C> {
        return runInAsync { collector.add(value) }.thenApply { collector }
    }
    
    @Api4J
    override fun collectToList(): List<T> {
        return listOf(value)
    }
    
    @Api4J
    override fun collectToListAsync(): CompletableFuture<out List<T>> {
        return CompletableFuture.completedFuture(listOf(value))
    }
}
