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

package love.forte.simbot.qguild.api.channel.schedules

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.Schedule
import kotlin.jvm.JvmStatic


/**
 * [获取日程详情](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/get_schedule.html)
 *
 * 获取日程子频道 `channel_id` 下 `schedule_id` 指定的的日程的详情。
 *
 * @author ForteScarlet
 */
public class GetScheduleApi private constructor(
    channelId: String, scheduleId: String
) : GetQQGuildApi<Schedule>() {
    public companion object Factory : SimpleGetApiDescription("/channels/{channel_id}/schedules/{schedule_id}") {

        /**
         * 构造 [GetScheduleApi]。
         *
         * @param channelId 频道ID
         * @param scheduleId 日程ID
         *
         */
        @JvmStatic
        public fun create(channelId: String, scheduleId: String): GetScheduleApi = GetScheduleApi(channelId, scheduleId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<Schedule> get() = Schedule.serializer()

    override val path: Array<String> = arrayOf("channels", channelId, "schedules", scheduleId)

}
