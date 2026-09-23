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

package love.forte.simbot.qguild.model.group

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 群级全员禁言模式。
 *
 * 已知取值为 [None]、[Always] 与 [Schedule]；未知平台取值通过 [of] 保留。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class GroupGlobalMuteMode private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 未开启全员禁言的原始值。
         */
        public const val NONE_VALUE: String = "none"

        /**
         * 始终全员禁言的原始值。
         */
        public const val ALWAYS_VALUE: String = "always"

        /**
         * 按时间规则禁言的原始值。
         */
        public const val SCHEDULE_VALUE: String = "schedule"

        /**
         * 未开启全员禁言。
         */
        @JvmStatic
        public val None: GroupGlobalMuteMode = GroupGlobalMuteMode(NONE_VALUE)

        /**
         * 始终全员禁言。
         */
        @JvmStatic
        public val Always: GroupGlobalMuteMode = GroupGlobalMuteMode(ALWAYS_VALUE)

        /**
         * 按时间规则禁言。
         */
        @JvmStatic
        public val Schedule: GroupGlobalMuteMode = GroupGlobalMuteMode(SCHEDULE_VALUE)

        /**
         * 根据平台原始值构造禁言模式，保留未知值。
         */
        @JvmStatic
        public fun of(value: String): GroupGlobalMuteMode = GroupGlobalMuteMode(value)
    }
}
