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
 *
 * @property content 消息内容，文本内容，支持内嵌格式。
 * @property embed Embed 消息，一种特殊的 ark。
 * @property ark ark 消息对象。
 * @property image 图片 url 地址。
 * @property msgId 要回复的消息 id（Message.id），在 CREATE_MESSAGE 事件中获取。带了 msg_id 视为被动回复消息，否则视为主动推送消息。
 * @property eventId 选填，要回复的事件 id，在各事件对象中获取。
 * @property markdown 选填，markdown 消息。
 */
@Serializable
internal data class QGMessageForSending @JvmOverloads constructor(
    var content: String? = null,
    internal var embed: Message.Embed? = null,
    internal var ark: Message.Ark? = null,
    internal var image: String? = null,

    @SerialName("msg_id")
    internal var msgId: String? = null,

    @SerialName("event_id")
    var eventId: String? = null,
    var markdown: Message.Markdown? = null,
)

