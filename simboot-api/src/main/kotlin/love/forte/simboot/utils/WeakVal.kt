/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.utils

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

public class WeakVal<T>(init: Boolean, private val getFunc: () -> T) {

    private var weak = WeakReference<T>(if (init) getFunc() else null)

    public operator fun getValue(instance: Any, property: KProperty<*>): T {
        return weak.get() ?: synchronized(this) {
            weak.get() ?: getFunc().also { weak = WeakReference(it) }
        }
    }
}
