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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor

/**
 * 群成员列表的一页结果。
 *
 * @property members 本页成员，每页最多 30 条。
 * @property nextCursor 下一页游标，空串表示已到末页。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupMemberPage @ApiModelConstructor internal constructor(
    public val members: List<GroupMemberInfo>,
    @SerialName("next_cursor")
    public val nextCursor: String,
) {
    override fun toString(): String =
        "GroupMemberPage(members=$members, nextCursor=$nextCursor)"
}
