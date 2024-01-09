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

/**
 * 时间单位，用于时间转化。
 *
 * 在 JVM 中会使用 `java.util.concurrent.TimeUnit`，
 * 其他平台则会参照进行简单实现。
 */
public expect enum class TimeUnit {
    NANOSECONDS,
    MICROSECONDS,
    MILLISECONDS,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS;


    /**
     * 转为 Nanos
     */
    public open fun toNanos(duration: Long): Long

    /**
     * 转为 Micros
     */
    public open fun toMicros(duration: Long): Long

    /**
     * 转为 Millis
     */
    public open fun toMillis(duration: Long): Long

    /**
     * 转为 Seconds
     */
    public open fun toSeconds(duration: Long): Long

    /**
     * 转为 Minutes
     */
    public open fun toMinutes(duration: Long): Long

    /**
     * 转为 Hours
     */
    public open fun toHours(duration: Long): Long

    /**
     * 转为 Days
     */
    public open fun toDays(duration: Long): Long

    /**
     * 将以 [sourceUnit] 为单位的 [sourceDuration] 转化为当前单位的值
     */
    public fun convert(sourceDuration: Long, sourceUnit: TimeUnit): Long

}
