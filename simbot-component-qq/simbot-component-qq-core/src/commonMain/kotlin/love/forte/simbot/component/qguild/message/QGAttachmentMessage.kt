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

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.message.BinaryDataAwareMessage
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.common.QGUnstableProperty
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
 * @property url 附件下载地址，缺少协议前缀时会补充 `https://`。
 * @property filename 文件名。
 * @property width 图片宽度，单位为像素。
 * @property height 图片高度，单位为像素。
 * @property size 文件大小，单位为字节。
 * @property contentType 附件内容类型。
 * @property voiceWavUrl 语音转换后的 WAV 文件地址。
 * @property asrReferText 语音识别参考结果。
 * @property attachmentId QQ 附件 ID，与旧版的 [id] 不同。
 *
 * @author ForteScarlet
 */
@OptIn(QGUnstableProperty::class)
@SerialName("qg.attachment")
@Serializable
public class QGAttachmentMessage internal constructor(
    public val url: String,
    public val filename: String? = null,
    public val width: Int? = null,
    public val height: Int? = null,
    public val size: Long? = null,
    @SerialName("content_type") public val contentType: String? = null,
    @SerialName("voice_wav_url") public val voiceWavUrl: String? = null,
    @SerialName("asr_refer_text") public val asrReferText: String? = null,
    @SerialName("id") @property:QGUnstableProperty public val attachmentId: String? = null,
) : QGMessageElement, BinaryDataAwareMessage {

    @Transient
    private var legacyProperties: Map<String, String> = emptyMap()

    /**
     * 兼容旧版使用字符串属性表创建附件消息的方式。
     */
    @JvmOverloads
    @Deprecated("Use received attachment messages and their typed properties")
    public constructor(url: String, properties: Map<String, String> = emptyMap()) : this(
        url = url,
        filename = properties["filename"],
        width = properties["width"]?.toIntOrNull(),
        height = properties["height"]?.toIntOrNull(),
        size = properties["size"]?.toLongOrNull(),
        contentType = properties["content_type"],
        voiceWavUrl = properties["voice_wav_url"],
        asrReferText = properties["asr_refer_text"],
        attachmentId = properties["id"],
    ) {
        legacyProperties = properties
    }

    /**
     * 附件属性的旧版字符串视图。新代码应使用类型化属性。
     */
    @Deprecated("Use the typed attachment properties")
    public val properties: Map<String, String>
        get() = buildMap {
            putAll(legacyProperties)
            put("url", url)
            filename?.let { put("filename", it) }
            width?.let { put("width", it.toString()) }
            height?.let { put("height", it.toString()) }
            this@QGAttachmentMessage.size?.let { put("size", it.toString()) }
            contentType?.let { put("content_type", it) }
            voiceWavUrl?.let { put("voice_wav_url", it) }
            asrReferText?.let { put("asr_refer_text", it) }
            attachmentId?.let { put("id", it) }
        }

    internal var bot: QGBot? = null

    @Transient
    private lateinit var _source: Message.Attachment

    /**
     * 得到当前消息可对应的原始类型 [Message.Attachment].
     *
     * 需要注意的是 [Message.Attachment.url] **有可能** 没有 `https://` 前缀。
     *
     */
    public val source: Message.Attachment
        get() = if (::_source.isInitialized) {
            _source
        } else {
            @Suppress("DEPRECATION")
            Message.Attachment(url, properties).also { _source = it }
        }

    /**
     * 旧版使用下载地址表示的消息 ID。
     */
    @Deprecated("Just get url", ReplaceWith("url.ID", "love.forte.simbot.ID"))
    public val id: ID get() = url.ID

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QGAttachmentMessage) return false

        if (url != other.url) return false
        if (filename != other.filename) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (size != other.size) return false
        if (contentType != other.contentType) return false
        if (voiceWavUrl != other.voiceWavUrl) return false
        if (asrReferText != other.asrReferText) return false
        if (attachmentId != other.attachmentId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + voiceWavUrl.hashCode()
        result = 31 * result + asrReferText.hashCode()
        result = 31 * result + attachmentId.hashCode()
        return result
    }

    override fun toString(): String =
        "QGAttachmentMessage(url='$url', filename=$filename, width=$width, height=$height, " +
            "size=$size, contentType=$contentType, voiceWavUrl=$voiceWavUrl, " +
            "asrReferText=$asrReferText, attachmentId=$attachmentId)"

    /**
     * 兼容旧版解构得到下载地址。
     */
    @Deprecated("Use url", ReplaceWith("url"))
    public operator fun component1(): String = url

    /**
     * 兼容旧版解构得到字符串属性表。
     */
    @Suppress("DEPRECATION")
    @Deprecated("Use the typed attachment properties")
    public operator fun component2(): Map<String, String> = properties

    /**
     * 兼容旧版复制附件消息的方式。
     */
    @Suppress("DEPRECATION")
    @Deprecated("Use received attachment messages and their typed properties")
    public fun copy(
        url: String = this.url,
        properties: Map<String, String> = this.properties,
    ): QGAttachmentMessage = QGAttachmentMessage(url, properties)

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
        @OptIn(QGUnstableProperty::class)
        public fun Message.Attachment.toMessage(): QGAttachmentMessage {
            val url0 = if (!url.startsWith("http")) "https://$url" else url
            return QGAttachmentMessage(
                url = url0,
                filename = filename,
                width = width,
                height = height,
                size = size,
                contentType = contentType,
                voiceWavUrl = voiceWavUrl,
                asrReferText = asrReferText,
                attachmentId = id,
            ).also {
                it._source = this
            }
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
