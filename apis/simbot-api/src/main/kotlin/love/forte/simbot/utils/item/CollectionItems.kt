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