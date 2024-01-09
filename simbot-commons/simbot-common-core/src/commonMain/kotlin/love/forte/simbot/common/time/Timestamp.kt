/*
 *     Copyright (c) 2023-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.common.time

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import kotlin.jvm.JvmStatic

/**
 * 一个用于表示 Unix 时间戳的类型。
 * 是从 `UTC 1970.01.01T00:00:00Z` 直至现在所经过的时间，
 * 常见的时间单位有秒或毫秒。
 *
 * [Timestamp] **不是日期API** ，而仅是一种忽略时间单位的时间戳包装体。
 * 因此 [Timestamp] 本身不提供例如解析某格式的日期（例如 ISO-8601）或进行日期格式化等功能。
 *
 * 这些功能也许会在某些支持的特定平台上提供辅助实现（例如在JVM平台上使用 `java.time` 相关API）。
 *
 * 当然，也可以自行实现 `Timestamp` 来定制化其内部细节。
 *
 * ## 简单包装
 *
 * [Timestamp] 是一种简单的包装类型，大多数情况下对外提供的 [Timestamp] 很可能是在保证结果一致的情况下即用即造的。
 * 例如：
 *
 * ```kotlin
 * interface Foo {
 *    val timestamp: Timestamp
 * }
 *
 * class FooImpl : Foo {
 *     val timestamp: Timestamp
 *          get() = InternalTimestampImpl(time)
 * }
 * ```
 *
 * ## [equals] & [hashCode]
 *
 * [Timestamp] 应支持与任意 [Timestamp] 类型进行匹配，但是无法保证 [equals] 结果为 `true` 的两个结果的 [hashCode] 相同。
 *
 *
 * ## [MillisecondTimestamp]
 *
 * [MillisecondTimestamp] 是全平台的默认实现，提供一个毫秒值，进行一个简单的包装。
 *
 * @author ForteScarlet
 */
public interface Timestamp : Comparable<Timestamp> {
    /**
     * 毫秒级时间戳结果。
     */
    public val milliseconds: Long

    /**
     * 将时间戳时间转化为指定单位的结果。
     */
    public infix fun timeAs(unit: TimeUnit): Long = unit.convert(milliseconds, TimeUnit.MILLISECONDS)

    /**
     * 默认情况下，[Timestamp] 通过 [milliseconds] 进行顺序比较。
     */
    override fun compareTo(other: Timestamp): Int = milliseconds.compareTo(other.milliseconds)

    public companion object {
        /**
         * 通过毫秒值得到一个 [Timestamp]。
         *
         * @see MillisecondTimestamp
         */
        @JvmStatic
        public fun ofMilliseconds(milliseconds: Long): Timestamp = MillisecondTimestamp(milliseconds)

        /**
         * 得到一个记录了当前时间戳信息的 [Timestamp] 实例。
         *
         * 实验性: [now] 在大多数原生平台中实现不稳定。请尽可能在可靠平台中使用（例如 `JVM`、`JS`）
         */
        @JvmStatic
        @ExperimentalSimbotAPI
        public fun now(): Timestamp = nowInternal()
    }
}

/**
 * 得到一个记录了当前时间戳信息的 [Timestamp] 实例。
 */
internal expect fun nowInternal(): Timestamp
