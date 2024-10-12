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

package love.forte.simbot.common.id

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 *
 * @author ForteScarlet
 */
class UUIDTests {

    @Test
    fun uuidParseTest() {
        val uid = UUID.from(444821983651646037L, -5295967363889204301L)
        assertEquals("062c533c-c3d7-4a55-b680-f1bde55273b3", uid.literal)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun ktUuidToUUIDTest() {
        val ktUuid = Uuid.random()
        val uuid = ktUuid.ID

        ktUuid.toLongs { mostSignificantBits, leastSignificantBits ->
            assertEquals(mostSignificantBits, uuid.mostSignificantBits)
            assertEquals(leastSignificantBits, uuid.leastSignificantBits)
        }

        assertEquals(ktUuid.toString(), uuid.literal)

        val ktUuid2 = uuid.toKotlin()
        assertEquals(ktUuid, ktUuid2)
    }


}
