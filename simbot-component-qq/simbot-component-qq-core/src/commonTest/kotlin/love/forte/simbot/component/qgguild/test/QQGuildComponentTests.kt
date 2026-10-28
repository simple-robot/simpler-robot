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

package love.forte.simbot.component.qgguild.test

import love.forte.simbot.component.qguild.QQGuildComponent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [QQGuildComponent] class.
 */
class QQGuildComponentTests {

    @Test
    fun testComponentId() {
        val component = QQGuildComponent()
        assertEquals(QQGuildComponent.ID_VALUE, component.id)
        assertEquals("simbot.qqguild", component.id)
    }

    @Test
    fun testComponentEquality() {
        val component1 = QQGuildComponent()
        val component2 = QQGuildComponent()

        assertTrue(component1 == component2)
        assertEquals(component1.hashCode(), component2.hashCode())
    }

    @Test
    fun testSerializersModule() {
        val component = QQGuildComponent()
        val module = component.serializersModule

        assertNotNull(module)
        // The module should be the same as the one in the companion object
        assertEquals(QQGuildComponent.messageSerializersModule, module)
    }

    @Test
    fun testToString() {
        val component = QQGuildComponent()
        assertEquals("QQGuildComponent(id=simbot.qqguild)", component.toString())
    }
}
