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
import love.forte.simbot.interception.MessageSendInterceptionPoint.invoke
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
 * 组件根据入口需要检查上下文类型，复制方法保留本次操作的来源、原始消息和目标信息。
 * 拦截器内应当避免二次直接调用 send/reply, 它们会进入独立新链，需注意避免递归。
 *
 * 拦截器应尽量直接返回 proceed 的结果，并且应避免伪造回执。根据不同的组件需求不同，
 * 针对 [MessageReceipt] 的类型要求可能不同（例如 QQ 组件要求返回 `QGMessageReceipt`），
 * 擅自随意伪造结果可能会导致组件针对拦截器的最终类型校验失败。
 * 如需短路或替换回执，结果必须符合当前 send/reply 入口承诺的具体回执类型。
 * 组件通过 [invokeWithReceipt] 在整条链返回后校验一次最终类型，不校验中间层或回执真实性。
 * 校验失败时实际发送可能已经完成，调用方不应仅因该异常便直接重试发送。
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
     * 使用当前容器中的注册项执行一次消息发送。
     *
     * @param container 当前 Application 提供给组件的拦截器容器。
     * @param context 本次发送或回复的初始上下文。
     * @param interceptedTarget 实际发送终端，必须使用传入上下文中的当前消息及选项。
     * @see invokeWithReceipt 组件入口承诺具体回执类型时应使用的执行入口。
     * @since 5.0
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
 * 通过 [invoke] 执行完整拦截链，并在成功返回后校验一次最终回执是否符合约定的类型 [R]。
 *
 * @param R 当前 send/reply 入口承诺的回执类型。
 * @param container 当前 Application 提供给组件的拦截器容器。
 * @param context 本次发送或回复的初始上下文。
 * @param interceptedTarget 实际发送逻辑，即被拦截的逻辑。
 * @throws IllegalStateException 最终回执不符合约定的类型 [R] 时。
 * @since 5.0
 */
public suspend inline fun <reified R : MessageReceipt> MessageSendInterceptionPoint.invokeWithReceipt(
    container: InterceptorContainer,
    context: MessageSendContext,
    noinline interceptedTarget: suspend (MessageSendContext) -> R,
): R {
    val receipt = invoke(container, context, interceptedTarget)
    check(receipt is R) {
        "Expected message receipt of type ${R::class.simpleName}, " +
            "but interceptor chain returned ${receipt::class.simpleName}."
    }
    return receipt
}

/**
 * 消息发送操作的拦截器类型。
 *
 * 用于拦截由 [MessageSendInterceptionPoint] 标识的消息发送或回复流程。
 * 拦截器接收 [MessageSendContext] 作为本次发送操作的上下文，并返回 [MessageReceipt] 作为发送结果。
 *
 * 拦截器应尽量直接返回 proceed 的结果，并且应避免伪造回执。
 * 更多约定、警告和说明参考 [MessageSendInterceptionPoint]。
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
 * 应尽量直接返回 proceed 的结果。短路或替换回执时须满足当前组件入口的具体返回类型的约定。
 * 更多约定、警告和说明参考 [MessageSendInterceptionPoint]。
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
