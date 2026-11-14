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
     * 被调用 send/reply 的对象。回复来源与最终发送目标可能不同。
     * @since 5.0
     */
    public abstract val source: Any

    /**
     * 本次操作确定的不会产生副作用的稳定目标描述类型。由实现者决定内容。
     * @since 5.0
     */
    public abstract val destination: Any?

    /**
     * 按需解析入口目标的实体，无法获取时返回 null。
     * 原生请求替换后仍解析本次操作最初确定的目标。
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
