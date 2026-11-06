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

import kotlin.jvm.JvmSynthetic
import kotlin.time.Instant

@Retention(AnnotationRetention.BINARY)
@DslMarker
private annotation class GroupJoinApprovalStrategyBuilderDsl

/**
 * 通过 DSL 构建 [GroupJoinApprovalStrategyCreate]。
 *
 * @param block 构建器配置。
 * @throws IllegalArgumentException 群标识组合无效、群列表为空、群 openid 或 QQ 群号为空白。
 * @since 5.0
 */
@JvmSynthetic
public inline fun GroupJoinApprovalStrategyCreate(
    block: GroupJoinApprovalStrategyCreateBuilder.() -> Unit,
): GroupJoinApprovalStrategyCreate =
    GroupJoinApprovalStrategyCreateBuilder().apply(block).build()

/**
 * 创建入群自动审批策略的构建器。
 *
 * @since 5.0
 */
@GroupJoinApprovalStrategyBuilderDsl
public class GroupJoinApprovalStrategyCreateBuilder {
    private val groupOpenids: MutableList<String> = mutableListOf()

    private val groupIds: MutableList<String> = mutableListOf()

    /**
     * 是否启用策略，true 对应 on，false 对应 off；不传默认启用。
     */
    public var isEnable: Boolean? = null

    /**
     * 过期时间（RFC3339 格式）；不传默认一年过期。
     */
    public var expireAt: Instant? = null

    /**
     * 策略备注，最多 255 个汉字，不必填。
     */
    public var remark: String? = null

    /**
     * 添加一个群 openid。
     */
    public fun addGroupOpenid(groupOpenid: String): GroupJoinApprovalStrategyCreateBuilder = also {
        groupOpenids.add(groupOpenid)
    }

    /**
     * 批量添加群 openid。
     */
    public fun addGroupOpenids(groupOpenids: Iterable<String>): GroupJoinApprovalStrategyCreateBuilder = also {
        this.groupOpenids.addAll(groupOpenids)
    }

    /**
     * 清空已添加的群 openid。
     */
    public fun clearGroupOpenids(): GroupJoinApprovalStrategyCreateBuilder = also {
        groupOpenids.clear()
    }

    /**
     * 添加一个 QQ 群号。
     */
    public fun addGroupId(groupId: String): GroupJoinApprovalStrategyCreateBuilder = also {
        groupIds.add(groupId)
    }

    /**
     * 批量添加 QQ 群号。
     */
    public fun addGroupIds(groupIds: Iterable<String>): GroupJoinApprovalStrategyCreateBuilder = also {
        this.groupIds.addAll(groupIds)
    }

    /**
     * 清空已添加的 QQ 群号。
     */
    public fun clearGroupIds(): GroupJoinApprovalStrategyCreateBuilder = also {
        groupIds.clear()
    }

    /**
     * 设置是否启用策略，true 对应 on，false 对应 off；不传默认启用。
     */
    public fun isEnable(isEnable: Boolean?): GroupJoinApprovalStrategyCreateBuilder = also {
        this.isEnable = isEnable
    }

    /**
     * 设置过期时间（RFC3339 格式）；不传默认一年过期。
     */
    public fun expireAt(expireAt: Instant?): GroupJoinApprovalStrategyCreateBuilder = also {
        this.expireAt = expireAt
    }

    /**
     * 设置策略备注，最多 255 个汉字，不必填。
     */
    public fun remark(remark: String?): GroupJoinApprovalStrategyCreateBuilder = also {
        this.remark = remark
    }

    /**
     * 构建 [GroupJoinApprovalStrategyCreate]。
     *
     * @throws IllegalArgumentException 未指定群标识、同时指定两种群标识、群 openid 或 QQ 群号为空白。
     */
    public fun build(): GroupJoinApprovalStrategyCreate {
        return GroupJoinApprovalStrategyCreate(
            groupOpenids = groupOpenids.takeIf { it.isNotEmpty() }?.toList(),
            groupIds = groupIds.takeIf { it.isNotEmpty() }?.toList(),
            isEnable = isEnable,
            expireAt = expireAt,
            remark = remark,
        )
    }
}

/**
 * 通过 DSL 构建 [GroupJoinApprovalStrategyModify]。
 *
 * @param block 构建器配置。
 * @throws IllegalArgumentException 未设置任何修改字段。
 * @since 5.0
 */
@JvmSynthetic
public inline fun GroupJoinApprovalStrategyModify(
    block: GroupJoinApprovalStrategyModifyBuilder.() -> Unit,
): GroupJoinApprovalStrategyModify =
    GroupJoinApprovalStrategyModifyBuilder().apply(block).build()

/**
 * 修改入群自动审批策略的构建器。
 *
 * @since 5.0
 */
@GroupJoinApprovalStrategyBuilderDsl
public class GroupJoinApprovalStrategyModifyBuilder {
    /**
     * 是否启用策略，true 对应 on，false 对应 off。
     */
    public var isEnable: Boolean? = null

    /**
     * 过期时间（RFC3339 格式）。
     */
    public var expireAt: Instant? = null

    /**
     * 关联群增删操作；群标识形式须与创建时一致。
     */
    public var groupAction: GroupJoinApprovalStrategyGroupAction? = null

