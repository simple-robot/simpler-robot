/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import kotlinx.coroutines.reactor.*
import kotlinx.coroutines.rx2.*
import kotlinx.coroutines.rx3.*
import love.forte.simbot.*
import love.forte.simbot.event.*
import org.slf4j.LoggerFactory
import java.util.concurrent.*

/**
 * 核心默认的事件上下文处理器。
 */
internal class CoreEventProcessingContextResolver(
    private val coroutineScope: CoroutineScope
) : EventProcessingContextResolver<CoreEventProcessingContext> {
    private val resumedListenerManager = ResumedListenerManager()

    @ExperimentalSimbotApi
    override val globalContext = GlobalScopeContext()

    @ExperimentalSimbotApi
    override val continuousSessionContext = CoreContinuousSessionContext(coroutineScope, resumedListenerManager)

    /**
     * 每一次的事件处理都应存在的属性内容。
     */
    @ExperimentalSimbotApi
    private val constMaps = mutableMapOf<Attribute<*>, Any>(
        EventProcessingContext.Scope.Global to globalContext,
        EventProcessingContext.Scope.ContinuousSession to continuousSessionContext
    )

    internal class GlobalScopeContext : ScopeContext, MutableAttributeMap by AttributeMutableMap(ConcurrentHashMap())
    private class InstantScopeContext : ScopeContext, MutableAttributeMap by AttributeMutableMap(ConcurrentHashMap())

    /**
     * 根据一个事件和当前事件对应的监听函数数量得到一个事件上下文实例。
     */
    @OptIn(ExperimentalSimbotApi::class)
    override suspend fun resolveEventToContext(event: Event, listenerSize: Int): CoreEventProcessingContext {
        val context = CoreEventProcessingContext(
            event, AttributeMutableMap(ConcurrentHashMap(
                constMaps,
            ).apply { put(EventProcessingContext.Scope.Instant, InstantScopeContext()) })
        ) {
            ArrayList(
                listenerSize
            )
        }

        coroutineScope.launch {
            resumedListenerManager.process(context, this)
        }

        return context
    }


    /**
     * 将一次事件结果拼接到当前上下文结果集中。
     */
    override suspend fun appendResultIntoContext(
        context: CoreEventProcessingContext, result: EventResult
    ): ListenerInvokeType {
        if (result != EventResult.Invalid) {
            val newResult = tryCollect(result)
            context._results.add(newResult)
        }
        return if (result.isTruncated) ListenerInvokeType.TRUNCATED
        else ListenerInvokeType.CONTINUE
    }

    /**
     * 只要存在任意会话监听函数，则都需要进行监听事件推送。
     */
    override fun isProcessable(eventKey: Event.Key<*>): Boolean {
        return !resumedListenerManager.isEmpty()
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(CoreEventProcessingContextResolver::class.java)

        private val reactiveSupport: Boolean by lazy {
            kotlin.runCatching {
                Companion::class.java.classLoader.loadClass("org.reactivestreams.Publisher")
                true
            }.getOrElse { false }
        }

        private val reactiveKotlinSupport: Boolean by lazy {
            try {
                Companion::class.java.classLoader.loadClass("kotlinx.coroutines.reactive.ReactiveFlowKt")
                true
            } catch (cnf: ClassNotFoundException) {
                logger.warn(
                    "Uses a result content of type `org.reactivestreams.Publisher`, but does not found `kotlinx-coroutine-reactive`. Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-reactive` to your classpath, otherwise the reactive API will not work as a return value (the `content` of SimpleEventResult) for the simbot listener.",
                    cnf
                )
                false
            }
        }

        private val reactorSupport: Boolean by lazy {
            kotlin.runCatching {
                Companion::class.java.classLoader.loadClass("reactor.core.publisher.Flux")
                Companion::class.java.classLoader.loadClass("reactor.core.publisher.Mono")
                true
            }.getOrElse { false }
        }

        private val reactorKotlinSupport: Boolean by lazy {
            try {
                Companion::class.java.classLoader.loadClass("kotlinx.coroutines.reactor.MonoKt")
                true
            } catch (cnf: ClassNotFoundException) {
                logger.warn(
                    "The reactor API is used, but the `kotlinx-coroutine-reactor` is not found. Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-reactor` to your classpath, otherwise the reactor API will not work as a return value (the `content` of SimpleEventResult) for the simbot listener.",
                    cnf
                )
                false
            }
        }

        private val rx2Support: Boolean by lazy {
            kotlin.runCatching {
                Companion::class.java.classLoader.loadClass("io.reactivex.Completable")
                Companion::class.java.classLoader.loadClass("io.reactivex.SingleSource")
                Companion::class.java.classLoader.loadClass("io.reactivex.MaybeSource")
                Companion::class.java.classLoader.loadClass("io.reactivex.ObservableSource")
                Companion::class.java.classLoader.loadClass("io.reactivex.Flowable")
                true
            }.getOrElse { false }
        }

        private val rx2KotlinSupport: Boolean by lazy {
            try {
                Companion::class.java.classLoader.loadClass("kotlinx.coroutines.rx2.RxAwaitKt")
                Companion::class.java.classLoader.loadClass("kotlinx.coroutines.rx2.RxConvertKt")
                true
            } catch (cnf: ClassNotFoundException) {
                logger.warn(
                    "The RxJava 2.x API is used, but the `kotlinx-coroutine-rx2` is not found. Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-rx2` to your classpath, otherwise the RxJava 2.x API will not work as a return value (the `content` of SimpleEventResult) for the simbot listener.",
                    cnf
                )
                false
            }
        }

        private val rx3Support: Boolean by lazy {
            kotlin.runCatching {
                Companion::class.java.classLoader.loadClass("io.reactivex.rxjava3.core.Completable")
                Companion::class.java.classLoader.loadClass("io.reactivex.rxjava3.core.SingleSource")
                Companion::class.java.classLoader.loadClass("io.reactivex.rxjava3.core.MaybeSource")
                Companion::class.java.classLoader.loadClass("io.reactivex.rxjava3.core.ObservableSource")
                Companion::class.java.classLoader.loadClass("io.reactivex.rxjava3.core.Flowable")
                true
            }.getOrElse { false }
        }

        private val rx3KotlinSupport: Boolean by lazy {
            try {
                Companion::class.java.classLoader.loadClass("kotlinx.coroutines.rx3.RxAwaitKt")
                Companion::class.java.classLoader.loadClass("kotlinx.coroutines.rx3.RxConvertKt")
                true
            } catch (cnf: ClassNotFoundException) {
                logger.warn(
                    "The RxJava 3.x API is used, but the `kotlinx-coroutine-rx3` is not found. Please consider adding the `org.jetbrains.kotlinx:kotlinx-coroutine-rx3` to your classpath, otherwise the RxJava 3.x API will not work as a return value (the `content` of SimpleEventResult) for the simbot listener.",
                    cnf
                )
                false
            }
        }

        private suspend fun tryCollect(result: EventResult): EventResult {
            if (result !is SimpleEventResult) return result
            val content = result.content ?: return result

            if (content is kotlinx.coroutines.flow.Flow<*>) {
                return result.copy(newContent = content.toList())
            }

            if (reactorSupport) {
                when (content) {
                    is reactor.core.publisher.Flux<*> -> return if (reactiveKotlinSupport) result.copy(
                        newContent = content.asFlow().toList()
                    ) else result // else return itself

                    is reactor.core.publisher.Mono<*> -> return if (reactorKotlinSupport) result.copy(newContent = content.awaitSingleOrNull()) else result
                }
            }

            if (rx2Support) {
                when (content) {
                    is io.reactivex.CompletableSource -> {
                        content.await() // Just await
                        return result.copy(newContent = null)
                    }
                    is io.reactivex.SingleSource<*> -> return if (rx2KotlinSupport) result.copy(newContent = content.await()) else result
                    is io.reactivex.MaybeSource<*> -> return if (rx2KotlinSupport) result.copy(newContent = content.awaitSingleOrNull()) else result
                    is io.reactivex.ObservableSource<*> -> return if (reactiveKotlinSupport) result.copy(
                        newContent = content.asFlow().toList()
                    ) else result
                    is io.reactivex.Flowable<*> -> return if (reactiveKotlinSupport) result.copy(
                        newContent = content.asFlow().toList()
                    ) else result
                }
            }

            if (rx3Support) {
                when (content) {
                    is io.reactivex.rxjava3.core.Completable -> {
                        content.await()
                        return result.copy(newContent = null)
                    }
                    is io.reactivex.rxjava3.core.SingleSource<*> -> return if (rx3KotlinSupport) result.copy(newContent = content.await()) else result
                    is io.reactivex.rxjava3.core.MaybeSource<*> -> return if (rx3KotlinSupport) result.copy(newContent = content.awaitSingleOrNull()) else result
                    is io.reactivex.rxjava3.core.ObservableSource<*> -> return result.copy(
                        newContent = content.asFlow().toList()
                    )
                    is io.reactivex.rxjava3.core.Flowable<*> -> return result.copy(
                        newContent = content.asFlow().toList()
                    )
                }
            }

            if (reactiveSupport) {
                when (content) {
                    is org.reactivestreams.Publisher<*> -> return if (reactiveKotlinSupport) result.copy(
                        newContent = content.asFlow().toList()
                    ) else result
                }
            }


            return result
        }
    }

}

