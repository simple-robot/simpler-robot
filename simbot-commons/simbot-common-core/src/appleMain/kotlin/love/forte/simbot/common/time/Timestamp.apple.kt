/*
 *     Copyright (c) 2024. ForteScarlet.
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

import kotlinx.cinterop.UnsafeNumber
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.time.NSDateTimestamp.Companion.toTimestamp
import platform.Foundation.NSDate
import platform.Foundation.compare
import platform.Foundation.now
import platform.Foundation.timeIntervalSince1970


/**
 * 基于 [NSDate] 的 [Timestamp] 实现。
 *
 * @see Timestamp
 */
public class NSDateTimestamp(public val date: NSDate) : Timestamp {
    /**
     * 得到 [date] 对应的 epoch 毫秒时间戳。
     *
     * 通过 [NSDate.timeIntervalSince1970] 计算得到。
     *
     * 计算方式：
     * ```kotlin
     * (date.timeIntervalSince1970() * 1000).toLong()
     * ```
     */
    override val milliseconds: Long
        get() = (date.timeIntervalSince1970() * 1000).toLong()

    @OptIn(UnsafeNumber::class)
    override fun compareTo(other: Timestamp): Int {
        if (other is NSDateTimestamp) {
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            return date.compare(other.date).toInt()
        }

        return milliseconds.compareTo(other.milliseconds)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Timestamp) return false
        if (other is NSDateTimestamp) return date == other.date

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int = date.hashCode()
    override fun toString(): String = "NSDateTimestamp(milliseconds=$milliseconds, date=$date)"


    public companion object {

        /**
         * 通过 [NSDate] 获取 [Timestamp]。
         */
        @ExperimentalSimbotAPI
        public fun NSDate.toTimestamp(): Timestamp = NSDateTimestamp(this)

    }
}


/**
 * 得到一个记录了当前 epoch 时间的 Timestamp 实例。
 *
 * 通过 [NSDate.timeIntervalSince1970] 计算得到。
 *
 * 计算方式：
 * ```kotlin
 * (NSDate.now().timeIntervalSince1970() * 1000).toLong()
 * ```
 *
 * @see NSDateTimestamp
 */
@OptIn(ExperimentalSimbotAPI::class)
internal actual fun nowInternal(): Timestamp =
    NSDate.now().toTimestamp()

// NSDate
