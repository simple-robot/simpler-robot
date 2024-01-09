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

import java.lang.ref.WeakReference

public actual fun <T : Any> weakRef(ref: T): WeakRef<T> =
    WeakRefImpl(ref)

private class WeakRefImpl<T : Any>(ref: T) : WeakRef<T> {
    @Volatile
    private var ref: WeakReference<T>? = WeakReference(ref)

    override val value: T?
        get() = ref?.let { r ->
            r.get().also { if (it == null) ref = null }
        }

    override fun clear() {
        ref?.clear()
        ref = null
    }
}
