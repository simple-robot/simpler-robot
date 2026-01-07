/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.blacklist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.api.blacklist.CreateBlacklistApi
import love.forte.simbot.suspendrunner.ST

/**
 * KOOK 服务器黑名单相关内容的操作器。
 *
 * @since 4.4.0
 *
 * @author ForteScarlet
 */
@ExperimentalBlacklistApi
public interface KookGuildBlacklistOperator {
    /**
     * 服务器ID
     */
    public val guildId: ID

    /**
     * 获取黑名单的分页列表。
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页列表
     */
    @ST
    public suspend fun list(page: Int?, size: Int?): ListData<KookBlacklistItem>

    /**
     * 获取全量列表数据。
     */
    @ST
    public suspend fun all(): List<KookBlacklistItem> = flow().toList()

    /**
     * 获取黑名单列表元素的 Flow。
     *
     * @param batchSize 每批次大小
     */
    public fun flow(batchSize: Int? = null): Flow<KookBlacklistItem>

    /**
     * 添加一个目标到黑名单。
     *
     * @param guildId 服务器id
     * @param targetId 目标用户id
     * @param remark 加入黑名单的原因
     * @param delMsgDays 删除最近几天的消息，最大 7 天, 默认 0
     * @see CreateBlacklistApi
     */
    @ST
    public suspend fun add(targetId: ID, remark: String?, delMsgDays: Int?)

    /**
     * 添加一个目标到黑名单。
     *
     * @param guildId 服务器id
     * @param targetId 目标用户id
     * @see CreateBlacklistApi
     */
    @ST
    public suspend fun add(targetId: ID) {
        add(targetId, null, null)
    }

    /**
     * 删除指定黑名单内的目标。
     *
     * @throws RuntimeException 如果在请求API过程中出现任何非预期异常，
     * 并且没有提供 [StandardDeleteOption.IGNORE_ON_FAILURE]
     */
    @ST
    public suspend fun delete(targetId: ID, vararg options: DeleteOption)
}
