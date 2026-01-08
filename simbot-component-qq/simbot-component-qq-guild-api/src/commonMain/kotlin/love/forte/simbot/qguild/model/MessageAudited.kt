/*
 * Copyright (c) 2023. ForteScarlet.
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

/**
 * [消息审核对象(MessageAudited)](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#%E6%B6%88%E6%81%AF%E5%AE%A1%E6%A0%B8%E5%AF%B9%E8%B1%A1-messageaudited)
 *
 * @author ForteScarlet
 */
@Serializable
public data class MessageAudited(
    /** 消息审核 id */
    @SerialName("audit_id") val auditId: String,
    /** 消息 id，只有审核通过事件才会有值 */
    @SerialName("message_id") val messageId: String? = null,
    /** 频道 id */
    @SerialName("guild_id") val guildId: String,
    /** 子频道 id */
    @SerialName("channel_id") val channelId: String,
    /** 消息审核时间 */
    @SerialName("audit_time") val auditTime: String,
    /** 消息创建时间 */
    @SerialName("create_time") val createTime: String,
    /** 子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序 */
    @SerialName("seq_in_channel") val seqInChannel: String
)

/*
audit_id	string	消息审核 id
message_id	string	消息 id，只有审核通过事件才会有值
guild_id	string	频道 id
channel_id	string	子频道 id
audit_time	ISO8601 timestamp	消息审核时间
create_time	ISO8601 timestamp	消息创建时间
seq_in_channel	string	子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序
 */
