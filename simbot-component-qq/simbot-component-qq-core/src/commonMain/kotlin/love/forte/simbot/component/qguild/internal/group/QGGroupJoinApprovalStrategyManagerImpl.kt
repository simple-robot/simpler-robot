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

import kotlinx.coroutines.flow.flow
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.group.*
import love.forte.simbot.qguild.api.group.CreateGroupJoinApprovalStrategyApi
import love.forte.simbot.qguild.api.group.GetGroupJoinApprovalStrategyListApi
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreate

@OptIn(ExperimentalQGApi::class, InternalForInheritanceQGGroupJoinApprovalStrategyApi::class)
internal class QGGroupJoinApprovalStrategyManagerImpl(private val bot: QGBot) : QGGroupJoinApprovalStrategyManager {
    override fun handle(strategyId: ID): QGGroupJoinApprovalStrategyHandle {
        require(strategyId.literal.isNotBlank()) { "strategyId must not be blank" }
        return QGGroupJoinApprovalStrategyHandleOperator(bot, strategyId)
    }

    override suspend fun create(input: GroupJoinApprovalStrategyCreate): QGGroupJoinApprovalStrategyCreated =
        QGGroupJoinApprovalStrategyCreatedImpl(bot, bot.executeData(CreateGroupJoinApprovalStrategyApi.create(input)))

    override fun list(
        startCursor: String?,
        limit: Int?,
        autoPagination: Boolean,
    ): Collectable<QGGroupJoinApprovalStrategyRecord> = flow {
        var cursor = startCursor
        do {
            val page = bot.executeData(GetGroupJoinApprovalStrategyListApi.create(cursor, limit))
            page.strategies.forEach { emit(QGGroupJoinApprovalStrategyRecordImpl(bot, it)) }
            cursor = page.nextCursor
        } while (autoPagination && cursor.isNotEmpty())
    }.asCollectable()
}
