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

import love.forte.simbot.resource.SourceResource
import love.forte.simbot.resource.toResource
import java.io.File
import java.net.URI
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class SourceResourceTypeTests {

    @Test
    fun testSourceResourceInheritance() {
        assertIs<SourceResource>(File(".").toResource())
        assertIs<SourceResource>(Path(".").toResource())
        assertIs<SourceResource>(URI.create("https://localhost:8080").toResource())
    }

}
