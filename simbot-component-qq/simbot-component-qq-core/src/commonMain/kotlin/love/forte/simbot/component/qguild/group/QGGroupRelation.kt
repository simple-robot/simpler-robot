/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.ability.AcceptOption
import love.forte.simbot.ability.RejectOption
import love.forte.simbot.bot.GroupRelation
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.emptyCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.qguild.model.group.GroupInfo
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic


/**
 * QQ组件中与QQ群相关的操作。
 * QQ平台尚不提供机器人已加入群的完整列表，因此：
 * - [groups] 始终为空。
 * - ID查询将是一种“伪”操作，会直接将提供的 `id` 包装为一个 [QGGroup] 返回。
 * 如果此ID不是真实存在的，则进行某些操作时（比如主动发送消息）将会抛出异常。
 *
 * @author ForteScarlet
 */
public interface QGGroupRelation : GroupRelation {

    /**
     * 无法获取列表，将始终得到空结果。
     */
    override val groups: Collectable<QGGroup>
        get() = emptyCollectable()

    /**
     * 使用 [id] 直接包装为一个伪 [QGGroup]，
     * **不会**校验其真实性。
     * 如果此ID对应的群不是真实存在的，
     * 则进行某些操作时（比如主动发送消息）将会抛出异常。
     */
    @ST(
        blockingBaseName = "getGroup",
        blockingSuffix = "",
        asyncBaseName = "getGroup",
        reserveBaseName = "getGroup"
    )
    override suspend fun group(id: ID): QGGroup?

    /**
     * 每次通过 API 查询 [groupId] 对应的 QQ 群基本信息。
     * 查询失败时透传平台或网络异常，不将不存在或无权限转换为空值。
     *
     * @since 5.0
     */
    @ST
    public suspend fun groupInfo(groupId: ID): GroupInfo

    /**
     * 拉取 [groupId] 对应群的入群申请列表的收集器。返回冷集合，收集时按平台游标分页请求并翻页。
     *
     * @param groupId 群ID。平台会校验群和管理员权限。
     *
     * @since 5.0
     */
    public fun joinRequests(groupId: ID): Collectable<QGGroupJoinRequest>

    /**
     * 通过 [groupId] 对应群中 [memberId] 的入群申请。
     *
     * @param groupId 群 ID。
     * @param joinRequestId 申请 ID。使用申请列表返回的 ID。
     *
     * @since 5.0
     */
    @ST
    public suspend fun approveJoinRequest(
        groupId: ID,
        memberId: ID,
        joinRequestId: ID? = null,
        vararg options: AcceptOption,
    )

    /**
     * 拒绝 [groupId] 对应群中 [memberId] 的入群申请。
     *
     * @param groupId 群 ID。
     * @param joinRequestId 申请 ID。使用申请列表返回的 ID。
     * @param options 拒绝选项。额外支持 [QGGroupJoinRequestRejectOption] 的相关类型。
     *
     * @since 5.0
     */
    @ST
    public suspend fun rejectJoinRequest(
        groupId: ID,
        memberId: ID,
        joinRequestId: ID? = null,
        vararg options: RejectOption,
    )

    /**
     * 无法得知已加入的群的总数，始终得到 `-1`。
     */
    @JvmSynthetic
    override suspend fun groupCount(): Int = -1
}
