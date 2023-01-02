/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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
