/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.JST
import love.forte.simbot.JSTP
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.item.Items


/**
 * 一个群。
 * @author ForteScarlet
 */
public interface Group : ChatRoom, GroupInfo {
    
    override val bot: GroupBot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    
    @JSTP
    override suspend fun owner(): GroupMember
    
    override val maximumMember: Int
    override val currentMember: Int
    
    /**
     * 获取当前群内的成员序列。
     */
    override val members: Items<GroupMember>
    
    
    // region member
    
    /**
     * 根据ID获取到指定的成员。
     */
    @JST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    override suspend fun member(id: ID): GroupMember?
    // endregion
    
}


/**
 * 群组信息。
 */
public interface GroupInfo : OrganizationInfo
