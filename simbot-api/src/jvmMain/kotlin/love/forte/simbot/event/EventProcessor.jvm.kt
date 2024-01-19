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

@file:JvmMultifileClass
@file:JvmName("EventProcessors")

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.asFlux
import reactor.core.publisher.Flux
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collector.Characteristics.*

/**
 * 推送事件并将结果转化为 [Flux].
 * 需要项目环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 */
public fun EventProcessor.pushAndAsFlux(event: Event): Flux<EventResult> =
    push(event).asFlux()

@Suppress("UNCHECKED_CAST")
internal suspend fun <T, R> kotlinx.coroutines.flow.Flow<T>.collectBy(
    scope: CoroutineScope,
    collector: Collector<T, *, R>
): R {
    val container = collector.supplier().get()
    val accumulator = collector.accumulator() as java.util.function.BiConsumer<Any?, T>
    val characteristics = collector.characteristics()

    if (CONCURRENT in characteristics && UNORDERED in characteristics) {
        // collect in launch
        collect { result ->
            scope.launch { accumulator.accept(container, result) }
        }
    } else {
        collect { result ->
            accumulator.accept(container, result)
        }
    }

    return if (IDENTITY_FINISH in characteristics) {
        container as R
    } else {
        (collector.finisher() as Function<Any?, R>).apply(container)
    }
}

@Suppress("UNCHECKED_CAST")
internal suspend fun <T, R> kotlinx.coroutines.flow.Flow<T>.collectBy(collector: Collector<T, *, R>): R {
    val container = collector.supplier().get()
    val accumulator = collector.accumulator() as java.util.function.BiConsumer<Any?, T>
    val characteristics = collector.characteristics()

    collect { result ->
        accumulator.accept(container, result)
    }

    return if (IDENTITY_FINISH in characteristics) {
        container as R
    } else {
        (collector.finisher() as Function<Any?, R>).apply(container)
    }
}

/**
 * 推送事件并将结果收集为 [C] 后返回 [CompletableFuture].
 */
public fun <C : MutableCollection<in EventResult>> EventProcessor.pushAndCollectToAsync(
    event: Event,
    scope: CoroutineScope,
    collection: C
): CompletableFuture<C> =
    scope.future { push(event).toCollection(collection) }

/**
 * 推送事件并将结果使用 [Collector] 收集为 [R] 后返回 [CompletableFuture].
 */
public fun <R> EventProcessor.pushAndCollectToAsync(
    event: Event,
    scope: CoroutineScope,
    collector: Collector<EventResult, *, R>
): CompletableFuture<R> =
    scope.future { push(event).collectBy(this, collector) }

/**
 * 推送事件并将结果收集为 [List] 后返回 [CompletableFuture].
 */
public fun EventProcessor.pushAndCollectToListAsync(
    event: Event,
    scope: CoroutineScope
): CompletableFuture<out List<EventResult>> = pushAndCollectToAsync(event, scope, ArrayList())
