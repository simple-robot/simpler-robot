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
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.common.collectable.*
import love.forte.simbot.suspendrunner.reserve.flux
import reactor.test.StepVerifier
import java.time.Duration
import java.util.stream.Collectors.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import love.forte.simbot.common.collectable.collect as collectJ


/**
 * 验证 Collectable 在 JVM 上使用 Java Collector 和响应式订阅时的收集行为。
 *
 * @author ForteScarlet
 */
class JvmCollectableTests {

    private val reactiveTimeout = Duration.ofSeconds(5)

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
            flowOf(1, 2, 3).onEach { delay(2.milliseconds) }.asCollectable()
                .asFlux()

        StepVerifier.create(flux)
            .expectNext(1, 2, 3)
            .verifyComplete()
    }

    /**
     * 转为 Publisher 时暂不获取迭代器。两次订阅各获取一个新迭代器，
     * 分别发布 1 和 2，然后正常完成。
     */
    @OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
    @Test
    fun collectablePublisherUsesFreshIteratorPerSubscription() {
        var iterations = 0
        val iterable = Iterable { listOf(++iterations).iterator() }
        val publisher = iterable.asCollectable().asPublisher()

        assertEquals(0, iterations)
        for (expected in 1..2) {
            StepVerifier.create(publisher)
                .expectNext(expected)
                .expectComplete()
                .verify(reactiveTimeout)
        }
    }

    /**
     * 对 Publisher 转回的 Collectable 求和会发布一次结果 6。
     * 将空 Collectable 收集为列表会发布一次空列表。两种收集均正常完成。
     */
    @OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
    @Test
    fun collectableReactiveReductionsPublishOneResult() {
        val publisher = listOf(1, 2, 3).asCollectable().asPublisher()

        StepVerifier.create(publisher.asCollectable().collectReactive(summingInt { it }))
            .expectNext(6)
            .expectComplete()
            .verify(reactiveTimeout)
        StepVerifier.create(emptyCollectable<Int>().toListReactive())
            .expectNext(emptyList())
            .expectComplete()
            .verify(reactiveTimeout)
    }

    @Test
    fun collectableTransformAsFluxTest() {
        val flux =
            flowOf(1, 2, 3).onEach { delay(2.milliseconds) }.asCollectable()
                .transform(transformer = flux())

        StepVerifier.create(flux)
            .expectNext(1, 2, 3)
            .verifyComplete()
    }

}
