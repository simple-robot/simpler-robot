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

@file:JvmName("SuspendReserves")
@file:JvmMultifileClass

package love.forte.simbot.suspendrunner.reserve

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.mono
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext
import io.reactivex.Maybe as Rx2Maybe
import io.reactivex.rxjava3.core.Maybe as Rx3Maybe

/**
 * 得到一个阻塞直到结果完成的阻塞转化器。
 * 此转化器不会使用任何作用域或上下文，[SuspendReserve.Transformer.invoke]
 * 中的相关参数会被忽略。
 *
 * ```java
 * String name = foo.getNameReserve().transform(block());
 * ```
 *
 */
@Suppress("UNCHECKED_CAST")
public fun <T> block(): SuspendReserve.Transformer<T, T> =
    BlockingTransformer as SuspendReserve.Transformer<T, T>

private object BlockingTransformer : SuspendReserve.Transformer<Any?, Any?> {
    override fun <T1> invoke(
        scope: CoroutineScope,
        context: CoroutineContext,
        block: suspend () -> T1
    ): Any? = runInNoScopeBlocking { block() }
}

/**
 * 得到一个将结果转化为 [CompletableFuture] 的异步执行转化器。
 *
 * ```java
 * CompletableFuture<String> nameFuture =
 *     foo.getNameReserve().transform(async());
 * ```
 *
 */
@Suppress("UNCHECKED_CAST")
public fun <T> async(): SuspendReserve.Transformer<T, CompletableFuture<T>> =
    AsyncTransformer as SuspendReserve.Transformer<T, CompletableFuture<T>>

private object AsyncTransformer : SuspendReserve.Transformer<Any?, CompletableFuture<*>> {
    override fun <T1> invoke(
        scope: CoroutineScope,
        context: CoroutineContext,
        block: suspend () -> T1
    ): CompletableFuture<*> = scope.future(context) { block() }
}

/**
 * 得到一个将结果转化为 [Mono] 的响应式转化器。
 * 需要你的依赖环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 * ```java
 * Mono<String> nameMono =
 *     foo.getNameReserve().transform(mono());
 * ```
 *
 */
@Suppress("UNCHECKED_CAST")
public fun <T> mono(): SuspendReserve.Transformer<T, Mono<T>> =
    MonoTransformer as SuspendReserve.Transformer<T, Mono<T>>

private object MonoTransformer : SuspendReserve.Transformer<Any?, Mono<*>> {
    override fun <T1> invoke(scope: CoroutineScope, context: CoroutineContext, block: suspend () -> T1): Mono<T1> =
        mono(context.minusKey(Job)) { block() }
}

/**
 * 得到一个将结果转化为 [Rx2Maybe] 的响应式转化器。
 * 需要你的依赖环境中存在
 * [`kotlinx-coroutines-rx2`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 * ```java
 * Maybe<String> nameMaybe =
 *     foo.getNameReserve().transform(rx2Maybe());
 * ```
 *
 */
@Suppress("UNCHECKED_CAST")
public fun <T> rx2Maybe(): SuspendReserve.Transformer<T, Rx2Maybe<T>> =
    Rx2CompletableTransformer as SuspendReserve.Transformer<T, Rx2Maybe<T>>

private object Rx2CompletableTransformer : SuspendReserve.Transformer<Any?, Rx2Maybe<*>> {
    override fun <T1> invoke(scope: CoroutineScope, context: CoroutineContext, block: suspend () -> T1): Rx2Maybe<T1> =
        kotlinx.coroutines.rx2.rxMaybe(context.minusKey(Job)) { block() }
}

/**
 * 得到一个将结果转化为 [Rx3Maybe] 的响应式转化器。
 * 需要你的依赖环境中存在
 * [`kotlinx-coroutines-rx3`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 * ```java
 * Maybe<String> nameMaybe =
 *     foo.getNameReserve().transform(rx3Maybe());
 * ```
 *
 */
@Suppress("UNCHECKED_CAST")
public fun <T> rx3Maybe(): SuspendReserve.Transformer<T, Rx3Maybe<T & Any>> =
    Rx3CompletableTransformer as SuspendReserve.Transformer<T, Rx3Maybe<T & Any>>

private object Rx3CompletableTransformer : SuspendReserve.Transformer<Any?, Rx3Maybe<*>> {
    override fun <T1> invoke(
        scope: CoroutineScope,
        context: CoroutineContext,
        block: suspend () -> T1
    ): Rx3Maybe<T1 & Any> =
        kotlinx.coroutines.rx3.rxMaybe(context.minusKey(Job)) { block() }
}

