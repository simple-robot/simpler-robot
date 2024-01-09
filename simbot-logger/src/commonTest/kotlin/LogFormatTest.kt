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

import love.forte.simbot.logger.internal.logFormat
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class LogFormatTest {
    
    @Test
    fun formatTest() {
        assertEquals("hello world", "hello {}".logFormat(arrayOf("world")) {
            assertEquals(0, it)
        }, "hello {} with ['world']")
        
        assertEquals("hello world", "hello {}".logFormat(arrayOf("world", "remained")) {
            assertEquals(1, it)
        }, "hello {} with ['world', 'remained']")
        
        assertEquals("hello", "hello".logFormat(arrayOf("world", "remained")) {
            assertEquals(2, it)
        })
        
        assertEquals("hello {}", "hello {}".logFormat(emptyArray<String>()) {
            assertEquals(0, it)
        })
    }
    
}
