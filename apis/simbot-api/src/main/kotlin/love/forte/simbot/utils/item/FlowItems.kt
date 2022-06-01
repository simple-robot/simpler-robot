package love.forte.simbot.utils.item

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.produceIn
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInBlocking
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 *
 * 使用 [Flow] 实现 [Items] 的基本约定。
 *
 * 需要提供一个 [produceScope] 来作为当 flow 转化为 [ChannelIterator] 时的作用域。
 *
 * @author ForteScarlet
 */
public class FlowItems<T>(
    private val produceScope: CoroutineScope,
    private val flowFactory: (Items.PreprocessingProperties) -> Flow<T>,
) :
    BaseItems<T, FlowItems<T>>() {
    override val self: FlowItems<T>
        get() = this
    
    private val flow: Flow<T> get() = flowFactory(preprocessingProperties)
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val flow = flow
        flow.collect { collector(it) }
    }
    
    override fun asFlow(): Flow<T> {
        return flow
    }
    
    @OptIn(FlowPreview::class)
    override fun asSequence(): Sequence<T> {
        val iter = flow.produceIn(produceScope).iterator()
        return sequence {
            while (runInBlocking { iter.hasNext() }) {
                yield(iter.next())
            }
        }
    }
    
    @Api4J
    @OptIn(FlowPreview::class)
    override fun asStream(): Stream<out T> {
        val iter = BlockingIterator(flow.produceIn(produceScope).iterator())
        return StreamSupport.stream(
            { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
            Spliterator.ORDERED,
            false
        )
    }
}