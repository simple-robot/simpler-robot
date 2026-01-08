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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.message.BinaryDataAwareMessage
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.message.Message as SimbotMessage


// TODO image support

/**
 * 附件消息。
 *
 * _仅支持在接收的消息中出现，暂不支持发送。_
 *
 * @author ForteScarlet
 */
@SerialName("qg.attachment")
@Serializable
public data class QGAttachmentMessage
@JvmOverloads constructor(
    public val url: String,
    public val properties: Map<String, String> = emptyMap()
) : QGMessageElement, BinaryDataAwareMessage {

    internal var bot: QGBot? = null

    private lateinit var _source: Message.Attachment

    /**
     * 得到当前消息可对应的原始类型 [Message.Attachment].
     *
     * 需要注意的是 [Message.Attachment.url] **有可能** 没有 `https://` 前缀。
     *
     */
    public val source: Message.Attachment
        get() = if (::_source.isInitialized) _source else Message.Attachment(url, properties).also { _source = it }

    @Deprecated("Just get url", ReplaceWith("url.ID", "love.forte.simbot.ID"))
    public val id: ID get() = url.ID

    /**
     * 尝试通过 [url] 读取二进制资源。
     *
     * @throws IllegalStateException 如果无法读取（例如因序列化而丢失了 HttpClient）
     * @throws Exception 请求过程中产生的任何异常
     */
    @JvmSynthetic
    override suspend fun binaryData(): ByteArray =
        readBinaryData(bot, url)

    public companion object {

        /**
         * 将 [Message.Attachment] 转化为 [QGAttachmentMessage].
         *
         * _注意：如果 [Message.Attachment.url] 不是以 `http` 开头，则会追加一个前缀 `https://` 。_
         *
         */
        @JvmStatic
        @JvmName("of")
        public fun Message.Attachment.toMessage(): QGAttachmentMessage {
            val url0 = if (!url.startsWith("http")) "https://$url" else url
            return QGAttachmentMessage(url0, properties)
        }
    }
}

internal expect suspend fun readBinaryData(
    bot: QGBot?,
    url: String
): ByteArray

/**
 * @suppress
 */
@Deprecated("Use QGAttachmentMessage.source", ReplaceWith("source"))
public fun QGAttachmentMessage.toAttachment(): Message.Attachment = source


internal object AttachmentParser : SendingMessageParser {
    private val logger = LoggerFactory.logger<AttachmentParser>()
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // TODO Attachment 暂不支持 Channel
        if (element is QGAttachmentMessage) {
            logger.warn("Attachment message is not yet supported for sending")
        }
    }

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        // TODO Attachment 暂不支持 GroupAndC2C
        if (element is QGAttachmentMessage) {
            logger.warn("Attachment message is not yet supported for sending")
        }
    }
}
