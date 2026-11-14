/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.utils.runCatchingCancellable
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.internal.message.asGroupReceipt
import love.forte.simbot.component.qguild.internal.message.asReceipt
import love.forte.simbot.component.qguild.internal.message.asUserReceipt
import love.forte.simbot.component.qguild.internal.message.interceptDirectSend
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.MessageAuditedException
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.api.message.group.GroupMessageDeleteApi
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.api.message.user.UserMessageDeleteApi
import love.forte.simbot.qguild.api.message.user.UserMessageSendApi
import love.forte.simbot.qguild.message.ContentTextEncoder
import love.forte.simbot.qguild.stdlib.MessageDestination
import love.forte.simbot.qguild.stdlib.requestDataBy

private val logger = LoggerFactory.getLogger("love.forte.simbot.component.qguild.message.MessageSenderKt")

/**
 * 保留按 ID 发送的来源，拦截完成后才解析私聊消息。
 */
internal suspend fun QGBot.sendDmsInteractionMessage(
    guildId: String,
    message: InteractionMessage,
): QGMessageReceipt = interceptDirectSend(
    QGMessageSendTarget.create(MessageDestination.DMS, guildId.ID),
    message,
) { current ->
    when (current) {
        is InteractionMessage.Message -> sendDmsMessageDirect(guildId, current.message)
        is InteractionMessage.MessageContent -> sendDmsMessageDirect(guildId, current.messageContent)
        is InteractionMessage.Text -> sendDmsMessageDirect(guildId, current.text)
        is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $current")
    }
}

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为 [message] 的解析结果。
 *
 * [message] 会通过 [MessageParsers.parse] 解析为一个或多个 [MessageSendApi.Body.Builder]。
 * 当解析结果数量为1时，[sendMessageDirect] 的结果为 [QGSingleMessageReceipt] 类型的回执。
 * 相反如果结果大于1，则 [sendMessageDirect] 会得到实际为 [QGAggregatedMessageReceipt] 类型的结果。
 *
 * 需要注意如果 [message] 的解析结果最终没有任何可用元素（例如元素为空）则可能会引发API请求异常。
 *
 * @param channelId 目标频道ID
 * @param message 解析消息
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
 *
 * @return 发送后的回执
 */
@PublishedApi
@QGInternalApi
internal suspend inline fun QGBot.sendMessageDirect(
    channelId: String,
    message: Message,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parse(this, MessageDestination.CHANNEL, message, onEachPre, onEachPost)

    if (parsed.size == 1) {
        val body = parsed[0].build()
        return sendMessageDirect(channelId, body)
    }

    return sendMessageDirect(channelId, parsed)
}

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为 [messageContent] 的解析结果。
 *
 * 如果 [messageContent] 是其他QQ频道事件收到的消息（即为 [QGMessageContent] 类型），
 * 则会直接通过 [MessageSendApi.Body.Builder.fromMessage] 转义内部信息而不会尝试解析 [MessageContent.messages]。
 *
 * **注意:** 在转化的过程中会丢失不支持发送的消息（例如附件、图片等）。
 *
 * 如果类型不是上述情况，则行为与使用 [MessageContent.messages] 一致。
 *
 * @param channelId 目标频道ID
 * @param messageContent 解析消息本体
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
 *
 * @return 发送后的回执
 */
@PublishedApi
@QGInternalApi
internal suspend inline fun QGBot.sendMessageDirect(
    channelId: String,
    messageContent: MessageContent,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    if (messageContent is QGMessageContent) {
        val body = MessageSendApi.Body {
            onEachPre()
            fromMessage(messageContent.sourceMessage)
            onEachPost()
        }

        return sendMessageDirect(channelId, body)
    }

    return sendMessageDirect(channelId, messageContent.messages, onEachPre, onEachPost)
}

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为通过 [ContentTextEncoder.encode] 转义后的无特殊含义 [text] 文本。
 *
 * [text] 会通过 [ContentTextEncoder.encode] 进行转义来消除其中可能存在的[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)文本。
 * 如果你希望保留内嵌格式，参考使用 [QGContentText] 而不是纯文本内容。
 *
 *
 * @param channelId 目标频道ID
 * @param text 纯文本消息
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 * @see QGContentText
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
 *
 * @return 发送后的回执
 */
