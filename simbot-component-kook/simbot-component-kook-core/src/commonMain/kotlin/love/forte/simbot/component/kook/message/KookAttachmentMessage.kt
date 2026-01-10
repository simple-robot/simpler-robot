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

@file:Suppress("ConstructorParameterNaming")

package love.forte.simbot.component.kook.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.message.KookAttachmentMessage.Companion.asMessage
import love.forte.simbot.kook.objects.Attachments
import love.forte.simbot.message.Image
import love.forte.simbot.message.RemoteUrlAwareImage
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 *
 * 将 [Attachments] 作为 simbot 消息元素。
 *
 * 通常在接收时使用.
 *
 * 使用 [Attachments.asMessage] 得到实例。
 *
 * @see KookAttachmentFile
 * @see KookAttachmentImage
 * @see KookAttachmentVideo
 *
 * @author ForteScarlet
 */
@SerialName("kook.attachment")
@Serializable
public sealed class KookAttachmentMessage : KookMessageElement {

    /**
     * 多媒体数据信息。
     */
    public abstract val attachment: Attachments

    /**
     * @see Attachments.type
     */
    public val attachmentType: String
        get() = attachment.type

    /**
     * @see Attachments.url
     */
    public val attachmentUrl: String
        get() = attachment.url

    /**
     * @see Attachments.name
     */
    public val attachmentName: String
        get() = attachment.name

    /**
     * @see Attachments.size
     */
    public val attachmentSize: Long
        get() = attachment.size

    public companion object {
        /**
         * 将 [Attachments] 转化为 [KookAttachmentMessage]。
         *
         * - 如果 [Attachments.type] == `image`, 会转化为 [KookAttachmentImage]
         * - 如果 [Attachments.type] == `file`, 会转化为 [KookAttachmentFile]
         * - 如果 [Attachments.type] == `video`, 会转化为 [KookAttachmentVideo]
         *
         * 其他情况则会直接使用 [KookAttachment]。
         *
         */
        @OptIn(ExperimentalSimbotAPI::class)
        @JvmStatic
        @JvmName("of")
        public fun Attachments.asMessage(): KookAttachmentMessage = when (type.lowercase()) {
            "image" -> KookAttachmentImage(this)
            "file" -> KookAttachmentFile(this)
            "video" -> KookAttachmentVideo(this)
            else -> KookAttachment(this)
        }
    }
}

@Serializable
private data class MessageAttachments(
    override val type: String,
    override val url: String,
    override val name: String,
    override val size: Long
) : Attachments

private fun Attachments.toMessageAttachment(): MessageAttachments =
    if (this is MessageAttachments) this else MessageAttachments(type, url, name, size)

/**
 * 普通的 [KookAttachmentMessage] 实现。
 */
@Serializable
@SerialName("kook.attachment.std")
public class KookAttachment private constructor(@SerialName("attachment") private val _attachment: MessageAttachments) :
    KookAttachmentMessage() {
    internal constructor(attachments: Attachments) : this(attachments.toMessageAttachment())

    override val attachment: Attachments
        get() = _attachment

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is KookAttachment) return false
        return attachment == other.attachment
    }

    override fun hashCode(): Int = attachment.hashCode()

    override fun toString(): String = "KookAttachment(attachment=$attachment)"
}

/**
 * 一个可以代表 [Image] 的 [KookAttachmentMessage]。
 */
@Serializable
@SerialName("kook.attachment.image")
@ExperimentalSimbotAPI
public class KookAttachmentImage private constructor(
    @SerialName(
        "attachment"
    ) private val _attachment: MessageAttachments
) :
    KookAttachmentMessage(), RemoteUrlAwareImage {
    internal constructor(attachments: Attachments) : this(attachments.toMessageAttachment())

    override val attachment: Attachments
        get() = _attachment

    override val id: ID = attachmentUrl.ID

    @JvmSynthetic
    override suspend fun url(): String = attachmentUrl

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is KookAttachmentImage) return false
        return attachment == other.attachment
    }

    override fun toString(): String = "KookAttachmentImage(attachment=$attachment)"

    override fun hashCode(): Int = attachment.hashCode()
}

/**
 * 文件附件类型的 [KookAttachmentMessage] 实现。
 */
@Serializable
@SerialName("kook.attachment.file")
@ExperimentalSimbotAPI
public class KookAttachmentFile private constructor(
    @SerialName(
        "attachment"
    ) private val _attachment: MessageAttachments
) :
    KookAttachmentMessage() {
    internal constructor(attachments: Attachments) : this(attachments.toMessageAttachment())

    override val attachment: Attachments
        get() = _attachment

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is KookAttachmentFile) return false
        return attachment == other.attachment
    }

    override fun toString(): String = "KookAttachmentFile(attachment=$attachment)"

    override fun hashCode(): Int = attachment.hashCode()
}

/**
 * 代表为视频类型的 [KookAttachmentMessage] 类型实现。
 */
@Serializable
@SerialName("kook.attachment.video")
@ExperimentalSimbotAPI
public class KookAttachmentVideo private constructor(
    @SerialName(
        "attachment"
    ) private val _attachment: MessageAttachments
) :
    KookAttachmentMessage() {
    internal constructor(attachments: Attachments) : this(attachments.toMessageAttachment())

    override val attachment: Attachments
        get() = _attachment

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is KookAttachmentVideo) return false
        return attachment == other.attachment
    }

    override fun toString(): String = "KookAttachmentVideo(attachment=$attachment)"

    override fun hashCode(): Int = attachment.hashCode()
}
