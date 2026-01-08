/*
 * Copyright (c) 2023-2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.message.QGBaseMessageContent
import love.forte.simbot.component.qguild.message.QGMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.definition.Actor
import love.forte.simbot.event.*
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.qguild.model.Message as QGSourceMessage


/**
 * 针对所有端（QQ群和QQ频道）的 [MessageEvent] 时间的统一父类型。
 * 原本的 [QGMessageEvent] 仅针对频道相关的消息事件，而群聊和私信的消息体类型不同，无法适配。
 *
 * @see QGMessageEvent
 * @see QGC2CMessageCreateEvent
 * @see QGGroupAtMessageCreateEvent
 *
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public sealed class QGBaseMessageEvent<T : Any> : QGBotEvent<T>(), MessageEvent {
    abstract override val bot: QGBot

    /**
     * 消息发生（收到）的时间
     */
    abstract override val time: Timestamp

    /**
     * 接收到的消息内容。
     */
    abstract override val messageContent: QGBaseMessageContent

}

/**
 * [消息事件](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#at-message-create-intents-public-guild-messages)
 * 的组件事件封装。
 *
 * [QGMessageEvent] 作为一个基础事件类型，本身不提供过多的类型约束，参考更具体的实现类型。
 * - [QGAtMessageCreateEvent]
 *
 *
 * @see QGAtMessageCreateEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public sealed class QGMessageEvent : QGBaseMessageEvent<QGSourceMessage>(), ContentEvent, ActorEvent {
    /**
     * 标准库接收到的原始事件内容。
     */
    abstract override val sourceEventEntity: QGSourceMessage

    /**
     * 消息发生（收到）的时间
     */
    abstract override val time: Timestamp

    /**
     * 接收到的消息内容。
     */
    abstract override val messageContent: QGMessageContent

    /**
     * 基于当前事件进行消息回复。
     *
     * 对于公域bot来说，也理应这么回复。[reply] 会默认根据当前事件为当前可能产生的所有消息体填充回复的目标（`msg_id` 或 `event_id`），
     * 并且与 `send` 相比会默认追加一个 [引用效果][Message.messageReference] （如果 [message] 中存在 [自定义reference][love.forte.simbot.component.qguild.message.QGReference]
     * 则在其影响范围内不会自动填充）。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     */
    abstract override suspend fun reply(message: love.forte.simbot.message.Message): QGMessageReceipt

    abstract override suspend fun reply(messageContent: MessageContent): QGMessageReceipt

    abstract override suspend fun reply(text: String): QGMessageReceipt

    /**
     * 接收到消息的事件主体。实现子类型来决定具体类型。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun content(): Actor
}


/**
 * [AT_MESSAGE_CREATE](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#at-message-create-intents-public-guild-messages)
 *
 * 新的@了bot的消息被创建，也就是公域的at消息事件。
 *
 * 发送时机
 * - 用户发送消息，@当前机器人或回复机器人消息时
 *
 * @author ForteScarlet
 */
@STP
public abstract class QGAtMessageCreateEvent : QGMessageEvent(), ChatChannelMessageEvent {
    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.timestamp.toTimestamp()

    override val authorId: ID
        get() = sourceEventEntity.author.id.ID

    /**
     * 发送消息的用户
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun author(): QGMember

    /**
     * 接收到消息的子频道。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun content(): QGTextChannel

    /**
     * 接收到消息的子频道的所属频道服务器。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun source(): QGGuild
}

// TODO 非公域的 Message event
///**
// * 新的频道全量消息被创建。
// *
// * @author ForteScarlet
// */
//@STP
//public abstract class QGMessageCreateEvent : QGMessageEvent(), ChatChannelMessageEvent {
//    @OptIn(ExperimentalQGApi::class)
//    override val time: Timestamp
//        get() = sourceEventEntity.timestamp.toTimestamp()
//
//    override val authorId: ID
//        get() = sourceEventEntity.author.id.ID
//
//    /**
//     * 发送消息的用户
//     *
//     * @throws QQGuildApiException 请求失败，例如无权限
//     * @throws NoSuchElementException 没有找到结果
//     */
//    abstract override suspend fun author(): QGMember
//
//    /**
//     * 接收到消息的子频道。
//     *
//     * @throws QQGuildApiException 请求失败，例如无权限
//     * @throws NoSuchElementException 没有找到结果
//     */
//    abstract override suspend fun content(): QGTextChannel
//
//    /**
//     * 接收到消息的子频道的所属频道服务器。
//     *
//     * @throws QQGuildApiException 请求失败，例如无权限
//     * @throws NoSuchElementException 没有找到结果
//     */
//    abstract override suspend fun source(): QGGuild
//}


// TODO 私聊 Message event
// TODO 消息删除事件
