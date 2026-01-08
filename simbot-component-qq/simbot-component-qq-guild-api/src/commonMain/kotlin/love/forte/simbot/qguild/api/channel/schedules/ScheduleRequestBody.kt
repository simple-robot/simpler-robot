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

package love.forte.simbot.qguild.api.channel.schedules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.model.Schedule
import love.forte.simbot.qguild.model.Schedule.RemindTypes
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 * 用于 [CreateScheduleApi]、[ModifyScheduleApi] 的请求体类型，
 * 结构类似于 [Schedule]，但是没有 `id` 和 `creator`。
 *
 * @see CreateScheduleApi
 * @see ModifyScheduleApi
 */
@Serializable
public data class ScheduleRequestBody(
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
     * 日程开始时跳转到的子频道 id
     */
    @SerialName("jump_channel_id") val jumpChannelId: String,
    /**
     * 日程提醒类型，取值参考 [RemindTypes]。
     */
    @SerialName("remind_type") val remindType: String,
) {

    public companion object {
        /**
         * 将一个 [Schedule] 转化为请求用的 [ScheduleRequestBody]。
         */
        @JvmStatic
        @JvmName("of")
        @JsName("ofSchedule")
        public fun Schedule.toCreateBody(): ScheduleRequestBody = ScheduleRequestBody(
            name = name,
            description = description,
            startTimestamp = startTimestamp,
            endTimestamp = endTimestamp,
            jumpChannelId = jumpChannelId,
            remindType = remindType,
        )
    }
}
