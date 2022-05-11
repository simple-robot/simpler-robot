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


/**
 *
 * 元素序列。
 *
 * 此序列代表一个可能包含了0到多个连续的元素序列. [ItemSequence] 关心针对内容元素的流式操作与收集，
 * 并尽可能兼顾在Kotlin中的便利性与在Java中的兼容性。
 *
 * [ItemSequence] 旨在消除或降低 [kotlin.sequences.Sequence] 、[java.util.stream.Stream]、 [Collection] （以及 [Java Collection][java.util.Collection]）
 * 之间的部分壁垒，尽可能的兼容它们之间最通用的行为。
 *
 *
 * @author ForteScarlet
 */
public interface ItemSequence<out V> {
    
    /**
     * 根据规则 [matcher] 过滤其中的参数并得到过滤后的下游序列。
     */
    public fun filter(matcher: Matcher<V>): ItemSequence<V>
    
    /**
     * 根据转化器 [mapper] 将序列中的元素转化为目标类型 [T] 并得到下游序列。
     */
    public fun <T> map(mapper: Mapper<V, T>): ItemSequence<T>
    
    
    /**
     * 通过 [visitor] 逐一遍历其中的所有元素。
     */
    public fun collect(visitor: Visitor<V>)
    
    /**
     * 将当前序列中的元素收集到目标集合 [destination] 中。
     */
    public fun <C : MutableCollection<in V>> collectTo(destination: C): C
    
    /**
     * 将当前序列中的元素收集并转化为一个 [List]。
     */
    public fun toList(): List<V>
    
    
    /**
     * 将当前序列转化为 [Kotlin Sequence][Sequence] 类型。
     */
    @JvmSynthetic
    public fun asSequence(): Sequence<V>
    
    /**
     * 将当前序列转化为 [Java Stream][Stream] 类型。
     */
    @Api4J
    public fun asStream(): Stream<out V>

    
    public companion object {
        /**
         * 得到一个空的 [ItemSequence]。
         */
        @JvmStatic
        public fun <V> empty(): ItemSequence<V> = EmptyItemSequence
        
        /**
         * 将 [Sequence] 转化为 [ItemSequence].
         */
        @JvmStatic
        @JvmName("of")
        public fun <V> Sequence<V>.asItemSequence(): ItemSequence<V> = SimpleItemSequence(this)
    
        /**
         * 将 [Collection] 转化为 [ItemSequence].
         */
        @JvmStatic
        @JvmName("of")
        public fun <V> Collection<V>.asItemSequence(): ItemSequence<V> = if (isEmpty()) empty() else CollectionItemSequence(this)
    }
}

