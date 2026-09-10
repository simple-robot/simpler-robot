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
 * 指令面板中的元素的元素类型
 * @see CommandPanel.Item.type
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class CommandPanelItemType private constructor(public val value: String) {
    public companion object {
        /**
         * 指令元素类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Command: CommandPanelItemType = CommandPanelItemType(CommandPanelItemTypeValues.COMMAND)

        /**
         * 链接元素类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Link: CommandPanelItemType = CommandPanelItemType(CommandPanelItemTypeValues.LINK)

        /**
         * 构建一个自定义值内容的 [CommandPanelItemType]。
         */
        @JvmStatic
        public fun of(value: String): CommandPanelItemType = CommandPanelItemType(value)


    }
}
