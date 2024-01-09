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

package love.forte.simbot.timestamp

import love.forte.simbot.common.weak.weakRef
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class WeakTests {

    @Test
    fun weakTest() {
        val data = Any()
        val ref = weakRef(data)
        println(ref)
        println(ref::class)
        val value = ref.value
        println(value)
        if (value != null) {
            println(value::class)
        }
    }

    @Test
    fun jsWeakTest() {
        @Suppress("UNUSED_VARIABLE") val ref = Any()
        val jsWeakRef = js("new WeakRef(ref);")
        println(jsWeakRef.deref)
        println(jsWeakRef.deref1)
    }

}
