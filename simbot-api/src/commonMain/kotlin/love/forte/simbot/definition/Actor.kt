/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.definition

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IDContainer

/**
 *
 * 一个行为主体。
 *
 * 或称为行为对象。
 *
 * [Actor] 是可能具有行为的目标的父类型，
 * 例如一个聊天室（群聊、文字频道等）或一个联系人（`Contact`）或组织成员（`Member`）。
 *
 * ## [CoroutineScope]
 *
 * [Actor] 继承 [CoroutineScope]，提供一个与所属 [Bot] 相关的作用域。
 * [Actor] 所描述的协程作用域可能与 [Bot] 相同、可能属于 [Bot] 的子作用域，也可能不存在 [Job]。
 * 如果 `cancel` 一个 [Actor] 也可能会导致与之关联的 [Bot] 被关闭，也可能无法关闭。
 * 这一切取决于 [Actor] 的具体实现。
 *
 * @see Contact
 * @see Member
 * @see ChatRoom
 * @see Organization
 *
 * @author ForteScarlet
 */
public interface Actor : CoroutineScope, IDContainer {
    /**
     * 行为主体的唯一标识。
     */
    public override val id: ID
}


