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

package love.forte.simbot.component.qguild.internal.group

import kotlinx.coroutines.flow.map
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.group.QGGroupMemberWithInfo
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.qguild.api.group.*
import love.forte.simbot.qguild.model.group.*
import kotlin.coroutines.CoroutineContext

internal class QGGroupMemberWithInfoImpl(
    override val bot: QGBotImpl,
    override val groupId: ID,
    override val source: GroupMemberInfo,
) : QGGroupMemberWithInfo(), QGGroupMemberMuteSupport {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
}

@OptIn(ExperimentalQGApi::class)
internal suspend fun QGBot.queryGroupMemberInfo(groupId: ID, memberId: ID): GroupMemberInfo {
    require(groupId.literal.isNotBlank()) { "Group OpenID must not be blank" }
    require(memberId.literal.isNotBlank()) { "Member OpenID must not be blank" }
    val info = executeData(GetGroupMemberInfoApi.create(groupId.literal, memberId.literal))
    check(info.memberOpenid == memberId.literal) {
        "Member info openid ${info.memberOpenid} does not match member id ${memberId.literal}"
    }
    return info
}

internal suspend fun QGBotImpl.queryGroupMemberWithInfo(groupId: ID, memberId: ID): QGGroupMemberWithInfo =
    QGGroupMemberWithInfoImpl(this, groupId, queryGroupMemberInfo(groupId, memberId))

@OptIn(ExperimentalQGApi::class)
internal fun QGBotImpl.groupMembers(groupId: ID): Collectable<QGGroupMemberWithInfo> =
    GetGroupMemberListApi.createFlow(groupId.literal) {
        require(groupId.literal.isNotBlank()) { "Group OpenID must not be blank" }
        executeData(this)
    }.map { QGGroupMemberWithInfoImpl(this, groupId, it) }.asCollectable()

@OptIn(ExperimentalQGApi::class)
internal fun QGBot.groupBlacklist(groupId: ID): Collectable<GroupBlacklistUser> =
    GetGroupMemberBlacklistApi.createFlow(groupId.literal) {
        require(groupId.literal.isNotBlank()) { "Group OpenID must not be blank" }
        executeData(this)
    }.asCollectable()

@OptIn(ExperimentalQGApi::class)
internal suspend fun QGBot.removeGroupMembers(
    groupId: ID,
    memberIds: List<ID>,
    addToBlacklist: Boolean,
): GroupMembersRemoveResult = executeData(
    BatchRemoveGroupMembersApi.create(groupId.literal, memberIds.map { it.literal }, addToBlacklist)
)

@OptIn(ExperimentalQGApi::class)
internal suspend fun QGBot.modifyGroupBlacklist(
    groupId: ID,
    memberIds: List<ID>,
    op: ModifyGroupMemberBlacklistOp,
): GroupBlacklistModifyResult = executeData(
    ModifyGroupMemberBlacklistApi.create(groupId.literal, op, memberIds.map { it.literal })
)
