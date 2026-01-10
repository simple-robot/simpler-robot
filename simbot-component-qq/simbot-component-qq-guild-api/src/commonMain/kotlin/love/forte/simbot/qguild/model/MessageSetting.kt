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
 *
 * [频道消息频率设置对象 (MessageSetting)](https://bot.q.qq.com/wiki/develop/api/openapi/setting/model.html#messagesetting)
 *
 * @author ForteScarlet
 */
@Serializable
public data class MessageSetting(
    /**
     * 是否允许创建私信
     */
    @SerialName("disable_create_dm") val disableCreateDm: String,
    /**
     * 是否允许发主动消息
     */
    @SerialName("disable_push_msg") val disablePushMsg: String,
    /**
     * 子频道 id 数组
     */
    @SerialName("channel_ids") val channelIds: List<String>,
    /**
     * 每个子频道允许主动推送消息最大消息条数
     */
    @SerialName("channel_push_max_num") val channelPushMaxNum: Int,
)

/*
disable_create_dm	string	是否允许创建私信
disable_push_msg	string	是否允许发主动消息
channel_ids	string 数组	子频道 id 数组
channel_push_max_num	uint32	每个子频道允许主动推送消息最大消息条数
 */
