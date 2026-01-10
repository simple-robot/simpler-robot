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

package love.forte.simbot.qguild.api.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmOverloads


// TODO?
/**
 * 用于发送的普通消息.
 *
 * _content, embed, ark, image/file_image, markdown 至少需要有一个字段，否则无法下发消息。_
 */
@Serializable
internal data class QGMessageForSending @JvmOverloads constructor(
    /**
     * 消息内容，文本内容，支持内嵌格式
     */
    var content: String? = null,

    /**
     * MessageEmbed	embed 消息，一种特殊的 ark
     */
    var embed: Message.Embed? = null,

    /**
     * ark消息对象	ark 消息
     */
    var ark: Message.Ark? = null,

    /**
     * 图片url地址
     */
    var image: String? = null,

    /**
     * 要回复的消息id(Message.id), 在 CREATE_MESSAGE 事件中获取。带了 msg_id 视为被动回复消息，否则视为主动推送消息
     */
    @SerialName("msg_id")
    var msgId: String? = null,

    /**
     * 选填，要回复的事件id, 在各事件对象中获取。
     */
    @SerialName("event_id")
    var eventId: String? = null,

    /**
     * 选填，markdown 消息
     */
    var markdown: Message.Markdown? = null,
)

