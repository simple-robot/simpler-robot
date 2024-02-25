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

import love.forte.simbot.resource.toResource
import java.io.File
import java.io.InputStream
import java.io.Reader
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

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

class ResourceTests {
    @Test
    fun fileResourceTest() {
        val file = File.createTempFile("simbot-file-resource", "test", File(".")).also {
            it.deleteOnExit()
        }

        val text = "Hello Simbot!"

        file.writeText(text, Charsets.UTF_8)

        with(file.toResource()) {
            assertEquals(text, string())
            assertContentEquals(text.toByteArray(), data())
            assertEquals(text, reader().use(Reader::readText))
            assertContentEquals(text.toByteArray(), inputStream().use(InputStream::readAllBytes))
        }
    }

    @Test
    fun pathResourceTest() {
        val path = File.createTempFile("simbot-path-resource", "test", File(".")).also {
            it.deleteOnExit()
        }.toPath()

        val text = "Hello Simbot!"

        path.writeText(text, Charsets.UTF_8)

        with(path.toResource()) {
            assertEquals(text, string())
            assertContentEquals(text.toByteArray(), data())
            assertEquals(text, reader().use(Reader::readText))
            assertContentEquals(text.toByteArray(), inputStream().use(InputStream::readAllBytes))
        }
    }

    @Test
    fun uriResourceTest() {
        val text = "Hello Simbot!"
        val uri = File.createTempFile("simbot-uri-resource", "test", File(".")).also {
            it.deleteOnExit()
            it.writeText(text, Charsets.UTF_8)
        }.toURI()

        with(uri.toResource()) {
            assertEquals(text, string())
            assertContentEquals(text.toByteArray(), data())
            assertContentEquals(text.toByteArray(), inputStream().use(InputStream::readAllBytes))
        }
    }

}
