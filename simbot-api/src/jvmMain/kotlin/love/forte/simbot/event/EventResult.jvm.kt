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

package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.rx2.awaitSingleOrNull
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.rx3.awaitSingleOrNull
import love.forte.simbot.logger.LoggerFactory
import java.util.concurrent.CompletionStage

/**
 * 收集 [StandardEventResult.CollectableReactivelyResult.content] 的结果并返回。
 * 如果结果不可收集或不支持收集，则得到原值。
 *
 * 可收集类型参考 [StandardEventResult.CollectableReactivelyResult.content] 说明。
 *
 * @see StandardEventResult.CollectableReactivelyResult.content
 * @return The collected result.
 */
public actual suspend fun StandardEventResult.CollectableReactivelyResult.collectCollectableReactively(): Any? {
    return when (val c = content) {
        null -> null
        is Deferred<*> -> c.await()
        is Flow<*> -> c.toList()
        is CompletionStage<*> -> c.await()
        else -> CollectableReactivelyResultAbilityHolder.tryCollect(c)
    }
}


private object CollectableReactivelyResultAbilityHolder {
    private val logger = LoggerFactory.getLogger(CollectableReactivelyResultAbilityHolder::class.java)


    private val reactiveSupport: Boolean by lazy {
        kotlin.runCatching {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("org.reactivestreams.Publisher")
            true
        }.getOrElse { false }
    }

