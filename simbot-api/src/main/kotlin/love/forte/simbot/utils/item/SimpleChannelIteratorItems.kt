/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.utils.item

import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 *
 * [Items] 的基础实现，通过一个构建 [ChannelIterator] 的函数实现 [Items] 约定的功能。
 *
 * @author ForteScarlet
 */
public class SimpleChannelIteratorItems<out T>(
    private val channelIteratorFactory: (Items.PreprocessingProperties) -> ChannelIterator<T>,
) : BaseItems<T, SimpleChannelIteratorItems<T>>() {
    override val self: SimpleChannelIteratorItems<T>
        get() = this
    
    private val iterator: ChannelIterator<T> get() = channelIteratorFactory(preprocessingProperties)
    
    /**
     * 收集当前数据序列中的元素. [collectTo] 可能会产生挂起，会直到当前序列中的所有可能产生的元素收集完毕后结束挂起。
     */
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val iter = iterator
        while (iter.hasNext()) {
            collector(iter.next())
        }
    }
    
    /**
     * 将当前元素序列转化为 [Flow] 。
     */
    override fun asFlow(): Flow<T> {
        val iter = iterator
        return flow {
            while (iter.hasNext()) {
                emit(iter.next())
            }
        }
    }
    
    /**
     * 将当前元素序列转化为 [Sequence].
     */
    override fun asSequence(): Sequence<T> {
        val iter = iterator
        return sequence {
            while (runInNoScopeBlocking { iter.hasNext() }) {
                yield(iter.next())
            }
        }
    }
    
    /**
     * 将当前元素序列转化为 [Stream].
     */
    @Api4J
    override fun asStream(): Stream<out T> {
        val iter = BlockingIterator(iterator)
        // see Sequence.asStream
        return StreamSupport.stream(
            { Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED) },
            Spliterator.ORDERED,
            false
        )
    }
    
    
}
