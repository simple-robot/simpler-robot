package love.forte.simbot.utils.item

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import love.forte.simbot.Api4J
import love.forte.simbot.Limiter
import love.forte.simbot.Limiter.ZERO.limit
import love.forte.simbot.Limiter.ZERO.offset
import love.forte.simbot.utils.runInBlocking
import love.forte.simbot.utils.runWithInterruptible
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 *
 * [Items] 是一个数据预处理序列，_类似于_ [java.util.stream.Stream] 或 [kotlinx.coroutines.flow.Flow]，
 * 但是不完全相同。
 *
 * 在使用 [items] 的预处理参数时，你不应去重复使用已经被操作过的过时实例。
 *
 * ## 预处理
 *
 * [Items] 中存在部分 **预处理** 函数：[limit][Items.limit]、[offset][Items.offset]、[batch][Items.batch]。
 *
 * 这些预处理函数的作用可能是单纯的记录参数，如果重复调用会覆盖上一个值而不是真正的流式处理。因此对于这些预处理函数，
 * 如果有需要则应当只调用一次。
 *
 * 这些预处理函数不同于其他流API中的类似api，在simbot中，组件的实现**可能**会根据这些预处理参数进行一定的优化。
 * 当一个组件底层的api原生的支持精准的分页、分流时，这些参数便会派上用场了。同理，如果底层api无法直接获取全部，
 * 需要分批次获取的时候，[batch] 则会有效影响到实际的批次数量 ———— 这些都是普通的流API无法满足的。
 *
 * 为了避免实现细节所产生的逻辑差异，对于 **预处理** 函数，同一个函数你应当至多只调用1次。
 *
 * ## 转化
 *
 * [Items] 中存在部分 **转化** 函数：[asFlow][Items.asFlow]、[asSequence][Items.asSequence]、[asStream][Items.asStream]。
 *
 * **转化** 函数是一种 _终结_ 函数，它会将当前预处理信息的瞬时信息提供给内部的构建器，并得到一个真正的序列对象。
 *
 * 当转化函数被调用的时候，内部的流实例才会根据上述的预处理api真正的被构建。在调用转化api之后，你不应再操作预处理函数了，
 * 因为这将不再影响到转化后的结果。
 *
 * ## 收集
 *
 * [Items] 中存在部分 **收集** 函数：[Items.collect] 、[Items.collectToList] 等。
 *
 * **收集** 函数是一种 _终结_ 函数，它会将当前预处理信息的瞬时信息提供给内部的构建器，并进行真正的处理逻辑。
 *
 * 与上述 **转化** 函数类似，收集函数被执行时才会根据预处理参数去真正的构建流实例并进行收集操作。
 * 因此在调用了 **收集** 函数后，你不应再对之前的 [Items] 实例进行任何操作。
 *
 *
 * @author ForteScarlet
 */
public interface Items<out T> {
    
    /**
     * 数据限流。取得的数据条数的最大上限。当 [count] > 0 时有效。
     *
     * ## 预处理
     * [limit] 是一个 **预处理** 函数，
     * 不同于 [Flow.take] 、 [Sequence.take] 或 [Stream.limit]。
     * 当配置 [limit] 的时候，不一定会有任何真正的上述序列类型产生，
     * 有可能只是记录了一个 `limit` 值并准备于转换或最终收集函数中使用。
     *
     * 为了避免实现细节所产生的逻辑差异，对于 **预处理** 函数，同一个函数你应当至多只调用1次。
     *
     * ```kotlin
     * items.limit(5).limit(10) // 可能的最终limit值：10
     * ```
     *
     *
     * 有关预处理api的描述参考 [Items] 类注释。
     *
     */
    public fun limit(count: Int): Items<T>
    
    
    /**
     * 数据偏移。从 [offset] 数量之后的数据后开始获取。当 [offset] > 0 时有效。
     *
     * ## 预处理
     * [offset] 是一个 **预处理** 函数，
     * 不同于 [Flow.drop] 、 [Sequence.drop] 或 [Stream.skip]。
     * 当配置 [offset] 的时候，不一定会有任何真正的上述序列类型产生，
     * 有可能只是记录了一个 `offset` 值并准备于转换或最终收集函数中使用。
     *
     * 为了避免实现细节所产生的逻辑差异，对于 **预处理** 函数，同一个函数你应当至多只调用1次。
     *
     * ```kotlin
     * limit.offset(10).offset(5) // 可能的最终offset值：5
     * ```
     *
     * 有关预处理api的描述参考 [Items] 类注释。
     */
    public fun offset(count: Int): Items<T>
    
    
    /**
     * 批次大小。如果支持批次获取的话，则每批次获取 [size] 的元素数量。通常 [size] > 0 时有效。
     *
     * ## 预处理
     * [batch] 是一个 **预处理** 函数。
     * 当配置 [batch] 的时候，不一定会有任何真正的上述序列类型产生，
     * 有可能只是记录了一个 `batch` 值并准备于转换或最终收集函数中使用。
     *
     * 为了避免实现细节所产生的逻辑差异，对于 **预处理** 函数，同一个函数你应当至多只调用1次。
     *
     * ```kotlin
     * limit.batch(15).batch(25) // 可能的最终batch值：25
     * ```
     */
    public fun batch(size: Int): Items<T>
    
    
    /**
     * 收集当前数据序列中的元素. [collect] 可能会产生挂起，会直到当前序列中的所有可能产生的元素收集完毕后结束挂起。
     */
    @JvmSynthetic
    public suspend fun collect(collector: suspend (T) -> Unit)
    
