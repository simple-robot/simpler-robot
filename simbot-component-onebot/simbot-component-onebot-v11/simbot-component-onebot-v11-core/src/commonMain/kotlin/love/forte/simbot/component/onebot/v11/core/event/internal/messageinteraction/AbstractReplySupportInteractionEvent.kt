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

package love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.StandardReplyOption
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.notifyMessageSent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.segmentsOrNull
import love.forte.simbot.component.onebot.v11.core.internal.message.toReceipt
import love.forte.simbot.component.onebot.v11.core.message.OneBotMessageReplyContext
import love.forte.simbot.component.onebot.v11.core.message.OneBotMessageSendTarget
import love.forte.simbot.component.onebot.v11.core.message.oneBotReplyQuote
import love.forte.simbot.component.onebot.v11.core.utils.resolveInteractionMessage
import love.forte.simbot.component.onebot.v11.core.utils.resolveToOneBotSegmentList
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotReply
import love.forte.simbot.component.onebot.v11.message.segment.OneBotUnknownSegment
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.MessageSendInterceptionPoint
import love.forte.simbot.interception.invokeWithReceipt
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.PlainText

/**
 * 在消息解析和平台请求之前执行一次应用级拦截，保留回复来源。
 */
internal abstract class AbstractReplySupportInteractionEvent : ReplySupport {
    /**
     * 提供当前操作所属的应用级拦截器容器。
     */
    protected abstract val bot: OneBotBotImpl

    /**
     * 仅在拦截器请求目标时获取会话信息。
     */
    protected abstract suspend fun replyTarget(): Any?

    protected abstract val replyDestination: OneBotMessageSendTarget

    /**
     * 提供终端在没有显式引用选项时使用的消息 ID。
     */
    protected open val defaultReplyQuote: ID? get() = null

    /**
     * 执行平台回复。
     */
    protected abstract suspend fun replyText(text: String, reply: ID?): SendMsgResult

    /**
     * 执行平台回复。
     */
    protected abstract suspend fun replySegments(segments: List<OneBotMessageSegment>, reply: ID?): SendMsgResult

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(text: String): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(text))

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(message: Message): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(message))

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(messageContent: MessageContent): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(messageContent))

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(text), options.toList())

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(message: Message, vararg options: ReplyOption): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(message), options.toList())

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 拦截并发送消息。
     * 仅在完整拦截链结束后校验入口要求的回执类型。
     */
    private suspend fun interceptionAndReply(
        message: InteractionMessage,
        options: List<ReplyOption> = emptyList(),
    ): OneBotMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<OneBotMessageReceipt>(
        bot.interceptorContainer,
        OneBotMessageReplyContext.create(
            bot = bot,
            source = this,
            message = message,
            destination = replyDestination,
            options = options,
            target = { replyTarget() },
        ),
    ) { context ->
        val finalOptions = (context as? MessageSendContext.Reply)?.options.orEmpty()
        val explicitQuote = finalOptions.oneBotReplyQuote()
        val noReference = StandardReplyOption.NO_REFERENCE in finalOptions
        require(!noReference || explicitQuote == null) {
            "NO_REFERENCE conflicts with a OneBot reply quote option"
        }
        val quote = if (noReference) null else explicitQuote ?: defaultReplyQuote
        val outgoing = context.message
        val nativeSegments = outgoing.segmentsOrNull

        // 复制收到的消息内容时移除继承的引用，显式消息链与原生段则检查冲突。
        val inheritedContent = nativeSegments == null && (
            outgoing is InteractionMessage.MessageContent ||
                (outgoing as? OneBotSegmentsInteractionMessage)?.message is InteractionMessage.MessageContent
            )

        suspend fun replyResolvedSegments(segments: List<OneBotMessageSegment>): SendMsgResult {
            val outgoingSegments = when {
                !noReference -> segments
                inheritedContent -> segments.filterNot(OneBotMessageSegment::isReplySegment)
                else -> {
                    require(segments.none(OneBotMessageSegment::isReplySegment)) {
                        "NO_REFERENCE conflicts with a OneBot reply message segment"
                    }
                    segments
                }
            }
            return replySegments(outgoingSegments, quote)
        }

        val result = if (nativeSegments != null) {
            replyResolvedSegments(nativeSegments)
        } else {
            resolveInteractionMessage(
                interactionMessage = outgoing,
                onSegments = { replyResolvedSegments(it) },
                onMessage = {
                    if (it is PlainText) {
                        replyText(it.text, quote)
                    } else {
                        replyResolvedSegments(it.resolveToOneBotSegmentList(bot))
                    }
                },
                onText = { replyText(it, quote) },
            )
        }
        result.toReceipt(bot).also { bot.notifyMessageSent(context, it) }
    }
}

/**
 * 识别普通回复段与外部以原生类型提供的回复段。
 */
@OptIn(FragileSimbotAPI::class)
private fun OneBotMessageSegment.isReplySegment(): Boolean =
    this is OneBotReply || (this is OneBotUnknownSegment && type == OneBotReply.TYPE)
