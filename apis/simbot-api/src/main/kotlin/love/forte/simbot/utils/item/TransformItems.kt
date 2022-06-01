package love.forte.simbot.utils.item

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream

/**
 *
 * 允许为一个 [Items] 提供一个转化函数的 [Items] 代理实现。
 *
 * @author ForteScarlet
 */
public class TransformItems<B, T>(
    private val baseItems: Items<B>,
    private val transform: suspend (B) -> T,
) : Items<T> {
    override fun limit(count: Int): Items<T> {
        baseItems.limit(count)
        return this
    }
    
    override fun offset(count: Int): Items<T> {
        baseItems.offset(count)
        return this
    }
    
    override fun batch(size: Int): Items<T> {
        baseItems.batch(size)
        return this
    }
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        baseItems.collect { collector(transform(it)) }
    }
    
    override fun asFlow(): Flow<T> {
        return baseItems.asFlow().map(transform)
    }
    
    override fun asSequence(): Sequence<T> {
        return baseItems.asSequence().map { runInBlocking { transform(it) } }
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return baseItems.asStream().map { runInBlocking { transform(it) } }
    }
}