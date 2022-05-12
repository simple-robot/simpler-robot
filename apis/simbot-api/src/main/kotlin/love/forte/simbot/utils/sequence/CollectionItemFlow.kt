package love.forte.simbot.utils.sequence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import love.forte.simbot.utils.sequence.ItemFlow.Companion.asItemFlow
import java.util.stream.Stream

/**
 * 直接通过 [Collection] 对 [ItemFlow] 进行实现。
 *
 * @author ForteScarlet
 */
internal class CollectionItemFlow<out V>(private val collection: Collection<V>) : ItemFlow<V> {
    private val bFilters = mutableListOf<(V) -> Boolean>()
    private val sFilters = mutableListOf<suspend (V) -> Boolean>()
    
    private fun addFilter(matcher: Matcher<V>) {
        if (sFilters.isNotEmpty()) {
            sFilters.add(matcher)
        } else {
            bFilters.add(matcher)
        }
    }
    
    private fun addFilter(matcher: suspend (V) -> Boolean) {
        if (bFilters.isNotEmpty()) {
            bFilters.forEach { bf ->
                sFilters.add(bf)
            }
            bFilters.clear()
        }
        sFilters.add(matcher)
    }
    
    @BlockingApi
    override fun filter(matcher: Matcher<V>): ItemFlow<V> {
        addFilter(matcher)
        return this
    }
    
    override fun filter(matcher: suspend (V) -> Boolean): ItemFlow<V> {
        addFilter(matcher)
        return this
    }
    
    @BlockingApi
    override fun <T> map(mapper: Mapper<V, T>): ItemFlow<T> {
        return map { mapper(it) }
    }
    
    override fun <T> map(mapper: suspend (V) -> T): ItemFlow<T> {
        return asFlow().map(mapper).asItemFlow()
    }
    
    @BlockingApi
    override fun collect(visitor: Visitor<V>) {
        when {
            bFilters.isNotEmpty() -> {
                collection.forEach {
                    if (bFilters.all { f -> f(it) }) {
                        visitor(it)
                    }
                }
            }
            sFilters.isNotEmpty() -> {
                collection.forEach {
                    if (sFilters.all { f -> runBlocking { f(it) } }) {
                        visitor(it)
                    }
                }
            }
            else -> collection.forEach { visitor(it) }
        }
        
    }
    
    override suspend fun collect(visitor: suspend (V) -> Unit) {
        when {
            bFilters.isNotEmpty() -> {
                collection.forEach {
                    if (bFilters.all { f -> f(it) }) {
                        visitor(it)
                    }
                }
            }
            sFilters.isNotEmpty() -> {
                collection.forEach {
                    if (sFilters.all { f -> f(it) }) {
                        visitor(it)
                    }
                }
            }
            else -> collection.forEach { visitor(it) }
        }
    }
    
    private fun collectBlockingWithFilter(destination: MutableCollection<in V>) {
        collection.forEach {
            if (bFilters.all { f -> f(it) }) {
                destination.add(it)
            }
        }
    }
    
    @BlockingApi
    override fun <C : MutableCollection<in V>> collectTo(destination: C): C {
        when {
            bFilters.isNotEmpty() -> {
                collectBlockingWithFilter(destination)
            }
            sFilters.isNotEmpty() -> {
                collection.forEach {
                    if (sFilters.all { f -> runBlocking { f(it) } }) {
                        destination.add(it)
                    }
                }
            }
            else -> destination.addAll(collection)
        }
        
        return destination
    }
    
    override suspend fun <C : MutableCollection<in V>> collection(destination: C): C {
        when {
            bFilters.isNotEmpty() -> {
                collectBlockingWithFilter(destination)
            }
            sFilters.isNotEmpty() -> {
                collection.forEach {
                    if (sFilters.all { f -> f(it) }) {
                        destination.add(it)
                    }
                }
            }
            else -> destination.addAll(collection)
        }
        
        return destination
    }
    
    private fun toListBlockingWithFilter(): List<V> {
        return collection.filter { bFilters.all { f -> f(it) } }
    }
    
    @BlockingApi
    override fun toList(): List<V> {
        return when {
            bFilters.isNotEmpty() -> {
                toListBlockingWithFilter()
            }
            sFilters.isNotEmpty() -> {
                collection.filter { sFilters.all { f -> runBlocking { f(it) } } }
            }
            else -> collection.toList()
        }
    }
    
    override suspend fun collectToList(): List<V> {
        return when {
            bFilters.isNotEmpty() -> {
                toListBlockingWithFilter()
            }
            sFilters.isNotEmpty() -> {
                collection.filter { sFilters.all { f -> f(it) } }
            }
            else -> collection.toList()
        }
    }
    
    override fun asFlow(): Flow<V> {
        var flow = collection.asFlow()
        when {
            bFilters.isNotEmpty() -> {
                val filtersCopy = bFilters.toList()
                flow = flow.filter { filtersCopy.all { f -> f(it) } }
            }
            sFilters.isNotEmpty() -> {
                val filtersCopy = sFilters.toList()
                flow = flow.filter { filtersCopy.all { f -> f(it) } }
            }
        }
        return flow
    }
    
    @BlockingApi
    override fun asSequence(): Sequence<V> {
        var seq = collection.asSequence()
        @Suppress("DuplicatedCode")
        when {
            bFilters.isNotEmpty() -> {
                val filtersCopy = bFilters.toList()
                seq = seq.filter { filtersCopy.all { f -> f(it) } }
            }
            sFilters.isNotEmpty() -> {
                val filtersCopy = sFilters.toList()
                seq = seq.filter { filtersCopy.all { f -> runBlocking { f(it) } } }
            }
        }
        return seq
    }
    
    @Api4J
    override fun asStream(): Stream<out V> {
        var stream = collection.stream()
        @Suppress("DuplicatedCode")
        when {
            bFilters.isNotEmpty() -> {
                val filtersCopy = bFilters.toList()
                stream = stream.filter { filtersCopy.all { f -> f(it) } }
            }
            sFilters.isNotEmpty() -> {
                val filtersCopy = sFilters.toList()
                stream = stream.filter { filtersCopy.all { f -> runBlocking { f(it) } } }
            }
        }
        return stream
    }
}