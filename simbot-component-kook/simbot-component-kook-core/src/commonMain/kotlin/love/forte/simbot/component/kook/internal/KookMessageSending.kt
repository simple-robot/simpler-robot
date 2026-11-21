/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.internal

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.ability.StandardReplyOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.kook.KookChatCapableChannel
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.event.KookMessageSentEvent
import love.forte.simbot.component.kook.message.*
import love.forte.simbot.component.kook.message.KookMessageCreatedReceipt.Companion.asReceipt
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.event.onEachError
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.MessageSendInterceptionPoint
import love.forte.simbot.interception.invokeWithReceipt
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import love.forte.simbot.kook.api.message.SendDirectMessageApi
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReference
import love.forte.simbot.message.Messages
import love.forte.simbot.message.toMessages

/**
 * 在组件能力入口进入一次拦截链，内部委托直接使用发送。
 * 发送终端保留回执，完整拦截链返回后才按入口声明的 [KookMessageReceipt] 校验一次。
 */
internal suspend inline fun KookBotImpl.interceptMessageSend(
    context: MessageSendContext,
    crossinline send: suspend (MessageSendContext) -> KookMessageReceipt,
): KookMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<KookMessageReceipt>(
    interceptorContainer,
    context
) { currentContext ->
    val receipt = send(currentContext)
    notifyMessageSent(currentContext, receipt)
    receipt
}

/**
 * 将平台实际发送的结果异步投递。
 */
internal fun KookBotImpl.notifyMessageSent(context: MessageSendContext, receipt: KookMessageReceipt) {
    val event = KookMessageSentEventImpl(context, receipt)
    launch {
        eventProcessor.push(event)
            .onEachError { logger.error("MessageSentEvent listener failed", it.content) }
            .collect()
    }.invokeOnCompletion { cause ->
        logger.error("MessageSentEvent dispatch failed", cause)
    }
}


private class KookMessageSentEventImpl(
    override val context: MessageSendContext,
    override val receipt: KookMessageReceipt,
) : KookMessageSentEvent() {
    override val id: ID = UUID.random()
    override val time: Timestamp = Timestamp.now()
}

/**
 * 将拦截后的消息发送到频道，并保留回复引用与临时消息目标。
 */
internal suspend fun KookChatCapableChannel.sendChannelMessage(
    message: InteractionMessage,
    quote: String? = null,
    defaultTempTargetId: String? = null,
    tempTargetId: String? = null,
    nonce: String? = null,
    templateId: String? = null,
    targetId: String = source.id,
    noReference: Boolean = false,
): KookMessageReceipt = when (message) {
    is InteractionMessage.Text -> SendChannelMessageApi.create(
        MessageType.TEXT.type,
        targetId,
        message.text,
        quote,
        nonce,
        tempTargetId,
        templateId,
    ).requestDataBy(bot).asReceipt(false, bot)

    is InteractionMessage.Message -> {
        if (noReference) {
            message.message.checkNoReference()
        }
        message.message.sendToChannelDirect(
            bot,
            targetId = targetId,
            quote = quote,
            nonce = nonce,
            tempTargetId = tempTargetId,
            defaultTempTargetId = defaultTempTargetId,
            templateId = templateId,
        ) ?: throw IllegalArgumentException("Valid messages must not be empty.")
    }

    is InteractionMessage.MessageContent -> when (val content = message.messageContent) {
        is KookReceiveMessageContent -> SendChannelMessageApi.create(
            content.source.type?.value,
            targetId,
            content.source.content,
            quote,
            nonce,
            tempTargetId,
            templateId,
        ).requestDataBy(bot).asReceipt(false, bot)

        is KookChannelMessageDetailsContent -> SendChannelMessageApi.create(
            content.details.type,
            targetId,
            content.details.content,
            quote ?: content.details.quote?.id?.takeUnless { noReference },
            nonce,
            tempTargetId,
            templateId,
        ).requestDataBy(bot).asReceipt(false, bot)

        else -> sendChannelMessage(
            InteractionMessage.valueOf(content.messages.forReply(noReference)),
            quote,
            defaultTempTargetId,
            tempTargetId,
            nonce,
            templateId,
            targetId,
            noReference,
        )
    }

    is KookChannelRequestMessage -> message.request.withOptions(
        quote,
        nonce,
        tempTargetId,
        templateId
    ).requestDataBy(bot).asReceipt(false, bot)

    is InteractionMessage.Extension -> throw UnsupportedOperationException("Unsupported KOOK message: $message")
}

/**
 * 直接发送到用户
 */
