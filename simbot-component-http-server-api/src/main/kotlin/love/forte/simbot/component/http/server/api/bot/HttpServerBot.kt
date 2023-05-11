/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.http.server.api.bot

import love.forte.simbot.ID
import love.forte.simbot.bot.Bot


/**
 * 描述一个Http服务器的bot类型。
 * @author ForteScarlet
 */
public interface HttpServerBot : Bot {
    // TODO
    
    /**
     * http服务器的ID通常代表一个当前被绑定的路径，例如 [host] 与 [port] 的组合。
     */
    override val id: ID
    
    /**
     * 服务器端口号，例如 `80`。
     */
    public val port: Int
    
    /**
     * 服务器绑定的主机IP，例如 `0.0.0.0`。
     */
    public val host: String
    
    
}
