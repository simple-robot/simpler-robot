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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.message.QGAttachmentMessage.Companion.toMessage
import love.forte.simbot.component.qguild.message.QGReference.Companion.toMessage
import love.forte.simbot.message.*
import love.forte.simbot.qguild.message.ContentTextDecoder
import love.forte.simbot.qguild.message.ContentTextEncoder
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage


internal object ContentParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        when (element) {
            is Text -> {
                // 转义为无内嵌格式的文本
                builderContext.builder.appendContent(ContentTextEncoder.encode(element.text))
            }

            is QGContentText -> {
                builderContext.builder.appendContent(element.content)
            }
        }
    }

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        fun builder() = builderContext.builder

        when (element) {
            is Text -> {
                // 转义为无内嵌格式的文本
                builder().content += ContentTextEncoder.encode(element.text)
            }

            is QGContentText -> {
                builder().content += element.content
            }
        }
    }
}

/**
 * 解析 [Face] 为 [内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html) 中的 `表情` 使用。
 *
 * 解析为系统表情，具体表情id参考 [Emoji 列表](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#emoji-%E5%88%97%E8%A1%A8)，
 * 仅支持type=1的系统表情，type=2的emoji表情直接按字符串填写即可
 *
 * @see love.forte.simbot.message.Face
 */
internal object FaceParser : SendingMessageParser {
    // <emoji:id>
    private const val EMOJI_PREFIX = "<emoji:"
    private const val EMOJI_SUFFIX = ">"

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        if (element is Face) {
            val id = element.id.literal
            builderContext.builder.appendContent("$EMOJI_PREFIX$id$EMOJI_SUFFIX")
        }
    }

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is Face) {
            val builder = builderContext.builder

            val id = element.id.literal
            builder.content += "$EMOJI_PREFIX$id$EMOJI_SUFFIX"
        }
    }
}


internal object QGMessageParser : ReceivingMessageParser {
    private const val AT_USER_VALUE = "uv"
    private const val AT_EVERYONE_GROUP = "all"
    // 2024-07-13 兼容之前的两个写法解析：因为好像内容还没变
    private const val AT_USER_OLD_VALUE = "uvold"
    private const val AT_EVERYONE_OLD_GROUP = "allold"

    private const val MENTION_CHANNEL_VALUE = "cv"
    private const val EMOJI_VALUE = "ev"
//
//    private val replaceRegex = Regex(
//        "<@!?(?<$AT_USER_VALUE>\\d+)>" +
//                "|(?<$AT_EVERYONE_GROUP>@everyone)" +
//                "|<#(?<$MENTION_CHANNEL_VALUE>\\d+)>" +
//                "|<emoji:(?<$EMOJI_VALUE>\\d+)>"
//    )
//
//    private val replaceWithoutMentionAllRegex = Regex(
//        "<@!?(?<$AT_USER_VALUE>\\d+)>" +
//                "|<#(?<$MENTION_CHANNEL_VALUE>\\d+)>" +
//                "|<emoji:(?<$EMOJI_VALUE>\\d+)>"
//    )

    /*
     * 嵌入文本使用格式：<qqbot-at-user id="" /> 协议：<@userid>即将弃用，请使用上述最新格式。
     * 嵌入文本使用格式：<qqbot-at-everyone /> 协议：@everyone即将弃用，请使用上述最新格式。
     */

    private val replaceRegex = Regex(
        "<qqbot-at-user +id=\"(?<$AT_USER_VALUE>[.a-zA-Z0-9_-]+)\" */>" +
                "|(?<$AT_EVERYONE_GROUP><qqbot-at-everyone */>)" +
                "|<#(?<$MENTION_CHANNEL_VALUE>\\d+)>" +
                "|<emoji:(?<$EMOJI_VALUE>\\d+)>" +
                // 兼容之前的两个写法解析
                "|<@!?(?<$AT_USER_OLD_VALUE>\\d+)>" +
                "|(?<$AT_EVERYONE_OLD_GROUP>@everyone)"
    )

    private val replaceWithoutMentionAllRegex = Regex(
        "<qqbot-at-user +id=\"(?<$AT_USER_VALUE>[.a-zA-Z0-9_-]+)\" */>" +
                "|<#(?<$MENTION_CHANNEL_VALUE>\\d+)>" +
                "|<emoji:(?<$EMOJI_VALUE>\\d+)>" +
                // 兼容之前的两个写法解析
                "|<@!?(?<$AT_USER_OLD_VALUE>\\d+)>"
    )

    override fun invoke(qgContent: String, context: ReceivingMessageParser.Context): ReceivingMessageParser.Context {
        return invoke0(qgContent, null, context)
    }

    override fun invoke(qgMessage: Message, context: ReceivingMessageParser.Context): ReceivingMessageParser.Context {
        return invoke0(qgMessage.content, qgMessage, context)
    }


    private inline fun resolveAttachments(attachments: List<Message.Attachment>, block: (QGAttachmentMessage) -> Unit) {
        attachments.forEach {
            block(it.toMessage())
        }
    }