    /**
     * 将当前元素序列转化为 [Flow].
     */
    @JvmSynthetic
    public fun asFlow(): Flow<T>
    
    /**
     * 将当前元素序列转化为 [Sequence].
     */
    @JvmSynthetic
    public fun asSequence(): Sequence<T>
    
    /**
     * 将当前元素序列转化为 [Stream].
     */
    @Api4J
    public fun asStream(): Stream<out T>
    
    /**
     * 阻塞的收集当前序列中的元素。
     *
     * @throws CancellationException 当被中断时
     */
    @Api4J
    public fun collect(collector: Consumer<in T>) {
        runInBlocking { collect { runWithInterruptible { collector.accept(it) } } }
    }
    
    
    /**
     * 阻塞的收集当前序列中的元素到目标 [collector] 中。
     *
     * @throws CancellationException 当被中断时
     */
    @Api4J
    public fun <C : MutableCollection<in T>> collect(collector: C): C {
        runInBlocking { collect { collector.add(it) } }
        return collector
    }
    
    /**
     * 阻塞的收集当前序列中的元素到列表中。
     *
     * @throws CancellationException 当被中断时
     */
    @Api4J
    public fun collectToList(): List<T> {
        return collect(mutableListOf())
    }
    
    
    /**
     * [Items] 中通过预处理函数所配置的预处理值。
     * 如果值为 `-1` 则代表未配置。
     *
     */
    public data class PreprocessingProperties internal constructor(
        val limit: Int,
        val offset: Int,
        val batch: Int,
    )
    
    
    public companion object {
        /**
         * 得到内容为空的 [Items] 实例。
         */
        @JvmStatic
        public fun <T> emptyItems(): Items<T> = EmptyItems
        
        /**
         * 通过迭代器构建 [Items] 实例。
         */
        @JvmStatic
        @JvmName("of")
        public fun <T> blockingItemsBy(factory: (PreprocessingProperties) -> Iterator<T>): Items<T> {
            return SimpleBlockingItems(factory)
        }
        
        /**
         * 使用 [List] 构建为 [Items]
         */
        @JvmStatic
        @JvmName("of")
        public fun <T> Collection<T>.asItems(): Items<T> {
            return CollectionItems(this)
        }
   
        
        /**
         * 转化一个 [Items] 中的元素类型并得到新的 [Items] 实例。
         */
        @JvmStatic
        @JvmName("transform")
        public fun <B, T> Items<B>.transformBlocking(transform: (B) -> T): Items<T> {
            return transform { runWithInterruptible { transform(it) } }
        }
    }
}

/**
 * 将 [Items.PreprocessingProperties] 作用于目标 [Flow] 中。
 */
@Suppress("DuplicatedCode")
public fun <E> Items.PreprocessingProperties.effectOn(flow: Flow<E>): Flow<E> {
    val offset = offset
    val limit = limit
    
    return flow.let { f ->
        if (offset > 0) f.drop(offset) else f
    }.let { f ->
        if (limit > 0) f.take(limit) else f
    }
}

/**
 * 将 [Items.PreprocessingProperties] 作用于目标 [Sequence] 中。
 */
