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

import kotlinx.coroutines.flow.map
import love.forte.simbot.ability.AcceptOption
import love.forte.simbot.ability.RejectOption
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.group.QGGroupJoinRequest
import love.forte.simbot.component.qguild.group.QGGroupJoinRequestRejectOption
import love.forte.simbot.qguild.api.group.ApproveGroupJoinRequestApi
import love.forte.simbot.qguild.api.group.ApproveGroupJoinRequestOp
import love.forte.simbot.qguild.api.group.GetGroupJoinRequestListApi
import love.forte.simbot.qguild.api.group.createFlow
import love.forte.simbot.qguild.model.group.GroupJoinRequest

/**
 * 保留原始申请数据与所属群，以便直接审批。
 */
internal class QGGroupJoinRequestImpl(
    private val requestBot: QGBot,
    private val groupId: ID,
    override val source: GroupJoinRequest,
) : QGGroupJoinRequest() {
    override suspend fun accept() {
        approveGroupJoinRequest(requestBot, groupId, memberOpenid, joinRequestId)
    }

    override suspend fun accept(vararg options: AcceptOption) {
        accept()
    }

    override suspend fun reject() {
        rejectGroupJoinRequest(requestBot, groupId, memberOpenid, joinRequestId, emptyArray())
    }

    override suspend fun reject(vararg options: RejectOption) {
        rejectGroupJoinRequest(requestBot, groupId, memberOpenid, joinRequestId, options)
    }

    override fun toString(): String =
        "QGGroupJoinRequest(groupId=$groupId, joinRequestId=$joinRequestId, memberOpenid=$memberOpenid)"
}

@OptIn(ExperimentalQGApi::class)
internal fun QGBot.groupJoinRequests(groupId: ID): Collectable<QGGroupJoinRequest> =
    GetGroupJoinRequestListApi.createFlow(groupId.literal) {
        executeData(this)
    }.map { QGGroupJoinRequestImpl(this, groupId, it) }.asCollectable()

@OptIn(ExperimentalQGApi::class)
internal suspend fun approveGroupJoinRequest(
    bot: QGBot,
    groupId: ID,
    memberId: ID,
    joinRequestId: ID?,
) {
    bot.executeData(
        ApproveGroupJoinRequestApi.create(
            groupOpenid = groupId.literal,
            memberOpenid = memberId.literal,
            op = ApproveGroupJoinRequestOp.APPROVE,
            joinRequestId = joinRequestId?.literal,
        )
    )
}

@OptIn(ExperimentalQGApi::class)
internal suspend fun rejectGroupJoinRequest(
    bot: QGBot,
    groupId: ID,
    memberId: ID,
    joinRequestId: ID?,
    options: Array<out RejectOption>,
) {
    var addToBlacklist = false
    var reason: StringBuilder? = null

    options.forEach { option ->
        if (option is QGGroupJoinRequestRejectOption) {
            when (option) {
                is QGGroupJoinRequestRejectOption.Reason -> {
                    reason = (reason ?: StringBuilder(option.reason.length)).append(option.reason)
                }

                QGGroupJoinRequestRejectOption.AddToBlacklist -> {
                    addToBlacklist = true
                }
            }
        }
    }

    bot.executeData(
        ApproveGroupJoinRequestApi.create(
            groupOpenid = groupId.literal,
            memberOpenid = memberId.literal,
            op = ApproveGroupJoinRequestOp.DECLINE,
            joinRequestId = joinRequestId?.literal,
            rejectReason = reason?.toString(),
            addToMemberBlacklist = if (addToBlacklist) true else null,
        )
    )
}
