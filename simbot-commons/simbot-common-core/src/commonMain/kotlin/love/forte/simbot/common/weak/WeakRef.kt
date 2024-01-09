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

package love.forte.simbot.common.weak

import kotlin.reflect.KProperty


/**
 * A weak reference definition.
 *
 * @author ForteScarlet
 */
public interface WeakRef<T : Any> {
    public val value: T?
    public fun clear()
}

/**
 * Create [WeakRef] from [ref].
 */
public expect fun <T : Any> weakRef(ref: T): WeakRef<T>

/**
 * Delegate [WeakRef.value] to a property.
 *
 * ```kotlin
 * val property by weak(value)
 * ```
 */
public operator fun <T : Any> WeakRef<T>.getValue(o: Any?, property: KProperty<*>): T? = value
