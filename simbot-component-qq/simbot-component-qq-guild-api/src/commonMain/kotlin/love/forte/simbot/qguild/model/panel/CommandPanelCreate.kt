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
import kotlin.jvm.JvmStatic

/**
 * 创建指令面板的请求体。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class CommandPanelCreate @ApiModelConstructor internal constructor(
    /**
     * 面板生效场景。
     *
     * 四种场景均支持创建面板，但 channel 和 dm 场景仅支持全局配置（target_type 只能为 all）
     */
    public val scope: CommandPanelScope? = null,
    /**
     * 面板生效范围。
     *
     * 仅 c2c 和 group 场景支持 specific；channel 和 dm 场景只能传 all
     */
    @SerialName("target_type")
    public val targetType: CommandPanelTargetType? = null,
    /**
     * C2C 场景中关联的用户 OpenID，仅 c2c 场景且 target_type=specific 时有效。
     * 指定面板对这些用户生效，一次最多传 20 个。后续可通过「修改指令面板关联对象」接口增删
     */
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    /**
     * 群聊场景中关联的群 OpenID，仅 group 场景且 target_type=specific 时有效。
     * 指定面板对这些群生效，一次最多传 20 个。
     */
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
    /**
     * 面板配置内容，定义面板中展示的指令和链接项
     */
    public val panel: CommandPanel? = null,
) {
    public companion object {
        /**
         * 获取 [CommandPanelCreateBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelCreateBuilder = CommandPanelCreateBuilder()
    }

    override fun toString(): String {
        return "CommandPanelCreate(" +
            "scope=$scope, " +
            "targetType=$targetType, " +
            "userOpenids=$userOpenids, " +
            "groupOpenids=$groupOpenids, " +
            "panel=$panel)"
    }
}

/**
 * 创建指令面板后的结果。
 *
 * @property panelId 新建面板 ID。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class CommandPanelCreated @ApiModelConstructor internal constructor(
    @SerialName("panel_id")
    public val panelId: String,
) {
    override fun toString(): String {
        return "CommandPanelCreated(panelId='$panelId')"
    }
}
