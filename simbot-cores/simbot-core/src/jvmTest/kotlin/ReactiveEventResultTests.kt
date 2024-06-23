/*
 *     Copyright (c) 2024. ForteScarlet.
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

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.nonBlock
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertTrue

/*
 *     Copyright (c) 2024. ForteScarlet.
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

/**
 *
 * @author ForteScarlet
 */
class ReactiveEventResultTests {

    @Test
    @OptIn(ExperimentalSimbotAPI::class)
    fun reactiveResultCollectTest() = runTest {
        val app = launchSimpleApplication { }
        val event = object : Event {
            override val id: ID = UUID.random()
            override val time: Timestamp = Timestamp.now()
        }

        val monoCollected = AtomicBoolean(false)

        app.eventDispatcher.register(
            nonBlock {
                EventResult.of(
                    Mono.just(1).then(
                        Mono.fromSupplier {
                            monoCollected.set(true)
                            2
                        }
                    )
                )
            }
        )

        app.eventDispatcher.push(event).collect()

        assertTrue(monoCollected.get())
    }

}
