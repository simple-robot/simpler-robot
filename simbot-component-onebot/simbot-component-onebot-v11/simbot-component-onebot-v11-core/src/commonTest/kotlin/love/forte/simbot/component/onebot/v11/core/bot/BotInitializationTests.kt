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

package love.forte.simbot.component.onebot.v11.core.bot

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.EventResult
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class BotInitializationTests {
    private fun bot() = OneBotBotImpl(
        uniqueId = "UNIQUE_ID",
        coroutineContext = EmptyCoroutineContext,
        job = SupervisorJob(),
        configuration = OneBotBotConfiguration(),
        component = OneBot11Component(),
        eventProcessor = object : EventProcessor {
            override fun push(event: Event): Flow<EventResult> {
                TODO("Not yet implemented")
            }
        },
        baseDecoderJson = OneBot11.DefaultJson
    )

    @Test
    fun testBotInit() = runTest {
        val bot = bot()

        assertFalse(bot.isConfigurationInitialized)
        assertFalse(bot.isConfigurationInitializing)

        assertTrue(bot.initConfiguration())
        assertTrue(bot.isConfigurationInitialized)
        assertFalse(bot.initConfiguration())

        assertTrue(bot.isConfigurationInitialized)
        bot.cancel()
    }

    @Test
    fun testBotStartInit() = runTest {
        val bot = bot()

        assertFalse(bot.isConfigurationInitialized)
        assertFalse(bot.isConfigurationInitializing)

        runCatching { bot.start() }

        assertTrue(bot.isConfigurationInitialized)
        assertFalse(bot.initConfiguration())

        assertTrue(bot.isConfigurationInitialized)

        bot.cancel()
    }



}
