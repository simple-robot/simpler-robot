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
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupWithInfo
import love.forte.simbot.qguild.api.group.GetGroupInfoApi
import love.forte.simbot.qguild.common.QGInternalInheritanceApi
import love.forte.simbot.qguild.model.group.GroupInfo

/**
 * 通过底层 API 查询指定群的基本信息。
 */
@OptIn(ExperimentalQGApi::class)
internal suspend fun QGBot.queryGroupInfo(groupId: ID): GroupInfo =
    executeData(GetGroupInfoApi.create(groupId.literal))

/**
 * 在保留原群句柄全部操作与事件上下文的同时，附加一次查询得到的基本信息快照。
 */
@OptIn(QGInternalInheritanceApi::class)
internal class QGGroupWithInfoImpl(
    private val delegate: QGGroupImpl,
    override val groupInfo: GroupInfo,
) : QGGroupWithInfo, QGGroup by delegate {
    init {
        check(delegate.id.literal == groupInfo.groupOpenid) {
            "Group info openid ${groupInfo.groupOpenid} does not match group id ${delegate.id.literal}"
        }
    }

    override val name: String
        get() = groupInfo.groupName

    override suspend fun groupInfo(): GroupInfo = delegate.groupInfo()

    override suspend fun includeInfo(): QGGroupWithInfo = delegate.includeInfo()

    override suspend fun groupInfoOrQuery(): GroupInfo = groupInfo

    override suspend fun includeInfoOrSelf(): QGGroupWithInfo = this

    override fun toString(): String = "QGGroupWithInfo(id=$id, name=$name)"
}
