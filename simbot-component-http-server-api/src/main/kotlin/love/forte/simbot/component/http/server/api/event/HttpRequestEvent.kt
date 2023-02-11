/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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


