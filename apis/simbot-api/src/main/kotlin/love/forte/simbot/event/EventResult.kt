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
 *
 */

package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.event.EventResult.Default.NormalEmpty
import love.forte.simbot.event.EventResult.Default.Truncated
import love.forte.simbot.event.EventResult.Invalid
import java.util.concurrent.Future


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
     * 对于此内容，
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
        public fun of(content: Any? = null, isTruncated: Boolean = false): EventResult =
            if (content == null) {
                if (isTruncated) Truncated else NormalEmpty
            } else {
                EventResultImpl(content, isTruncated)
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
        public fun default(isTruncated: Boolean = false): EventResult = of(null, isTruncated)


        /**
         * 返回一个阻断后续监听函数执行的响应体(`EventResult(isTruncated=true)`)。
         */
        @JvmStatic
        public fun truncate(): EventResult = default(true)
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
 */
public open class AsyncEventResult(override val content: Deferred<EventResult>) : EventResult {
    override val isTruncated: Boolean get() = false
    public fun contentAsFuture(): Future<EventResult> = content.asCompletableFuture()
}


private data class EventResultImpl(override val content: Any?, override val isTruncated: Boolean) : EventResult


