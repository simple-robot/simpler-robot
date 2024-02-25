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

package love.forte.simbot.common

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.collectable.asFlux
import love.forte.simbot.common.collectable.transform
import love.forte.simbot.suspendrunner.reserve.flux
import reactor.test.StepVerifier
import java.util.stream.Collectors.*
import kotlin.test.Test
import kotlin.test.assertEquals
import love.forte.simbot.common.collectable.collect as collectJ


/**
 *
 * @author ForteScarlet
 */
class JvmCollectableTests {

    @Test
    fun collectableCollectTest() {
        val collectable = flowOf(1, 2, 3, 4, 5).asCollectable()

        val group = collectable.collectJ(
            filtering(
                { it > 3 },
                partitioningBy(
                    { it % 2 == 0 },
                    counting()
                ),
            )
        )

        assertEquals(2, group.size)
        assertEquals(1L, group[true])
        assertEquals(1L, group[false])
    }

    @Test
    fun collectableAsFluxTest() {
        val flux =
            flowOf(1, 2, 3).onEach { delay(2) }.asCollectable()
                .asFlux()

        StepVerifier.create(flux)
            .expectNext(1, 2, 3)
            .verifyComplete()
    }

    @Test
    fun collectableTransformAsFluxTest() {
        val flux =
            flowOf(1, 2, 3).onEach { delay(2) }.asCollectable()
                .transform(transformer = flux())

        StepVerifier.create(flux)
            .expectNext(1, 2, 3)
            .verifyComplete()
    }

}
