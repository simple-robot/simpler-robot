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

import kotlin.test.*

/**
 * Tests for [ValueMatcher] interface, especially the new findParam/findParameters methods.
 *
 * @author ForteScarlet
 */
class ValueMatcherTests {

    @Test
    fun getParamVsFindParamTest() {
        val matcher = RegexValueMatcher("Hello, {{name,.+}}!", false, emptySet())

        // getParam requires exact match
        val exactMatch = matcher.getParam("name", "Hello, World!")
        assertNotNull(exactMatch)
        assertEquals("World", exactMatch)

        // getParam fails with partial match
        val partialMatch = matcher.getParam("name", "prefix Hello, World! suffix")
        assertNull(partialMatch)

        // findParam succeeds with partial match
        val findResult = matcher.findParam("name", "prefix Hello, World! suffix")
        assertNotNull(findResult)
        assertEquals("World", findResult)

        // findParam also works with exact match
        val findExact = matcher.findParam("name", "Hello, World!")
        assertNotNull(findExact)
        assertEquals("World", findExact)
    }

    @Test
    fun getParametersVsFindParametersTest() {
        val matcher = RegexValueMatcher("{{greeting,Hello|Hi}}, {{name,.+}}!", false, emptySet())

        // getParameters requires exact match
        val exactParams = matcher.getParameters("Hello, World!")
        assertEquals("Hello", exactParams["greeting"])
        assertEquals("World", exactParams["name"])

        // getParameters fails with partial match
        val partialParams = matcher.getParameters("prefix Hello, World! suffix")
        assertNull(partialParams["greeting"])
        assertNull(partialParams["name"])

        // findParameters succeeds with partial match
        val findParams = matcher.findParameters("prefix Hi, Forte! suffix")
        assertEquals("Hi", findParams["greeting"])
        assertEquals("Forte", findParams["name"])
    }

    @Test
    fun multilineContentFindTest() {
        val matcher = RegexValueMatcher(
            "^start: {{content,.+}}$",
            false,
            setOf(RegexOption.MULTILINE)
        )

        val multilineText = """
            line1
            start: important data
            line3
        """.trimIndent()

        // getParam should fail (doesn't match entire text)
        val getResult = matcher.getParam("content", multilineText)
        assertNull(getResult)

        // findParam should succeed (finds matching line)
        val findResult = matcher.findParam("content", multilineText)
        assertNotNull(findResult)
        assertEquals("important data", findResult)
    }

    @Test
    fun multipleMatchesFindTest() {
        val matcher = RegexValueMatcher("{{word,\\w+}}", false, emptySet())

        val text = "The quick brown fox"

        // findParam finds the first match
        val firstWord = matcher.findParam("word", text)
        assertNotNull(firstWord)
        assertEquals("The", firstWord)

        // Can use findParameters to get the first match
        val params = matcher.findParameters(text)
        assertEquals("The", params["word"])
    }

    @Test
    fun emptyMatcherTest() {
        // Test EmptyFilterParameterMatcher
        assertNull(EmptyFilterParameterMatcher.getParam("any", "text"))
        assertNull(EmptyFilterParameterMatcher.findParam("any", "text"))

        val getParams = EmptyFilterParameterMatcher.getParameters("text")
        assertNull(getParams["any"])

        val findParams = EmptyFilterParameterMatcher.findParameters("text")
        assertNull(findParams["any"])
    }

    @Test
    fun complexPatternFindTest() {
        val matcher = RegexValueMatcher(
            "User: {{user,\\w+}}, Score: {{score,\\d+}}",
            false,
            setOf(RegexOption.IGNORE_CASE)
        )

        val text = """
            Welcome to the game!
            USER: Alice, SCORE: 100
            End of message
        """.trimIndent()

        // getParam should fail (doesn't match entire text)
        assertNull(matcher.getParam("user", text))
        assertNull(matcher.getParam("score", text))

        // findParam should succeed
        val user = matcher.findParam("user", text)
        val score = matcher.findParam("score", text)

        assertNotNull(user)
        assertEquals("Alice", user)

        assertNotNull(score)
        assertEquals("100", score)
    }

    @Test
    fun namedGroupAndDynamicParamCombinedTest() {
        val matcher = RegexValueMatcher(
            "(?<greeting>Hello|Hi), {{name,.+?}}(?<punctuation>!+)",
            false,
            emptySet()
        )

        val text = "Hi, World!! suffix"

        assertTrue(matcher.regex.containsMatchIn(text))

        // findParam can extract both named groups and dynamic params
        assertEquals("Hi", matcher.findParam("greeting", text))
        assertEquals("World", matcher.findParam("name", text))
        assertEquals("!!", matcher.findParam("punctuation", text))
    }

    @Test
    fun regexValueMatcherToStringTest() {
        val matcher = RegexValueMatcher(
            "Hello, {{name,.+}}!",
            false,
            setOf(RegexOption.IGNORE_CASE)
        )

        val str = matcher.toString()
        assertTrue(str.contains("Hello, {{name,.+}}!"))
        assertTrue(str.contains("isPlainText=false"))
        assertTrue(str.contains("IGNORE_CASE"))
    }
}
