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

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.group.QGGroupMember
import love.forte.simbot.component.qguild.group.QGGroupMemberMuteChange
import love.forte.simbot.qguild.api.group.GetGroupMuteSettingApi
import love.forte.simbot.qguild.api.group.SetGroupMuteSettingApi
import love.forte.simbot.qguild.api.group.SetGroupMuteSettingOp
import love.forte.simbot.qguild.api.group.SetMemberMuteState
import love.forte.simbot.qguild.common.QGInternalInheritanceApi
import love.forte.simbot.qguild.model.group.GroupMemberMuteState
import love.forte.simbot.qguild.model.group.GroupMuteSetting
import kotlin.time.Instant

/**
 * 查询完整群禁言快照，供群、群关系和成员共用。
 */
@OptIn(ExperimentalQGApi::class)
internal suspend fun QGBot.queryGroupMuteSetting(groupId: ID): GroupMuteSetting {
    val groupOpenid = groupId.literal
    require(groupOpenid.isNotBlank()) { "Group OpenID must not be blank" }
    return executeData(GetGroupMuteSettingApi.create(groupOpenid))
}

/**
 * 固定请求输入并完成整个批次的校验后，提交一次成员禁言请求。
 */
@OptIn(ExperimentalQGApi::class)
internal suspend fun QGBot.submitGroupMemberMutes(groupId: ID, changes: List<QGGroupMemberMuteChange>) {
    val groupOpenid = groupId.literal
    require(groupOpenid.isNotBlank()) { "Group OpenID must not be blank" }
    require(changes.size in 1..20) { "A mute batch must contain 1 to 20 changes" }

    val members = changes.map { change ->
        val memberOpenid = change.memberId.literal
        require(memberOpenid.isNotBlank()) { "Member OpenID must not be blank" }
        when (change) {
            is QGGroupMemberMuteChange.Add ->
                SetMemberMuteState.of(SetGroupMuteSettingOp.ADD, memberOpenid, change.muteExpireAt)

            is QGGroupMemberMuteChange.Update ->
                SetMemberMuteState.of(SetGroupMuteSettingOp.UPDATE, memberOpenid, change.muteExpireAt)

            is QGGroupMemberMuteChange.Delete ->
                SetMemberMuteState.of(SetGroupMuteSettingOp.DEL, memberOpenid)
        }
    }
    executeData(SetGroupMuteSettingApi.create(groupOpenid, members))
}

/**
 * 各种群成员实现共用的禁言查询与提交逻辑。
 */
@OptIn(QGInternalInheritanceApi::class)
internal interface QGGroupMemberMuteSupport : QGGroupMember {
    /**
     * 执行所属群操作的机器人。
     */
    val bot: QGBot

    override suspend fun muteState(): GroupMemberMuteState? {
        val memberOpenid = id.literal
        require(memberOpenid.isNotBlank()) { "Member OpenID must not be blank" }
        val setting = bot.queryGroupMuteSetting(groupId)
        return setting.members.firstOrNull { it.memberOpenid == memberOpenid }
    }

    override suspend fun addMute(expireAt: Instant) {
        bot.submitGroupMemberMutes(groupId, listOf(QGGroupMemberMuteChange.add(id, expireAt)))
    }

    override suspend fun updateMute(expireAt: Instant) {
        bot.submitGroupMemberMutes(groupId, listOf(QGGroupMemberMuteChange.update(id, expireAt)))
    }

    override suspend fun unmute() {
        bot.submitGroupMemberMutes(groupId, listOf(QGGroupMemberMuteChange.remove(id)))
    }
}
