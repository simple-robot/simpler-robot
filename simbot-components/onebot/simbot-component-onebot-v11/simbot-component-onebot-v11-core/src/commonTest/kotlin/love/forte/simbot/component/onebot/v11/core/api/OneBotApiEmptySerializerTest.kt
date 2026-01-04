/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.json.Json
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class OneBotApiEmptySerializerTest {
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Test
    fun deserializeNullDataTest() {
        val jsonText = """
            {"retcode":0,"status":"OK","data":null}
        """.trim()

        val data = json.decodeFromString(
            OneBotApiResult.emptySerializer(),
            jsonText
        )

        assertTrue(data.isSuccess)
        assertEquals(Unit, data.data)
    }

    @Test
    fun deserializeNoFieldDataTest() {
        val jsonText = """
            {"retcode":0,"status":"OK"}
        """.trim()

        val data = json.decodeFromString(
            OneBotApiResult.emptySerializer(),
            jsonText
        )

        assertTrue(data.isSuccess)
        assertEquals(Unit, data.data)
    }

    @Test
    fun deserializeFailedDataTest() {
        val jsonText = """
            {"retcode":100,"status":"failed"}
        """.trim()

        val data = json.decodeFromString(
            OneBotApiResult.emptySerializer(),
            jsonText
        )

        assertFalse(data.isSuccess)
        assertNull(data.data)
    }

}
