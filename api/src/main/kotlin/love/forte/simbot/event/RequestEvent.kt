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

import love.forte.simbot.Bot
import love.forte.simbot.action.ActionReceipt
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.event.RequestEvent.AcceptAction.Default as AccDefault
import love.forte.simbot.event.RequestEvent.RejectAction.Default as RejDefault


/**
 *
 * 与 **请求** 有关的事件。
 *
 * 对于一个请求，如果要对其响应，必然
 *
 * @author ForteScarlet
 */
public interface RequestEvent : Event {
    override val metadata: Event.Metadata
    override val bot: Bot

    /**
     * 对于请求事件，可见范围普遍为 [Event.VisibleScope.INTERNAL] 或 [Event.VisibleScope.PRIVATE].
     */
    override val visibleScope: Event.VisibleScope

    /**
     * 是否同意/接受此次请求。
     * 提供一个 [接受行为][AcceptAction] 并得到一个请求行为回执。
     *
     * @see AcceptAction
     */
    public suspend fun accept(action: AcceptAction = AccDefault): RequestActionReceipt

    /**
     * 是否拒绝/回绝此次请求。
     */
    public suspend fun <R> reject(action: RejectAction = RejDefault): RequestActionReceipt


    /**
     * 此行为接口代表在 [RequestEvent] 下所产生的两种行为类型。
     *
     * @see RejectAction
     * @see AcceptAction
     */
    public sealed interface Action : love.forte.simbot.action.Action

    /**
     * 接受的行为。
     * 这个行为本质上没有任何内容，并且提供一个默认实现 [AccDefault].
     *
     * 此行为的实现完全由实现者自行决定，并提供响应的完整说明。
     *
     * 同时，实现者应考虑处理使用默认值 [AccDefault] 的情况。
     *
     * ## 实现者参考
     * 对于一些比较常见的场景可以参考：
     * - 接受后提供一个备注名称
     *
     */
    public interface AcceptAction : Action {
        public companion object Default : AcceptAction
    }


    /**
     * 拒绝的行为。
     * 这个行为本质上没有任何内容，并且提供一个默认实现 [RejDefault].
     *
     * 此行为的实现完全由实现者自行决定，并提供响应的完整说明。
     *
     * 同时，实现者应考虑处理使用默认值 [RejDefault] 的情况。
     *
     * ## 实现者参考
     * 对于一些比较常见的场景可以参考：
     * - 拒绝时提供一个原因说明
     * - 拒绝时将目标标记为 **黑名单**。
     */
    public interface RejectAction : Action {
        public companion object Default : RejectAction
    }


    public companion object Key : BaseEventKey<RequestEvent>("api-request") {
        override fun safeCast(value: Any): RequestEvent? = doSafeCast(value)
    }
}


/**
 * 对于请求操作的一次 **行为回执** 。
 *
 * [RequestActionReceipt] 描述对于本次请求行为的结果, 但是不代表会对**异常**进行捕获。
 *
 *
 * 对于实现者，如果没有任何除 [isSuccess] 以外可提供内容，建议直接使用 [RequestActionReceipt.Success] 或 [RequestActionReceipt.Failure].
 *
 *
 * @see RequestActionReceipt.Success
 * @see RequestActionReceipt.Failure
 */
public interface RequestActionReceipt : ActionReceipt {

    /**
     * 本次行为是否成功。
     */
    override val isSuccess: Boolean


    public object Success : RequestActionReceipt {
        override val isSuccess: Boolean get() = true
    }

    public object Failure : RequestActionReceipt {
        override val isSuccess: Boolean get() = false
    }

}