@PublishedApi
@QGInternalApi
internal suspend inline fun QGBot.sendMessageDirect(
    channelId: String,
    text: String,
    onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val body = MessageSendApi.Body {
        onEachPre()
        // 转义后的纯文本字符串
        val content = ContentTextEncoder.encode(text)
        if (source.configuration.contentAsMarkdown[MessageDestination.CHANNEL] == true) {
            appendMarkdownContent(content)
        } else {
            this.content = content
        }
        onEachPost()
    }

    return sendMessageDirect(channelId, body)
}

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendMessageDirect(channelId: String, body: MessageSendApi.Body): QGMessageReceipt =
    MessageSendApi.create(channelId, body).requestDataBy(source).asReceipt()

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendMessageDirect(
    channelId: String, bodyBuilderList: List<MessageSendApi.Body.Builder>
): QGMessageReceipt = bodyBuilderList.mapTo(ArrayList(bodyBuilderList.size)) {
    MessageSendApi.create(channelId, it.build()).requestDataBy(source)
}.asReceipt()

internal suspend inline fun QGBot.sendDmsMessageDirect(
    guildId: String,
    message: Message,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parse(this, MessageDestination.DMS, message, onEachPre, onEachPost)

    if (parsed.size == 1) {
        val body = parsed[0].build()
        return sendDmsMessageDirect(guildId, body)
    }

    return sendDmsMessageDirect(guildId, parsed)
}

internal suspend inline fun QGBot.sendDmsMessageDirect(
    guildId: String,
    messageContent: MessageContent,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    if (messageContent is QGMessageContent) {
        val body = MessageSendApi.Body {
            onEachPre()
            fromMessage(messageContent.sourceMessage)
            onEachPost()
        }

        return sendDmsMessageDirect(guildId, body)
    }

    return sendDmsMessageDirect(guildId, messageContent.messages, onEachPre, onEachPost)
}

internal suspend inline fun QGBot.sendDmsMessageDirect(
    guildId: String,
    text: String,
    onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val body = MessageSendApi.Body {
        onEachPre()
        // 转义后的纯文本字符串
        val content = ContentTextEncoder.encode(text)
        if (source.configuration.contentAsMarkdown[MessageDestination.DMS] == true) {
            appendMarkdownContent(content)
        } else {
            this.content = content
        }
        onEachPost()
    }

    return sendDmsMessageDirect(guildId, body)
}

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendDmsMessageDirect(guildId: String, body: MessageSendApi.Body): QGMessageReceipt =
    DmsSendApi.create(guildId, body).requestDataBy(source).asReceipt()

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendDmsMessageDirect(
    guildId: String, bodyBuilderList: List<MessageSendApi.Body.Builder>
): QGMessageReceipt = bodyBuilderList.mapTo(ArrayList(bodyBuilderList.size)) {
    DmsSendApi.create(guildId, it.build()).requestDataBy(source)
}.asReceipt()


@PublishedApi
internal suspend fun QGBot.sendGroupMessageDirect(
    openid: String,
    body: GroupAndC2CSendBody,
): QGMessageReceipt {
    return GroupMessageSendApi.create(openid, body).requestDataBy(source).asReceipt(this, openid)
}

@PublishedApi
internal suspend fun QGBot.sendGroupMessageDirect(
    openid: String,
    list: List<GroupAndC2CSendBody>,
): QGMessageReceipt {
    val receipts = list.map {
        GroupMessageSendApi.create(openid, it).requestDataBy(source)
    }

    return receipts.asGroupReceipt(this, openid)
}

@PublishedApi
internal suspend fun QGBot.sendUserMessageDirect(
    openid: String,
    list: List<GroupAndC2CSendBody>,
): QGMessageReceipt {
    val receipts = list.map {
        UserMessageSendApi.create(openid, it).requestDataBy(source)
    }

    return receipts.asUserReceipt(this, openid)
}

