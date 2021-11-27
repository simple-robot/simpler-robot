/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot


import kotlinx.coroutines.flow.take
import love.forte.simbot.Limiter.ZERO


/**
 *
 * 一个**限流器**。
 *
 * 通俗一点的理解，限流器可以理解为一个用于需要进行 **分页** 的地方，常见于一些返回值为 [kotlinx.coroutines.flow.Flow] 或者 [List] 之类的地方。
 *
 * 对于限流的具体实现细节由对应功能的实现者自行决定（包括是否真的进行分页等）。
 *
 * 虽然例如 [kotlinx.coroutines.flow.Flow] 有提供 [kotlinx.coroutines.flow.Flow.take] 、
 * [java.util.stream.Stream] 有提供 [java.util.stream.Stream.limit] 之类的函数，
 *
 * 但是 [Limiter] 更多的是为功能提供一个初始量。
 *
 * ## 有效数字
 * [Limiter] 中，约定其所有有效数字均应大于 `0`。
 *
 * ## 默认实现
 * [Limiter] 提供两个默认实现：
 * - [ZERO] 所有数值恒为 `0` 的伴生对象，可直接作为默认值使用。
 * - 通过 [Limiter.of] (for java) 或者 [limiter] (for kotlin) 得到一个默认的数据类实现。
 *
 * ## 其他实现
 * 对于默认实现无法满足需求的情况，需要进行独立的实现，这一般由核心或者其他组件提供，并会整合在其他参数类型中，
 * 例如存在一个 `Query` 类型，它也许就会为了支持可限流的情况而对 Limiter 进行实现。
 *
 * @see ZERO
 * @see limiter
 *
 * @author ForteScarlet
 */
public interface Limiter {
    /**
     * 偏移量，即从第 [offset] 条数据开始返回。
     * 偏移量预期中的基数为 `0`, 即如果为 `0` 或者小于 `0`, 则代表从第一条数据开始获取。
     */
    public val offset: Int

    /**
     * 限流数量，即本次所得数据量最大不应超过此限制。
     * 例如 `limit = 10`, 那么返回值结果中的最终元素数量应当 `<= 10`.
     *
     * 当 `limit <= 0` 的时候，可认为返回值不受限制，或者使用实现方的默认值。
     *
     * 对于极少的情况，limit = 0 是存在特殊含义的时候，实现方应当有所说明。
     *
     */
    public val limit: Int

    /**
     * 对于部分平台的实现中，很有可能是分批次查询来获取结果的。
     * 当平台支持的时候，可以通过 [batchSize] 来指定一个批次大小来限制其内部每次对于API的请求数量。
     *
     * 正常情况下，此值只有在 >0 的时候生效。
     */
    public val batchSize: Int

    /**
     * [Limiter] 的默认值实现，[offset][ZERO.offset] 与 [limit][ZERO.limit] 均恒等于 `0`。
     *
     */
    public companion object ZERO : Limiter {
        override val offset: Int get() = 0
        override val limit: Int get() = 0
        override val batchSize: Int get() = 0

        @Api4J
        @JvmStatic
        @JvmOverloads
        public fun of(offset: Int = ZERO.offset, limit: Int = ZERO.limit, batchSize: Int = ZERO.batchSize): Limiter =
            if (offset <= 0 && limit <= 0 && batchSize <= 0) ZERO else LimiterImpl(offset, limit, batchSize)

        /**
         * 根据分页信息计算得到limiter。
         * 分页以 `0` 作为起始页码
         *
         * 如果参数为无效参数（`pageSize <= 0 && pageNum < 0`）则返回 [ZERO].
         */
        public fun ofPage(pageSize: Int, pageNum: Int): Limiter {
            if (pageSize <= 0 && pageNum < 0) return ZERO
            return LimiterImpl(pageSize * pageNum, pageSize, 0)
        }
    }
}

/**
 * 得到一个 [Limiter] 的默认实现。
 */
public fun limiter(offset: Int = ZERO.offset, limit: Int = ZERO.limit, batchSize: Int = ZERO.batchSize): Limiter =
    if (offset <= 0 && limit <= 0 && batchSize <= 0) Limiter else LimiterImpl(offset, limit, batchSize)


private data class LimiterImpl(override val offset: Int, override val limit: Int, override val batchSize: Int) :
    Limiter {
    override fun toString(): String = "Limiter(offset=$offset, limit=$limit, batchSize=$batchSize)"
}


