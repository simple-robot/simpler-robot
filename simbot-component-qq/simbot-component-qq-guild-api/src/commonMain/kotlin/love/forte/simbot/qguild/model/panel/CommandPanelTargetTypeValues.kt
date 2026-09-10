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

/**
 * 与指令面板生效范围相关的已知可选值的常量类。
 *
 * @see CommandPanelRecord.targetType
 * @see CommandPanelTargetType
 * @since 5.0
 */
public object CommandPanelTargetTypeValues {
    /**
     * 对指定场景下的所有目标生效。
     */
    public const val ALL: String = "all"

    /**
     * 仅对指定用户或群生效。
     */
    public const val SPECIFIC: String = "specific"
}