@PublishedApi
internal suspend fun QGBot.sendUserMessageDirect(
    openid: String,
    body: GroupAndC2CSendBody,
): QGMessageReceipt {
    return UserMessageSendApi.create(openid, body).requestDataBy(source).asReceipt(this, openid)
}

internal suspend fun QGBot.deleteGroupMessage(
    groupOpenid: String,
    messageId: String,
    vararg options: DeleteOption,
) {
    executeMessageDelete(options, targetLogValue = { "(groupOpenid: $groupOpenid, messageId: $messageId)" }) {
        GroupMessageDeleteApi.create(groupOpenid, messageId).requestDataBy(source)
    }
}

internal suspend fun QGBot.deleteUserMessage(
    userOpenid: String,
    messageId: String,
    vararg options: DeleteOption,
) {
    executeMessageDelete(options, targetLogValue = { "(userOpenid: $userOpenid, messageId: $messageId)" }) {
        UserMessageDeleteApi.create(userOpenid, messageId).requestDataBy(source)
    }
}

private suspend inline fun executeMessageDelete(
    options: Array<out DeleteOption>,
    targetLogValue: () -> String = { "" },
    request: suspend () -> Unit,
) {
    runCatchingCancellable { request() }.onFailure { e ->
        if (StandardDeleteOption.IGNORE_ON_FAILURE !in options) {
            throw e
        } else {
            logger.debug(
                "Failed to delete message${targetLogValue()}, " +
                    "but ignore it because of option [StandardDeleteOption.IGNORE_ON_FAILURE].",
                e
            )
        }
    }
}

/**
 * 执行已通过入口拦截的群文本发送。
 */
@PublishedApi
internal suspend inline fun QGBot.sendGroupMessageDirect(
    openid: String,
    text: String,
    msgType: Int,
    configure: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    // 转义
    val contentPlainText = ContentTextEncoder.encode(text)
    val body = if (
        msgType == GroupAndC2CSendBody.MSG_TYPE_TEXT &&
        source.configuration.contentAsMarkdown[MessageDestination.GROUP] == true
    ) {
        GroupAndC2CSendBody.create(" ", GroupAndC2CSendBody.MSG_TYPE_MARKDOWN).also {
            it.markdown = love.forte.simbot.qguild.model.Message.Markdown(content = contentPlainText)
        }
    } else {
        GroupAndC2CSendBody.create(contentPlainText, msgType)
    }.also(configure)

    return sendGroupMessageDirect(openid, body)
}

@OptIn(ExperimentalQGApi::class)
@PublishedApi
internal suspend inline fun QGBot.sendGroupMessageDirect(
    openid: String,
    message: Message,
    crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
    onEachPost: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parseToGroupAndC2C(
        this,
        message = message,
        builderType = SendingMessageParser.GroupBuilderType.GROUP,
        targetOpenid = openid,
        factory = {
            GroupAndC2CSendBody.create("", GroupAndC2CSendBody.MSG_TYPE_TEXT)
        },
        onEachPre = onEachPre,
        onEachPost = onEachPost
    )

    if (parsed.size == 1) {
        val body = parsed[0]
        return sendGroupMessageDirect(openid, body)
    }

    return sendGroupMessageDirect(openid, parsed)
}

@PublishedApi
@QGInternalApi
internal suspend inline fun QGBot.sendGroupMessageDirect(
    openid: String,
    messageContent: MessageContent,
    crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
    onEachPost: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    if (messageContent is QGGroupAndC2CMessageContent) {
        val body = GroupAndC2CSendBody.create(
            messageContent.sourceContent,
            GroupAndC2CSendBody.MSG_TYPE_TEXT,
        ) {
            onEachPre()
            // TODO attachments?
            onEachPost()
        }

        return sendGroupMessageDirect(openid, body)
    }

    return sendGroupMessageDirect(openid, messageContent.messages, onEachPre, onEachPost)
}

// // Users

/**
 * 执行已通过入口拦截的用户文本发送。
 */
