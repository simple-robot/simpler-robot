package love.forte.simbot.utils.sequence

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import java.util.stream.Stream

/**
 * 直接通过 [Collection] 对 [ItemFlow] 进行实现。
 *
 * @author ForteScarlet
 */
internal class CollectionItemFlow<out V>(private val collection: Collection<V>) : ItemFlow<V> {
    
    @BlockingApi
    override fun filter(matcher: Matcher<V>): ItemFlow<V> {
        TODO("Not yet implemented")
    }
    
    override fun filter(matcher: suspend (V) -> Boolean): ItemFlow<V> {
        TODO("Not yet implemented")
    }
    
    @BlockingApi
    override fun <T> map(mapper: Mapper<V, T>): ItemFlow<T> {
        TODO("Not yet implemented")
    }
    
    override fun <T> map(mapper: suspend (V) -> T): ItemFlow<T> {
        TODO("Not yet implemented")
    }
    
    @BlockingApi
    override fun collect(visitor: Visitor<V>) {
        TODO("Not yet implemented")
    }
    
    override suspend fun collect(visitor: suspend (V) -> Unit) {
        TODO("Not yet implemented")
    }
    
    @BlockingApi
    override fun <C : MutableCollection<in V>> collectTo(destination: C): C {
        TODO("Not yet implemented")
    }
    
    override suspend fun <C : MutableCollection<in V>> collection(destination: C): C {
        TODO("Not yet implemented")
    }
    
    @BlockingApi
    override fun toList(): List<V> {
        TODO("Not yet implemented")
    }
    
    override suspend fun collectToList(): List<V> {
        TODO("Not yet implemented")
    }
    
    override fun asFlow(): Flow<V> {
        TODO("Not yet implemented")
    }
    
    @BlockingApi
    override fun asSequence(): Sequence<V> {
        TODO("Not yet implemented")
    }
    
    @Api4J
    override fun asStream(): Stream<out V> {
        TODO("Not yet implemented")
    }
}