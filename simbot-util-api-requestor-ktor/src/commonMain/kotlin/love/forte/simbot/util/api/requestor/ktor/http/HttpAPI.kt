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

import io.ktor.http.*
import love.forte.simbot.util.api.requestor.Requestor
import love.forte.simbot.util.api.requestor.ktor.KtorAPI


/**
 * 一个可以描述为HttpAPI的 [KtorAPI] 子类型。
 *
 * [HttpAPI] 不会强制要求 [RQ] 或 [R] 的结果，但是通常情况下会选择使用 [HttpRequestor].
 *
 * @author ForteScarlet
 */
public interface HttpAPI<in RQ : Requestor, out R> : KtorAPI<RQ, R> {
    
    /**
     * 此API所表示的最终请求url目标。
     */
    public val url: Url
    
    /**
     * 此API的请求方法。
     */
    public val method: HttpMethod
    
    /**
     * 借助 [请求器][RQ] 向当前所表示的API发起请求，并得到结果 [R].
     */
    override suspend fun requestBy(requestor: RQ): R
}
