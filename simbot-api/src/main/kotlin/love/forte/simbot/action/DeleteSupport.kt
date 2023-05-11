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

package love.forte.simbot.action

import love.forte.simbot.JST


/**
 * 允许一种删除行为。
 * 标记一个消息为可删除的。
 *
 * 对于一种**删除行为**来讲，它最常见的含义就是**撤回**（针对于消息, 参考 [RemoteMessageContent][love.forte.simbot.message.RemoteMessageContent]、[MessageReceipt][love.forte.simbot.message.MessageReceipt]）
 * 和 **踢出/移除**（针对于好友、群成员等, 但是没有提供默认实现）。
 *
 * @see love.forte.simbot.message.RemoteMessageContent
 *
 * @author ForteScarlet
 */
@JST
public interface DeleteSupport {

    /**
     * 删除当前目标。
     *
     * 如果因为组件自身特性而导致任何条件都无法满足任何对象的 `delete` 操作，
     * 则可能固定返回 `false`, 否则大多数情况下会返回 `true`.
     *
     * 如果是因为诸如权限、超时等限制条件导致的无法删除，则可能会抛出相应的异常。
     *
     * @return 在支持的情况下代表是否删除成功，不支持的情况下可能恒返回 `false`。
     */
    public suspend fun delete(): Boolean
}
