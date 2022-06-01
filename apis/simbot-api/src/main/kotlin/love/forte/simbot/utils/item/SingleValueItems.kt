package love.forte.simbot.utils.item

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import love.forte.simbot.Api4J
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
}