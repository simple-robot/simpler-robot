/*
 *     Copyright (c) 2024. ForteScarlet.
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

package resource

import love.forte.simbot.resource.*
import love.forte.simbot.resource.ResourceResolver.Companion.resolve
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class ResourceResolverTests {
    private enum class Value {
        UKN,
        BA,
        S
    }

    @Test
    fun resourceResolverTest() {
        var value: Value? = null

        val resource = object : ResourceResolver<Unit> {
            override fun resolveUnknown(resource: Resource, context: Unit) {
                value = Value.UKN
            }

            override fun resolveByteArray(resource: ByteArrayResource, context: Unit) {
                value = Value.BA
            }

            override fun resolveString(resource: StringResource, context: Unit) {
                value = Value.S
            }
        }

        resource.resolve(
            byteArrayOf().toResource(),
            Unit
        )

        assertEquals(Value.BA, value)

        resource.resolve(
            "".toStringResource(),
            Unit
        )

        assertEquals(Value.S, value)

        resource.resolve(
            object : Resource {
                override fun data(): ByteArray {
                    return byteArrayOf()
                }
            },
            Unit
        )

        assertEquals(Value.UKN, value)

    }

}
