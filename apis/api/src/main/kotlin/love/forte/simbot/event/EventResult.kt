/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import love.forte.simbot.event.EventResult.Invalid


/**
 *
 * 事件结果, 用于提供有关一次事件处理执行后的响应。
 *
 * [EventResult] 的特殊伴生对象 [Invalid] 可用于拦截器、过滤器中。事件处理器如果遇到了 [Invalid], 则应该直接忽略此值。
 *
 * 事件管理器 [EventListenerContainer]
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
         */
        @JvmOverloads
        @JvmStatic
        public fun of(content: Any? = null, blockNext: Boolean = false): EventResult =
            EventResultImpl(content, blockNext)


        @JvmSynthetic
        public fun async(content: Deferred<Any?>): AsyncEventResult = AsyncEventResult(content)

    }

    /**
     * 代表着 **无效** 的 [EventResult] 实例。事件处理器不应对此结果进行保留或处理。
     */
    public object Invalid : EventResult {
        override val content: Any?
            get() = null

        override val isTruncated: Boolean
            get() = false
    }

}

/**
 * [content] 作为 [Deferred] 的异步函数返回值。
 *
 */
public open class AsyncEventResult(override val content: Deferred<Any?>) : EventResult {
    override val isTruncated: Boolean get() = false
}



private data class EventResultImpl(override val content: Any?, override val isTruncated: Boolean) : EventResult


