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
