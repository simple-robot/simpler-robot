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

package love.forte.simbot.component.qguild.internal.group

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.common.utils.runCatchingCancellable
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.group.InternalForInheritanceQGGroupJoinApprovalStrategyApi
import love.forte.simbot.component.qguild.group.QGGroupJoinApprovalStrategyHandle
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.qguild.api.group.DeleteGroupJoinApprovalStrategyApi
import love.forte.simbot.qguild.api.group.ExecuteGroupJoinApprovalStrategyApi
import love.forte.simbot.qguild.api.group.ModifyGroupJoinApprovalStrategyApi
import love.forte.simbot.qguild.api.group.ModifyGroupJoinApprovalStrategyWhitelistApi
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModified
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModify
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyOp
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyWhitelistModified

@OptIn(ExperimentalQGApi::class, InternalForInheritanceQGGroupJoinApprovalStrategyApi::class)
internal class QGGroupJoinApprovalStrategyHandleOperator(
    private val bot: QGBot,
    override val id: ID,
) : QGGroupJoinApprovalStrategyHandle {
    override suspend fun update(input: GroupJoinApprovalStrategyModify): GroupJoinApprovalStrategyModified =
        bot.executeData(ModifyGroupJoinApprovalStrategyApi.create(id.literal, input))

    override suspend fun addWhitelistUsers(users: Collection<ID>): GroupJoinApprovalStrategyWhitelistModified =
        bot.executeData(
            ModifyGroupJoinApprovalStrategyWhitelistApi.create(
                id.literal,
                GroupJoinApprovalStrategyOp.ADD,
                users.map { it.literal }
            )
        )

    override suspend fun removeWhitelistUsers(users: Collection<ID>): GroupJoinApprovalStrategyWhitelistModified =
        bot.executeData(
            ModifyGroupJoinApprovalStrategyWhitelistApi.create(
                id.literal,
                GroupJoinApprovalStrategyOp.DEL,
                users.map { it.literal }
            )
        )

    override suspend fun execute() {
        bot.executeData(ExecuteGroupJoinApprovalStrategyApi.create(id.literal))
    }

    override suspend fun delete(vararg options: DeleteOption) {
        suspend fun doDelete() {
            bot.executeData(DeleteGroupJoinApprovalStrategyApi.create(id.literal))
        }

        if (options.any { it == StandardDeleteOption.IGNORE_ON_FAILURE }) {
            runCatchingCancellable {
                doDelete()
            }.onFailure { e -> logger.debug("Failed to delete group join approval strategy {}: {}", id, e.message, e) }
        } else {
            doDelete()
        }
    }


    override fun toString(): String = "QGGroupJoinApprovalStrategyHandleOperator(id=$id)"

    companion object {
        private val logger = LoggerFactory.logger<QGGroupJoinApprovalStrategyHandleOperator>()
    }
}
