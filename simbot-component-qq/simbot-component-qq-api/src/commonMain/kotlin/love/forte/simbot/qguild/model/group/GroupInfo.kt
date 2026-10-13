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
 * QQ群基本信息。
 *
 * @property groupOpenid 群 OpenID。
 * @property groupName 群名称。
 * @property groupFingerMemo 群简介。
 * @property groupClassText 群分类。
 * @property groupTags 群标签。
 * @property groupMemberNum 群成员人数。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupInfo @ApiModelConstructor internal constructor(
    @SerialName("group_openid")
    public val groupOpenid: String,
    @SerialName("group_name")
    public val groupName: String,
    @SerialName("group_finger_memo")
    public val groupFingerMemo: String,
    @SerialName("group_class_text")
    public val groupClassText: String,
    @SerialName("group_tags")
    public val groupTags: List<String>,
    @SerialName("group_member_num")
    public val groupMemberNum: Int,
) {
    override fun toString(): String {
        return "GroupInfo(" +
            "groupOpenid='$groupOpenid', " +
            "groupName='$groupName', " +
            "groupFingerMemo='$groupFingerMemo', " +
            "groupClassText='$groupClassText', " +
            "groupTags=$groupTags, " +
            "groupMemberNum=$groupMemberNum)"
    }
}
