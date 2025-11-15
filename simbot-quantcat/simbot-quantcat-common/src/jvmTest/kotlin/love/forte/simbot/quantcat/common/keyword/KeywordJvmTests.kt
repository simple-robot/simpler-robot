/*
 *     Copyright (c) 2024-2025. ForteScarlet.
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
 *
 * @author ForteScarlet
 */
class KeywordJvmTests {

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
        // Test IGNORE_CASE option (strict matching)
        with(SimpleKeyword("hello", false, setOf(RegexOption.IGNORE_CASE), isStrict = true)) {
            assertTrue(isStrict)
            assertTrue(regex.matches("hello"))
            assertTrue(regex.matches("Hello"))
            assertTrue(regex.matches("HELLO"))
            assertTrue(regex.matches("HeLLo"))
        }

        // Test MULTILINE option (non-strict matching for containsMatchIn)
        with(SimpleKeyword("^Hello$", false, setOf(RegexOption.MULTILINE), isStrict = false)) {
            assertFalse(isStrict)
            assertTrue(regex.containsMatchIn("Hello\nWorld"))
            assertTrue(regex.containsMatchIn("World\nHello"))
            assertTrue(regex.containsMatchIn("World\nHello\nForte"))
        }

        // Test DOT_MATCHES_ALL option (strict matching)
        with(SimpleKeyword("Hello.*World", false, setOf(RegexOption.DOT_MATCHES_ALL), isStrict = true)) {
            assertTrue(regex.matches("Hello\nWorld"))
            assertTrue(regex.matches("Hello\n\nWorld"))
            assertTrue(regex.matches("Hello   World"))
        }

        // Test multiple options (IGNORE_CASE + DOT_MATCHES_ALL)
        with(SimpleKeyword("hello.*world", false, setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL), isStrict = true)) {
            assertTrue(regex.matches("Hello\nWorld"))
            assertTrue(regex.matches("HELLO\nWORLD"))
            assertTrue(regex.matches("HeLLo\n\nWoRLd"))
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

        // Test parameter extraction with DOT_MATCHES_ALL (strict matching)
        with(SimpleKeyword("start{{content,.+}}end", false, setOf(RegexOption.DOT_MATCHES_ALL), isStrict = true)) {
            val content = regexValueMatcher.getParam("content", "start\nmulti\nline\nend")
            assertNotNull(content)
            assertEquals("\nmulti\nline\n", content)
        }

        // Test parameter extraction with IGNORE_CASE (strict matching)
        with(SimpleKeyword("(?<greeting>hello|hi), {{name,.+}}!", false, setOf(RegexOption.IGNORE_CASE), isStrict = true)) {
            val greeting = regexValueMatcher.getParam("greeting", "HELLO, forte!")
            assertNotNull(greeting)
            assertEquals("HELLO", greeting)

            val name = regexValueMatcher.getParam("name", "Hi, FORTE!")
            assertNotNull(name)
            assertEquals("FORTE", name)
        }
    }
    
    @Test
    fun jvmSpecificRegexOptionsTest() {
        // Test COMMENTS option (allows whitespace and comments in pattern)
        with(SimpleKeyword("hello\\s+world", false, setOf(RegexOption.COMMENTS), isStrict = true)) {
            assertTrue(regex.matches("hello world"))
            assertTrue(regex.matches("hello   world"))
            assertTrue(regex.matches("hello\tworld"))
        }

        // Test LITERAL option would make everything literal (not useful for regex, but testing)
        with(SimpleKeyword("Hello.*", false, setOf(RegexOption.LITERAL), isStrict = true)) {
            assertTrue(regex.matches("Hello.*"))
            assertFalse(regex.matches("Hello World"))
        }

        // Test UNIX_LINES option (only \n is recognized as line terminator)
        with(SimpleKeyword("^Hello$", false, setOf(RegexOption.MULTILINE, RegexOption.UNIX_LINES), isStrict = false)) {
            assertTrue(regex.containsMatchIn("Hello\nWorld"))
            assertTrue(regex.containsMatchIn("World\nHello"))
        }
    }
    
    @Test
    fun dotMatchesAllWithMultilineContentTest() {
        // Test complex multiline content extraction with DOT_MATCHES_ALL
        with(SimpleKeyword("```{{lang,\\w+}}\n{{code,.+?}}\n```", false, setOf(RegexOption.DOT_MATCHES_ALL), isStrict = true)) {
            val text = "```kotlin\nfun main() {\n    println(\"Hello\")\n}\n```"
            val lang = regexValueMatcher.getParam("lang", text)
            val code = regexValueMatcher.getParam("code", text)
            
            assertNotNull(lang)
            assertEquals("kotlin", lang)
            
            assertNotNull(code)
            assertEquals("fun main() {\n    println(\"Hello\")\n}", code)
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

}
