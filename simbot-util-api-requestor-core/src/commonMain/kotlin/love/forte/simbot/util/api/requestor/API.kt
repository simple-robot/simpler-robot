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

package love.forte.simbot.util.api.requestor


/**
 *
 * 一个 API 的基本表示类型，代表可通过当前 [API] 发起请求并最终得到一个结果。
 * [API] 的基本实现应当为不可变类型，其内在不应持有可变状态。
 *
 * ## 实现建议
 * ### 工厂构建
 * 建议 [API] 的所有具体实现类都应隐藏自身的主构造，并通过提供 `create` 系命名的工厂方法来代替构建。
 *
 * ```kotlin
 * public class FooAPI private constructor(val bar: Bar) : API<Requestor, Foo> {
 *     override suspend fun requestBy(requestor: Requestor): Foo {
 *          // ...
 *     }
 *
 *     public companion object Factory {
 *         @JvmStatic
 *         public fun create(bar: Bar): FooAPI {
 *             return FooAPI(bar)
 *         }
 *     }
 * }
 * ```
 *
 * ### 单例和可预测分支
 * 尽管如此，但是如果某个 [API] 实现可以设计为单例或多个不变的具体状态，则可以直接通过 `object` 等方式实现。
 *
 * ```kotlin
 * public object SingletonAPI : API<Requestor, Bar> {
 *     override suspend fun requestBy(requestor: Requestor): Bar {
 *         // ...
 *     }
 * }
 * ```
 *
 * ```kotlin
 * public class SimpleAPI private constructor(private val value: Int) : API<Requestor, SimpleResult> {
 *     override suspend fun requestBy(requestor: Requestor): SimpleResult {
 *         // ...
 *     }
 *     public companion object Factory {
 *         @JvmField public val FIRST: SimpleAPI = SimpleAPI(Int.MIN_VALUE)
 *         @JvmField public val LAST: SimpleAPI = SimpleAPI(Int.MAX_VALUE)
 *
 *         @JvmStatic
 *         public fun create(value: Int): SimpleAPI {
 *            return when (value) {
 *                Int.MIN_VALUE -> FIRST
 *                Int.MAX_VALUE -> LAST
 *                else -> SimpleAPI(value)
 *            }
 *         }
 *     }
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
public interface API<in RQ : Requestor, out R> {
    
    /**
     * 借助 [请求器][RQ] 向当前所表示的API发起请求, 并得到结果 [R].
     *
     * @throws Exception 请求过程中可能会产生任何异常, 包括但不限于网络、序列化、参数检验等
     *
     */
    @Throws(Exception::class)
    public suspend fun requestBy(requestor: RQ): R
    
}

