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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmExposeBoxed


/**
 * [日程对象](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/model.html)
 *
 * 用于描述一个日程。
 *
 * @property id 日程 id
 * @property name 日程名称
 * @property description 日程描述
 * @property startTimestamp 日程开始时间戳(ms)
 * @property endTimestamp 日程结束时间戳(ms)
 * @property creator 创建者
 * @property jumpChannelId 日程开始时跳转到的子频道 id
 * @property scheduleRemindType 日程提醒类型，取值参考 [ScheduleRemindType]
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class Schedule
@ApiModelConstructor @JvmExposeBoxed
internal constructor(
    public val id: String,
    public val name: String,
    public val description: String,
    @SerialName("start_timestamp") public val startTimestamp: Long,
    @SerialName("end_timestamp") public val endTimestamp: Long,
    public val creator: SimpleMember,
    @SerialName("jump_channel_id") public val jumpChannelId: String,
    @get:JvmExposeBoxed
    @SerialName("remind_type") public val scheduleRemindType: ScheduleRemindType,
) {
    @Deprecated("Use scheduleRemindType instead.", ReplaceWith("scheduleRemindType.value"))
    public val remindType: String
        get() = scheduleRemindType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Schedule) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (startTimestamp != other.startTimestamp) return false
        if (endTimestamp != other.endTimestamp) return false
        if (creator != other.creator) return false
        if (jumpChannelId != other.jumpChannelId) return false
        if (scheduleRemindType != other.scheduleRemindType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + startTimestamp.hashCode()
        result = 31 * result + endTimestamp.hashCode()
        result = 31 * result + creator.hashCode()
        result = 31 * result + jumpChannelId.hashCode()
        result = 31 * result + scheduleRemindType.hashCode()
        return result
    }

    override fun toString(): String {
        return "Schedule(" +
            "id='$id', " +
            "name='$name', " +
            "description='$description', " +
            "startTimestamp=$startTimestamp, " +
            "endTimestamp=$endTimestamp, " +
            "creator=$creator, " +
            "jumpChannelId='$jumpChannelId', " +
            "remindType=$scheduleRemindType)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
    public operator fun component2(): String = name

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("description"))
    public operator fun component3(): String = description

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("startTimestamp"))
    public operator fun component4(): Long = startTimestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("endTimestamp"))
    public operator fun component5(): Long = endTimestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("creator"))
    public operator fun component6(): SimpleMember = creator

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("jumpChannelId"))
    public operator fun component7(): String = jumpChannelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("scheduleRemindType.value"))
    public operator fun component8(): String = scheduleRemindType.value

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        name: String = this.name,
        description: String = this.description,
        startTimestamp: Long = this.startTimestamp,
        endTimestamp: Long = this.endTimestamp,
        creator: SimpleMember = this.creator,
        jumpChannelId: String = this.jumpChannelId,
        remindType: String = this.scheduleRemindType.value,
    ): Schedule = Schedule(
        id = id,
        name = name,
        description = description,
        startTimestamp = startTimestamp,
        endTimestamp = endTimestamp,
        creator = creator,
        jumpChannelId = jumpChannelId,
        scheduleRemindType = ScheduleRemindType.of(remindType),
    )
    //endregion

    /**
     * 旧版日程提醒类型常量。
     *
     * 新代码请使用 [ScheduleRemindType]。
     */
    public object RemindTypes {
        /**
         * 不提醒
         */
        @Deprecated(
            "Use ScheduleRemindType.NO_REMIND_VALUE instead.",
            ReplaceWith("ScheduleRemindType.NO_REMIND_VALUE")
        )
        public const val NO_REMIND: String = ScheduleRemindType.NO_REMIND_VALUE

        /**
         * 开始时提醒
         */
        @Deprecated("Use ScheduleRemindType.AT_START_VALUE instead.", ReplaceWith("ScheduleRemindType.AT_START_VALUE"))
        public const val AT_START: String = ScheduleRemindType.AT_START_VALUE

        /**
         * 开始前 5 分钟提醒
         */
        @Deprecated(
            "Use ScheduleRemindType.AT_5_MINUTES_BEFORE_START_VALUE instead.",
            ReplaceWith("ScheduleRemindType.AT_5_MINUTES_BEFORE_START_VALUE")
        )
        public const val AT_5_MINUTES_BEFORE_START: String = ScheduleRemindType.AT_5_MINUTES_BEFORE_START_VALUE

        /**
         * 开始前 15 分钟提醒
         */
        @Deprecated(
            "Use ScheduleRemindType.AT_15_MINUTES_BEFORE_START_VALUE instead.",
            ReplaceWith("ScheduleRemindType.AT_15_MINUTES_BEFORE_START_VALUE")
        )
        public const val AT_15_MINUTES_BEFORE_START: String = ScheduleRemindType.AT_15_MINUTES_BEFORE_START_VALUE

        /**
         * 开始前 30 分钟提醒
         */
        @Deprecated(
            "Use ScheduleRemindType.AT_30_MINUTES_BEFORE_START_VALUE instead.",
            ReplaceWith("ScheduleRemindType.AT_30_MINUTES_BEFORE_START_VALUE")
        )
        public const val AT_30_MINUTES_BEFORE_START: String = ScheduleRemindType.AT_30_MINUTES_BEFORE_START_VALUE

        /**
         * 开始前 60 分钟提醒
         */
        @Deprecated(
            "Use ScheduleRemindType.AT_60_MINUTES_BEFORE_START_VALUE instead.",
            ReplaceWith("ScheduleRemindType.AT_60_MINUTES_BEFORE_START_VALUE")
        )
        public const val AT_60_MINUTES_BEFORE_START: String = ScheduleRemindType.AT_60_MINUTES_BEFORE_START_VALUE
    }

}