    private fun invoke0(
        content: String,
        qgMessage: Message?,
        context: ReceivingMessageParser.Context
    ): ReceivingMessageParser.Context {
        val isMentionEveryone = qgMessage?.mentionEveryone
        val maybeMentionEveryone = isMentionEveryone != false

        var match: MatchResult? =
            // null or true
            (if (maybeMentionEveryone) replaceRegex else replaceWithoutMentionAllRegex).find(content)


        if (match == null) {
            context.plainTextBuilder.append(ContentTextDecoder.decode(content))
            context.messages += QGContentText(content)
            context.attachments?.also {
                resolveAttachments(it) { attachment ->
                    context.messages += attachment
                }
            }
            return context
        }

        val messageList = mutableListOf<SimbotMessage.Element>()

        // plainText builder
        val textBuilder = StringBuilder()

        fun appendText(value: String, startIndex: Int, endIndex: Int) {
            ContentTextDecoder.decodeTo(value, startIndex, endIndex, object : Appendable {
                override fun append(value: CharSequence?): Appendable = also {
                    textBuilder.append(value)
                    context.plainTextBuilder.append(value)
                }

                override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable = also {
                    textBuilder.append(value, startIndex, endIndex)
                    context.plainTextBuilder.append(value, startIndex, endIndex)
                }

                override fun append(value: Char): Appendable = also {
                    textBuilder.append(value)
                    context.plainTextBuilder.append(value)
                }
            })
        }

        fun flushText() {
            if (textBuilder.isNotEmpty()) {
                val text = textBuilder.toString()
                messageList.add(text.toText())
                textBuilder.clear()
            }
        }

        var lastStart = 0
        val length = content.length
        do {
            val foundMatch = match!!

            appendText(content, lastStart, foundMatch.range.first)
            //region match transform
            kotlin.run {
                val groups = foundMatch.groups

                groups[AT_USER_VALUE]?.also { atUserValue ->
                    flushText()
                    messageList.add(At(atUserValue.value.ID, originContent = toQQBotAtUser(atUserValue.value)))
                    return@run
                }
                // 兼容旧的
                groups[AT_USER_OLD_VALUE]?.also { atUserValue ->
                    flushText()
                    messageList.add(At(atUserValue.value.ID, originContent = "<@${atUserValue.value}>"))
                    return@run
                }

                groups[MENTION_CHANNEL_VALUE]?.also { atChannelValue ->
                    flushText()
                    messageList.add(
                        At(
                            atChannelValue.value.ID,
                            type = QQGuildComponent.AT_CHANNEL_TYPE,
                            originContent = "<#${atChannelValue.value}>"
                        )
                    )
                    return@run
                }

                if (maybeMentionEveryone) {
                    groups[AT_EVERYONE_GROUP]?.also {
                        flushText()
                        messageList.add(AtAll)
                        return@run
                    }
                    groups[AT_EVERYONE_OLD_GROUP]?.also {
                        flushText()
                        messageList.add(AtAll)
                        return@run
                    }
                }

                groups[EMOJI_VALUE]?.also { emojiValue ->
                    flushText()
                    messageList.add(Face(emojiValue.value.ID))
                    return@run
                }

            }

            //endregion

            lastStart = foundMatch.range.last + 1
            match = foundMatch.next()
        } while (lastStart < length && match != null)

        if (lastStart < length) {
            appendText(content, lastStart, content.length)
        }

        // end flush
        flushText()

        // ark
        qgMessage?.ark?.toMessage()?.also { messageList.add(it) }

        // attachments
        context.attachments?.mapTo(messageList) { it.toMessage() }

        // TODO embeds?
        // reference
        qgMessage?.messageReference?.also { messageList.add(it.toMessage(qgMessage.channelId.ID)) }

        context.messages += messageList
        return context
    }
}

/*
 * 嵌入文本使用格式：<qqbot-at-user id="" /> 协议：<@userid>即将弃用，请使用上述最新格式。
 * 嵌入文本使用格式：<qqbot-at-everyone /> 协议：@everyone即将弃用，请使用上述最新格式。
 */

internal const val QQ_BOT_AT_USER_PREFIX = "<qqbot-at-user id=\""
internal const val QQ_BOT_AT_USER_SUFFIX = "\" />"
internal const val QQ_BOT_AT_EVERYONE = "<qqbot-at-everyone />"

internal fun toQQBotAtUser(id: String): String =
    "$QQ_BOT_AT_USER_PREFIX$id$QQ_BOT_AT_USER_SUFFIX"

internal const val OLD_QQ_BOT_AT_USER_PREFIX = "<@"
internal const val OLD_QQ_BOT_AT_USER_SUFFIX = ">"
internal const val OLD_QQ_BOT_AT_EVERYONE = "@everyone"

internal fun toOldQQBotAtUser(id: String): String =
    "$OLD_QQ_BOT_AT_USER_PREFIX$id$OLD_QQ_BOT_AT_USER_SUFFIX"
