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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.coroutines.test.runTest
import love.forte.simbot.component.findAndInstallAllComponents
import love.forte.simbot.component.onebot.v11.core.bot.firstOneBotBotManagerOrNull
import love.forte.simbot.component.onebot.v11.core.bot.useOneBot11BotManager
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.component.useOneBot11Component
import love.forte.simbot.component.onebot.v11.core.useOneBot11
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.plugin.findAndInstallAllPlugins
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class ApplicationUsageTests {

    @Test
    fun applicationUsageTest() = runTest {
        with(
            launchSimpleApplication {
                useOneBot11()
            }
        ) {
            assertTrue(components.any { it is OneBot11Component })
            assertNotNull(plugins.firstOneBotBotManagerOrNull())
            assertNotNull(botManagers.firstOneBotBotManagerOrNull())
        }
        with(
            launchSimpleApplication {
                useOneBot11Component()
                useOneBot11BotManager()
            }
        ) {
            assertTrue(components.any { it is OneBot11Component })
            assertNotNull(plugins.firstOneBotBotManagerOrNull())
            assertNotNull(botManagers.firstOneBotBotManagerOrNull())
        }

        with(
            launchSimpleApplication {
                findAndInstallAllComponents(false)
                findAndInstallAllPlugins(false)
            }
        ) {
            assertTrue(components.any { it is OneBot11Component })
            assertNotNull(plugins.firstOneBotBotManagerOrNull())
            assertNotNull(botManagers.firstOneBotBotManagerOrNull())
        }

        with(
            launchSimpleApplication {
                useOneBot11Component()
            }
        ) {
            assertTrue(components.any { it is OneBot11Component })
        }


    }


}
