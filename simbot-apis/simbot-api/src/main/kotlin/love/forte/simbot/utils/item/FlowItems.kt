/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

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