    private val reactiveKotlinSupport: Boolean by lazy {
        try {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "kotlinx.coroutines.reactive.ReactiveFlowKt"
            )
            true
        } catch (cnf: ClassNotFoundException) {
            logger.warn(
                "Uses a result content of type `org.reactivestreams.Publisher`, " +
                    "but does not found `kotlinx-coroutine-reactive`. " +
                    "Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-reactive` " +
                    "to your classpath, " +
                    "otherwise the reactive API will not work as a return value " +
                    "(the `content` of SimpleEventResult) for the simbot listener.",
                cnf
            )
            false
        }
    }

    private val reactorSupport: Boolean by lazy {
        kotlin.runCatching {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("reactor.core.publisher.Flux")
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("reactor.core.publisher.Mono")
            true
        }.getOrElse { false }
    }

    private val reactorKotlinSupport: Boolean by lazy {
        try {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "kotlinx.coroutines.reactor.MonoKt"
            )
            true
        } catch (cnf: ClassNotFoundException) {
            logger.warn(
                "The reactor API (`reactor.core.publisher.Mono` or `reactor.core.publisher.Flux`) is used, " +
                    "but the `kotlinx-coroutine-reactor` is not found. " +
                    "Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-reactor` " +
                    "to your classpath, " +
                    "otherwise the reactor API will not work as a return value " +
                    "(the `content` of SimpleEventResult) for the simbot listener.",
                cnf
            )
            false
        }
    }

    private val rx2Support: Boolean by lazy {
        kotlin.runCatching {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("io.reactivex.Completable")
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("io.reactivex.SingleSource")
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("io.reactivex.MaybeSource")
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("io.reactivex.ObservableSource")
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass("io.reactivex.Flowable")
            true
        }.getOrElse { false }
    }

    private val rx2KotlinSupport: Boolean by lazy {
        try {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "kotlinx.coroutines.rx2.RxAwaitKt"
            )
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "kotlinx.coroutines.rx2.RxConvertKt"
            )
            true
        } catch (cnf: ClassNotFoundException) {
            logger.warn(
                "The RxJava 2.x API is used, but the `kotlinx-coroutine-rx2` is not found. " +
                    "Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-rx2` " +
                    "to your classpath, " +
                    "otherwise the RxJava 2.x API will not work as a return value " +
                    "(the `content` of SimpleEventResult) for the simbot listener.",
                cnf
            )
            false
        }
    }

    private val rx3Support: Boolean by lazy {
        kotlin.runCatching {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "io.reactivex.rxjava3.core.Completable"
            )
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "io.reactivex.rxjava3.core.SingleSource"
            )
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "io.reactivex.rxjava3.core.MaybeSource"
            )
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "io.reactivex.rxjava3.core.ObservableSource"
            )
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "io.reactivex.rxjava3.core.Flowable"
            )
            true
        }.getOrElse { false }
    }

    private val rx3KotlinSupport: Boolean by lazy {
        try {
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "kotlinx.coroutines.rx3.RxAwaitKt"
            )
            CollectableReactivelyResultAbilityHolder::class.java.classLoader.loadClass(
                "kotlinx.coroutines.rx3.RxConvertKt"
            )
            true
        } catch (cnf: ClassNotFoundException) {
            logger.warn(
                "The RxJava 3.x API is used, but the `kotlinx-coroutine-rx3` is not found. " +
                    "Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-rx3` " +
                    "to your classpath, " +
                    "otherwise the RxJava 3.x API will not work as a return value " +
                    "(the `content` of SimpleEventResult) for the simbot listener.",
                cnf
            )
            false
        }
    }

    @Suppress("ReturnCount")
    suspend fun tryCollect(content: Any): Any? {
        when {
            reactorSupport -> {
                when (content) {
                    is reactor.core.publisher.Flux<*> ->
                        return if (reactorKotlinSupport) content.collectList().awaitSingleOrNull() else content

                    is reactor.core.publisher.Mono<*> ->
                        return if (reactorKotlinSupport) content.awaitSingleOrNull() else content
                }
            }

            rx2Support -> {
                when (content) {
                    is io.reactivex.CompletableSource -> {
                        if (rx2KotlinSupport) {
                            content.await() // Just await
                            return null
                        }

                        return content
                    }

                    is io.reactivex.SingleSource<*> ->
                        return if (rx2KotlinSupport) content.await() else content

                    is io.reactivex.MaybeSource<*> ->
                        return if (rx2KotlinSupport) content.awaitSingleOrNull() else content

                    is io.reactivex.ObservableSource<*> ->
                        return if (reactiveKotlinSupport) {
                            content.asFlow()
                                .toList()
                        } else {
                            content
                        }

                    is io.reactivex.Flowable<*> -> return if (rx2KotlinSupport) content.toList().await() else content
                }
            }

            rx3Support -> {
                when (content) {
                    is io.reactivex.rxjava3.core.Completable -> {
                        if (rx3KotlinSupport) {
                            content.await() // Just await
                            return null
                        }
                    }

                    is io.reactivex.rxjava3.core.SingleSource<*> -> {
                        return if (rx3KotlinSupport) {
                            @Suppress("UNCHECKED_CAST")
                            (content as io.reactivex.rxjava3.core.SingleSource<Any>).await()
                        } else {
                            content
                        }
                    }

                    is io.reactivex.rxjava3.core.MaybeSource<*> -> {
                        return if (rx3KotlinSupport) {
                            @Suppress("UNCHECKED_CAST")
                            (content as io.reactivex.rxjava3.core.MaybeSource<Any>).awaitSingleOrNull()
                        } else {
                            content
                        }
                    }

                    is io.reactivex.rxjava3.core.ObservableSource<*> -> return if (rx3KotlinSupport) {
                        content.asFlow()
                            .toList()
                    } else {
                        content
                    }

                    is io.reactivex.rxjava3.core.Flowable<*> -> return if (rx3KotlinSupport) {
                        @Suppress("UNCHECKED_CAST")
                        (content.toList() as io.reactivex.rxjava3.core.Single<List<*>>).await()
                    } else {
                        content
                    }
                }
            }

            reactiveSupport -> {
                when (content) {
                    is org.reactivestreams.Publisher<*> -> return if (reactiveKotlinSupport) {
                        content.asFlow()
                            .toList()
                    } else {
                        content
                    }
                }
            }
        }

        return content
    }
}
