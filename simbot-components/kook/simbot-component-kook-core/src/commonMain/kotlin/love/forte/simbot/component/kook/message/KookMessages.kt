/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.message

import love.forte.simbot.common.id.ID
import love.forte.simbot.message.At
import kotlin.jvm.JvmStatic

/**
 * 提供 KOOK 组件中一些会用到的信息。
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public object KookMessages {

    /**
     * 当at(mention)的目标为用户时，[At.type] 所使用的值。[AT_TYPE_USER] 也是 [At.type] 的默认值。
     */
    public const val AT_TYPE_USER: String = "user"

    /**
     * 当at(mention)的目标为角色时，[At.type] 所使用的值。
     */
    public const val AT_TYPE_ROLE: String = "role"

    /**
     * 当at(mention)的目标为频道时。用于使用 `KMarkdown` 类型发送的时候。
     */
    public const val AT_TYPE_CHANNEL: String = "channel"


    /**
     * 构建一个 at(mention) 用户的 [At] 消息对象。
     */
    @JvmStatic
    public fun atUser(id: ID): At = At(target = id, type = AT_TYPE_USER, originContent = "(met)$id(met)")

    /**
     * 构建一个 at(mention) 整个角色的 [At] 消息对象。
     */
    @JvmStatic
    public fun atRole(id: ID): At = At(target = id, type = AT_TYPE_ROLE, originContent = "(rol)$id(rol)")

    /**
     * 构建一个 at(mention) 频道的 [At] 消息对象。
     */
    @JvmStatic
    public fun atChannel(id: ID): At = At(target = id, type = AT_TYPE_CHANNEL, originContent = "(chn)$id(chn)")

}
