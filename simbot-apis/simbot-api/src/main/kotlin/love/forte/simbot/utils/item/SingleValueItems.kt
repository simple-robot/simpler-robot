/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
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