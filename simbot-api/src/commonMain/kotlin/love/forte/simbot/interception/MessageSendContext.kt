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
 * 一次消息发送操作的不可变上下文，分别由 [Send] 和 [Reply] 保留调用来源及选项类型。
 *
 * 可通过 [withMessage] 替换向下游传递的消息。
 *
 * @since 5.0
 */
public sealed class MessageSendContext private constructor(
    /**
     * 执行发送操作的 Bot。
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
     * 创建同一次操作的新上下文，仅替换当前消息。
     */
    public abstract fun withMessage(message: InteractionMessage): MessageSendContext

    /**
     * [SendSupport.send] 的上下文。
     *
     * @param options 本次发送的选项，构造时复制列表以隔离调用方的列表修改。
     */
    public class Send private constructor(
        bot: Bot,
        override val source: SendSupport,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
        /**
         * 当前发送的选项列表。
         */
        public val options: List<SendOption>,
    ) : MessageSendContext(bot, originalMessage, message) {
        /**
         * 创建一次独立发送操作的上下文。
         */
        public constructor(
            bot: Bot,
            source: SendSupport,
            message: InteractionMessage,
            options: List<SendOption> = emptyList(),
        ) : this(bot, source, message, message, options.toList())

        override suspend fun target(): SendSupport = source

        override fun withMessage(message: InteractionMessage): Send =
            Send(bot, source, originalMessage, message, options)

        /**
         * 替换本次发送选项。
         */
        public fun withOptions(options: Collection<SendOption>): Send =
            Send(bot, source, originalMessage, message, options.toList())
    }

    /**
     * [ReplySupport.reply] 的上下文，保留回复来源及延迟解析的最终目标。
     *
     * @param options 本次回复的选项，构造时复制列表。
     */
    public class Reply private constructor(
        bot: Bot,
        override val source: ReplySupport,
        originalMessage: InteractionMessage,
        message: InteractionMessage,
        /**
         * 当前回复的选项列表。
         */
        public val options: List<ReplyOption>,
        /**
         * 延迟解析目标，避免仅为拦截便提前请求组件数据。
         */
        private val resolveTarget: suspend () -> Any?,
    ) : MessageSendContext(bot, originalMessage, message) {
        /**
         * 创建一次独立回复操作的上下文；默认不解析最终目标。
         *
         * @param target 从回复来源解析最终目标，不能确定时返回 null。
         */
        public constructor(
            bot: Bot,
            source: ReplySupport,
            message: InteractionMessage,
            options: List<ReplyOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ) : this(bot, source, message, message, options.toList(), target)

        override suspend fun target(): Any? = resolveTarget()

        override fun withMessage(message: InteractionMessage): Reply =
            Reply(bot, source, originalMessage, message, options, resolveTarget)

        /**
         * 替换本次回复选项。
         */
        public fun withOptions(options: Collection<ReplyOption>): Reply =
            Reply(bot, source, originalMessage, message, options.toList(), resolveTarget)
    }
}
