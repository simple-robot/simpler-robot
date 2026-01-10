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

import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmStatic


/**
 * [修改日程](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/patch_schedule.html)
 *
 * 用于修改日程子频道 `channel_id` 下 `schedule_id` 指定的日程的详情。
 *
 * - 要求操作人具有 `管理频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 *
 * @author ForteScarlet
 */
public class DeleteScheduleApi private constructor(
    channelId: String, scheduleId: String
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    public companion object Factory :
        SimpleDeleteApiDescription("/channels/{channel_id}/schedules/{schedule_id}") {

        /**
         * 构造 [DeleteScheduleApi]
         *
         * @param channelId 日程子频道ID
         * @param scheduleId 日程ID
         *
         */
        @JvmStatic
        public fun create(channelId: String, scheduleId: String): DeleteScheduleApi =
            DeleteScheduleApi(channelId, scheduleId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "schedules", scheduleId)

}
