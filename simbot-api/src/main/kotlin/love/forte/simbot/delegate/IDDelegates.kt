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

package love.forte.simbot.delegate

import love.forte.simbot.ID
import love.forte.simbot.LongID
import love.forte.simbot.Timestamp
import kotlin.reflect.KProperty


/**
 * 将一个 [Long] 委托为 [LongID].
 *
 * e.g.
 * ```kotlin
 * val id by longID { 123L }
 * ```
 *
 * @since 3.1.0
 *
 * @see Timestamp
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun LongIDDelegate.getValue(o: Any?, property: KProperty<*>?): LongID = value.ID

/**
 * 用于在 [LongIDDelegate.getValue] 中作为委托接收器，通过 [longID] 构造。
 *
 * @since 3.1.0
 *
 * @see LongIDDelegate.getValue
 */
@JvmInline
public value class LongIDDelegate @PublishedApi internal constructor(@PublishedApi internal val value: Long) {
    public companion object
}

/**
 * 构造一个 [LongIDDelegate] 对象，用于进行 [LongID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see LongIDDelegate.getValue
 */
public inline fun longID(block: LongIDDelegate.Companion.() -> Long): LongIDDelegate =
    LongIDDelegate(block(LongIDDelegate))

// TODO uLongID uIntID intID StringID
