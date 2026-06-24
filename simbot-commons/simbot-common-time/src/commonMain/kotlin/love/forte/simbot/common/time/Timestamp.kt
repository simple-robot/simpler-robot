/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.common.time

import love.forte.simbot.common.time.Timestamp.Companion.now
import kotlin.jvm.JvmStatic
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * 一个用于表示 Unix 时间戳的类型。
 * 是从 `UTC 1970.01.01T00:00:00Z` 直至现在所经过的时间，
 * 常见的时间单位有秒或毫秒。
 *
 * [Timestamp] 是线程安全的**不可变**对象。
 *
 * [Timestamp] **不是日期API** ，而仅是一种忽略时间单位的时间戳包装体。
 * 因此 [Timestamp] 本身不提供例如解析某格式的日期（例如 ISO-8601）或进行日期格式化等功能。
 *
 * 它类似 [Instant][kotlin.time.Instant]，只不过诞生自 Kotlin 标准库尚未提供 [Instant][kotlin.time.Instant] 的时代。
 *
 * 这些功能也许会在某些支持的特定平台上提供辅助实现或额外实现：
 * - 在JVM平台上使用 `java.time` 相关API
 * - 在 apple 平台上提供基于 `platform.Foundation.NSDate` 的实现
 * - 在 web 平台上提供基于 `js.date.Date` 的实现
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
 * ## 默认实现
 *
 * - [MillisecondTimestamp] 是全平台的默认实现，提供一个毫秒值，进行一个简单的包装。
 * - [StandardInstantTimestamp] 是自 5.0 开始提供的新的全平台的默认实现，
 *   基于 Kotlin （2.3+）的标准库的 [Instant][kotlin.time.Instant] 提供 [Timestamp] 的能力实现。
 *
 * @see MillisecondTimestamp
 * @see StandardInstantTimestamp
 *
 * @author ForteScarlet
 */
public interface Timestamp : Comparable<Timestamp> {
    /**
     * 毫秒级时间戳，代表从 `UTC 1970.01.01T00:00:00Z` 直至现在所经过的时间。
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

    /**
     * 根据当前的毫秒时间戳信息，转化为一个 [Instant]。
     *
     * @since 5.0
     * @see Instant.fromEpochMilliseconds
     */
    public fun toInstant(): Instant = Instant.fromEpochMilliseconds(milliseconds)

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
         * 从 5.0（对应 Kotlin 2.3）开始，[now] 直接通过 Kotlin 标准库的 [Clock.System] 得到 [Timestamp]。
         *
         * 相当于：
         * ```Kotlin
         * val now = now(Clock.System)
         * ```
         */
        @JvmStatic
        public fun now(): Timestamp = now(Clock.System)

        /**
         * 得到一个记录了当前时间戳信息的 [Timestamp] 实例。
         *
         * @since 5.0
         */
        @JvmStatic
        public fun now(clock: Clock): Timestamp = StandardInstantTimestamp(clock.now())
    }
}

