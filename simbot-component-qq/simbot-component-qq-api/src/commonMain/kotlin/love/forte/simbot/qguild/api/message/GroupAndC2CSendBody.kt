/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.api.message.user.UserMessageSendApi
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.model.MessageKeyboard
import love.forte.simbot.qguild.model.MessageKeyboards
import love.forte.simbot.qguild.model.MessageMedia
import kotlin.jvm.JvmStatic


/**
 * 用于 [GroupMessageSendApi] 和 [UserMessageSendApi]
 * 进行消息发送的数据体。
 *
 * @property content 文本内容
 * @property msgType 消息类型： 0 文本，2 是 markdown，3 ark 消息，4 embed，7 media 富媒体
 * @property markdown
 * @property keyboards 消息按钮内容，会序列化为 `keyboard` 字段。
 * @property media
 * @property ark
 * @property messageReference
 * @property eventId
 * @property msgId
 * @property msgSeq
 * @author ForteScarlet
 */
@Serializable
public open class GroupAndC2CSendBody internal constructor(
    public open var content: String,
    @SerialName("msg_type")
    public open var msgType: Int,
) {
    public open var markdown: Message.Markdown? = null

    /**
     * @since 4.4.0
     */
    @SerialName("keyboard")
    public open var keyboards: MessageKeyboards? = null

    /**
     * `MessageKeyboard` 的结构存在缺损，请直接使用 [keyboards]。
     *
     * 目前 `keyboard` 属性会直接读取或修改 [keyboards]:
     * - setter 会覆盖设置为 [keyboards] 的第一个 row 的第一个 button。
     * - getter 会尝试获取 [keyboards] 的第一个 row 的第一个 button。
     */
    @Deprecated(message = "`MessageKeyboard` 的结构存在缺损，请直接使用 keyboards。", level = DeprecationLevel.ERROR)
    public open var keyboard: MessageKeyboard?
        get() = keyboards?.content?.rows?.firstOrNull()?.buttons?.firstOrNull()
        set(value) {
            keyboards = value?.let { button ->
                MessageKeyboards.create(button)
            }
        }

    public open var media: MessageMedia? = null
    public open var ark: Message.Ark? = null

    @SerialName("message_reference")
    @IgnoreWhenUseFormData
    public open var messageReference: Message.Reference? = null

    @SerialName("event_id")
    public open var eventId: String? = null

    @SerialName("msg_id")
    public open var msgId: String? = null

    @SerialName("msg_seq")
    public open var msgSeq: Int? = null

    public companion object {
        public const val MSG_TYPE_TEXT: Int = 0
        public const val MSG_TYPE_MARKDOWN: Int = 2
        public const val MSG_TYPE_ARK: Int = 3
        public const val MSG_TYPE_EMBED: Int = 4
        public const val MSG_TYPE_MEDIA: Int = 7

        /**
         * Create [GroupAndC2CSendBody]
         */
        @JvmStatic
        public fun create(content: String, msgType: Int): GroupAndC2CSendBody =
            GroupAndC2CSendBody(content, msgType)

        /**
         * Create [GroupAndC2CSendBody]
         */
        public inline fun create(
            content: String,
            msgType: Int,
            block: GroupAndC2CSendBody.() -> Unit
        ): GroupAndC2CSendBody =
            create(content, msgType).also(block)
    }

    override fun toString(): String {
        return "GroupAndC2CSendBody(content='$content', msgType=$msgType, markdown=$markdown, " +
            "keyboards=$keyboards, media=$media, ark=$ark, messageReference=$messageReference, " +
            "eventId=$eventId, msgId=$msgId, msgSeq=$msgSeq)"
    }
}

@QGInternalApi
public fun GroupAndC2CSendBody.isEmpty(): Boolean =
    content.isEmpty() &&
        markdown == null &&
        keyboards == null &&
        media == null &&
        ark == null &&
        messageReference == null &&
        eventId == null &&
        msgId == null &&
        msgSeq == null
