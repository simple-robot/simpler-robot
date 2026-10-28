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

import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.intents
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ConfigurableBotConfigurationIntentsAppenderTest {
    @Test
    fun testIntentsDsl() {
        val configuration = ConfigurableBotConfiguration()

        configuration.intents {
            audioAction()
        }

        assertTrue { EventIntents.AudioAction.intents in configuration.intents }
        assertFalse { EventIntents.ForumsEvent.intents in configuration.intents }

        configuration.intents {
            forumsEvent()
        }

        assertTrue { EventIntents.ForumsEvent.intents in configuration.intents }
    }
}
