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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.asFlux
import love.forte.simbot.common.collectable.collectBy
import love.forte.simbot.common.collection.asIterator
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import reactor.core.publisher.Flux
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collector
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 推送事件并将结果转化为 [Flux].
 * 需要项目环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 */
public fun EventProcessor.pushAndAsFlux(event: Event): Flux<EventResult> =
    push(event).asFlux()

@Deprecated(
    "Use collectBy", ReplaceWith(
        "collectBy(scope, launchContext, collector)",
        "love.forte.simbot.common.collectable.collectBy"
    ),
    DeprecationLevel.HIDDEN
)
@JvmName("collectBy")
internal suspend fun <T, R> Flow<T>.collectBy0(
    scope: CoroutineScope,
    launchContext: CoroutineContext = EmptyCoroutineContext,
    collector: Collector<T, *, R>
): R = collectBy(scope, launchContext, collector)

@Deprecated(
    "Use collectBy",
    ReplaceWith("collectBy(collector)", "love.forte.simbot.common.collectable.collectBy"),
    DeprecationLevel.HIDDEN
)
@JvmName("collectBy")
internal suspend fun <T, R> Flow<T>.collectBy0(collector: Collector<T, *, R>): R =
    collectBy(collector)

//region async
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
    scope.future { push(event).collectBy(scope = this, collector = collector) }

/**
 * 推送事件并将结果收集为 [List] 后返回 [CompletableFuture].
 */
public fun EventProcessor.pushAndCollectToListAsync(
    event: Event,
    scope: CoroutineScope
): CompletableFuture<out List<EventResult>> = pushAndCollectToAsync(event, scope, ArrayList())
//endregion


//region block
/**
 * 推送事件并将结果转化为 [Stream] 后返回。
 */
public fun EventProcessor.pushAndAsStream(event: Event, scope: CoroutineScope): Stream<EventResult> {
    val iterator = push(event).asIterator(
        scope,
        hasNext = { runInNoScopeBlocking { hasNext() } },
        next = { runInNoScopeBlocking { next() } })

    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
}


/**
 * 推送事件并将结果收集为 [C] 后返回。
 */
public fun <C : MutableCollection<in EventResult>> EventProcessor.pushAndCollectToBlocking(
    event: Event,
    collection: C
): C = runInNoScopeBlocking { push(event).toCollection(collection) }

/**
 * 推送事件并将结果使用 [Collector] 收集为 [R] 后返回。
 */
public fun <R> EventProcessor.pushAndCollectToBlocking(event: Event, collector: Collector<EventResult, *, R>): R =
    runInNoScopeBlocking { push(event).collectBy(collector = collector) }

/**
 * 推送事件并将结果收集为 [List] 后返回。
 */
public fun EventProcessor.pushAndCollectToListBlocking(event: Event): List<EventResult> =
    pushAndCollectToBlocking(event, ArrayList())
//endregion
