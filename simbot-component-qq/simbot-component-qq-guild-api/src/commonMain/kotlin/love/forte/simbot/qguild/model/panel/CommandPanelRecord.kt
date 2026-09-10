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
 * 指令面板记录。
 *
 * 列表查询不会返回 [userOpenids] 和 [groupOpenids]；详情查询才会在适用时返回它们。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class CommandPanelRecord @ApiModelConstructor internal constructor(
    /**
     * 面板 ID。
     */
    @SerialName("panel_id")
    public val panelId: String,
    /**
     * 面板生效场景。
     */
    public val scope: CommandPanelScope,
    /**
     * 面板生效范围。
     */
    @SerialName("target_type")
    public val targetType: CommandPanelTargetType,
    /**
     * 面板配置。
     */
    public val panel: CommandPanel,
    /**
     * 面板创建时间。
     */
    @SerialName("created_at")
    public val createdAt: String? = null,
    /**
     * 面板更新时间。
     */
    @SerialName("updated_at")
    public val updatedAt: String? = null,
    /**
     * 面板版本。
     */
    public val version: Int? = null,
    /**
     * 关联的 C2C 用户 OpenID 列表。
     */
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    /**
     * 关联的群 OpenID 列表。
     */
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
) {

    override fun toString(): String {
        return "CommandPanelRecord(" +
            "panelId='$panelId', " +
            "scope='$scope', " +
            "targetType='$targetType', " +
            "panel=$panel, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "version=$version, " +
            "userOpenids=$userOpenids, " +
            "groupOpenids=$groupOpenids)"
    }


}
