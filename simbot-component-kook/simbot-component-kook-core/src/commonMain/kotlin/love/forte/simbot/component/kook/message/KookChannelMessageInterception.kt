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

package love.forte.simbot.component.kook.message

import love.forte.simbot.ability.SendOption
import love.forte.simbot.bot.Bot
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.kook.api.message.SendChannelMessageApi

/**
 * 频道原生发送请求。拦截器可保留该实例或使用另一个请求替换它。
 *
 * 替换为标准消息时保留原始请求的目标及发送选项，消息类型由替换消息决定。
 */
public class KookChannelRequestMessage(
    /**
     * 完整的原生请求，未被替换时直接执行，不重建或丢弃字段。
     */
    public val request: SendChannelMessageApi,
) : InteractionMessage.Extension()

/**
 * KOOK 按 ID 发送的上下文。
 */
public class KookMessageSendContext private constructor(
    bot: Bot,
    /**
     * 发送方法的接收者。
     */
    override val source: Any,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 本次发送选项的快照。
     */
    override val options: List<SendOption>,
    /**
     * 当前操作的频道、用户或私聊会话目标。
     */
    public val destination: KookMessageSendTarget,
) : MessageSendContext.Send(bot, originalMessage, message) {
    public companion object {
        /**
         * 创建一次 KOOK 按 ID 发送操作，复制调用方的选项列表。
         *
         * @param source 发送方法的接收者，可以是频道对象或待发送消息。
         * @param destination 无需请求平台即可确定的发送目标。
         */
        public fun create(
            bot: KookBot,
            source: Any,
            message: InteractionMessage,
            destination: KookMessageSendTarget,
            options: List<SendOption> = emptyList(),
        ): KookMessageSendContext =
            KookMessageSendContext(bot, source, message, message, options.toList(), destination)
    }

    /**
     * 返回固定目标描述，不请求平台。
     */
    override suspend fun target(): KookMessageSendTarget = destination

    /**
     * 只替换当前消息，保留 KOOK 目标及原始消息。
     */
    override fun withMessage(message: InteractionMessage): KookMessageSendContext =
        KookMessageSendContext(bot, source, originalMessage, message, options, destination)

    /**
     * 复制新选项，保留 KOOK 目标及消息信息。
     */
    override fun withOptions(options: Collection<SendOption>): KookMessageSendContext =
        KookMessageSendContext(bot, source, originalMessage, message, options.toList(), destination)
}

/**
 * KOOK 消息发送目标。
 *
 * 根据具体类型区分频道、用户和私聊会话，无需请求平台。
 */
public sealed class KookMessageSendTarget private constructor() {
    /**
     * 频道 ID、用户 ID 或私聊会话 code。
     */
    public abstract val id: String

    /**
     * 频道 ID目标。
     */
    public class Channel internal constructor(override val id: String) : KookMessageSendTarget() {
        override fun equals(other: Any?): Boolean =
            this === other || other is Channel && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Channel(id=$id)"
    }

    /**
     * 用户 ID目标。
     */
    public class User internal constructor(override val id: String) : KookMessageSendTarget() {
        override fun equals(other: Any?): Boolean =
            this === other || other is User && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "User(id=$id)"
    }

    /**
     * 私聊会话 code目标。
     */
    public class ChatCode internal constructor(override val id: String) : KookMessageSendTarget() {
        override fun equals(other: Any?): Boolean =
            this === other || other is ChatCode && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "ChatCode(id=$id)"
    }

    companion {
        /**
         * 创建频道 ID 目标。
         */
        public fun channel(id: String): Channel = Channel(id)

        /**
         * 创建用户 ID 目标。
         */
        public fun user(id: String): User = User(id)

        /**
         * 创建私聊会话 code 目标。
         */
        public fun chatCode(id: String): ChatCode = ChatCode(id)
    }
}
