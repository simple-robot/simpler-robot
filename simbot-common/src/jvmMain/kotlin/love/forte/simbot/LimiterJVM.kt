package love.forte.simbot

import java.util.stream.Stream


/**
 * 使用 [limiter] 对目标 [Stream] 进行限流。
 */
public fun <T> Stream<T>.withLimiter(limiter: Limiter): Stream<T> =
    let {
        with(limiter.offset) { if (this > 0) skip(toLong()) else it }
    }.let {
        with(limiter.limit) { if (this > 0) limit(toLong()) else it }
    }