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

package love.forte.simbot.util.api.requestor.ktor

import love.forte.simbot.util.api.requestor.API
import love.forte.simbot.util.api.requestor.Requestor


/**
 * 基于 ktor 的 [API] 实现的基础父类型。
 * @author ForteScarlet
 */
public interface KtorAPI<in RQ : Requestor, out R> : API<RQ, R> {
    
    /**
     * 借助 [请求器][RQ] 向当前所表示的API发起请求，并得到结果 [R].
     */
    override suspend fun requestBy(requestor: RQ): R
}


