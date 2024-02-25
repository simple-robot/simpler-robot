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

import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.ability.SendSupport


/**
 *
 * 一个联系人。
 *
 * 联系人是一种可以与 bot 建立独立会话、进行通讯的行为对象。
 * 联系人可能代表一个其他用户，也可能代表一个与某用户关联的“会话”。
 *
 * ## DeleteSupport
 *
 * 联系人有可能会实现 [DeleteSupport]。如果实现，则或许代表 bot 可以主动的与此联系人断开关系，
 * 或者主动删除与之关联的 “会话”。
 * 具体的实际含义由实现者定义并提供说明。
 *
 *
 * @author ForteScarlet
 */
public interface Contact : User, SendSupport {
    /**
     * 此联系人的名称
     */
    override val name: String
}
