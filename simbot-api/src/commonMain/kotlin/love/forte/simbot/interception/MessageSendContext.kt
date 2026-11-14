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
     */
    public val bot: Bot,
    /**
     * 最初传入发送或回复方法的消息。
     */
    public val originalMessage: InteractionMessage,
    /**
     * 当前传递给下游的消息。
     */
    public val message: InteractionMessage,
) {
    /**
     * 被调用 send/reply 的对象。回复来源与最终发送目标可能不同。
     */
    public abstract val source: Any

    /**
     * 获取最终发送目标，无法获取时返回 null。
     */
    @STP
    public abstract suspend fun target(): Any?

    /**
     * 创建同一次操作的新上下文，替换当前消息。
     */
    public abstract fun withMessage(message: InteractionMessage): MessageSendContext

    /**
     * [SendSupport.send] 或一些与消息发送相关的拦截器的抽象上下文。
     *
     * 组件通过继承此类型扩展发送参数，并实现保持具体类型与附加字段的复制方法。
     *
     * @param originalMessage 本次操作最初的消息。
     */
    public abstract class Send protected constructor(
        bot: Bot,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
    ) : MessageSendContext(bot, originalMessage, message) {
        /**
         * 当前发送选项。
         */
        public abstract val options: List<SendOption>

        public abstract override fun withMessage(message: InteractionMessage): Send

        /**
         * 替换发送选项并复制传入 [options] 列表。
         */
        public abstract fun withOptions(options: Collection<SendOption>): Send
    }

    /**
     * 所有回复 [ReplySupport.reply] 或相关行为的抽象上下文。
     *
     * @param originalMessage 本次操作最初的消息。
     */
    public abstract class Reply protected constructor(
        bot: Bot,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
    ) : MessageSendContext(bot, originalMessage, message) {
        /**
         * 当前回复选项。
         */
        public abstract val options: List<ReplyOption>

        public abstract override fun withMessage(message: InteractionMessage): Reply

        /**
         * 替换回复选项并复制传入 [options] 列表。
         */
        public abstract fun withOptions(options: Collection<ReplyOption>): Reply
    }

    // TODO 仅过渡用

    /**
     * 无额外组件字段时使用的默认发送上下文。
     *
     * 标准发送的来源通常为 [SendSupport]，直发也可使用 Bot 或消息对象。
     * 目标独立于来源，初始化和复制均不触发目标解析。
     */
    public class DefaultSend private constructor(
        bot: Bot,
        override val source: Any,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
        override val options: List<SendOption>,
        private val resolveTarget: suspend () -> Any?,
    ) : Send(bot, originalMessage, message) {
        /**
         * 创建发送操作并复制选项列表，默认以方法接收者为目标。
         *
         * @param source 被调用发送方法的接收者。
         * @param target 按需解析目标实体或组件目标描述。
         */
        public constructor(
            bot: Bot,
            source: Any,
            message: InteractionMessage,
            options: List<SendOption> = emptyList(),
            target: suspend () -> Any? = { source },
        ) : this(bot, source, message, message, options.toList(), target)

        override suspend fun target(): Any? = resolveTarget()

        override fun withMessage(message: InteractionMessage): DefaultSend =
            DefaultSend(bot, source, originalMessage, message, options, resolveTarget)

        override fun withOptions(options: Collection<SendOption>): DefaultSend =
            DefaultSend(bot, source, originalMessage, message, options.toList(), resolveTarget)
    }

    /**
     * 无额外组件字段时使用的默认回复上下文。
     *
     * 标准回复的来源通常为 [ReplySupport]；最终目标按需解析。
     */
    public class DefaultReply private constructor(
        bot: Bot,
        override val source: Any,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
        override val options: List<ReplyOption>,
        private val resolveTarget: suspend () -> Any?,
    ) : Reply(bot, originalMessage, message) {
        /**
         * 创建回复操作并复制选项列表，默认不解析目标。
         *
         * @param source 被调用回复方法的接收者。
         * @param target 按需解析最终目标，无法确定时返回 null。
         */
        public constructor(
            bot: Bot,
            source: Any,
            message: InteractionMessage,
            options: List<ReplyOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ) : this(bot, source, message, message, options.toList(), target)

        override suspend fun target(): Any? = resolveTarget()

        override fun withMessage(message: InteractionMessage): DefaultReply =
            DefaultReply(bot, source, originalMessage, message, options, resolveTarget)

        override fun withOptions(options: Collection<ReplyOption>): DefaultReply =
            DefaultReply(bot, source, originalMessage, message, options.toList(), resolveTarget)
    }
}