    /**
     * 策略备注，最多 255 个汉字，不必填。
     */
    public var remark: String? = null

    /**
     * 设置是否启用策略，true 对应 on，false 对应 off。
     */
    public fun isEnable(isEnable: Boolean?): GroupJoinApprovalStrategyModifyBuilder = also {
        this.isEnable = isEnable
    }

    /**
     * 设置过期时间（RFC3339 格式）。
     */
    public fun expireAt(expireAt: Instant?): GroupJoinApprovalStrategyModifyBuilder = also {
        this.expireAt = expireAt
    }

    /**
     * 设置关联群增删操作；群标识形式须与创建时一致。
     */
    public fun groupAction(
        groupAction: GroupJoinApprovalStrategyGroupAction?
    ): GroupJoinApprovalStrategyModifyBuilder = also {
        this.groupAction = groupAction
    }

    /**
     * 设置策略备注，最多 255 个汉字，不必填。
     */
    public fun remark(remark: String?): GroupJoinApprovalStrategyModifyBuilder = also {
        this.remark = remark
    }

    /**
     * 通过 DSL 设置关联群增删操作。
     */
    @JvmSynthetic
    public fun groupAction(
        block: GroupJoinApprovalStrategyGroupActionBuilder.() -> Unit,
    ): GroupJoinApprovalStrategyModifyBuilder = also {
        groupAction = GroupJoinApprovalStrategyGroupActionBuilder().apply(block).build()
    }

    /**
     * 构建 [GroupJoinApprovalStrategyModify]。
     *
     * @throws IllegalArgumentException 未设置任何修改字段。
     */
    public fun build(): GroupJoinApprovalStrategyModify {
        return GroupJoinApprovalStrategyModify(
            isEnable = isEnable,
            expireAt = expireAt,
            groupAction = groupAction,
            remark = remark,
        )
    }
}

/**
 * 通过 DSL 构建 [GroupJoinApprovalStrategyGroupAction]。
 *
 * @param block 构建器配置。
 * @throws IllegalArgumentException 操作类型缺失、群标识组合无效、群列表为空、群 openid 或 QQ 群号为空白。
 * @since 5.0
 */
@JvmSynthetic
public inline fun GroupJoinApprovalStrategyGroupAction(
    block: GroupJoinApprovalStrategyGroupActionBuilder.() -> Unit,
): GroupJoinApprovalStrategyGroupAction =
    GroupJoinApprovalStrategyGroupActionBuilder().apply(block).build()

/**
 * 入群自动审批策略的关联群增删操作的构建器。
 *
 * @since 5.0
 */
@GroupJoinApprovalStrategyBuilderDsl
public class GroupJoinApprovalStrategyGroupActionBuilder {
    /**
     * 操作类型：ADD 新增关联群，DEL 删除关联群。
     */
    public var op: GroupJoinApprovalStrategyOp? = null

    private val groupOpenids: MutableList<String> = mutableListOf()

    private val groupIds: MutableList<String> = mutableListOf()

    /**
     * 设置操作类型：ADD 新增关联群，DEL 删除关联群。
     */
    public fun op(op: GroupJoinApprovalStrategyOp?): GroupJoinApprovalStrategyGroupActionBuilder = also {
        this.op = op
    }

    /**
     * 添加一个群 openid。
     */
    public fun addGroupOpenid(groupOpenid: String): GroupJoinApprovalStrategyGroupActionBuilder = also {
        groupOpenids.add(groupOpenid)
    }

    /**
     * 批量添加群 openid。
     */
    public fun addGroupOpenids(groupOpenids: Iterable<String>): GroupJoinApprovalStrategyGroupActionBuilder = also {
        this.groupOpenids.addAll(groupOpenids)
    }

    /**
     * 清空已添加的群 openid。
     */
    public fun clearGroupOpenids(): GroupJoinApprovalStrategyGroupActionBuilder = also {
        groupOpenids.clear()
    }

    /**
     * 添加一个 QQ 群号。
     */
    public fun addGroupId(groupId: String): GroupJoinApprovalStrategyGroupActionBuilder = also {
        groupIds.add(groupId)
    }

    /**
     * 批量添加 QQ 群号。
     */
    public fun addGroupIds(groupIds: Iterable<String>): GroupJoinApprovalStrategyGroupActionBuilder = also {
        this.groupIds.addAll(groupIds)
    }

    /**
     * 清空已添加的 QQ 群号。
     */
    public fun clearGroupIds(): GroupJoinApprovalStrategyGroupActionBuilder = also {
        groupIds.clear()
    }

    /**
     * 构建 [GroupJoinApprovalStrategyGroupAction]。
     *
     * @throws IllegalArgumentException 未设置操作类型、未指定群标识、同时指定两种群标识、群 openid 或 QQ 群号为空白。
     */
    public fun build(): GroupJoinApprovalStrategyGroupAction {
        return GroupJoinApprovalStrategyGroupAction(
            op = requireNotNull(op) { "op must be specified" },
            groupOpenids = groupOpenids.takeIf { it.isNotEmpty() }?.toList(),
            groupIds = groupIds.takeIf { it.isNotEmpty() }?.toList(),
        )
    }
}
