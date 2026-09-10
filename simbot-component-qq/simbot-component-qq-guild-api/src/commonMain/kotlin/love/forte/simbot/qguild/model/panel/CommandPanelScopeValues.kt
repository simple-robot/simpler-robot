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
 * 与指令面板生效场景相关的已知可选值的常量类。
 *
 * @see CommandPanelRecord.scope
 * @see CommandPanelScope
 * @since 5.0
 */
public object CommandPanelScopeValues {
    /**
     * C2C 单聊场景。
     */
    public const val C2C: String = "c2c"

    /**
     * 群聊场景。
     */
    public const val GROUP: String = "group"

    /**
     * 文字子频道场景。
     */
    public const val CHANNEL: String = "channel"

    /**
     * 频道私信场景。
     */
    public const val DM: String = "dm"
}
