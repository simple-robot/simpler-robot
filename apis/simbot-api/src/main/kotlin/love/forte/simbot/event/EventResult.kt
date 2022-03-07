/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.event

import kotlinx.coroutines.*
import kotlinx.coroutines.future.*
import love.forte.simbot.*
import love.forte.simbot.event.EventResult.Default.NormalEmpty
import love.forte.simbot.event.EventResult.Default.Truncated
import love.forte.simbot.event.EventResult.Invalid
import java.util.concurrent.*


/**
 *
 * 事件结果, 用于提供有关一次事件处理执行后的响应。
 *
 * [EventResult] 的特殊伴生对象 [Invalid] 可用于拦截器、过滤器中。事件处理器如果遇到了 [Invalid], 则应该直接忽略此值。
 *
 * 事件管理器 [EventListenerContainer]
 *
 * @see Invalid
 *
 * @author ForteScarlet
 */
public interface EventResult {

    /**
     * 此为监听函数所返回的真正内容。
     *
     * ### Reactive API
     *
     * 当 [content] 的返回值为 reactive api相关的内容，且当前 [EventResult] 实例为 [SimpleEventResult] 类型的时候，
     * 处理器应当对 reactive 的相关api对其进行转化收集。这通常使用在Java使用者或者与其他reactive API配合使用的时候。
     *
     * 比如当你的函数返回了 [flux](https://projectreactor.io/docs/core/3.4.1/api/reactor/core/publisher/Flux.html),
     * 那么它将会被收集为 [List] 后重新作为 [content] 构建为 EventResult.
     *
     * 同样的，如果你返回的是 [kotlinx.coroutines.flow.Flow], 也会在函数返回后进行收集。
     *
     * 值得注意的是, 这个行为会在返回值返回后立即执行, 而不是等待所有监听函数执行结束后。
     *
     * 支持的收集类型有：
     * - [kotlinx.coroutines.flow.Flow]
     *
     * - `org.reactivestreams.Publisher`
     * - `reactor.core.publisher.Flux`
     * - `reactor.core.publisher.Mono`
     *
     * - `io.reactivex.CompletableSource`
     * - `io.reactivex.SingleSource`
     * - `io.reactivex.MaybeSource`
     * - `io.reactivex.ObservableSource`
     * - `io.reactivex.Flowable`
     *
     * - `io.reactivex.rxjava3.core.CompletableSource`
     * - `io.reactivex.rxjava3.core.SingleSource`
     * - `io.reactivex.rxjava3.core.MaybeSource`
     * - `io.reactivex.rxjava3.core.ObservableSource`
     * - `io.reactivex.rxjava3.core.Flowable`
     *
     * 但是相对应的，
     *
     *
     * 详情请见 [kotlinx-coroutines-reactive](https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/README.md) .
     */
    public val content: Any?


    /**
     * 是否阻止下一个监听函数的执行。
     *
     * 这只会截断顺序执行的函数，而不会影响异步函数，异步函数也无法通过 [isTruncated] 对后续函数进行截断。
     */
    public val isTruncated: Boolean


    /**
     * 代表着 **无效** 的 [EventResult] 实例。事件处理器不应对此结果进行保留或处理。
     */
    public companion object {

        /**
         * 得到一个无效的特殊默认值。
         */
        @JvmStatic
        public fun invalid(): EventResult = Invalid

        /**
         * 提供一个 [content] 得到一个 [EventResult] 的简易实例。
         *
         * @param content 结果内容。
         * @param
         */
        @JvmOverloads
        @JvmStatic
        public fun of(content: Any? = null, isTruncated: Boolean = false): EventResult = if (content == null) {
            if (isTruncated) Truncated else NormalEmpty
        } else {
            SimpleEventResult(content, isTruncated)
        }


        /**
         * 得到一个异步执行函数的 [AsyncEventResult],
         * 其 [AsyncEventResult.content] 为一个预期返回 [EventResult] 的 [Deferred].
         *
         * @see AsyncEventResult
         */
        @JvmSynthetic
        public fun async(content: Deferred<EventResult>): AsyncEventResult = AsyncEventResult(content)

        /**
         * 根据是否需要阻断后续监听 [isTruncated] 来得到一个默认的 [EventResult] 实例。
         */
        @JvmOverloads
        @JvmStatic
        public fun defaults(isTruncated: Boolean = false): EventResult = of(null, isTruncated)


        /**
         * 返回一个阻断后续监听函数执行的响应体(`EventResult(isTruncated=true)`)。
         */
        @JvmStatic
        public fun truncate(): EventResult = defaults(true)
    }

    /**
     * 代表着 **无效** 的 [EventResult] 实例，是一个具有特殊意义的类型。事件处理器不应对此结果进行保留或处理。
     */
    public object Invalid : EventResult {
        override val content: Any?
            get() = null

        override val isTruncated: Boolean
            get() = false
    }

    /**
     * 默认的 [EventResult] 实现，也是部分常见策略下的结果内容。
     *
     * @see Truncated
     */
    private sealed class Default : EventResult {
        object Truncated : Default() {
            override val content: Any? get() = null
            override val isTruncated: Boolean get() = true
        }

        object NormalEmpty : Default() {
            override val content: Any? get() = null
            override val isTruncated: Boolean get() = false
        }

    }

}

/**
 * [content] 作为 [Deferred] 的异步函数返回值。
 *
 * @property content 异步事件的结果内容。
 */
public open class AsyncEventResult(override val content: Deferred<EventResult>) : EventResult {
    /**
     * 正常情况下，异步任务将不会阻断后续事件的执行。
     */
    override val isTruncated: Boolean get() = false

    /**
     * 将 [content] 转化为 [Future].
     */
    @Api4J
    public fun contentAsFuture(): Future<EventResult> = content.asCompletableFuture()

    /**
     * 等待 [content] 的异步任务响应。
     */
    @JvmSynthetic
    public suspend fun awaitContent(): EventResult = content.await()
}

/**
 * [EventResult] 的基础实现类型，是通过 [EventResult.of] 得到的基本结果类型。
 *
 * [SimpleEventResult.content] 支持收集响应式数据，详情参见 [EventResult.content] 说明。
 *
 * @see EventResult
 * @property content 事件响应内容。支持解析收集响应式数据。
 * @property isTruncated 标记是否截断后续事件的执行。
 */
public open class SimpleEventResult
@JvmOverloads constructor(
    override val content: Any? = null, override val isTruncated: Boolean = false
) : EventResult {

    /**
     * 根据当前的 [content] 和 [isTruncated] 拷贝得到一个新的实例，并且允许提供额外的参数覆盖当前内容。
     */
    @JvmOverloads
    public open fun copy(newContent: Any? = content, newTruncated: Boolean = isTruncated): SimpleEventResult {
        return SimpleEventResult(newContent, newTruncated)
    }

    override fun toString(): String = "SimpleEventResult(content=$content, isTruncated=$isTruncated)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleEventResult) return false

        if (content != other.content) return false
        if (isTruncated != other.isTruncated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + isTruncated.hashCode()
        return result
    }


}