@Suppress("DuplicatedCode")
public fun <E> Items.PreprocessingProperties.effectOn(sequence: Sequence<E>): Sequence<E> {
    val offset = offset
    val limit = limit
    
    return sequence.let { s ->
        if (offset > 0) s.drop(offset) else s
    }.let { s ->
        if (limit > 0) s.take(limit) else s
    }
}

/**
 * 将 [Items.PreprocessingProperties] 作用于目标 [Stream] 中。
 */
public fun <E> Items.PreprocessingProperties.effectOn(stream: Stream<E>): Stream<E> {
    val offset = offset
    val limit = limit
    
    return stream.let { s ->
        if (offset > 0) s.skip(offset.toLong()) else s
    }.let { s ->
        if (limit > 0) s.limit(limit.toLong()) else s
    }
}


/**
 * 通过 [Limiter] 对 [Items] 的预处理参数进行处理。
 *
 */
public fun <T> Items<T>.withLimiter(limiter: Limiter): Items<T> {
    return limit(limiter.limit).offset(limiter.offset).batch(limiter.batchSize)
}


private object EmptyItems : Items<Nothing> {
    override fun limit(count: Int): Items<Nothing> {
        return this
    }
    
    override fun offset(count: Int): Items<Nothing> {
        return this
    }
    
    override fun batch(size: Int): Items<Nothing> {
        return this
    }
    
    override suspend fun collect(collector: suspend (Nothing) -> Unit) {
        // do nothing
    }
    
    override fun asFlow(): Flow<Nothing> {
        return emptyFlow()
    }
    
    override fun asSequence(): Sequence<Nothing> {
        return emptySequence()
    }
    
    @Api4J
    override fun asStream(): Stream<out Nothing> {
        return Stream.empty()
    }
}


/**
 * 提供构建 [ChannelIterator] 的函数来构建一个 [Items].
 */
@JvmSynthetic
public fun <T> itemsBy(factory: (Items.PreprocessingProperties) -> ChannelIterator<T>): Items<T> {
    return SimpleItems(factory)
}


/**
 * 在一个协程作用域环境下通过 [produce] 构建一个 [Items].
 */
@OptIn(ExperimentalCoroutinesApi::class)
public inline fun <T> CoroutineScope.items(
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = 0,
    crossinline block: suspend ProducerScope<T>.(Items.PreprocessingProperties) -> Unit,
): Items<T> {
    return itemsBy { pre ->
        produce(context, capacity) {
            block(pre)
        }.iterator()
    }
}

/**
 * 提供 [CoroutineScope] 和构建 [Flow] 的函数 [flowFactory] 来构建 [Items].
 */
public inline fun <T> CoroutineScope.items(crossinline flowFactory: (Items.PreprocessingProperties) -> Flow<T>): Items<T> {
    return FlowItems(this) {
        flowFactory(it)
    }
}

/**
 * 以构建 flow 的方式构建 [Items].
 */
public inline fun <T> CoroutineScope.items(crossinline block: suspend FlowCollector<T>.(Items.PreprocessingProperties) -> Unit): Items<T> {
    return items(flowFactory = { pre ->
        flow {
            block(pre)
        }
    })
}


/**
 * 将 [Items] 中的元素收集为目标集合 [C].
 *
 * @see Items.toList
 */
public suspend inline fun <T, C : MutableCollection<T>> Items<T>.toCollection(collection: C): C {
    collect { collection.add(it) }
    return collection
}


/**
 * 将 [Items] 中的元素收集为 List.
 *
 * @see Items.toCollection
 */
public suspend inline fun <T> Items<T>.toList(): List<T> {
    return toCollection(mutableListOf())
}


/**
 * 转化一个 [Items] 中的元素类型并得到新的 [Items] 实例。
 */
@JvmSynthetic
public fun <B, T> Items<B>.transform(transform: suspend (B) -> T): Items<T> {
    return TransformItems(this, transform)
}

/**
 * 转化一个 [Items] 中的元素类型并得到新的 [Items] 实例。
 *
 * @see Items.transform
 *
 */
public inline fun <B, T> Items<B>.map(crossinline transform: suspend (B) -> T): Items<T> {
    return transform { transform(it) }
}


internal class BlockingIterator<out T>(private val iterator: ChannelIterator<T>) : Iterator<T> {
    override fun hasNext(): Boolean {
        return runInBlocking { iterator.hasNext() }
    }
    
    override fun next(): T {
        return iterator.next()
    }
}