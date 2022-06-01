package love.forte.simbot.utils.item

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import love.forte.simbot.Api4J
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * [Items] 的基础阻塞实现，通过提供 [Iterator] 的工厂函数实现 [Items] 的基本约定。
 * @author ForteScarlet
 */
public class SimpleBlockingItems<out T>(
    private val iteratorFactory: (Items.PreprocessingProperties) -> Iterator<T>,
) : BaseItems<T, SimpleBlockingItems<T>>() {
    override val self: SimpleBlockingItems<T>
        get() = this
    
    private val iterator: Iterator<T> get() = iteratorFactory(preprocessingProperties)
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val iter = iterator
        for (v in iter) {
            collector(v)
        }
        
        
    }
    
    override fun asFlow(): Flow<T> {
        return iterator.asFlow()
    }
    
    override fun asSequence(): Sequence<T> {
        return iterator.asSequence()
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        val iter = iterator
        return StreamSupport.stream(
            { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
            Spliterator.ORDERED,
            false
        )
    }
}