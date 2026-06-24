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

import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceReferenceDate
import kotlin.test.Test
import kotlin.test.assertEquals

private const val SECONDS_BETWEEN_1970_AND_2001_FOR_TEST = 978_307_200L

/**
 * [NSDate] 内部以 2001-01-01T00:00:00Z 为 reference date；
 * 测试中显式保留这个常量，避免预期值看起来像没有来源的魔法数字。
 */
class NSDateTimestampTests {

    @Test
    fun toInstantUsesReferenceDateFraction() {
        // 使用一个接近现代日期的 reference 秒数，验证实现没有先放大到 1970 epoch 后再拆分小数。
        val referenceEpochSeconds = 789_123_456L
        val referenceSeconds = referenceEpochSeconds + 0.123_456

        val timestamp = NSDateTimestamp(NSDate.dateWithTimeIntervalSinceReferenceDate(referenceSeconds))
        val instant = timestamp.toInstant()

        assertEquals(SECONDS_BETWEEN_1970_AND_2001_FOR_TEST + referenceEpochSeconds, instant.epochSeconds)
        // 这里的 123_456_001 来自 Double 对 789_123_456.123456 的实际表示；
        // 测试目标是保留 NSDate 已经持有的 Double 精度，而不是把十进制字面量还原成理想纳秒。
        assertEquals(123_456_001, instant.nanosecondsOfSecond)
    }

    @Test
    fun toInstantNormalizesRoundedNanoseconds() {
        // 0.9999999995 秒四舍五入后会得到 1_000_000_000 纳秒。
        // 这里验证实现使用 Long 版 nanosecondAdjustment，让 Instant 负责安全归一化到下一秒。
        val timestamp = NSDateTimestamp(NSDate.dateWithTimeIntervalSinceReferenceDate(0.999_999_999_5))
        val instant = timestamp.toInstant()

        assertEquals(SECONDS_BETWEEN_1970_AND_2001_FOR_TEST + 1, instant.epochSeconds)
        assertEquals(0, instant.nanosecondsOfSecond)
    }

    @Test
    fun toInstantHandlesNegativeReferenceDate() {
        // 1970 之后但 2001 reference date 之前的 NSDate 会得到负 reference 秒数。
        // floor(-0.25) == -1，剩余 fraction 为 0.75 秒，这能验证负数场景不会被 toLong 截断破坏。
        val timestamp = NSDateTimestamp(NSDate.dateWithTimeIntervalSinceReferenceDate(-0.25))
        val instant = timestamp.toInstant()

        assertEquals(SECONDS_BETWEEN_1970_AND_2001_FOR_TEST - 1, instant.epochSeconds)
        assertEquals(750_000_000, instant.nanosecondsOfSecond)
    }
}
