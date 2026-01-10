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

package love.forte.simbot.qguild.api.message.group

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.model.MessageKeyboard
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [发送消息到群](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/send.html#群聊)
 *
 * @author ForteScarlet
 */
public class GroupMessageSendApi private constructor(
    groupId: String,
    private val _body: GroupAndC2CSendBody, // TencentMessageForSending || MultiPartFormDataContent
) : PostQQGuildApi<GroupMessageSendResult>() {
    public companion object Factory : SimplePostApiDescription(
        "/v2/groups/{group_openid}/messages"
    ) {
        public const val MSG_TYPE_TEXT: Int = GroupAndC2CSendBody.MSG_TYPE_TEXT
        public const val MSG_TYPE_MARKDOWN: Int = GroupAndC2CSendBody.MSG_TYPE_MARKDOWN
        public const val MSG_TYPE_ARK: Int = GroupAndC2CSendBody.MSG_TYPE_ARK
        public const val MSG_TYPE_EMBED: Int = GroupAndC2CSendBody.MSG_TYPE_EMBED
        public const val MSG_TYPE_MEDIA: Int = GroupAndC2CSendBody.MSG_TYPE_MEDIA

        /**
         * Create a [GroupMessageSendApi].
         */
        @JvmStatic
        public fun create(groupId: String, body: GroupAndC2CSendBody): GroupMessageSendApi =
            GroupMessageSendApi(groupId, body)

        /**
         * Create a [GroupMessageSendApi].
         *
         * @param msgType 消息类型： 0 文本，2 是 markdown，3 ark 消息，4 embed，7 media 富媒体
         * 由于此处仅提供 content, 因此类型应当是 `0` 或 `2`。
         */
        @JvmStatic
        public fun create(groupId: String, content: String, msgType: Int): GroupMessageSendApi =
            create(groupId, GroupAndC2CSendBody.create(content = content, msgType = msgType))

        /**
         * Create a [GroupMessageSendApi].
         *
         * @param text 文本消息内容
         */
        @JvmStatic
        public fun createText(groupId: String, text: String): GroupMessageSendApi =
            create(groupId, GroupAndC2CSendBody.create(content = text, msgType = MSG_TYPE_TEXT))

        /**
         * Create a [GroupMessageSendApi].
         *
         * @param markdown markdown消息内容
         */
        @JvmStatic
        @JvmOverloads
        public fun createMarkdown(
            groupId: String,
            markdown: String,
            keyboard: MessageKeyboard? = null
        ): GroupMessageSendApi =
            create(
                groupId,
                GroupAndC2CSendBody.create(content = markdown, msgType = MSG_TYPE_MARKDOWN) {
                    this.keyboard = keyboard
                }
            )

    }

    override val resultDeserializationStrategy: DeserializationStrategy<GroupMessageSendResult>
        get() = GroupMessageSendResult.serializer()

    override val path: Array<String> = arrayOf("v2", "groups", groupId, "messages")

    override fun createBody(): Any = _body
}

/**
 * The result of [GroupMessageSendApi]
 */
@Serializable
public data class GroupMessageSendResult(
    val id: String,
    val timestamp: String,
)
