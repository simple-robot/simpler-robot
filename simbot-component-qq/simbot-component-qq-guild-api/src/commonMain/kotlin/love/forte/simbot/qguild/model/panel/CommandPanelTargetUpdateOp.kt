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
 * 修改指令面板关联对象的操作类型 `op`。
 *
 * @see CommandPanelTargetUpdate.op
 *
 * @since 5.0
 * @author Forte Scarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class CommandPanelTargetUpdateOp private constructor(public val value: String) {
    public companion object {
        /**
         * 添加关联对象操作的原始值。
         */
        public const val ADD_VALUE: String = "add"

        /**
         * 删除关联对象操作的原始值。
         */
        public const val DEL_VALUE: String = "del"

        /**
         * 添加关联对象的操作类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Add: CommandPanelTargetUpdateOp = CommandPanelTargetUpdateOp(ADD_VALUE)

        /**
         * 删除关联对象的操作类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Del: CommandPanelTargetUpdateOp = CommandPanelTargetUpdateOp(DEL_VALUE)

        /**
         * 构建一个自定义值的 [CommandPanelTargetUpdateOp]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: String): CommandPanelTargetUpdateOp = CommandPanelTargetUpdateOp(value)
    }
}
