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
public class SimpleIteratorItems<out T>(
    private val iteratorFactory: (Items.PreprocessingProperties) -> Iterator<T>,
) : BaseItems<T, SimpleIteratorItems<T>>() {
    override val self: SimpleIteratorItems<T>
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
