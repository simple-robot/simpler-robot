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

package love.forte.simbot.component.http.server.api.event

import love.forte.simbot.component.http.server.api.bot.HttpServerBot
import love.forte.simbot.event.Event


/**
 *
 * 描述一次Http请求的事件。
 *
 * @author ForteScarlet
 */
public interface HttpRequestEvent : Event {
    
    /**
     * 此请求发生所在的Http服务器
     */
    override val bot: HttpServerBot
    
    
    /**
     * 描述本次请求的 Request 信息。
     */
    public val request: Request
    
    /**
     * 描述本次请求预期的响应信息。
     */
    public val response: Response
    
    
    /**
     * [HttpRequestEvent] 的一次请求信息。
     */
    public interface Request {
        // TODO
        // headers
        // body
        // method
        // uri
        // remote host
    }
    
    /**
     * [HttpRequestEvent] 的预期响应信息。
     */
    public interface Response {
        // TODO
        // headers
        // body
        // status
    }
}


