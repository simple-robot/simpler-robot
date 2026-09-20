/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 日程提醒类型。
 *
 * @see Schedule.remindType
 * @property value 提醒类型的原始值。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class ScheduleRemindType private constructor(public val value: String) {
    public companion object {
        /**
         * 不提醒的原始值。
         */
        public const val NO_REMIND_VALUE: String = "0"

        /**
         * 开始时提醒的原始值。
         */
        public const val AT_START_VALUE: String = "1"

        /**
         * 开始前 5 分钟提醒的原始值。
         */
        public const val AT_5_MINUTES_BEFORE_START_VALUE: String = "2"

        /**
         * 开始前 15 分钟提醒的原始值。
         */
        public const val AT_15_MINUTES_BEFORE_START_VALUE: String = "3"

        /**
         * 开始前 30 分钟提醒的原始值。
         */
        public const val AT_30_MINUTES_BEFORE_START_VALUE: String = "4"

        /**
         * 开始前 60 分钟提醒的原始值。
         */
        public const val AT_60_MINUTES_BEFORE_START_VALUE: String = "5"

        /**
         * 不提醒。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val NoRemind: ScheduleRemindType = ScheduleRemindType(NO_REMIND_VALUE)

        /**
         * 开始时提醒。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val AtStart: ScheduleRemindType = ScheduleRemindType(AT_START_VALUE)

        /**
         * 开始前 5 分钟提醒。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val At5MinutesBeforeStart: ScheduleRemindType =
            ScheduleRemindType(AT_5_MINUTES_BEFORE_START_VALUE)

        /**
         * 开始前 15 分钟提醒。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val At15MinutesBeforeStart: ScheduleRemindType =
            ScheduleRemindType(AT_15_MINUTES_BEFORE_START_VALUE)

        /**
         * 开始前 30 分钟提醒。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val At30MinutesBeforeStart: ScheduleRemindType =
            ScheduleRemindType(AT_30_MINUTES_BEFORE_START_VALUE)

        /**
         * 开始前 60 分钟提醒。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val At60MinutesBeforeStart: ScheduleRemindType =
            ScheduleRemindType(AT_60_MINUTES_BEFORE_START_VALUE)

        /**
         * 构建一个自定义的日程提醒类型。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: String): ScheduleRemindType = ScheduleRemindType(value)
    }
}
