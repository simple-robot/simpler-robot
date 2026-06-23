/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.kook.api

import io.ktor.client.statement.*
import io.ktor.http.*
import love.forte.simbot.kook.KookException

/**
 * Kook  [超速限制](https://developer.kaiheila.cn/doc/rate-limit) 异常。
 *
 * @author ForteScarlet
 */
public open class ApiRateLimitException : KookException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * [超速限制](https://developer.kaiheila.cn/doc/rate-limit) 中的部分常量信息。
 *
 * @author ForteScarlet
 */
public object ApiRateLimits {

    /**
     * 一段时间内允许的最大请求次数的请求头。
     */
    public const val RATE_LIMIT_LIMIT_HEAD: String = "X-Rate-Limit-Limit"

    /**
     * 一段时间内还剩下的请求数的请求头。
     */
    public const val RATE_LIMIT_REMAINING_HEAD: String = "X-Rate-Limit-Remaining"

    /**
     *回复到最大请求次数需要等待的时间的请求头。
     */
    public const val RATE_LIMIT_RESET_HEAD: String = "X-Rate-Limit-Reset"

    /**
     * 请求数的bucket的请求头。
     */
    public const val RATE_LIMIT_BUCKET_HEAD: String = "X-Rate-Limit-Bucket"

    /**
     * 触犯全局请求次数限制的请求头。
     */
    public const val RATE_LIMIT_GLOBAL_HEAD: String = "X-Rate-Limit-Global"


}

/**
 * 尝试获取 [HttpResponse] 中的 [ApiRateLimits.RATE_LIMIT_LIMIT_HEAD] 信息。
 */
public inline val HttpResponse.rateLimit: Long? get() = headers.rateLimit

/**
 * 尝试获取 [HttpResponse] 中的 [ApiRateLimits.RATE_LIMIT_REMAINING_HEAD] 信息。
 */
public inline val HttpResponse.rateRemaining: Long? get() = headers.rateRemaining

/**
 * 尝试获取 [HttpResponse] 中的 [ApiRateLimits.RATE_LIMIT_RESET_HEAD] 信息。
 */
public inline val HttpResponse.rateReset: Long? get() = headers.rateReset

/**
 * 尝试获取 [HttpResponse] 中的 [ApiRateLimits.RATE_LIMIT_BUCKET_HEAD] 信息。
 */
public inline val HttpResponse.rateBucket: String? get() = headers.rateBucket

/**
 * [HttpResponse] 中是否存在 [ApiRateLimits.RATE_LIMIT_GLOBAL_HEAD]。
 */
public inline val HttpResponse.isRateGlobal: Boolean get() = headers.isRateGlobal


/**
 * 尝试获取 [Headers] 中的 [ApiRateLimits.RATE_LIMIT_LIMIT_HEAD] 信息。
 */
public inline val Headers.rateLimit: Long? get() = this[ApiRateLimits.RATE_LIMIT_LIMIT_HEAD]?.toLongOrNull()

/**
 * 尝试获取 [Headers] 中的 [ApiRateLimits.RATE_LIMIT_REMAINING_HEAD] 信息。
 */
public inline val Headers.rateRemaining: Long? get() = this[ApiRateLimits.RATE_LIMIT_REMAINING_HEAD]?.toLongOrNull()

/**
 * 尝试获取 [Headers] 中的 [ApiRateLimits.RATE_LIMIT_RESET_HEAD] 信息。
 */
public inline val Headers.rateReset: Long? get() = this[ApiRateLimits.RATE_LIMIT_RESET_HEAD]?.toLongOrNull()

/**
 * 尝试获取 [Headers] 中的 [ApiRateLimits.RATE_LIMIT_BUCKET_HEAD] 信息。
 */
public inline val Headers.rateBucket: String? get() = this[ApiRateLimits.RATE_LIMIT_BUCKET_HEAD]

/**
 * [Headers] 中是否存在 [ApiRateLimits.RATE_LIMIT_GLOBAL_HEAD]。
 */
public inline val Headers.isRateGlobal: Boolean get() = this[ApiRateLimits.RATE_LIMIT_GLOBAL_HEAD] != null

/**
 * 通过 [Headers] 构建并得到 [RateLimit]。
 */
internal fun Headers.createRateLimit(): RateLimit {
    val limit = this.rateLimit
    val remaining = this.rateRemaining
    val reset = this.rateReset
    val bucket = this.rateBucket
    val global = this.isRateGlobal

    return RateLimit(
        limit ?: RateLimit.DEFAULT_LIMIT,
        remaining ?: RateLimit.DEFAULT_REMAINING,
        reset ?: RateLimit.DEFAULT_RESET,
        bucket,
        global
    )
}
