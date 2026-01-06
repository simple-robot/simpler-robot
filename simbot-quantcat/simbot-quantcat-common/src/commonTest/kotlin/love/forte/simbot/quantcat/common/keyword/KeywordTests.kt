/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.quantcat.common.keyword

import love.forte.simbot.quantcat.common.filter.MatchType.*
import kotlin.test.*

/**
 * 有些测试（正则规格）在 JS 中通过不了。
 */
internal expect val isWebPlatform: Boolean

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

    @Test
    fun keywordWithRegexOptionsTest() {
        // Test IGNORE_CASE option with strict matching
        with(SimpleKeyword("hello", false, setOf(RegexOption.IGNORE_CASE), isStrict = true)) {
            assertTrue(isStrict)
            assertTrue(regex.matches("hello"))
            assertTrue(regex.matches("Hello"))
            assertTrue(regex.matches("HELLO"))
            assertTrue(regex.matches("HeLLo"))
        }

        // Test MULTILINE option with non-strict matching
        with(SimpleKeyword("^Hello$", false, setOf(RegexOption.MULTILINE), isStrict = false)) {
            assertFalse(isStrict)
            assertTrue(regex.containsMatchIn("Hello\nWorld"))
            assertTrue(regex.containsMatchIn("World\nHello"))
            assertTrue(regex.containsMatchIn("World\nHello\nForte"))
        }

        // Test multiple options (IGNORE_CASE + MULTILINE)
        with(SimpleKeyword("^hello$", false, setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE), isStrict = false)) {
            assertTrue(regex.containsMatchIn("Hello\nWorld"))
            assertTrue(regex.containsMatchIn("HELLO\nWorld"))
            assertTrue(regex.containsMatchIn("World\nHeLLo\nForte"))
        }

        // Test that plain text cannot use regex options
        assertFailsWith<IllegalStateException> {
            SimpleKeyword("hello", true, setOf(RegexOption.IGNORE_CASE), isStrict = true)
                .regex // lazy initialization triggers the error
        }
    }

    @Test
    fun keywordWithRegexOptionsParamTest() {
        // Test parameter extraction with IGNORE_CASE (strict matching)
        with(SimpleKeyword("hello, {{name,.+}}!", false, setOf(RegexOption.IGNORE_CASE), isStrict = true)) {
            val name1 = regexValueMatcher.getParam("name", "hello, forte!")
            assertNotNull(name1)
            assertEquals("forte", name1)

            val name2 = regexValueMatcher.getParam("name", "HELLO, FORTE!")
            assertNotNull(name2)
            assertEquals("FORTE", name2)

            val name3 = regexValueMatcher.getParam("name", "HeLLo, FoRtE!")
            assertNotNull(name3)
            assertEquals("FoRtE", name3)
        }

        // Test parameter extraction with MULTILINE using findParam (non-strict)
        with(
            SimpleKeyword(
                "^start: {{content,.+}}$",
                false,
                setOf(RegexOption.MULTILINE),
                isStrict = false
            )
        ) {
            // Using findParam for non-strict matching
            val content = regexValueMatcher.findParam("content", "line1\nstart: important\nline3")
            assertNotNull(content)
            assertEquals("important", content)

            // getParam should fail for non-matching entire text
            // TODO JS not work here.
            //  see https://youtrack.jetbrains.com/issue/KT-82450
            if (!isWebPlatform) {
                val strictResult = regexValueMatcher.getParam("content", "line1\nstart: important\nline3")
                assertNull(strictResult)
            }
        }

        // Test parameter extraction with multiple options (IGNORE_CASE + MULTILINE)
        with(
            SimpleKeyword(
                text = "^(?<greeting>hello|hi), {{name,.+}}!$",
                isPlainText = false,
                regexOptions = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE),
                isStrict = false
            )
        ) {
            // Use findParam for non-strict matching
            val greeting = regexValueMatcher.findParam("greeting", "prefix\nHELLO, forte!\nsuffix")
            assertNotNull(greeting)
            assertEquals("HELLO", greeting)

            val name = regexValueMatcher.findParam("name", "prefix\nHi, FORTE!\nsuffix")
            assertNotNull(name)
            assertEquals("FORTE", name)
        }
    }

    @Test
    fun strictVsNonStrictParamExtractionTest() {
        // Strict matching requires full text match
        with(SimpleKeyword("Hello, {{name,.+}}!", false, emptySet(), isStrict = true)) {
            assertTrue(isStrict)

            // getParam should succeed with exact match
            val name1 = regexValueMatcher.getParam("name", "Hello, World!")
            assertNotNull(name1)
            assertEquals("World", name1)

            // getParam should fail with partial match
            val name2 = regexValueMatcher.getParam("name", "prefix Hello, World! suffix")
            assertNull(name2)
        }

        // Non-strict matching allows partial text match
        with(SimpleKeyword("Hello, {{name,.+}}!", false, emptySet(), isStrict = false)) {
            assertFalse(isStrict)

            // findParam should succeed with partial match
            val name1 = regexValueMatcher.findParam("name", "prefix Hello, World! suffix")
            assertNotNull(name1)
            assertEquals("World", name1)

            // findParam also works with exact match
            val name2 = regexValueMatcher.findParam("name", "Hello, World!")
            assertNotNull(name2)
            assertEquals("World", name2)
        }
    }

    @Test
    fun backwardCompatibilityTest() {
        // Test that old constructor (without regexOptions) still works
        val keyword1 = SimpleKeyword("Hello", true)
        assertTrue(keyword1.isStrict) // Default is strict
        assertTrue(keyword1.regex.matches("Hello"))
        assertFalse(keyword1.regex.matches("hello"))

        val keyword2 = SimpleKeyword("Hello.*", false)
        assertTrue(keyword2.isStrict) // Default is strict
        assertTrue(keyword2.regex.matches("Hello"))
        assertTrue(keyword2.regex.matches("Hello, World"))
        assertFalse(keyword2.regex.matches("hello"))
    }

    @Test
    fun matchTypeStrictFlagTest() {
        // Test strict match types
        assertTrue(TEXT_EQUALS.isStrict)
        assertTrue(TEXT_EQUALS_IGNORE_CASE.isStrict)
        assertTrue(REGEX_MATCHES.isStrict)

        // Test non-strict match types
        assertFalse(TEXT_STARTS_WITH.isStrict)
        assertFalse(TEXT_ENDS_WITH.isStrict)
        assertFalse(TEXT_CONTAINS.isStrict)
        assertFalse(REGEX_CONTAINS.isStrict)
    }

}
