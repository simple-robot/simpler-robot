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

package love.forte.simbot.kook.api

import io.ktor.http.*
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.util.buildUrl
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class UrlTest {

    /**
     * The [KTOR-4402](https://youtrack.jetbrains.com/issue/KTOR-4402/The-pathSegments-returns-empty-strings-for-trailing-slashes).
     */
    @Test
    fun testUrlPathSegments() {
        assertEquals(
            listOf("", "api", "v3"),
            Url("https://www.kookapp.cn/api/v3").pathSegments,
            "Url.pathSegments should be ['', 'api', 'v3']"
        )

        assertEquals(
            listOf("", "api", "v3"),
            Kook.SERVER_URL_WITH_VERSION.pathSegments,
            "Kook.SERVER_URL_WITH_VERSION.pathSegments should be ['', 'api', 'v3']"
        )

        val api = buildUrl(Kook.SERVER_URL_WITH_VERSION) {}
        assertEquals(
            listOf("", "api", "v3"),
            api.pathSegments,
            "api.pathSegments should be ['', 'api', 'v3']"
        )
    }

}
