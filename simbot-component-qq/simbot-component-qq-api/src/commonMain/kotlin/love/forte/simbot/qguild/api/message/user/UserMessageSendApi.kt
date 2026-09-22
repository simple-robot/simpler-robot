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

package love.forte.simbot.qguild.api.message.user

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.IgnoreWhenUseFormData
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.model.MessageKeyboard
import love.forte.simbot.qguild.model.MessageKeyboards
import love.forte.simbot.qguild.model.SendMessageMedia
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [发送消息-单聊](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/send.html#单聊)
 *
 * 单独发动消息给用户。
 *
 * @author ForteScarlet
 */
public class UserMessageSendApi private constructor(
    openid: String,
    @Suppress("ConstructorParameterNaming")
    private val _body: GroupAndC2CSendBody,
) : PostQQGuildApi<UserMessageSendResult>() {
    public companion object Factory : SimplePostApiDescription(
        "/v2/users/{openid}/messages"
    ) {
        public const val MSG_TYPE_TEXT: Int = GroupAndC2CSendBody.MSG_TYPE_TEXT
        public const val MSG_TYPE_MARKDOWN: Int = GroupAndC2CSendBody.MSG_TYPE_MARKDOWN
        public const val MSG_TYPE_ARK: Int = GroupAndC2CSendBody.MSG_TYPE_ARK
        public const val MSG_TYPE_EMBED: Int = GroupAndC2CSendBody.MSG_TYPE_EMBED
        public const val MSG_TYPE_MEDIA: Int = GroupAndC2CSendBody.MSG_TYPE_MEDIA

        /**
         * Create a [UserMessageSendApi].
         */
        @JvmStatic
        public fun create(openid: String, body: GroupAndC2CSendBody): UserMessageSendApi =
            UserMessageSendApi(openid, body)

        /**
         * Create a [UserMessageSendApi].
         *
         * @param msgType 消息类型： 0 文本，2 是 markdown，3 ark 消息，4 embed，7 media 富媒体
         * 由于此处仅提供 content, 因此类型应当是 `0` 或 `2`。
         */
        @JvmStatic
        public fun create(openid: String, content: String, msgType: Int): UserMessageSendApi =
            create(
                openid,
                GroupAndC2CSendBody.create(
                    content = content,
                    msgType = msgType
                )
            )

        /**
         * Create a [UserMessageSendApi].
         *
         * @param text 文本消息内容
         */
        @JvmStatic
        public fun createText(openid: String, text: String): UserMessageSendApi =
            create(
                openid,
                GroupAndC2CSendBody.create(content = text, msgType = MSG_TYPE_TEXT)
            )

        /**
         * Create a [UserMessageSendApi].
         *
         * @param markdown markdown消息内容
         */
        @JvmStatic
        @Deprecated(
            message = "使用带 `MessageKeyboards` 的方法，而不是直接使用 `MessageKeyboard`",
            level = DeprecationLevel.ERROR
        )
        public fun createMarkdown(
            openid: String,
            markdown: String,
            keyboard: MessageKeyboard? = null
        ): UserMessageSendApi = create(
            openid,
            GroupAndC2CSendBody.create(
                content = markdown,
                msgType = MSG_TYPE_MARKDOWN,
            ) {
                this.keyboards = keyboard?.let { button -> MessageKeyboards.create(button) }
            }
        )

        /**
         * Create a [UserMessageSendApi].
         *
         * @param markdown markdown消息内容
         * @since 4.4.0
         */
        @JvmStatic
        @JvmOverloads
        public fun createMarkdown(
            openid: String,
            markdown: String,
            keyboards: MessageKeyboards? = null
        ): UserMessageSendApi = create(
            openid,
            GroupAndC2CSendBody.create(
                content = markdown,
                msgType = MSG_TYPE_MARKDOWN,
            ) {
                this.keyboards = keyboards
            }
        )
    }

    override val resultDeserializationStrategy: DeserializationStrategy<UserMessageSendResult>
        get() = UserMessageSendResult.serializer()

    override val path: Array<String> = arrayOf("v2", "users", openid, "messages")

    override fun createBody(): Any = _body

    /**
     * @property content 文本内容
     * @property msgType 消息类型： 0 文本，2 是 markdown，3 ark 消息，4 embed，7 media 富媒体
     * @property markdown
     * @property keyboard
     * @property media
     * @property ark
     * @property messageReference
     * @property eventId
     * @property msgId
     * @property msgSeq
     */
    @ApiModel
    @Serializable
    public class Body(
        public val content: String,
        @SerialName("msg_type")
        public val msgType: Int,
    ) {
        public var markdown: Message.Markdown? = null
        public var keyboard: MessageKeyboard? = null
        public var media: SendMessageMedia? = null
        public var ark: Message.Ark? = null

        @SerialName("message_reference")
        @IgnoreWhenUseFormData
        public var messageReference: Message.Reference? = null

        @SerialName("event_id")
        public var eventId: String? = null

        @SerialName("msg_id")
        public var msgId: String? = null

        @SerialName("msg_seq")
        public var msgSeq: Int? = null
    }
}


/**
 * The result of [UserMessageSendApi]
 */
@Serializable
public class UserMessageSendResult @ApiModelConstructor public constructor(
    public val id: String,
    public val timestamp: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserMessageSendResult) return false

        if (id != other.id) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int = 31 * id.hashCode() + timestamp.hashCode()

    override fun toString(): String = "UserMessageSendResult(id=$id, timestamp=$timestamp)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
    public operator fun component2(): String = timestamp

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        timestamp: String = this.timestamp,
    ): UserMessageSendResult = UserMessageSendResult(id, timestamp)
    //endregion
}
