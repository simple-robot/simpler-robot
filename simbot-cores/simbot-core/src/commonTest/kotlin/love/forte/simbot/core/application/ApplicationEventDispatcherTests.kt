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

package love.forte.simbot.core.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.listeners
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.event.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 *
 * @author ForteScarlet
 */
class ApplicationEventDispatcherTests {

    @Test
    fun launchTest() = runTest {
        val app = launchSimpleApplication {
            config {
                coroutineContext = Dispatchers.Default
            }
        }

        app.listeners {
            // first
            register({
                priority = 0
            }) {
                assertIs<MyEvent>(event)
                EventResult.of(1)
            }
            // second
            listen<MyEvent>({
                priority = 1
            }) { e ->
                assertIs<MyEvent>(event)
                assertEquals(event, e)
                EventResult.of(2)
            }
            // third
            process<MyEvent>({
                priority = 2
            }) { e ->
                assertIs<MyEvent>(event)
                assertEquals(event, e)
            }
            // forth error
            process<MyEvent>({
                priority = 3
            }) { e ->
                assertIs<MyEvent>(event)
                assertEquals(event, e)
                throw TestErr()
            }
        }

        with(app.eventDispatcher.push(MyEvent()).toList()) {
            assertEquals(4, size)
            with(get(0)) {
                assertIs<StandardEventResult.Simple>(this)
                assertEquals(1, content)
            }
            with(get(1)) {
                assertIs<StandardEventResult.Simple>(this)
                assertEquals(2, content)
            }
            with(get(2)) {
                assertIs<StandardEventResult.Empty>(this)
                assertNull(content)
            }
            with(get(3)) {
                assertIs<StandardEventResult.Error>(this)
                assertIs<TestErr>(content)
            }

        }


    }

    private class MyEvent : Event {
        override val id: ID = UUID.random()

        @OptIn(ExperimentalSimbotAPI::class)
        override val time: Timestamp = Timestamp.now()
    }

    private class TestErr : RuntimeException()
}
