/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.kook

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.definition.ChatChannel
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext

/**
 * 代表一个 KOOK 中可以发送消息(有聊天能力的)的频道。
 *
 * @see KookChatChannel
 * @see KookVoiceChannel
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
public interface KookChatCapableChannel : KookChannel, ChatChannel, CoroutineScope {
    /**
     * 源于 bot 的上下文，但是没有 Job。
     */
    override val coroutineContext: CoroutineContext

    override val name: String
        get() = source.name

    /**
     * 此频道的分组。
     *
     * 如果当前频道是属于“顶层分类”的频道（即没有分类、 [source.parentId][love.forte.simbot.kook.objects.Channel.parentId] 为空），
     * 则 [category] 结果为null。
     *
     * @see KookCategory
     */
    override val category: KookCategory?

    // region send api

    /**
     * 根据 [SendChannelMessageApi] api 构建并发送消息。
     */
    @ST
    public suspend fun send(request: SendChannelMessageApi): KookMessageReceipt

    /**
     * 根据 [SendChannelMessageApi] api 构建并发送消息。
     */
    @ST
    public suspend fun send(
        type: Int,
        content: String,
        quote: ID?,
        nonce: String?,
        tempTargetId: ID?,
    ): KookMessageReceipt {
        val request =
            SendChannelMessageApi.create(type, source.id, content, quote?.literal, nonce, tempTargetId?.literal)
        return send(request)
    }


    /**
     * 发送纯文本消息，并指定 [tempTargetId].
     *
     * @see SendChannelMessageApi.tempTargetId
     */
    @ST
    public suspend fun send(text: String, quote: ID? = null, tempTargetId: ID? = null): KookMessageReceipt {
        return send(
            MessageType.TEXT.type,
            text,
            quote,
            null,
            tempTargetId
        )
    }

    /**
     * 发送消息，并可选的指定 [quote] 和 [tempTargetId].
     *
     * @see SendChannelMessageApi.tempTargetId
     * @see SendChannelMessageApi.quote
     */
    @ST
    public suspend fun send(message: Message, quote: ID? = null, tempTargetId: ID? = null): KookMessageReceipt

    /**
     * 发送消息，并可选的指定 [quote] 和 [tempTargetId].
     *
     * @see SendChannelMessageApi.tempTargetId
     * @see SendChannelMessageApi.quote
     */
    @ST
    public suspend fun send(
        message: MessageContent,
        quote: ID? = null,
        tempTargetId: ID? = null,
    ): KookMessageReceipt


    /**
     * 发送纯文本消息。
     */
    @ST
    override suspend fun send(text: String): KookMessageReceipt =
        send(
            MessageType.TEXT.type,
            text,
            null,
            null,
            null
        )

    /**
     * 发送消息。
     */
    @ST
    override suspend fun send(message: Message): KookMessageReceipt =
        send(message, null)

    /**
     * 发送消息。
     */
    @ST
    override suspend fun send(messageContent: MessageContent): KookMessageReceipt =
        send(messageContent, null)

    // endregion
}
