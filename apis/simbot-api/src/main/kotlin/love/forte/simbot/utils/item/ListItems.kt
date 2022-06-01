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
public class ListItems<T>(private val list: List<T>) : BaseItems<T, ListItems<T>>() {
    
    override val self: ListItems<T>
        get() = this
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        if (list.isEmpty()) {
            return
        }
        
        val (limit, offset, _) = preprocessingProperties
        if (offset >= list.size) return
        
        val iter = if (offset > 0) list.listIterator(offset) else list.listIterator()
        
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
        return preprocessingProperties.effectOn(list.asFlow())
    }
    
    override fun asSequence(): Sequence<T> {
        return preprocessingProperties.effectOn(list.asSequence())
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return preprocessingProperties.effectOn(list.stream())
    }
}