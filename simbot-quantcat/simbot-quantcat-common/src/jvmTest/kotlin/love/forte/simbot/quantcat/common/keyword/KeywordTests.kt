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

package love.forte.simbot.quantcat.common.keyword

import love.forte.simbot.quantcat.common.filter.MatchType.*
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class KeywordTests {

    @Test
    fun keywordMatchTest() {
        val plainKeyword = SimpleKeyword("Hello", true)
        with(TEXT_EQUALS) {
            assertTrue(match(plainKeyword, "Hello"))
            assertFalse(match(plainKeyword, "Hello1"))
        }

        with(TEXT_EQUALS_IGNORE_CASE) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "hello"))
            assertFalse(match(plainKeyword, "1Hello"))
        }

        with(TEXT_ENDS_WITH) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "Forte, Hello"))
            assertFalse(match(plainKeyword, "Hello1"))
        }

        with(TEXT_STARTS_WITH) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "Hello, forte"))
            assertFalse(match(plainKeyword, "1Hello"))
        }

        with(TEXT_CONTAINS) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "Hello, forte"))
            assertTrue(match(plainKeyword, "1Hello"))
            assertTrue(match(plainKeyword, "1Hello1"))
            assertTrue(match(plainKeyword, "Hello1"))
        }


        with(REGEX_MATCHES) {
            val regexKeyword = SimpleKeyword("Hello.*", false)
            assertTrue(match(regexKeyword, "Hello"))
            assertTrue(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertFalse(match(regexKeyword, "1Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
        }

        with(REGEX_MATCHES) {
            val regexKeyword = SimpleKeyword("Hello\\d+", false)
            assertFalse(match(regexKeyword, "Hello"))
            assertFalse(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertFalse(match(regexKeyword, "1Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
            assertTrue(match(regexKeyword, "Hello123"))
        }

        with(REGEX_CONTAINS) {
            val regexKeyword = SimpleKeyword("Hello.+", false)
            assertFalse(match(regexKeyword, "Hello"))
            assertTrue(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertTrue(match(regexKeyword, "1Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
        }

        with(REGEX_CONTAINS) {
            val regexKeyword = SimpleKeyword("Hello\\d+", false)
            assertFalse(match(regexKeyword, "Hello"))
            assertFalse(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertTrue(match(regexKeyword, "123Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
            assertTrue(match(regexKeyword, "Hello123"))
        }


    }

    @Test
    fun keywordParamTest() {
        with(SimpleKeyword("Hello, {{name,.+}}!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte!!", name)
        }
        with(SimpleKeyword("Hello, {{name,.+?}}!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte", name)
        }
        with(SimpleKeyword("Hello, (?<name>.+)!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte!!", name)
        }
        with(SimpleKeyword("Hello, (?<name>.+?)!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte", name)
        }
    }

}
