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
 * 得到一个记录了当前 epoch 时间的 Timestamp 实例。
 */
internal actual fun nowInternal(): Timestamp = DateTimestamp(newDate())


/**
 * 基于 [kotlin.js.Date] 的 [Timestamp] 实现。
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class DateTimestamp(public val date: JsAny) : Timestamp {
    override val milliseconds: Long
        get() = getDateTime(date).toDouble().toLong()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Timestamp) return false
        if (other is DateTimestamp) return date == other.date

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int = date.hashCode()
    override fun toString(): String = "DateTimestamp(milliseconds=$milliseconds, date=$date)"
}


private fun newDate(): JsAny = js("new Date()")
@Suppress("UNUSED_PARAMETER")
private fun getDateTime(date: JsAny): JsNumber = js("date.getTime()")
