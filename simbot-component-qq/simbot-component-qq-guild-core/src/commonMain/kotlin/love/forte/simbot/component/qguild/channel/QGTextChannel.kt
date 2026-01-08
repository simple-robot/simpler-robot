/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.channel

import io.ktor.client.*
import io.ktor.client.request.*
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.definition.ChatChannel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.MessageAuditedException
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.suspendrunner.ST

/**
 * QQ频道中的 **文字子频道**。
 *
 * [QGTextChannel] 是当 [source.type][love.forte.simbot.qguild.model.Channel.type] 为 [ChannelType.TEXT] 类型时的表现。
 *
 *
 * @see QGChannel
 * @see QGGuild
 * @author ForteScarlet
 */
public interface QGTextChannel : QGChannel, ChatChannel {
    override val name: String
        get() = super.name

    override val category: QGCategory

    /**
     * 向子频道发送消息。此频道需要为文字子频道，否则会产生异常。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws UnsupportedOperationException 如果当前子频道类型不是文字子频道
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     */
    @ST
    override suspend fun send(message: Message): QGMessageReceipt

    /**
     * 向子频道发送消息。此频道需要为文字子频道，否则会产生异常。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws UnsupportedOperationException 如果当前子频道类型不是文字子频道
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     */
    @ST
    override suspend fun send(text: String): QGMessageReceipt

    /**
     * 向子频道发送消息。此频道需要为文字子频道，否则会产生异常。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws UnsupportedOperationException 如果当前子频道类型不是文字子频道
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     */
    @ST
    override suspend fun send(messageContent: MessageContent): QGMessageReceipt
}


/**
 * QQ频道中的 **非文字子频道**。
 *
 * 与 [QGTextChannel] 类型相对，[QGNonTextChannel]
 * 用于统一表示那些不支持消息发送的子频道类型。
 *
 * [QGNonTextChannel] 可能会有进一步的子类型实现用来提供更多功能或用以描述它们的类型，
 * 例如：
 * - [QGCategoryChannel]
 * - [QGForumChannel]
 *
 * @see QGTextChannel
 * @see QGChannel
 * @see QGCategoryChannel
 * @see QGForumChannel
 *
 */
public interface QGNonTextChannel : QGChannel
