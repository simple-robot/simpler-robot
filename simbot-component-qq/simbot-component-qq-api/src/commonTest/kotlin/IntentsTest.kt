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

package test

import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class IntentsTest {

    @Test
    fun testIntentsFactory() {
        assertContains(
            Intents {
                audioAction()
            },
            EventIntents.AudioAction.intents,
        )

        assertEquals(0, Intents {}.value)

        Intents {
            audioAction()
            forumsEvent()
            groupMembers()
        }.also {
            assertContains(it, EventIntents.AudioAction.intents)
            assertContains(it, EventIntents.ForumsEvent.intents)
            assertContains(it, EventIntents.GroupMembers.intents)
        }
    }

    private fun assertContains(actual: Intents, expect: Intents) {
        assertTrue(expect in actual)
    }

}
