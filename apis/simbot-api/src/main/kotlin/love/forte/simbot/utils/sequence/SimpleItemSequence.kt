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

package love.forte.simbot.utils.sequence

import love.forte.simbot.Api4J
import java.util.stream.Stream
import kotlin.streams.asStream


// TODO

/**
 *
 * 使用 [Sequence] 直接对 [ItemSequence] 进行实现的基础实现类。
 *
 * @author ForteScarlet
 */
internal class SimpleItemSequence<out V>(private var sequence: Sequence<V>) : ItemSequence<V> {
    override fun filter(matcher: Matcher<V>): ItemSequence<V> {
        sequence = sequence.filter(matcher)
        return this
    }
    
    override fun <T> map(mapper: Mapper<V, T>): ItemSequence<T> {
        return SimpleItemSequence(sequence.map(mapper))
    }
    
    override fun collect(visitor: Visitor<V>) {
        sequence.forEach { visitor(it) }
    }
    
    override fun <C : MutableCollection<in V>> collectTo(destination: C): C {
        return sequence.toCollection(destination)
    }
    
    override fun toList(): List<V> = collectTo(mutableListOf())
    
    override fun asSequence(): Sequence<V> = sequence
    
    @Api4J
    override fun asStream(): Stream<out V> = sequence.asStream()
}