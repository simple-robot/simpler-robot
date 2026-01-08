/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.dms

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.component.qguild.event.QGDirectMessageCreateEvent
import love.forte.simbot.definition.Contact
import love.forte.simbot.qguild.model.User


/**
 * 一个频道中的私聊联系人。
 * 通过 [QGDirectMessageCreateEvent] 事件得到的内容。
 *
 * @author ForteScarlet
 */
public interface QGDmsContact : CoroutineScope, Contact, QGObjectiveContainer<User> {
    override val source: User

    /**
     * 用户ID
     */
    override val id: ID
        get() = source.id.ID

    /**
     * 用户名
     */
    override val name: String
        get() = source.username

    /**
     * 用户头像地址。如果为空则得到 `null`。
     */
    override val avatar: String?
        get() = source.avatar.takeUnless { it.isEmpty() }


}
