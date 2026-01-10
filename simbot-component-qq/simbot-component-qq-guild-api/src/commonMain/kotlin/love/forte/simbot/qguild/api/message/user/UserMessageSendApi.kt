/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.message.user

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.IgnoreWhenUseFormData
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.model.MessageKeyboard
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
        @JvmOverloads
        public fun createMarkdown(
            openid: String,
            markdown: String,
            keyboard: MessageKeyboard? = null
        ): UserMessageSendApi =
            create(
                openid,
                GroupAndC2CSendBody.create(
                    content = markdown,
                    msgType = MSG_TYPE_MARKDOWN,
                ) {
                    this.keyboard = keyboard
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
public data class UserMessageSendResult(
    val id: String,
    val timestamp: String,
)
