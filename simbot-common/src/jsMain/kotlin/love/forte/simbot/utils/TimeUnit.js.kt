/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.utils

private const val NANO_SCALE = 1L
private const val MICRO_SCALE = 1000L * NANO_SCALE
private const val MILLI_SCALE = 1000L * MICRO_SCALE
private const val SECOND_SCALE = 1000L * MILLI_SCALE
private const val MINUTE_SCALE = 60L * SECOND_SCALE
private const val HOUR_SCALE = 60L * MINUTE_SCALE
private const val DAY_SCALE = 24L * HOUR_SCALE

/**
 * 时间单位，用于时间转化。
 */
public actual enum class TimeUnit(private val scale: Long) {
    NANOSECONDS(NANO_SCALE),
    MICROSECONDS(MICRO_SCALE),
    MILLISECONDS(MILLI_SCALE),
    SECONDS(SECOND_SCALE),
    MINUTES(MINUTE_SCALE),
    HOURS(HOUR_SCALE),
    DAYS(DAY_SCALE);

    private val maxNanos: Long = Long.MAX_VALUE / scale
    private val maxMicros: Long
    private val maxMillis: Long
    private val maxSecs: Long
    private val microRatio: Long
    private val milliRatio: Int
    private val secRatio: Int

    init {
        val ur: Long =
            if (scale >= MICRO_SCALE) scale / MICRO_SCALE else MICRO_SCALE / scale
        microRatio = ur
        maxMicros = Long.MAX_VALUE / ur
        val mr: Long =
            if (scale >= MILLI_SCALE) scale / MILLI_SCALE else MILLI_SCALE / scale
        milliRatio = mr.toInt()
        maxMillis = Long.MAX_VALUE / mr
        val sr: Long =
            if (scale >= SECOND_SCALE) scale / SECOND_SCALE else SECOND_SCALE / scale
        secRatio = sr.toInt()
        maxSecs = Long.MAX_VALUE / sr
    }

    /**
     * 转为 Nanos
     */
    public actual open fun toNanos(duration: Long): Long {
        return when {
            scale == NANO_SCALE -> duration
            duration > maxNanos -> Long.MAX_VALUE
            duration < -maxNanos -> Long.MIN_VALUE
            else -> duration * scale
        }
    }

    /**
     * 转为 Micros
     */
    public actual open fun toMicros(duration: Long): Long {
        return when {
            scale <= MICRO_SCALE -> if (scale == MICRO_SCALE) {
                duration
            } else {
                duration / microRatio
            }

            duration > maxMicros -> Long.MAX_VALUE
            duration < -maxMicros -> Long.MIN_VALUE
            else -> duration * microRatio
        }
    }

    /**
     * 转为 Millis
     */
    public actual open fun toMillis(duration: Long): Long {
        return when {
            scale <= MILLI_SCALE -> if (scale == MILLI_SCALE) {
                duration
            } else {
                duration / milliRatio
            }

            duration > maxMillis -> Long.MAX_VALUE
            duration < -maxMillis -> Long.MIN_VALUE
            else -> duration * milliRatio
        }
    }

    /**
     * 转为 Seconds
     */
    public actual open fun toSeconds(duration: Long): Long {
        return when {
            scale <= SECOND_SCALE -> if (scale == SECOND_SCALE) {
                duration
            } else {
                duration / secRatio
            }

            duration > maxSecs -> Long.MAX_VALUE
            duration < -maxSecs -> Long.MIN_VALUE
            else -> duration * secRatio
        }
    }

    /**
     * 转为 Minutes
     */
    public actual open fun toMinutes(duration: Long): Long {
        return when {
            scale <= SECOND_SCALE -> if (scale == SECOND_SCALE) {
                duration
            } else {
                duration / secRatio
            }

            duration > maxSecs -> Long.MAX_VALUE
            duration < -maxSecs -> Long.MIN_VALUE
            else -> duration * secRatio
        }
    }

    /**
     * 转为 Hours
     */
    public actual open fun toHours(duration: Long): Long {
        return cvt(duration, HOUR_SCALE, scale)
    }

    /**
     * 转为 Days
     */
    public actual open fun toDays(duration: Long): Long {
        return cvt(duration, DAY_SCALE, scale)
    }

    /**
     * 将以 [sourceUnit] 为单位的 [sourceDuration] 转化为当前单位的值
     */
    public actual fun convert(sourceDuration: Long, sourceUnit: TimeUnit): Long {
        return when (this) {
            NANOSECONDS -> sourceUnit.toNanos(sourceDuration)
            MICROSECONDS -> sourceUnit.toMicros(sourceDuration)
            MILLISECONDS -> sourceUnit.toMillis(sourceDuration)
            SECONDS -> sourceUnit.toSeconds(sourceDuration)
            else -> cvt(sourceDuration, scale, sourceUnit.scale)
        }
    }


}

/**
 * General conversion utility.
 *
 * @param d duration
 * @param dst result unit scale
 * @param src source unit scale
 */
private fun cvt(d: Long, dst: Long, src: Long): Long {
    var r: Long
    return when {
        src == dst -> d
        src < dst -> d / (dst / src)
        d > (Long.MAX_VALUE / (src / dst).also { r = it }) -> Long.MAX_VALUE
        d < -Long.MAX_VALUE -> Long.MIN_VALUE
        else -> d * r
    }
}
