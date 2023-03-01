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

import io.ktor.client.statement.*
import love.forte.simbot.util.api.requestor.Requestor


/**
 *
 * 实现 [HttpAPI] 的基础抽象类。
 *
 * @author ForteScarlet
 */
public abstract class BaseHttpAPI<in RQ : Requestor, out R> : HttpAPI<RQ, R> {
    
    /**
     * 通过 [RQ] 发起请求，并得到响应结果。
     */
    protected abstract suspend fun httpRequestBy(requestor: RQ): HttpResponse
    
    /**
     * 根据 [httpRequestBy] 请求所得到的结果进行处理并得到最终的 [R]。
     */
    protected abstract suspend fun processResponse(requestor: RQ, response: HttpResponse): R
    
    /**
     * 借助 [请求器][RQ] 向当前所表示的API发起请求，并得到结果 [R].
     */
    override suspend fun requestBy(requestor: RQ): R {
        val response = httpRequestBy(requestor)
        return processResponse(requestor, response)
    }
}
