/*
 * Copyright (c) 2023-2024. ForteScarlet.
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
import love.forte.simbot.qguild.QGApi4J
import kotlin.jvm.JvmName


/**
 * [公告对象](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html)
 */
@ApiModel
@Serializable
public data class Announces(
    /** 频道 id */
    @SerialName("guild_id") val guildId: String,
    /** 子频道 id */
    @SerialName("channel_id") val channelId: String,
    /** 消息 id */
    @SerialName("message_id") val messageId: String,
    /** 公告类别 0:成员公告 1:欢迎公告，默认成员公告 */
    @SerialName("announces_type") @get:JvmName("getAnnouncesType") val announcesType: UInt,
    /**
     * [RecommendChannel] 数组
     */
    @SerialName("recommend_channels") val recommendChannels: List<RecommendChannel> = emptyList()
) {
    /**
     * 获取 [announcesType] 的结果并从 [Unsigned integer type](https://kotlinlang.org/docs/unsigned-integer-types.html) 转为Java可用的 [Int]
     *
     */
    @QGApi4J
    public val announcesTypeIntValue: Int get() = announcesType.toInt()
}

/**
 * [推荐子频道对象(RecommendChannel)](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html#%E6%8E%A8%E8%8D%90%E5%AD%90%E9%A2%91%E9%81%93%E5%AF%B9%E8%B1%A1-recommendchannel)
 *
 * @see Announces.recommendChannels
 */
@ApiModel
@Serializable
public data class RecommendChannel(
    /** 子频道 id */
    @SerialName("channel_id") val channelId: String,
    /** 推荐语 */
    val introduce: String
)