@PublishedApi
internal suspend inline fun QGBot.sendUserMessageDirect(
    openid: String,
    text: String,
    msgType: Int,
    configure: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    // 转义
    val contentPlainText = ContentTextEncoder.encode(text)
    val body = if (
        msgType == GroupAndC2CSendBody.MSG_TYPE_TEXT &&
        source.configuration.contentAsMarkdown[MessageDestination.USER] == true
    ) {
        GroupAndC2CSendBody.create(" ", GroupAndC2CSendBody.MSG_TYPE_MARKDOWN).also {
            it.markdown = love.forte.simbot.qguild.model.Message.Markdown(content = contentPlainText)
        }
    } else {
        GroupAndC2CSendBody.create(contentPlainText, msgType)
    }.also(configure)

    return sendUserMessageDirect(openid, body)
}

@OptIn(ExperimentalQGApi::class)
@PublishedApi
internal suspend inline fun QGBot.sendUserMessageDirect(
    openid: String,
    message: Message,
    crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
    onEachPost: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parseToGroupAndC2C(
        this,
        message = message,
        builderType = SendingMessageParser.GroupBuilderType.C2C,
        targetOpenid = openid,
        factory = {
            GroupAndC2CSendBody.create("", GroupAndC2CSendBody.MSG_TYPE_TEXT)
        },
        onEachPre = onEachPre,
        onEachPost = onEachPost
    )

    if (parsed.size == 1) {
        val body = parsed[0]
        return sendUserMessageDirect(openid, body)
    }

    return sendUserMessageDirect(openid, parsed)
}

@PublishedApi
@QGInternalApi
internal suspend inline fun QGBot.sendUserMessageDirect(
    openid: String,
    messageContent: MessageContent,
    crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
    onEachPost: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    if (messageContent is QGGroupAndC2CMessageContent) {
        val body = GroupAndC2CSendBody.create(
            messageContent.sourceContent,
            GroupAndC2CSendBody.MSG_TYPE_TEXT,
        ) {
            onEachPre()
            // TODO attachments?
            onEachPost()
        }

        return sendUserMessageDirect(openid, body)
    }

    return sendUserMessageDirect(openid, messageContent.messages, onEachPre, onEachPost)
}

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为 [message] 的解析结果。
 *
 * [message] 会通过 [MessageParsers.parse] 解析为一个或多个 [MessageSendApi.Body.Builder]。
 * 当解析结果数量为1时，[sendMessage] 的结果为 [QGSingleMessageReceipt] 类型的回执。
 * 相反如果结果大于1，则 [sendMessage] 会得到实际为 [QGAggregatedMessageReceipt] 类型的结果。
 *
 * 需要注意如果 [message] 的解析结果最终没有任何可用元素（例如元素为空）则可能会引发API请求异常。
 *
 * @param channelId 目标频道ID
 * @param message 解析消息
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
 *
 * @return 发送后的回执

 * 短路时不解析消息或执行构建回调；回调延迟到发送终端执行，不支持非局部返回。
 */
@QGInternalApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    message: Message,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    crossinline onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt = interceptDirectSend(
    QGMessageSendTarget.create(MessageDestination.CHANNEL, channelId.ID),
    InteractionMessage.valueOf(message),
) { message ->
    sendChannelInteractionMessageDirect(channelId, message, onEachPre, onEachPost)
}

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为 [messageContent] 的解析结果。
 *
 * 如果 [messageContent] 是其他QQ频道事件收到的消息（即为 [QGMessageContent] 类型），
 * 则会直接通过 [MessageSendApi.Body.Builder.fromMessage] 转义内部信息而不会尝试解析 [MessageContent.messages]。
 *
 * **注意:** 在转化的过程中会丢失不支持发送的消息（例如附件、图片等）。
 *
 * 如果类型不是上述情况，则行为与使用 [MessageContent.messages] 一致。
 *
 * @param channelId 目标频道ID
 * @param messageContent 解析消息本体
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
 *
 * @return 发送后的回执

 * 短路时不解析消息或执行构建回调；回调延迟到发送终端执行，不支持非局部返回。
 */
@QGInternalApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    messageContent: MessageContent,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    crossinline onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt = interceptDirectSend(
    QGMessageSendTarget.create(MessageDestination.CHANNEL, channelId.ID),
    InteractionMessage.valueOf(messageContent),
) { message ->
    sendChannelInteractionMessageDirect(channelId, message, onEachPre, onEachPost)
}

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为通过 [ContentTextEncoder.encode] 转义后的无特殊含义 [text] 文本。
 *
 * [text] 会通过 [ContentTextEncoder.encode] 进行转义来消除其中可能存在的[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)文本。
 * 如果你希望保留内嵌格式，参考使用 [QGContentText] 而不是纯文本内容。
 *
 *
 * @param channelId 目标频道ID
 * @param text 纯文本消息
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 * @see QGContentText
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
 *
 * @return 发送后的回执

 * 短路时不解析消息或执行构建回调；回调延迟到发送终端执行，不支持非局部返回。
 */
@QGInternalApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    text: String,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    crossinline onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt = interceptDirectSend(
    QGMessageSendTarget.create(MessageDestination.CHANNEL, channelId.ID),
    InteractionMessage.valueOf(text),
) { message ->
    sendChannelInteractionMessageDirect(channelId, message, onEachPre, onEachPost)
}

/**
 * 按目标 openid 发送消息内容，支持应用级消息拦截。
 *
 * @param onEachPre 每个发送体完成构造后的初始化操作。
 * @param onEachPost 每个发送体发起请求前的收尾操作。

 * 短路时不解析消息或执行构建回调；回调延迟到发送终端执行，不支持非局部返回。
 */
@QGInternalApi
public suspend inline fun QGBot.sendGroupMessage(
    openid: String,
    messageContent: MessageContent,
    crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
    crossinline onEachPost: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt = interceptDirectSend(
    QGMessageSendTarget.create(MessageDestination.GROUP, openid.ID),
    InteractionMessage.valueOf(messageContent),
) { message ->
    when (message) {
        is InteractionMessage.Message -> sendGroupMessageDirect(openid, message.message, onEachPre, onEachPost)
        is InteractionMessage.MessageContent -> sendGroupMessageDirect(
            openid,
            message.messageContent,
            onEachPre,
            onEachPost
        )

        is InteractionMessage.Text -> sendGroupMessageDirect(openid, message.text, GroupAndC2CSendBody.MSG_TYPE_TEXT) {
            onEachPre()
            onEachPost()
        }

        is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
    }
}

/**
 * 按目标 openid 发送消息内容，支持应用级消息拦截。
 *
 * @param onEachPre 每个发送体完成构造后的初始化操作。
 * @param onEachPost 每个发送体发起请求前的收尾操作。

 * 短路时不解析消息或执行构建回调；回调延迟到发送终端执行，不支持非局部返回。
 */
@QGInternalApi
public suspend inline fun QGBot.sendUserMessage(
    openid: String,
    messageContent: MessageContent,
    crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
    crossinline onEachPost: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt = interceptDirectSend(
    QGMessageSendTarget.create(MessageDestination.USER, openid.ID),
    InteractionMessage.valueOf(messageContent),
) { message ->
    when (message) {
        is InteractionMessage.Message -> sendUserMessageDirect(openid, message.message, onEachPre, onEachPost)
        is InteractionMessage.MessageContent ->
            sendUserMessageDirect(openid, message.messageContent, onEachPre, onEachPost)

        is InteractionMessage.Text -> sendUserMessageDirect(openid, message.text, GroupAndC2CSendBody.MSG_TYPE_TEXT) {
            onEachPre()
            onEachPost()
        }

        is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
    }
}

/**
 * 在完成入口拦截后解析频道消息，避免替换消息类型时丢失构建回调。
 */
@PublishedApi
@QGInternalApi
internal suspend inline fun QGBot.sendChannelInteractionMessageDirect(
    channelId: String,
    message: InteractionMessage,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit,
    crossinline onEachPost: MessageSendApi.Body.Builder.() -> Unit,
): QGMessageReceipt = when (message) {
    is InteractionMessage.Message -> sendMessageDirect(channelId, message.message, onEachPre, onEachPost)
    is InteractionMessage.MessageContent -> sendMessageDirect(channelId, message.messageContent, onEachPre, onEachPost)
    is InteractionMessage.Text -> sendMessageDirect(channelId, message.text, onEachPre, onEachPost)
    is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
}
