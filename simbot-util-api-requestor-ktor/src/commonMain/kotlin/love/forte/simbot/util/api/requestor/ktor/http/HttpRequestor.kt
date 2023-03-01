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

package love.forte.simbot.util.api.requestor.ktor.http

import io.ktor.client.*
import love.forte.simbot.util.api.requestor.Requestor


/**
 *
 * 可以提供 [HttpClient] 的 [Requestor] 子类型。
 *
 * @author ForteScarlet
 */
public interface HttpRequestor : Requestor {
    
    /**
     * 可以对外提供一个 [HttpClient] 实例。
     */
    public val httpClient: HttpClient
    
}
