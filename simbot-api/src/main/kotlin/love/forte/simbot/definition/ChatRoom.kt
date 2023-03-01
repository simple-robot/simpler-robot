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
import love.forte.simbot.action.SendSupport
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems


/**
 * 一个 **聊天室**。 聊天室是 [组织][Organization] 的子类型，代表其是一个存在多人且允许相互交流“发送消息”的组织。
 *
 * 聊天室实现 [SendSupport] ，允许发送消息。
 *
 * @author ForteScarlet
 */
public interface ChatRoom : Organization, SendSupport {
    
    /**
     * 聊天室子集。
     *
     * 默认为空，通常情况下能够作为聊天室的组织不存在子集。
     */
    override val children: Items<Organization> get() = emptyItems()
    
    
    /**
     * 根据ID寻找当前聊天室下匹配的子聊天室。
     *
     * 默认得到null，通常情况下能够作为聊天室的组织不存在子集。
     */
    override suspend fun child(id: ID): Organization? = null
}
