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

package love.forte.simbot.component.qguild.internal.panel

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.common.utils.runCatchingCancellable
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.panel.InternalForInheritanceQGPanelApi
import love.forte.simbot.component.qguild.panel.QGCommandPanelHandle
import love.forte.simbot.component.qguild.panel.QGCommandPanelUpdateReceipt
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.qguild.api.panel.DeleteCommandPanelApi
import love.forte.simbot.qguild.api.panel.GetCommandPanelApi
import love.forte.simbot.qguild.api.panel.ModifyCommandPanelApi
import love.forte.simbot.qguild.api.panel.ModifyCommandPanelTargetApi
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelRecord
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdate


/**
 * @since 4.7.0
 */
@OptIn(ExperimentalQGApi::class, InternalForInheritanceQGPanelApi::class)
internal class QGCommandPanelHandleOperator(
    private val bot: QGBot,
    override val id: ID,
) : QGCommandPanelHandle {
    override suspend fun get(): QGCommandPanelRecordImpl {
        val source = bot.executeData(GetCommandPanelApi.create(id.literal))
        return source.toSnapshot(bot)
    }

    override suspend fun update(panel: CommandPanel): QGCommandPanelUpdateReceipt {
        val source = bot.executeData(ModifyCommandPanelApi.create(id.literal, panel))
        return QGCommandPanelUpdateReceipt(source = source)
    }

    override suspend fun addTargets(userOpenids: Collection<ID>?, groupOpenids: Collection<ID>?) {
        updateTargets(CommandPanelTargetUpdate.OP_ADD, userOpenids, groupOpenids)
    }

    override suspend fun removeTargets(userOpenids: Collection<ID>?, groupOpenids: Collection<ID>?) {
        updateTargets(CommandPanelTargetUpdate.OP_DEL, userOpenids, groupOpenids)
    }

    /**
     * 删除此指令面板。
     *
     * 成功时返回 `Unit`，不会隐式查询或缓存删除后的状态。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     * @since 4.7.0
     */
    override suspend fun delete(vararg options: DeleteOption) {
        suspend fun doDelete() {
            bot.executeData(DeleteCommandPanelApi.create(id.literal))
        }

        if (options.any { it == StandardDeleteOption.IGNORE_ON_FAILURE }) {
            runCatchingCancellable {
                doDelete()
            }.onFailure { e -> logger.debug("Failed to delete command panel {}: {}", id, e.message, e) }
        } else {
            doDelete()
        }
    }

    private suspend fun updateTargets(
        operation: String,
        userOpenids: Collection<ID>?,
        groupOpenids: Collection<ID>?
    ) {
        val api = ModifyCommandPanelTargetApi.create(id.literal) {
            op = operation
            userOpenids?.forEach { addUserOpenid(it.literal) }
            groupOpenids?.forEach { addGroupOpenid(it.literal) }
        }
        bot.executeData(api)
    }

    override fun toString(): String {
        return "QGCommandPanelHandleOperator(id=$id)"
    }

    companion object {
        private val logger = LoggerFactory.logger<QGCommandPanelHandleOperator>()
    }
}

private fun CommandPanelRecord.toSnapshot(bot: QGBot): QGCommandPanelRecordImpl = QGCommandPanelRecordImpl(bot, this)
