/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.event

import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.message.MessageReceipt

/**
 * 在内部一个跟 [Message][love.forte.simbot.message.Message] 的交互有关的事件。
 *
 * @since 4.11.0
 *
 * @see SendSupportInteractionEvent
 * @see ReplySupportInteractionEvent
 *
 * @see SendSupport
 * @see ReplySupport
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface InternalMessageInteractionEvent : InternalEvent {
    /**
     * 进行消息交互的实体。
     */
    public val content: Any

    /**
     * 进行交互的消息内容，例如
     * [SendSupport.send] 或 [ReplySupport.reply]
     * 调用时传递的参数。
     */
    public val message: InteractionMessage
}

/**
 * 针对消息交互时的内部拦截事件，可以对其中的参数进行修改。
 *
 * 注意：尽可能避免在处理此事件的时候再次进行消息发送，
 * 以避免出现循环导致应用程序资源匮乏或无法正常运行。
 *
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface InternalMessagePreSendEvent :
    InternalInterceptionEvent,
    InternalMessageInteractionEvent {
    /**
     * 最初在消息发送
     * (例如 [SendSupport.send] 或 [ReplySupport.reply] )
     * 调用时传递的参数。
     * 不会因为 [currentMessage] 的变化而改变。
     */
    override val message: InteractionMessage

    /**
     * 可以进行修改的 [InteractionMessage] 内容，会在事件处理完成后被替换为原本的参数。
     *
     * [currentMessage] 的变化不会影响 [message] 的值。
     *
     * 修改不是线程安全的，不要并发地进行修改，也不可以在异步中延迟修改。
     * 如果在 [currentMessage] 已经被使用后仍尝试修改，会抛出 [IllegalStateException]。
     *
     * @throws IllegalStateException 如果在 [currentMessage] 已经被使用后仍尝试修改，会抛出此异常。
     */
    public var currentMessage: InteractionMessage
}

/**
 * 针对消息发送
 * (例如 [SendSupport.send] 或 [ReplySupport.reply] )
 * 成功后的内部通知事件。
 * 会在相关API执行成功后带着它的相关结果进行异步通知。
 *
 * 注意：尽可能避免在处理此事件的时候再次进行消息发送，
 * 以避免出现循环导致应用程序资源匮乏或无法正常运行。
 *
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface InternalMessagePostSendEvent : InternalNotificationEvent, InternalMessageInteractionEvent {
    /**
     * 在消息发送时
     * (例如 [SendSupport.send] 或 [ReplySupport.reply] )
     * 实际传递的参数。
     */
    override val message: InteractionMessage

    /**
     * 消息发送
     * (例如 [SendSupport.send] 或 [ReplySupport.reply] )
     * 成功后的结果。
     */
    public val receipt: MessageReceipt
}
