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

package love.forte.simbot.component.qguild.panel

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.qguild.model.panel.*
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic

/**
 * 指令面板目标关联操作的可扩展字符串值对象。
 *
 * @property value 服务端使用的原始操作字符串。
 * @since 4.7.0
 */
public enum class QGCommandPanelTargetOperation(public val value: String) {
    /**
     *  添加目标关联。
     *  @see CommandPanelTargetUpdate.OP_ADD
     */
    ADD(CommandPanelTargetUpdate.OP_ADD),

    /**
     * 删除目标关联。
     * @see CommandPanelTargetUpdate.OP_DEL
     */
    DEL(CommandPanelTargetUpdate.OP_DEL),
}


/**
 * 指令面板操作器。
 *
 * 此 facade 不缓存远端面板，也不会为了补齐创建结果自动发起详情查询。
 *
 * @since 4.7.0
 */
@SubclassOptInRequired(InternalForInheritanceQGPanelApi::class)
public interface QGCommandPanelManager {
    /**
     * 创建一个只携带 [panelId] 的指令面板句柄。
     *
     * 此操作不产生请求。
     *
     * @since 4.7.0
     */
    public fun handle(panelId: ID): QGCommandPanelHandle

    /**
     * 创建一个指令面板。
     *
     * QQ API 的创建接口只返回新面板 ID 而不包含完整数据，因此本方法只返回带 ID 的句柄。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 4.7.0
     */
    @ST
    public suspend fun create(input: CommandPanelCreate): QGCommandPanelHandle

    /**
     * 创建一个指令面板。
     *
     * 创建接口只返回新面板 ID，因此本方法只返回带 ID 的句柄，不隐式查询详情。
     *
     * @param scope 指令面板生效的会话场景。
     * @param targetType 指令面板的目标范围类型。可参考 [QGCommandPanelTargetType]。
     * @param userOpenids C2C 用户 OpenID 列表，仅 c2c 场景且 target_type=specific 时有效。
     * @param groupOpenids 群 OpenID 列表，仅 group 场景且 target_type=specific 时有效。
     * @param panel 面板配置内容，定义面板中展示的指令和链接项
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 4.7.0
     */
    @ST
    public suspend fun create(
        scope: String? = null,
        targetType: String? = null,
        userOpenids: List<ID>? = null,
        groupOpenids: List<ID>? = null,
        panel: CommandPanel? = null
    ): QGCommandPanelHandle = create(
        CommandPanelCreate.builder()
            .scope(scope?.let(CommandPanelScope::of))
            .targetType(targetType)
            .addUserOpenids(userOpenids?.map { it.literal } ?: emptyList())
            .addGroupOpenids(groupOpenids?.map { it.literal } ?: emptyList())
            .panel(panel)
            .build()
    )

    /**
     * 获取指定指令面板的详情快照。
     *
     * @param panelId 指令面板 ID。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 4.7.0
     */
    @ST
    public suspend fun get(panelId: ID): QGCommandPanelRecord

    /**
     * 按 [scope] 分页获取指令面板。
     *
     * 当 [autoPagination] 为 `true` 时，返回的 [Collectable] 是基于冷流的分页收集器，
     * 它会在收集过程中自动根据上一次返回列表的尾标持续分页查询直到数据全部获取完毕。
     * 如果你只需要单页的数据，则需要将 [autoPagination] 设置为 `false`。
     *
     * 因为它基于冷流，只有当开始收集此流时才会真正产生请求。
     *
     * @param scope 生效场景。
     * @param startCursor 分页起始游标。默认为 0.
     * @param limit 每页请求数量；`null` 表示使用服务端默认值。
     * @param autoPagination 是否自动分页。默认为 true。
     *
     * @since 4.7.0
     */
    public fun list(
        scope: String,
        startCursor: String? = null,
        limit: Int? = null,
        autoPagination: Boolean = true
    ): Collectable<QGCommandPanelRecord>

    /**
     * 按 [scope] 获取全部指令面板。
     *
     * 返回的 [Collectable] 是基于冷流的分页收集器，只有当开始收集此流时才会真正产生请求。
     *
     * @param scope 生效场景。
     * @since 4.7.0
     */
    public fun list(scope: String): Collectable<QGCommandPanelRecord> = list(scope, startCursor = null)
}

/**
 * 创建一个指令面板。
 *
 * 创建接口只返回新面板 ID，因此本方法只返回带 ID 的句柄，不隐式查询详情。
 *
 * @param scope 指令面板生效的会话场景。
 * @param targetType 指令面板的目标范围类型。可参考 [QGCommandPanelTargetType]。
 * @param userOpenids C2C 用户 OpenID 列表，仅 c2c 场景且 target_type=specific 时有效。
 * @param groupOpenids 群 OpenID 列表，仅 group 场景且 target_type=specific 时有效。
 * @param panel 面板配置内容，定义面板中展示的指令和链接项
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
 * @since 5.0
 */
public suspend fun QGCommandPanelManager.create(
    scope: CommandPanelScope? = null,
    targetType: String? = null,
    userOpenids: List<ID>? = null,
    groupOpenids: List<ID>? = null,
    panel: CommandPanel? = null
): QGCommandPanelHandle = create(
    CommandPanelCreate.builder()
        .scope(scope)
        .targetType(targetType)
        .addUserOpenids(userOpenids?.map { it.literal } ?: emptyList())
        .addGroupOpenids(groupOpenids?.map { it.literal } ?: emptyList())
        .panel(panel)
        .build()
)

/**
 * 使用 [CommandPanelCreateBuilder] DSL 创建一个指令面板。
 *
 * @since 4.7.0
 */
@JvmSynthetic
public suspend inline fun QGCommandPanelManager.create(
    block: CommandPanelCreateBuilder.() -> Unit
): QGCommandPanelHandle =
    create(CommandPanelCreateBuilder().apply(block).build())

/**
 * 指令面板内容更新后的服务端回执。
 *
 * @property version 服务端返回的更新后面板版本。
 * @property source 低层 API 返回的原始回执。
 * @since 4.7.0
 */
public class QGCommandPanelUpdateReceipt internal constructor(
    override val source: CommandPanelUpdated,
) : QGObjectiveContainer<CommandPanelUpdated> {
    public val version: Int
        get() = source.version
}
