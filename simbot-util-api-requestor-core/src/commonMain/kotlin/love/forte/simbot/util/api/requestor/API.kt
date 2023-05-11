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

