/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.channel.schedules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.model.Schedule
import love.forte.simbot.qguild.model.ScheduleRemindType
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 * 用于 [CreateScheduleApi]、[ModifyScheduleApi] 的请求体类型，
 * 结构类似于 [Schedule]，但是没有 `id` 和 `creator`。
 *
 * @property name 日程名称
 * @property description 日程描述
 * @property startTimestamp 日程开始时间戳(ms)
 * @property endTimestamp 日程结束时间戳(ms)
 * @property jumpChannelId 日程开始时跳转到的子频道 id
 * @property remindType 日程提醒类型，取值参考 [ScheduleRemindType]
 * @see CreateScheduleApi
 * @see ModifyScheduleApi
 */
@Serializable
public class ScheduleRequestBody @ApiModelConstructor public constructor(
    public val name: String,
    public val description: String,
    @SerialName("start_timestamp") public val startTimestamp: Long,
    @SerialName("end_timestamp") public val endTimestamp: Long,
    @SerialName("jump_channel_id") public val jumpChannelId: String,
    @SerialName("remind_type") public val remindType: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScheduleRequestBody) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (startTimestamp != other.startTimestamp) return false
        if (endTimestamp != other.endTimestamp) return false
        if (jumpChannelId != other.jumpChannelId) return false
        if (remindType != other.remindType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + startTimestamp.hashCode()
        result = 31 * result + endTimestamp.hashCode()
        result = 31 * result + jumpChannelId.hashCode()
        result = 31 * result + remindType.hashCode()
        return result
    }

    override fun toString(): String =
        "ScheduleRequestBody(" +
            "name=$name, " +
            "description=$description, " +
            "startTimestamp=$startTimestamp, " +
            "endTimestamp=$endTimestamp, " +
            "jumpChannelId=$jumpChannelId, " +
            "remindType=$remindType)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
    public operator fun component1(): String = name

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("description"))
    public operator fun component2(): String = description

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("startTimestamp"))
    public operator fun component3(): Long = startTimestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("endTimestamp"))
    public operator fun component4(): Long = endTimestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("jumpChannelId"))
    public operator fun component5(): String = jumpChannelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("remindType"))
    public operator fun component6(): String = remindType

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        name: String = this.name,
        description: String = this.description,
        startTimestamp: Long = this.startTimestamp,
        endTimestamp: Long = this.endTimestamp,
        jumpChannelId: String = this.jumpChannelId,
        remindType: String = this.remindType,
    ): ScheduleRequestBody = ScheduleRequestBody(
        name = name,
        description = description,
        startTimestamp = startTimestamp,
        endTimestamp = endTimestamp,
        jumpChannelId = jumpChannelId,
        remindType = remindType,
    )

    public companion object {
        /**
         * 构造一个 [ScheduleRequestBody]。
         *
         * @since 5.0
         */
        @JvmStatic
        public fun of(
            name: String,
            description: String,
            startTimestamp: Long,
            endTimestamp: Long,
            jumpChannelId: String,
            remindType: String,
        ): ScheduleRequestBody = ScheduleRequestBody(
            name = name,
            description = description,
            startTimestamp = startTimestamp,
            endTimestamp = endTimestamp,
            jumpChannelId = jumpChannelId,
            remindType = remindType,
        )

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
            remindType = scheduleRemindType.value,
        )
    }
}
