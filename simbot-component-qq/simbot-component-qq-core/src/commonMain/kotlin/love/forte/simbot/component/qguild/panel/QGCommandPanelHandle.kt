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

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelBuilder
import love.forte.simbot.suspendrunner.ST

/**
 * 指令面板操作句柄，保存面板 ID 和对其的操作行为。
 *
 * @since 5.0
 */
@SubclassOptInRequired(InternalForInheritanceQGPanelApi::class)
public interface QGCommandPanelHandle : DeleteSupport {
    /**
     * 指令面板 ID。
     */
    public val id: ID

    /**
     * 获取此面板的详情快照。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun get(): QGCommandPanelRecord

    /**
     * 整体覆盖此面板的内容与备注。
     *
     * 面板关联对象不会被修改。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun update(panel: CommandPanel): QGCommandPanelUpdateReceipt

    /**
     * 添加面板关联目标。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun addTargets(userOpenids: Collection<ID>?, groupOpenids: Collection<ID>?)

    /**
     * 添加用户相关的面板关联目标。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun addUserTargets(userOpenids: Collection<ID>) {
        addTargets(userOpenids, null)
    }

    /**
     * 添加群聊相关的面板关联目标。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun addGroupTargets(groupOpenids: Collection<ID>) {
        addTargets(null, groupOpenids)
    }

    /**
     * 删除面板关联目标。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun removeTargets(userOpenids: Collection<ID>?, groupOpenids: Collection<ID>?)

    /**
     * 删除用户面板关联目标。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun removeUserTargets(userOpenids: Collection<ID>) {
        removeTargets(userOpenids, null)
    }

    /**
     * 删除群聊面板关联目标。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    public suspend fun removeGroupTargets(groupOpenids: Collection<ID>) {
        removeTargets(null, groupOpenids)
    }

    /**
     * 删除此指令面板。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 5.0
     */
    @ST
    override suspend fun delete(vararg options: DeleteOption)
}

/**
 * 使用 [love.forte.simbot.qguild.model.panel.CommandPanelBuilder] DSL 整体覆盖此面板的内容与备注。
 *
 * @since 5.0
 */
public suspend inline fun QGCommandPanelHandle.update(
    block: CommandPanelBuilder.() -> Unit
): QGCommandPanelUpdateReceipt = update(CommandPanelBuilder().apply(block).build())
