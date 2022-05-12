package love.forte.simbot.utils.sequence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import java.util.stream.Stream

/**
 * 没有实际元素的 [ItemFlow] 实现.
 *
 * @author ForteScarlet
 */
public object EmptyItemFlow : ItemFlow<Nothing> {
    @OptIn(BlockingApi::class)
    override fun filter(matcher: Matcher<Nothing>): ItemFlow<Nothing> = this
    
    override fun filter(matcher: suspend (Nothing) -> Boolean): ItemFlow<Nothing> = this
    
    @OptIn(BlockingApi::class)
    override fun <T> map(mapper: Mapper<Nothing, T>): ItemFlow<T> = this
    
    override fun <T> map(mapper: suspend (Nothing) -> T): ItemFlow<T> = this
    
    @OptIn(BlockingApi::class)
    override fun collect(visitor: Visitor<Nothing>) {
    }
    
    override suspend fun collect(visitor: suspend (Nothing) -> Unit) {
    }
    
    @OptIn(BlockingApi::class)
    override fun <C : MutableCollection<in Nothing>> collectTo(destination: C): C = destination
    
    override suspend fun <C : MutableCollection<in Nothing>> collection(destination: C): C = destination
    
    @OptIn(BlockingApi::class)
    override fun toList(): List<Nothing> = emptyList()
    
    override suspend fun collectToList(): List<Nothing> = emptyList()
    
    override fun asFlow(): Flow<Nothing> = emptyFlow()
    
    @OptIn(BlockingApi::class)
    override fun asSequence(): Sequence<Nothing> = emptySequence()
    
    @Api4J
    override fun asStream(): Stream<out Nothing> = Stream.empty()
}
