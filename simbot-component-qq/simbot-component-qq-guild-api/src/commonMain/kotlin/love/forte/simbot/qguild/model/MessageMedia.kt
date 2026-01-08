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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel


/**
 * [富媒体消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html)
 *
 * @property fileUuid 文件 ID
 * @property fileInfo 文件信息，用于发消息接口的 media 字段使用
 * @property ttl 有效期，表示剩余多少秒到期，到期后 file_info 失效，当等于 0 时，表示可长期使用
 * @property id 发送消息的唯一ID，当srv_send_msg设置为true时返回
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class MessageMedia(
    @SerialName("file_uuid")
    val fileUuid: String,
    @SerialName("file_info")
    val fileInfo: String,
    val ttl: Int,
    val id: String? = null,
)

/**
 * [富媒体消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html)
 *
 * @property fileInfo 文件信息，用于发消息接口的 media 字段使用
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class SendMessageMedia(
    // TODO
    @SerialName("file_info")
    val fileInfo: String,
)

/**
 * [MessageMedia] to [SendMessageMedia]
 */
public fun MessageMedia.forSend(): SendMessageMedia = SendMessageMedia(fileInfo)
