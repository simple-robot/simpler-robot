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

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import love.forte.simbot.InternalSimbotApi
import java.util.stream.Stream


/**
 *
 *
 */
// TODO
@OptIn(InternalSimbotApi::class)
public interface ItemFlow<out V> : ItemSequence<V>, BaseSequence<V> {
    
    /**
     * 根据规则 [matcher] 过滤其中的参数并得到过滤后的下游流。
     */
    @BlockingApi
    override fun filter(matcher: Matcher<V>): ItemFlow<V>
    
    /**
     * 根据规则 [matcher] 过滤其中的参数并得到过滤后的下游流。
     */
    @JvmSynthetic
    public fun filter(matcher: suspend (V) -> Boolean): ItemFlow<V>
    
    /**
     * 根据转化器 [mapper] 将流中的元素转化为目标类型 [T] 并得到下游流。
     */
    @BlockingApi
    override fun <T> map(mapper: Mapper<V, T>): ItemFlow<T>
    
    /**
     * 根据转化器 [mapper] 将流中的元素转化为目标类型 [T] 并得到下游流。
     */
    @JvmSynthetic
    public fun <T> map(mapper: suspend (V) -> T): ItemFlow<T>
    
    
    /**
     * 通过 [visitor] 逐一遍历其中的所有元素。
     */
    @BlockingApi
    override fun collect(visitor: Visitor<V>)
    
    /**
     * 通过 [visitor] 逐一遍历其中的所有元素。
     */
    @JvmSynthetic
    public suspend fun collect(visitor: suspend (V) -> Unit)
    
    /**
     * 将当前流中的元素阻塞的收集到目标集合 [destination] 中。
     */
    @BlockingApi
    override fun <C : MutableCollection<in V>> collectTo(destination: C): C
    
    
    /**
     * 将当前流中的元素收集到目标集合 [destination] 中。
     */
    @JvmSynthetic
    public suspend fun <C : MutableCollection<in V>> collection(destination: C): C
    
    
    /**
     * 将当前流中的元素阻塞的收集到一个 [List] 中。
     *
     * @see collectToList
     */
    @BlockingApi
    override fun toList(): List<V>
    
    /**
     * 将当前流中的元素收集到一个 [List] 中。
     */
    @JvmSynthetic
    public suspend fun collectToList(): List<V>
    
    
    /**
     *  将当前流转化为 [Kotlin Flow][Flow] 类型。
     */
    @JvmSynthetic
    public fun asFlow(): Flow<V>
    
    /**
     * 将当前流转化为内部阻塞的 [Kotlin Sequence][Sequence] 类型。
     */
    @JvmSynthetic
    @BlockingApi
    override fun asSequence(): Sequence<V>
    
    /**
     * 将当前流转化为内部阻塞的 [Java Stream][Stream] 类型。
     */
    @Api4J
    override fun asStream(): Stream<out V>
}


