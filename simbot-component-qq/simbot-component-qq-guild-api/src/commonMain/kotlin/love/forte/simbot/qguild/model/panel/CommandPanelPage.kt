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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor

/**
 * 指令面板分页查询结果。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelPage @ApiModelConstructor internal constructor(
    /**
     * 本页记录。
     */
    public val records: List<CommandPanelRecord> = emptyList(),
    /**
     * 下一页游标。
     */
    @SerialName("next_cursor")
    public val nextCursor: String = "",
    /**
     * 是否已经到达最后一页。
     */
    @SerialName("is_end")
    public val isEnd: Boolean = false,
) {
    override fun toString(): String {
        return "CommandPanelPage(records=$records, nextCursor='$nextCursor', isEnd=$isEnd)"
    }
}
