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

package love.forte.simbot.timestamp

import love.forte.simbot.utils.TimeUnit


/**
 * 一个 Unix 时间戳。
 * 是从 `UTC 1970.01.01T00:00:00Z` 直至现在所经过的时间，
 * 常见的时间单位有秒或毫秒。
 *
 *
 *  不是日期API，而仅是一种无视时间戳数值单位的包装体。
 *
 *
 * @author ForteScarlet
 */
public sealed class Timestamp {
    // TODO

    public abstract val milliseconds: Long

    public fun getTime(unit: TimeUnit): Long = unit.convert(milliseconds, TimeUnit.MILLISECONDS)
}


