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

package love.forte.simbot.id

import kotlinx.serialization.json.Json
import love.forte.simbot.common.id.*
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.LongID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UIntID.Companion.ID
import love.forte.simbot.common.id.ULongID.Companion.ID
import love.forte.simbot.common.id.UUID.Companion.UUID
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IDTests {

    @Test
    fun uuidGenerateTest() {
        val id = UUID.random()
        val id2 = id.toString().UUID

        assertEquals(id, id2)
        assertEquals(id as ID, id2.toString().ID as ID)
        assertEquals(id as ID, id2.copy().toString().ID as ID)
        assertTrue(id.equalsExact(id2))
        assertTrue(id.equalsExact(id.copy()))
        assertTrue(id.equalsExact(id2.copy()))
    }

    @Test
    fun equalsTest() {
        assertEquals("1".ID as ID, 1.ID as ID)
    }

    @Test
    fun serializerTest() {
        val json = Json {
            isLenient = true
        }

        val i = Random.nextInt()
        val l = Random.nextLong()
        val ui = Random.nextUInt()
        val ul = Random.nextULong()

        assertEquals(json.encodeToString(IntID.serializer(), i.ID), i.toString())
        assertEquals(json.encodeToString(LongID.serializer(), l.ID), l.toString())
        assertEquals(json.encodeToString(UIntID.serializer(), ui.ID), ui.toString())
        assertEquals(json.encodeToString(ULongID.serializer(), ul.ID), ul.toString())

    }

}
