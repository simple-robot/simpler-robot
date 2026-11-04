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

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import kotlin.jvm.JvmStatic

/**
 * 入群自动审批策略的关联群增删操作的请求体。
 *
 * @property op 操作类型：ADD 新增关联群，DEL 删除关联群。
 * @property groupOpenids 待操作的群 openid 列表；与 `groupIds` 二选一必填。
 * @property groupIds 待操作的 QQ 群号列表；与 `groupOpenids` 二选一必填。
 * @since 5.0
 */
@OptIn(ExperimentalSerializationApi::class)
@ApiModel
@Serializable
public class GroupJoinApprovalStrategyGroupAction @ApiModelConstructor internal constructor(
    public val op: GroupJoinApprovalStrategyOp,
    @SerialName("group_openids")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val groupOpenids: List<String>? = null,
    @SerialName("group_ids")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val groupIds: List<String>? = null,
) {
    init {
        require((groupOpenids != null) xor (groupIds != null)) {
            "Exactly one of groupOpenids and groupIds must be specified"
        }
        groupOpenids?.also {
            require(it.isNotEmpty()) { "groupOpenids must not be empty" }
            require(it.all { id -> id.isNotBlank() }) { "groupOpenids must not contain blank IDs" }
        }
        groupIds?.also {
            require(it.isNotEmpty()) { "groupIds must not be empty" }
            require(it.all { id -> id.isNotBlank() }) { "groupIds must not contain blank IDs" }
        }
    }

    public companion object {
        /**
         * 获取 [GroupJoinApprovalStrategyGroupActionBuilder]。
         */
        @JvmStatic
        public fun builder(): GroupJoinApprovalStrategyGroupActionBuilder = GroupJoinApprovalStrategyGroupActionBuilder()
    }

    override fun toString(): String {
        return "GroupJoinApprovalStrategyGroupAction(" +
            "op=$op, " +
            "groupOpenids=$groupOpenids, " +
            "groupIds=$groupIds)"
    }
}
