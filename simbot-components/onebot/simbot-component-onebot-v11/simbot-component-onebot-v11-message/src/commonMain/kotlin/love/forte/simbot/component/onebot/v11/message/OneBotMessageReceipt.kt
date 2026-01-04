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

package love.forte.simbot.component.onebot.v11.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.message.MessageReceipt


/**
 * OneBot组件中，消息发送成功后得到的回执。
 *
 * @author ForteScarlet
 */
public interface OneBotMessageReceipt : MessageReceipt {
    /**
     * 消息发送后的结果id。
     */
    public val messageId: ID

    /**
     * 删除此消息。
     *
     * 支持的操作：
     * - [StandardDeleteOption.IGNORE_ON_FAILURE] 忽略请求API所产生的异常
     *
     * @throws Exception 任何请求API过程中可能会产生的异常，
     * 例如因权限不足或消息不存在得到的请求错误
     */
    override suspend fun delete(vararg options: DeleteOption)
}

// 实现在core模块：因为要使用到API
