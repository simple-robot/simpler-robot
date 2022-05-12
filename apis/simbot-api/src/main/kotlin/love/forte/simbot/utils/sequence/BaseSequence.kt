package love.forte.simbot.utils.sequence

import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import love.forte.simbot.InternalSimbotApi
import java.util.stream.Stream

/**
 * 为 [ItemSequence] 和 [ItemFlow] 提供基础的阻塞API抽象。
 *
 * [BaseSequence] 用于为上述两者进行进一步实现，不建议也不应该直接使用当前类型。
 *
 * @see ItemSequence
 * @see ItemFlow
 *
 * @author ForteScarlet
 */
@InternalSimbotApi
public interface BaseSequence<out V> {
    
    
    /**
     * 根据规则 [matcher] 过滤其中的参数并得到过滤后的下游序列。
     */
    @BlockingApi
    public fun filter(matcher: Matcher<V>): BaseSequence<V>
    
    /**
     * 根据转化器 [mapper] 将序列中的元素转化为目标类型 [T] 并得到下游序列。
     */
    @BlockingApi
    public fun <T> map(mapper: Mapper<V, T>): BaseSequence<T>
    
    
    /**
     * 通过 [visitor] 逐一遍历其中的所有元素。
     */
    @BlockingApi
    public fun collect(visitor: Visitor<V>)
    
    /**
     * 将当前序列中的元素收集到目标集合 [destination] 中。
     */
    @BlockingApi
    public fun <C : MutableCollection<in V>> collectTo(destination: C): C
    
    /**
     * 将当前序列中的元素收集并转化为一个 [List]。
     */
    @BlockingApi
    public fun toList(): List<V>
    
    
    /**
     * 将当前序列转化为 [Kotlin Sequence][Sequence] 类型。
     */
    @BlockingApi
    @JvmSynthetic
    public fun asSequence(): Sequence<V>
    
    /**
     * 将当前序列转化为 [Java Stream][Stream] 类型。
     */
    @Api4J
    public fun asStream(): Stream<out V>
    
}