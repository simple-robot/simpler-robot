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
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.model.Schedule.RemindTypes


/**
 * [日程对象](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/model.html)
 *
 * 用于描述一个日程。
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class Schedule(
    /**
     * 日程 id
     */
    val id: String,
    /**
     * 日程名称
     */
    val name: String,
    /**
     * 日程描述
     */
    val description: String,
    /**
     * 日程开始时间戳(ms)
     */
    @SerialName("start_timestamp") val startTimestamp: Long,
    /**
     * 日程结束时间戳(ms)
     */
    @SerialName("end_timestamp") val endTimestamp: Long,
    /**
     * 创建者
     */
    val creator: SimpleMember,
    /**
     * 日程开始时跳转到的子频道 id
     */
    @SerialName("jump_channel_id") val jumpChannelId: String,
    /**
     * 日程提醒类型，取值参考 [RemindTypes]。
     */
    @SerialName("remind_type") val remindType: String,
) {

    /**
     * [日程对象#RemindType](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/model.html#remindtype)
     *
     * @see Schedule.remindType
     */
    public object RemindTypes {
        /** 不提醒 */
        public const val NO_REMIND: String = "0"

        /** 开始时提醒 */
        public const val AT_START: String = "1"

        /** 开始前 5 分钟提醒 */
        public const val AT_5_MINUTES_BEFORE_START: String = "2"

        /** 开始前 15 分钟提醒 */
        public const val AT_15_MINUTES_BEFORE_START: String = "3"

        /** 开始前 30 分钟提醒 */
        public const val AT_30_MINUTES_BEFORE_START: String = "4"

        /** 开始前 60 分钟提醒 */
        public const val AT_60_MINUTES_BEFORE_START: String = "5"
    }

}

