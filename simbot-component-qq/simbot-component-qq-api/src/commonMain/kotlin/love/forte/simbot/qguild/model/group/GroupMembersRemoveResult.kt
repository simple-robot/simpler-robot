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
 * 批量移除群成员的结果，移除成功时附带拉黑仍可能失败。
 *
 * @property removeMembersResult 移除状态；成功时返回 [`success`][REMOVE_MEMBERS_RESULT_SUCCESS]。
 * @property addToMemberBlacklistFailOpenids 拉黑失败的 openid。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupMembersRemoveResult @ApiModelConstructor internal constructor(
    @SerialName("remove_members_result")
    public val removeMembersResult: String,
    @SerialName("add_to_member_blacklist_fail_openids")
    public val addToMemberBlacklistFailOpenids: List<String>,
) {
    public companion object {
        public const val REMOVE_MEMBERS_RESULT_SUCCESS: String = "success"
    }

    public val isRemoveMembersSuccess: Boolean
        get() = removeMembersResult == REMOVE_MEMBERS_RESULT_SUCCESS

    override fun toString(): String =
        "GroupMembersRemoveResult(" +
            "removeMembersResult=$removeMembersResult, " +
            "addToMemberBlacklistFailOpenids=$addToMemberBlacklistFailOpenids" +
            ")"
}
