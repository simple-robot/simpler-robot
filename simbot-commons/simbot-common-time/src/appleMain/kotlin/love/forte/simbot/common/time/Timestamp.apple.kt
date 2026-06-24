/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

import kotlinx.cinterop.UnsafeNumber
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import platform.Foundation.NSDate
import platform.Foundation.compare
import platform.Foundation.timeIntervalSince1970
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.time.Instant

private const val SECONDS_BETWEEN_1970_AND_2001 = 978_307_200L
private const val NANOS_PER_SECOND = 1_000_000_000L


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

    /**
     * 将 [NSDate] 尽可能无损地转化为 [Instant]。
     *
     * 使用 [NSDate.timeIntervalSinceReferenceDate] 先按 Apple reference epoch 拆分整数秒与小数秒，
     * 再只把固定偏移加到整数秒上，避免把大数秒值放大后再取小数而额外损失精度。
     *
     * 纳秒调整值使用 [Long] 传给 [Instant.fromEpochSeconds]，由标准库负责处理四舍五入后的进位归一化。
     */
    override fun toInstant(): Instant {
        val referenceSeconds = date.timeIntervalSinceReferenceDate()
        val referenceEpochSeconds = floor(referenceSeconds).toLong()
        val fraction = referenceSeconds - referenceEpochSeconds.toDouble()
        val nanosecondAdjustment = (fraction * NANOS_PER_SECOND).roundToLong()

        return Instant.fromEpochSeconds(
            referenceEpochSeconds + SECONDS_BETWEEN_1970_AND_2001,
            nanosecondAdjustment
        )
    }

    @OptIn(UnsafeNumber::class)
    override fun compareTo(other: Timestamp): Int {
        if (other is NSDateTimestamp) {
            @Suppress("REDUNDANT_CALL_OF_CONVERSION_METHOD")
            // 如果不转化，编译器报错：
            // e: file:///simbot-commons/simbot-common-core/src/appleMain/kotlin/love/forte/simbot/common/time/Timestamp.apple.kt:57:20 Return type mismatch: expected 'Int', actual 'Long'.
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
    override fun toString(): String = "NSDateTimestamp(date=$date)"


    public companion object {

        /**
         * 通过 [NSDate] 获取 [Timestamp]。
         */
        @ExperimentalSimbotAPI
        public fun NSDate.toTimestamp(): Timestamp = NSDateTimestamp(this)

    }
}
