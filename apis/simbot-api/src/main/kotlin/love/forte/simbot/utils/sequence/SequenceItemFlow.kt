package love.forte.simbot.utils.sequence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.utils.sequence.ItemFlow.Companion.asItemFlow
import java.util.stream.Stream

/**
 * 使用 [Sequence] ([ItemSequence]) 对 [ItemFlow] 进行实现。
 * @author ForteScarlet
 */
@OptIn(InternalSimbotApi::class)
internal class SequenceItemFlow<out V>(private var sequence: ItemSequence<V>) : ItemFlow<V>, BaseSequence<V> {
    @OptIn(BlockingApi::class)
    override fun filter(matcher: Matcher<V>): ItemFlow<V> {
        sequence = sequence.filter(matcher)
        return this
    }
    
    override fun filter(matcher: suspend (V) -> Boolean): ItemFlow<V> {
        return sequence.asSequence().asFlow().filter(matcher).asItemFlow()
    }
    
    @OptIn(BlockingApi::class)
    override fun <T> map(mapper: Mapper<V, T>): ItemFlow<T> {
        return sequence.map(mapper).asItemFlow()
    }
    
    override fun <T> map(mapper: suspend (V) -> T): ItemFlow<T> {
        return sequence.asSequence().asFlow().map(mapper).asItemFlow()
    }
    
    @OptIn(BlockingApi::class)
    override fun collect(visitor: Visitor<V>) {
        sequence.collect(visitor)
    }
    
    override suspend fun collect(visitor: suspend (V) -> Unit) {
        sequence.asSequence().asFlow().collect {
            visitor(it)
        }
    }
    
    @OptIn(BlockingApi::class)
    override fun <C : MutableCollection<in V>> collectTo(destination: C): C {
        return sequence.collectTo(destination)
    }
    
    override suspend fun <C : MutableCollection<in V>> collection(destination: C): C {
        return collectTo(destination)
    }
    
    @BlockingApi
    override fun toList(): List<V> {
        return sequence.toList()
    }
    
    override suspend fun collectToList(): List<V> {
        return sequence.toList()
    }
    
    override fun asFlow(): Flow<V> {
        val seq = sequence
        if (seq is ItemFlow) {
            return seq.asFlow()
        }
        return seq.asSequence().asFlow()
    }
    
    @BlockingApi
    override fun asSequence(): Sequence<V> {
        return sequence.asSequence()
    }
    
    @Api4J
    override fun asStream(): Stream<out V> {
        return sequence.asStream()
    }
}