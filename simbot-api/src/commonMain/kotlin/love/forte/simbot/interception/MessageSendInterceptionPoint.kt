/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.interception

import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.message.MessageReceipt

/**
 * [love.forte.simbot.ability.SendSupport.send] 与 [love.forte.simbot.ability.ReplySupport.reply]
 * 共用的标准消息发送拦截点。
 *
 * 组件实现须主动通过 [invoke] 接入，每次逻辑发送时进入一次且仅只进入一次拦截点。
 *
 * 注意：拦截器每层的 proceed 原则是至多调用一次，但不做运行时强制校验。
 * 重复调用会重新执行相同的后续链及发送终端，可能导致重复发送。
 *
 * 替换上下文可使用 [MessageSendContext.withMessage] 或具体上下文的 withOptions。
 * 约定替换时保留同一次操作的 Bot、调用来源、原始消息及发送或回复类型，不做运行时强制校验。
 * 拦截器内应当避免二次直接调用 send/reply, 它们会进入独立新链，需注意避免递归。
 *
 * 注册示例：
 * ```kotlin
 * application.interceptors {
 *     register(MessageSendInterceptionPoint) { _, context, chain ->
 *         chain.proceed(context.withMessage(InteractionMessage.valueOf("替换后的消息")))
 *     }
 * }
 * ```
 *
 * @since 5.0
 */
public object MessageSendInterceptionPoint : InterceptionPoint<MessageSendContext, MessageReceipt> {
    /**
     * 使用当前容器中的注册项执行一次消息发送，保留惰性读取与注销后放行语义。
     *
     * @param container 当前 Application 提供给组件的拦截器容器。
     * @param context 本次发送或回复的初始上下文。
     * @param interceptedTarget 实际发送终端，必须使用传入上下文中的当前消息及选项。
     */
    public suspend fun invoke(
        container: InterceptorContainer,
        context: MessageSendContext,
        interceptedTarget: suspend (MessageSendContext) -> MessageReceipt,
    ): MessageReceipt {
        val interceptors = container.interceptors(this).map { it.interceptor }.iterator()
        return InterceptorInvoker(this, interceptors, interceptedTarget = interceptedTarget).invoke(context)
    }
}

/**
 * 消息发送操作的拦截器类型。
 *
 * 用于拦截由 [MessageSendInterceptionPoint] 标识的消息发送或回复流程。
 * 拦截器接收 [MessageSendContext] 作为本次发送操作的上下文，并返回 [MessageReceipt] 作为发送结果。
 *
 * 实现者可以在拦截过程中检查或替换向下游传递的消息、继续执行后续拦截链，或直接返回自定义回执以短路后续发送流程。
 *
 * @see Interceptor
 * @see MessageSendContext
 * @see MessageReceipt
 * @see MessageSendInterceptionPoint
 * @see onMessageSend
 *
 * @since 5.0
 */
public typealias MessageSendInterceptor = Interceptor<MessageSendContext, MessageReceipt, MessageSendInterceptionPoint>

/**
 * 为消息发送操作注册一个拦截器。
 *
 * 拦截器与 [MessageSendInterceptionPoint] 关联并接收 [MessageSendContext] 作为拦截上下文。拦截结果为 [MessageReceipt]。
 *
 * @param priority 在消息发送拦截点内执行优先级，默认为 [PriorityConstant.DEFAULT]。
 * @param interceptor 应用于消息发送操作的拦截器逻辑。
 * @return 包含注册信息的注册句柄，允许这个拦截器进行注销或查看它本次的注册 ID。
 * @since 5.0
 */
public fun InterceptorRegistrar.onMessageSend(
    priority: Int = PriorityConstant.DEFAULT,
    interceptor: MessageSendInterceptor
): InterceptorRegistration<MessageSendContext, MessageReceipt, MessageSendInterceptionPoint> = register(
    MessageSendInterceptionPoint,
    priority,
    interceptor
)