internal suspend fun sendDirectMessage(
    bot: KookBot,
    targetId: String,
    message: InteractionMessage,
    quote: String? = null,
    nonce: String? = null,
    noReference: Boolean = false,
): KookMessageReceipt = when (message) {
    is InteractionMessage.Text -> SendDirectMessageApi.createByTargetId(
        targetId,
        message.text,
        MessageType.TEXT.type,
        quote,
        nonce,
    ).requestDataBy(bot).asReceipt(true, bot)

    is InteractionMessage.Message -> {
        if (noReference) {
            message.message.checkNoReference()
        }
        message.message.sendToDirectByTargetIdDirect(bot, targetId, quote, nonce, null)
            ?: throw IllegalArgumentException("Valid messages must not be empty.")
    }

    is InteractionMessage.MessageContent -> when (val content = message.messageContent) {
        is KookReceiveMessageContent -> SendDirectMessageApi.createByTargetId(
            targetId = targetId,
            content = content.source.content,
            type = content.source.type?.value,
            quote = quote,
            nonce = nonce,
        ).requestDataBy(bot).asReceipt(true, bot)

        is KookChannelMessageDetailsContent -> SendDirectMessageApi.createByTargetId(
            targetId = targetId,
            content = content.details.content,
            type = content.details.type,
            quote = quote ?: content.details.quote?.id?.takeUnless { noReference },
            nonce = nonce,
        ).requestDataBy(bot).asReceipt(true, bot)

        else -> sendDirectMessage(
            bot,
            targetId,
            InteractionMessage.valueOf(content.messages.forReply(noReference)),
            quote,
            nonce,
            noReference,
        )
    }

    is InteractionMessage.Extension -> throw UnsupportedOperationException("Unsupported KOOK message: $message")
}

/**
 * 取得当前上下文中适用的 KOOK 协议选项。
 */
internal fun MessageSendContext.kookMessageOptions(): KookChannelMessageSendOptions? =
    when (this) {
        is MessageSendContext.Send -> options
        is MessageSendContext.Reply -> options.filterIsInstance<KookChannelMessageSendOption>()
    }.toKookChannelMessageSendOptions()

private const val REFERENCE_CONFLICT = "NO_REFERENCE conflicts with an explicit KOOK message reference."

/**
 * 检查最终选项，避免在组件默认规则中混入显式的无引用要求。
 */
private fun MessageSendContext.requiresNoReference(options: KookChannelMessageSendOptions?): Boolean {
    if (this !is MessageSendContext.Reply || StandardReplyOption.NO_REFERENCE !in this.options) return false
    val native = message as? KookChannelRequestMessage
    require(options?.quote == null && native?.request?.quote == null) { REFERENCE_CONFLICT }
    return true
}

/**
 * 发送前检查完整消息，避免先上传资源或发送拆分消息。
 */
private fun Message.checkNoReference() {
    when (this) {
        is Message.Element -> checkElementNoReference()
        is Messages -> forEach { it.checkElementNoReference() }
    }
}

/**
 * 原生消息只有在可以检查其引用字段时才能满足无引用要求。
 */
private fun Message.Element.checkElementNoReference() {
    when (this) {
        is MessageReference -> throw IllegalArgumentException(REFERENCE_CONFLICT)
        is KookApiMessage -> {
            val quote = when (val request = api) {
                is SendChannelMessageApi -> request.quote
                is SendDirectMessageApi -> request.quote
                else -> throw UnsupportedOperationException("Cannot verify NO_REFERENCE for the native KOOK request.")
            }
            require(quote == null) { REFERENCE_CONFLICT }
        }
    }
}

/**
 * 仅从复用的消息正文中去除继承的引用元素。
 */
private fun Messages.forReply(noReference: Boolean): Messages =
    if (noReference) filterNot { it is MessageReference }.toMessages() else this

/**
 * 发送频道消息，并在终端应用回复入口提供的协议默认值。
 */
internal suspend fun KookChatCapableChannel.sendChannelMessage(
    context: MessageSendContext,
    defaultQuote: String? = null,
    defaultTempTargetId: String? = null,
): KookMessageReceipt {
    val options = context.kookMessageOptions()
    val noReference = context.requiresNoReference(options)
    return sendChannelMessage(
        message = context.message,
        quote = if (noReference) null else options?.quote ?: defaultQuote,
        defaultTempTargetId = options?.defaultTempTargetId ?: defaultTempTargetId,
        tempTargetId = options?.tempTargetId,
        nonce = options?.nonce,
        templateId = options?.templateId,
        targetId = (context.destination as KookMessageSendTarget.Channel).id,
        noReference = noReference,
    )
}

internal suspend fun sendDirectMessage(bot: KookBot, context: MessageSendContext): KookMessageReceipt {
    val options = context.kookMessageOptions()
    val noReference = context.requiresNoReference(options)
    return sendDirectMessage(
        bot = bot,
        targetId = (context.destination as KookMessageSendTarget.User).id,
        message = context.message,
        quote = options?.quote,
        nonce = options?.nonce,
        noReference = noReference,
    )
}
