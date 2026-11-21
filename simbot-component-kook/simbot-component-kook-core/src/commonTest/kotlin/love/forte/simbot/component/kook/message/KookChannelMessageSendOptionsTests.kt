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

package love.forte.simbot.component.kook.message

import love.forte.simbot.ability.SendOption
import love.forte.simbot.common.id.StringID.Companion.ID
import kotlin.test.*

class KookChannelMessageSendOptionsTests {
    @Test
    fun mergesAllOptionsAndIgnoresUnknownOptions() {
        val options = listOf(
            object : SendOption {},
            KookChannelMessageSendOption.quote("quote".ID),
            KookChannelMessageSendOption.nonce("nonce"),
            KookChannelMessageSendOption.tempTarget("temp".ID),
            KookChannelMessageSendOption.defaultTempTarget("default".ID),
            KookChannelMessageSendOption.template("template".ID),
        )
        val result = assertNotNull(options.toKookChannelMessageSendOptions())
        assertEquals("quote", result.quote)
        assertEquals("nonce", result.nonce)
        assertEquals("temp", result.tempTargetId)
        assertEquals("default", result.defaultTempTargetId)
        assertEquals("template", result.templateId)
        assertEquals(result, options.toTypedArray().toKookChannelMessageSendOptions())
    }

    @Test
    fun returnsNullWithoutUsableOptions() {
        assertNull(emptyList<SendOption>().toKookChannelMessageSendOptions())
        assertNull(emptyArray<SendOption>().toKookChannelMessageSendOptions())
        val unknown = object : SendOption {}
        assertNull(listOf(unknown).toKookChannelMessageSendOptions())
        assertNull(arrayOf(unknown).toKookChannelMessageSendOptions())
        // 空字符串仍是显式提供的值。
        val nonce = assertNotNull(listOf(KookChannelMessageSendOption.nonce("")).toKookChannelMessageSendOptions())
        assertEquals("", nonce.nonce)
    }

    @Test
    fun rejectsEveryDuplicateTypeEvenWithEqualValues() {
        val options = listOf(
            KookChannelMessageSendOption.quote("id".ID),
            KookChannelMessageSendOption.nonce(""),
            KookChannelMessageSendOption.tempTarget("id".ID),
            KookChannelMessageSendOption.defaultTempTarget("id".ID),
            KookChannelMessageSendOption.template("id".ID),
        )
        for (option in options) {
            assertFailsWith<IllegalArgumentException> {
                listOf(option, option).toKookChannelMessageSendOptions()
            }
            assertFailsWith<IllegalArgumentException> {
                arrayOf(option, option).toKookChannelMessageSendOptions()
            }
        }
        assertFailsWith<IllegalArgumentException> {
            listOf(
                KookChannelMessageSendOption.quote("first".ID),
                KookChannelMessageSendOption.quote("second".ID),
            ).toKookChannelMessageSendOptions()
        }
    }

    @Test
    fun optionsHaveValueSemantics() {
        val first = KookChannelMessageSendOptions("quote", "nonce", "temp", "default", "template")
        fun options() = listOf(
            KookChannelMessageSendOption.quote("quote".ID),
            KookChannelMessageSendOption.nonce("nonce"),
            KookChannelMessageSendOption.tempTarget("temp".ID),
            KookChannelMessageSendOption.defaultTempTarget("default".ID),
            KookChannelMessageSendOption.template("template".ID),
        )
        val second = assertNotNull(options().toKookChannelMessageSendOptions())
        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first.toString(), second.toString())
        options().zip(options()).forEach { (left, right) ->
            assertEquals(left, right)
            assertEquals(left.hashCode(), right.hashCode())
            assertEquals(left.toString(), right.toString())
        }
        assertNotEquals<SendOption>(
            KookChannelMessageSendOption.quote("id".ID),
            KookChannelMessageSendOption.tempTarget("id".ID),
        )
        assertNotEquals(
            KookChannelMessageSendOption.quote("first".ID),
            KookChannelMessageSendOption.quote("second".ID),
        )
    }
}
