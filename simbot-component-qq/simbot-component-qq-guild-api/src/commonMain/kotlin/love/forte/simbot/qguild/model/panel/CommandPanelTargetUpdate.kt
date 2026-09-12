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
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmStatic

/**
 * 修改指令面板关联对象的请求体。
 *
 * @property op 关联操作类型。
 * @property userOpenids C2C 场景中要操作的用户 OpenID。
 * @property groupOpenids 群聊场景中要操作的群 OpenID。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@ApiModel
@Serializable
public class CommandPanelTargetUpdate @ApiModelConstructor internal constructor(
    @get:JvmExposeBoxed
    public val op: CommandPanelTargetUpdateOp? = null,
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
) {
    public companion object {
        /**
         * 获取 [CommandPanelTargetUpdateBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelTargetUpdateBuilder = CommandPanelTargetUpdateBuilder()
    }

    override fun toString(): String {
        return "CommandPanelTargetUpdate(op=$op, userOpenids=$userOpenids, groupOpenids=$groupOpenids)"
    }
}

/**
 * 指令面板更新后的版本。
 *
 * @property version 更新后的面板版本。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class CommandPanelUpdated @ApiModelConstructor internal constructor(
    public val version: Int = 0,
) {
    override fun toString(): String {
        return "CommandPanelUpdated(version=$version)"
    }
}
