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

package love.forte.simbot.qguild.model.panel

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic


/**
 * 面板生效范围 `target_type`
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class CommandPanelTargetType private constructor(public val value: String) {

    public companion object {
        /**
         * 对指定场景下所有目标生效的原始值。
         */
        public const val ALL_VALUE: String = "all"

        /**
         * 仅对指定用户或群生效的原始值。
         */
        public const val SPECIFIC_VALUE: String = "specific"

        /**
         * 对指定场景下的所有目标生效。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val All: CommandPanelTargetType = of(ALL_VALUE)

        /**
         * 仅对指定用户或群生效。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Specific: CommandPanelTargetType = of(SPECIFIC_VALUE)

        /**
         * 构建一个自定义值的 [CommandPanelTargetType]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(scope: String): CommandPanelTargetType = CommandPanelTargetType(scope)
    }
}
