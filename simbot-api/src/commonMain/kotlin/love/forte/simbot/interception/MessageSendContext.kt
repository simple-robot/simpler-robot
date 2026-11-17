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

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.suspendrunner.STP

/**
 * 一次消息发送或回复的不可变上下文，分别通过 [Send] 和 [Reply] 扩展。
 *
 * @since 5.0
 */
public sealed class MessageSendContext private constructor(
    /**
     * 执行本次操作的 Bot。
     * @since 5.0
     */
    public open val bot: Bot,
    /**
     * 最初传入发送或回复方法的消息。
     * @since 5.0
     */
    public open val originalMessage: InteractionMessage,
    /**
     * 当前传递给下游的消息。
     * @since 5.0
     */
    public open val message: InteractionMessage,
) {
    /**
     * 发起本次发送或回复的调用来源。
     *
     * 通常是调用发送或回复方法时使用的对象，例如 [SendSupport] 实体或 [ReplySupport] 事件。
     * 组件也可能提供 [Bot] 按 ID 发送或消息扩展发送等入口，具体类型由组件和入口决定。
     * 回复时来源与 [target] 解析的目标实体可能不同。
     * @since 5.0
     */
    public abstract val source: Any

    /**
     * 本次操作入口已确定的稳定的、没有副作用的目标描述。
     *
     * 描述内容和类型由实现者决定，例如目标类别及其 ID。无法提供描述时可以为 null。
     * 通过 [withMessage] 或具体上下文替换选项时保留此描述。组件允许替换原生请求时，实际发送目标可能随请求改变，
     * 但此属性仍描述最初已确定的目标。
     * @since 5.0
     */
    public abstract val destination: Any?

    /**
     * 按需解析本次操作入口确定的目标实体，无法获取时返回 null。
     *
     * 实体的类型和解析方式由实现者决定，可能与 [source] 不同。解析可能执行挂起操作、
     * 查询平台或抛出异常，调用者应结合组件实现的具体说明处理。
     * @since 5.0
     */
    @STP
    public abstract suspend fun target(): Any?

    /**
     * 创建同一次操作的新上下文，替换当前消息。
     * 保留具体类型、原始消息、选项、来源和目标解析方式。
     * @since 5.0
     */
    public abstract fun withMessage(message: InteractionMessage): MessageSendContext

    /**
     * [SendSupport.send] 或一些与消息发送相关的拦截器的抽象上下文。
     *
     * 组件通过继承此类型扩展发送参数，并实现保持具体类型与附加字段的复制方法。
     *
     * @param originalMessage 本次操作最初的消息。
     * @since 5.0
     */
    public abstract class Send protected constructor(
        bot: Bot,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
    ) : MessageSendContext(bot, originalMessage, message) {
        /**
         * 当前发送选项。
         * @since 5.0
         */
        public abstract val options: List<SendOption>

        public abstract override fun withMessage(message: InteractionMessage): Send

        /**
         * 替换发送选项并复制传入 [options] 列表。
         * @since 5.0
         */
        public abstract fun withOptions(options: Collection<SendOption>): Send
    }

    /**
     * 所有回复 [ReplySupport.reply] 或相关行为的抽象上下文。
     *
     * @param originalMessage 本次操作最初的消息。
     * @since 5.0
     */
    public abstract class Reply protected constructor(
        bot: Bot,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
    ) : MessageSendContext(bot, originalMessage, message) {
        /**
         * 当前回复选项。
         * @since 5.0
         */
        public abstract val options: List<ReplyOption>

        public abstract override fun withMessage(message: InteractionMessage): Reply

        /**
         * 替换回复选项并复制传入 [options] 列表。
         * @since 5.0
         */
        public abstract fun withOptions(options: Collection<ReplyOption>): Reply
    }

}
