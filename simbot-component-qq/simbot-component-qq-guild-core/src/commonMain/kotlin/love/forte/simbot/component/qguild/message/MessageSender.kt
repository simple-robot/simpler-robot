/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.internal.message.asGroupReceipt
import love.forte.simbot.component.qguild.internal.message.asReceipt
import love.forte.simbot.component.qguild.internal.message.asUserReceipt
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.MessageAuditedException
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.api.message.user.UserMessageSendApi
import love.forte.simbot.qguild.message.ContentTextEncoder
import love.forte.simbot.qguild.stdlib.requestDataBy

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
 */
@QGInternalApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    message: Message,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parse(message = message, onEachPre = onEachPre, onEachPost = onEachPost)

    if (parsed.size == 1) {
        val body = parsed[0].build()
        return sendMessage(channelId, body)
    }

    return sendMessage(channelId, parsed)
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
@QGInternalApi
public suspend inline fun QGBot.sendMessage(
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

        return sendMessage(channelId, body)
    }

    return sendMessage(channelId, messageContent.messages, onEachPre, onEachPost)
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
@QGInternalApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    text: String,
    onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val body = MessageSendApi.Body {
        onEachPre()
        // 转义后的纯文本字符串
        content = ContentTextEncoder.encode(text)
        onEachPost()
    }

    return sendMessage(channelId, body)
}

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendMessage(channelId: String, body: MessageSendApi.Body): QGMessageReceipt =
    MessageSendApi.create(channelId, body).requestDataBy(source).asReceipt()

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendMessage(
    channelId: String, bodyBuilderList: List<MessageSendApi.Body.Builder>
): QGMessageReceipt = bodyBuilderList.mapTo(ArrayList(bodyBuilderList.size)) {
    MessageSendApi.create(channelId, it.build()).requestDataBy(source)
}.asReceipt()

internal suspend inline fun QGBot.sendDmsMessage(
    guildId: String,
    message: Message,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parse(message = message, onEachPre = onEachPre, onEachPost = onEachPost)

    if (parsed.size == 1) {
        val body = parsed[0].build()
        return sendDmsMessage(guildId, body)
    }

    return sendDmsMessage(guildId, parsed)
}

internal suspend inline fun QGBot.sendDmsMessage(
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

        return sendDmsMessage(guildId, body)
    }

    return sendDmsMessage(guildId, messageContent.messages, onEachPre, onEachPost)
}

internal suspend inline fun QGBot.sendDmsMessage(
    guildId: String,
    text: String,
    onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val body = MessageSendApi.Body {
        onEachPre()
        // 转义后的纯文本字符串
        content = ContentTextEncoder.encode(text)
        onEachPost()
    }

    return sendDmsMessage(guildId, body)
}

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendDmsMessage(guildId: String, body: MessageSendApi.Body): QGMessageReceipt =
    DmsSendApi.create(guildId, body).requestDataBy(source).asReceipt()

@PublishedApi
@QGInternalApi
internal suspend fun QGBot.sendDmsMessage(
    guildId: String, bodyBuilderList: List<MessageSendApi.Body.Builder>
): QGMessageReceipt = bodyBuilderList.mapTo(ArrayList(bodyBuilderList.size)) {
    DmsSendApi.create(guildId, it.build()).requestDataBy(source)
}.asReceipt()



@PublishedApi
internal suspend fun QGBot.sendGroupMessage(
    openid: String,
    body: GroupAndC2CSendBody,
): QGMessageReceipt {
    return GroupMessageSendApi.create(openid, body).requestDataBy(source).asReceipt()
}

@PublishedApi
internal suspend fun QGBot.sendGroupMessage(
    openid: String,
    list: List<GroupAndC2CSendBody>,
): QGMessageReceipt {
    val receipts = list.map {
        GroupMessageSendApi.create(openid, it).requestDataBy(source)
    }

    return receipts.asGroupReceipt()
}

@PublishedApi
internal suspend fun QGBot.sendUserMessage(
    openid: String,
    list: List<GroupAndC2CSendBody>,
): QGMessageReceipt {
    val receipts = list.map {
        UserMessageSendApi.create(openid, it).requestDataBy(source)
    }

    return receipts.asUserReceipt()
}

@PublishedApi
internal suspend fun QGBot.sendUserMessage(
    openid: String,
    body: GroupAndC2CSendBody,
): QGMessageReceipt {
    return UserMessageSendApi.create(openid, body).requestDataBy(source).asReceipt()
}

internal suspend inline fun QGBot.sendGroupMessage(
    openid: String,
    text: String,
    msgType: Int,
    configure: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    // 转义
    val contentPlainText = ContentTextEncoder.encode(text)
    val body = GroupAndC2CSendBody.create(contentPlainText, msgType)
        .also(configure)

    return sendGroupMessage(openid, body)
}

@OptIn(ExperimentalQGApi::class)
@PublishedApi
internal suspend inline fun QGBot.sendGroupMessage(
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
        return sendGroupMessage(openid, body)
    }

    return sendGroupMessage(openid, parsed)
}

@QGInternalApi
public suspend inline fun QGBot.sendGroupMessage(
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

        return sendGroupMessage(openid, body)
    }

    return sendGroupMessage(openid, messageContent.messages, onEachPre, onEachPost)
}

//// Users

internal suspend inline fun QGBot.sendUserMessage(
    openid: String,
    text: String,
    msgType: Int,
    configure: GroupAndC2CSendBody.() -> Unit = {},
): QGMessageReceipt {
    // 转义
    val contentPlainText = ContentTextEncoder.encode(text)
    val body = GroupAndC2CSendBody.create(contentPlainText, msgType)
        .also(configure)

    return sendUserMessage(openid, body)
}

@OptIn(ExperimentalQGApi::class)
@PublishedApi
internal suspend inline fun QGBot.sendUserMessage(
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
        return sendUserMessage(openid, body)
    }

    return sendUserMessage(openid, parsed)
}

@QGInternalApi
public suspend inline fun QGBot.sendUserMessage(
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

        return sendUserMessage(openid, body)
    }

    return sendUserMessage(openid, messageContent.messages, onEachPre, onEachPost)
}
