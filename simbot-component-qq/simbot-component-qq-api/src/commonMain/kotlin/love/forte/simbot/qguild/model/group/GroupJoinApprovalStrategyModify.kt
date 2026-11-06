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
import kotlin.time.Instant

/**
 * 修改入群自动审批策略的请求体。
 *
 * @property isEnable 是否启用策略，`true` 对应 `on`，`false` 对应 `off`。
 * @property expireAt 过期时间（RFC3339 格式）。
 * @property groupAction 关联群增删操作。群标识形式须与创建时一致。
 * @property remark 策略备注，最多 255 个汉字，不必填。
 * @since 5.0
 */
@OptIn(ExperimentalSerializationApi::class)
@ApiModel
@Serializable
public class GroupJoinApprovalStrategyModify @ApiModelConstructor internal constructor(
    @SerialName("is_enable")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @Serializable(with = GroupJoinApprovalStrategyEnabledSerializer::class)
    public val isEnable: Boolean? = null,
    @SerialName("expire_at")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val expireAt: Instant? = null,
    @SerialName("group_action")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val groupAction: GroupJoinApprovalStrategyGroupAction? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val remark: String? = null,
) {
    init {
        require(isEnable != null || expireAt != null || groupAction != null || remark != null) {
            "At least one field must be specified"
        }
    }

    public companion object {
        /**
         * 获取 [GroupJoinApprovalStrategyModifyBuilder]。
         */
        @JvmStatic
        public fun builder(): GroupJoinApprovalStrategyModifyBuilder = GroupJoinApprovalStrategyModifyBuilder()
    }

    override fun toString(): String {
        return "GroupJoinApprovalStrategyModify(" +
            "isEnable=$isEnable, " +
            "expireAt=$expireAt, " +
            "groupAction=$groupAction, " +
            "remark=$remark)"
    }
}
