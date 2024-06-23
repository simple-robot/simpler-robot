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

@file:JvmName("EventListeners")
@file:JvmMultifileClass

package love.forte.simbot.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.event.EventResult.Companion.invalid
import love.forte.simbot.event.JAsyncEventListener.Companion.toListener
import love.forte.simbot.event.JBlockEventListener.Companion.toListener
import love.forte.simbot.event.JNonBlockEventListener.Companion.toListener
import love.forte.simbot.event.TypedJAsyncEventListener.Companion.toListener
import love.forte.simbot.event.TypedJBlockEventListener.Companion.toListener
import love.forte.simbot.event.TypedJNonBlockEventListener.Companion.toListener
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.NonBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext

/**
 * 一个事件 [Event] 的异步监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [toListener] 转化为 [EventListener] 类型。
 *
 * 如果希望针对一个具体的类型进行处理，可参考 [TypedJAsyncEventListener]。
 *
 * @see EventListener
 * @see toListener
 * @see TypedJAsyncEventListener
 *
 * @author ForteScarlet
 */
public fun interface JAsyncEventListener {
    /**
     * 通过 [context] 异步处理事件并得到异步响应。
     *
     */
    @Throws(Exception::class)
    @NonBlocking
    public fun handle(context: EventListenerContext): CompletionStage<out EventResult>

    /**
     * 快速构建一个可用于返回的、值为 [EventResult.empty] 的 [CompletionStage] 类型结果。
     *
     * 用于辅助 [handle]。
     */
    public fun emptyResult(): CompletionStage<out EventResult> = CompletableFuture.completedStage(EventResult.empty())

    /**
     * 快速构建一个可用于返回的、值为 [EventResult.invalid] 的 [CompletionStage] 类型结果。
     *
     * 用于辅助 [handle]。
     */
    public fun invalidResult(): CompletionStage<out EventResult> = CompletableFuture.completedStage(invalid())

    /**
     * 快速构建一个可用于返回的、值为 [result] 的 [CompletionStage] 类型结果。
     *
     * 用于辅助 [handle]。
     */
    public fun result(result: EventResult): CompletionStage<out EventResult> = CompletableFuture.completedStage(result)

    public companion object {
        /**
         * Converts the given [JAsyncEventListener] to a standard [EventListener].
         *
         * @param listener The [JAsyncEventListener] to convert.
         * @return The converted [EventListener].
         */
        @JvmStatic
        public fun toListener(listener: JAsyncEventListener): EventListener = listener.toEventListener()

        /**
         * 将 [JAsyncEventListener] 转化为 [EventListener]。
         */
        private fun JAsyncEventListener.toEventListener(): EventListener = JAsyncEventListenerImpl(this)
    }
}

/**
 * 一个事件 [Event] 的异步监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [toListener] 转化为 [EventListener] 类型。
 *
 * 会通过指定的类型处理事件。如果类型不匹配则会返回 [EventResult.invalid]。
 *
 * @see EventListener
 * @see toListener
 *
 * @author ForteScarlet
 */
public fun interface TypedJAsyncEventListener<E : Event> {
    /**
     * 通过 [context] 异步处理事件并得到异步响应。
     */
    @Throws(Exception::class)
    @NonBlocking
    public fun handle(context: EventListenerContext, event: E): CompletionStage<out EventResult>

    /**
     * 快速构建一个可用于返回的、值为 [EventResult.empty] 的 [CompletionStage] 类型结果。
     *
     * 用于辅助 [handle]。
     */
    public fun emptyResult(): CompletionStage<out EventResult> = CompletableFuture.completedStage(EventResult.empty())

    /**
     * 快速构建一个可用于返回的、值为 [EventResult.invalid] 的 [CompletionStage] 类型结果。
     *
     * 用于辅助 [handle]。
     */
    public fun invalidResult(): CompletionStage<out EventResult> = CompletableFuture.completedStage(invalid())

    /**
     * 快速构建一个可用于返回的、值为 [result] 的 [CompletionStage] 类型结果。
     *
     * 用于辅助 [handle]。
     */
    public fun result(result: EventResult): CompletionStage<out EventResult> = CompletableFuture.completedStage(result)

    public companion object {
        /**
         * Converts the given [TypedJAsyncEventListener] to a standard [EventListener].
         *
         * @param listener The [TypedJAsyncEventListener] to convert.
         * @return The converted EventListener.
         */
        @JvmStatic
        public fun <E : Event> toListener(type: Class<E>, listener: TypedJAsyncEventListener<E>): EventListener =
            listener.toEventListener(type)


        /**
         * 将 [JAsyncEventListener] 转化为 [EventListener]。
         */
        private fun <E : Event> TypedJAsyncEventListener<E>.toEventListener(type: Class<E>): EventListener =
            TypedJAsyncEventListenerImpl(type, this)
    }
}

private class JAsyncEventListenerImpl(private val jaListener: JAsyncEventListener) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult =
        jaListener.handle(this).await()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is JAsyncEventListenerImpl) return false

        return jaListener == other.jaListener
    }

    override fun hashCode(): Int = jaListener.hashCode()

    override fun toString(): String {
        return "JAsyncEventListener($jaListener)"
    }
}

private class TypedJAsyncEventListenerImpl<E : Event>(
    private val type: Class<E>,
    private val jaListener: TypedJAsyncEventListener<E>
) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult {
        val event = context.event
        if (type.isInstance(event)) {
            return jaListener.handle(this, type.cast(event)).await()
        }

        return invalid()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypedJAsyncEventListenerImpl<*>) return false

        if (type != other.type) return false
        if (jaListener != other.jaListener) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + jaListener.hashCode()
        return result
    }

    override fun toString(): String {
        return "TypedJAsyncEventListener(type=$type, jaListener=$jaListener)"
    }
}

/**
 * 一个事件 [Event] 的阻塞监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [toListener] 转化为 [EventListener] 类型。
 *
 * 如果希望针对某个具体的事件类型进行处理，可参考 [TypedJBlockEventListener]
 *
 * @see EventListener
 * @see toListener
 * @see TypedJBlockEventListener
 *
 * @author ForteScarlet
 */
public fun interface JBlockEventListener {
    /**
     * 通过 [context] 处理事件并得到响应结果。
     *
     * @throws Exception 任何可能抛出的异常
     */
    @Throws(Exception::class)
    @Blocking
    public fun handle(context: EventListenerContext): EventResult

    public companion object {
        /**
         * Converts a [JBlockEventListener] to an EventListener.
         *
         * @param dispatcherContext The coroutine context to be used for dispatching events. Default value is [Dispatchers.IO].
         * Will be used in [runInterruptible].
         * @param listener The [JBlockEventListener] to be converted.
         * @return The converted [EventListener].
         */
        @JvmStatic
        @JvmOverloads
        public fun toListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO,
            listener: JBlockEventListener
        ): EventListener = listener.toEventListener(dispatcherContext)


        /**
         * 将 [JBlockEventListener] 转化为 [EventListener]。
         */
        private fun JBlockEventListener.toEventListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO
        ): EventListener = JBlockingEventListenerImpl(this, dispatcherContext)
    }
}

/**
 * 一个事件 [Event] 的阻塞监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [toListener] 转化为 [EventListener] 类型。
 *
 * 会针对指定的类型进行事件处理。如果类型不匹配则会返回 [EventResult.invalid]。
 *
 * @see EventListener
 * @see toListener
 *
 * @author ForteScarlet
 */
public fun interface TypedJBlockEventListener<E : Event> {
    /**
     * 通过 [context] 处理事件并得到响应结果。
     *
     * @throws Exception 任何可能抛出的异常
     */
    @Throws(Exception::class)
    @Blocking
    public fun handle(context: EventListenerContext, event: E): EventResult

    public companion object {
        /**
         * Converts a [TypedJBlockEventListener] to an EventListener.
         *
         * @param dispatcherContext The coroutine context to be used for dispatching events. Default value is [Dispatchers.IO].
         * Will be used in [runInterruptible].
         * @param listener The [TypedJBlockEventListener] to be converted.
         * @return The converted [EventListener].
         */
        @JvmStatic
        @JvmOverloads
        public fun <E : Event> toListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO,
            type: Class<E>,
            listener: TypedJBlockEventListener<E>
        ): EventListener = listener.toEventListener(type, dispatcherContext)


        /**
         * 将 [TypedJBlockEventListener] 转化为 [EventListener]。
         */
        private fun <E : Event> TypedJBlockEventListener<E>.toEventListener(
            type: Class<E>,
            dispatcherContext: CoroutineContext = Dispatchers.IO
        ): EventListener =
            TypedJBlockingEventListenerImpl(type, this, dispatcherContext)
    }
}

private class JBlockingEventListenerImpl(
    private val jbListener: JBlockEventListener,
    private val dispatcherContext: CoroutineContext
) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult {
        return runInterruptible(dispatcherContext) {
            jbListener.handle(this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JBlockingEventListenerImpl) return false

        if (jbListener != other.jbListener) return false
        if (dispatcherContext != other.dispatcherContext) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jbListener.hashCode()
        result = 31 * result + dispatcherContext.hashCode()
        return result
    }

    override fun toString(): String {
        return "JBlockEventListener(jbListener=$jbListener, dispatcherContext=$dispatcherContext)"
    }
}

private class TypedJBlockingEventListenerImpl<E : Event>(
    private val type: Class<E>,
    private val jbListener: TypedJBlockEventListener<E>,
    private val dispatcherContext: CoroutineContext
) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult {
        val event = context.event
        if (type.isInstance(event)) {
            return runInterruptible(dispatcherContext) {
                jbListener.handle(this, type.cast(event))
            }
        }

        return invalid()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypedJBlockingEventListenerImpl<*>) return false

        if (type != other.type) return false
        if (jbListener != other.jbListener) return false
        if (dispatcherContext != other.dispatcherContext) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + jbListener.hashCode()
        result = 31 * result + dispatcherContext.hashCode()
        return result
    }

    override fun toString(): String {
        return "TypedJBlockEventListener(type=$type, dispatcherContext=$dispatcherContext)"
    }
}


/**
 * 创建一个基于 [CompletionStage] 的异步事件处理器。
 */
@Api4J
public fun async(function: JAsyncEventListener): EventListener =
    toListener(function)

/**
 * 创建一个基于 [CompletionStage] 的异步事件处理器，
 * 只处理 [type] 类型的事件。
 * 其他类型的事件会直接返回 [EventResult.invalid]。
 */
@Api4J
public fun <E : Event> async(type: Class<E>, function: TypedJAsyncEventListener<E>): EventListener =
    toListener(type, function)

/**
 * 创建一个阻塞事件处理器。
 *
 * @param dispatcherContext 阻塞逻辑的调度上下文。默认为 [Dispatchers.IO]。
 * 会在 [runInterruptible] 中使用。
 */
@Api4J
@JvmOverloads
public fun block(dispatcherContext: CoroutineContext = Dispatchers.IO, function: JBlockEventListener): EventListener =
    toListener(dispatcherContext, function)

/**
 * 创建一个阻塞事件处理器，
 * 只处理 [type] 类型的事件。
 * 其他类型的事件会直接返回 [EventResult.invalid]。
 *
 * @param dispatcherContext 阻塞逻辑的调度上下文。默认为 [Dispatchers.IO]。
 * 会在 [runInterruptible] 中使用。
 */
@Api4J
@JvmOverloads
public fun <E : Event> block(
    dispatcherContext: CoroutineContext = Dispatchers.IO,
    type: Class<E>,
    function: TypedJBlockEventListener<E>
): EventListener =
    toListener(dispatcherContext, type, function)


/**
 * 一个事件 [Event] 的非阻塞监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [toListener] 转化为 [EventListener] 类型。
 *
 * 如果希望针对某个具体的事件类型进行处理，可参考 [TypedJNonBlockEventListener]。
 *
 * 与 [JBlockEventListener] 不同，[JNonBlockEventListener]
 * 不会在执行 [handle] 的时候进行额外的处理（例如使用 [runInterruptible]、[Dispatchers.IO] 等），
 * 因此 [JNonBlockEventListener] 更适合用于返回那些**非阻塞**的结果。
 *
 * [handle] 默认被视为非阻塞的，并将响应式结果 (或其他可收集结果)
 * 放在 [StandardEventResult.CollectableReactivelyResult]
 * 类型的结果内。
 *
 * ```java
 * EventListeners.nonBlock(
 *     (context) -> {
 *         return EventResult.of(
 *             Mono.just("Hello.");
 *         );
 *     }
 * );
 * ```
 *
 * @see EventListener
 * @see toListener
 * @see TypedJNonBlockEventListener
 *
 * @since 4.1.0
 *
 * @author ForteScarlet
 */
public fun interface JNonBlockEventListener {
    /**
     * 通过 [context] 处理事件并得到响应结果。
     *
     * @throws Exception 任何可能抛出的异常
     */
    @Throws(Exception::class)
    @NonBlocking
    public fun handle(context: EventListenerContext): EventResult

    public companion object {
        /**
         * Converts a [JNonBlockEventListener] to an EventListener.
         *
         * @param listener The [JNonBlockEventListener] to be converted.
         * @return The converted [EventListener].
         */
        @JvmStatic
        public fun toListener(listener: JNonBlockEventListener): EventListener =
            listener.toEventListener()


        /**
         * 将 [JNonBlockEventListener] 转化为 [EventListener]。
         */
        private fun JNonBlockEventListener.toEventListener(): EventListener =
            JNonBlockEventListenerImpl(this)
    }
}

/**
 * 一个事件 [Event] 的非阻塞监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [toListener] 转化为 [EventListener] 类型。
 *
 * 会针对指定的类型进行事件处理。如果类型不匹配则会返回 [EventResult.invalid]。
 *
 * 与 [TypedJBlockEventListener] 不同，[TypedJNonBlockEventListener]
 * 不会在执行 [handle] 的时候进行额外的处理（例如使用 [runInterruptible]、[Dispatchers.IO] 等），
 * 因此 [TypedJNonBlockEventListener] 更适合用于返回那些**非阻塞**的结果。
 *
 * [handle] 默认被视为非阻塞的，并将响应式结果 (或其他可收集结果)
 * 放在 [StandardEventResult.CollectableReactivelyResult]
 * 类型的结果内。
 *
 * ```java
 * EventListeners.nonBlock(
 *     Event.class,
 *     (context, event) -> {
 *         return EventResult.of(
 *             Mono.just("Hello.");
 *         );
 *     }
 * );
 * ```
 *
 * @see EventListener
 * @see toListener
 *
 * @see StandardEventResult.CollectableReactivelyResult
 * @see StandardEventResult.Simple
 *
 * @since 4.1.0
 *
 * @author ForteScarlet
 */
public fun interface TypedJNonBlockEventListener<E : Event> {
    /**
     * 通过 [context] 处理事件并得到响应结果。
     *
     * @throws Exception 任何可能抛出的异常
     */
    @Throws(Exception::class)
    @NonBlocking
    public fun handle(context: EventListenerContext, event: E): EventResult

    public companion object {
        /**
         * Converts a [TypedJNonBlockEventListener] to an EventListener.
         *
         * @param listener The [TypedJNonBlockEventListener] to be converted.
         * @return The converted [EventListener].
         */
        @JvmStatic
        public fun <E : Event> toListener(
            type: Class<E>,
            listener: TypedJNonBlockEventListener<E>
        ): EventListener = listener.toEventListener(type)


        /**
         * 将 [TypedJNonBlockEventListener] 转化为 [EventListener]。
         */
        private fun <E : Event> TypedJNonBlockEventListener<E>.toEventListener(
            type: Class<E>,
        ): EventListener =
            TypedJNonBlockEventListenerImpl(type, this)
    }
}

private class JNonBlockEventListenerImpl(
    private val jnbListener: JNonBlockEventListener,
) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult {
        return jnbListener.handle(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JNonBlockEventListenerImpl) return false

        if (jnbListener != other.jnbListener) return false

        return true
    }

    override fun hashCode(): Int {
        return jnbListener.hashCode()
    }

    override fun toString(): String {
        return "JNonBlockEventListener(listener=$jnbListener)"
    }
}

private class TypedJNonBlockEventListenerImpl<E : Event>(
    private val type: Class<E>,
    private val listener: TypedJNonBlockEventListener<E>,
) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult {
        val event = context.event
        if (type.isInstance(event)) {
            return this@TypedJNonBlockEventListenerImpl.listener.handle(this, type.cast(event))
        }

        return invalid()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypedJNonBlockEventListenerImpl<*>) return false

        if (type != other.type) return false
        if (listener != other.listener) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + listener.hashCode()
        return result
    }

    override fun toString(): String {
        return "TypedJNonBlockEventListener(type=$type, listener=$listener)"
    }
}


/**
 * 创建一个非阻塞事件处理器。
 *
 * @since 4.1.0
 *
 * @see JNonBlockEventListener
 */
@Api4J
public fun nonBlock(function: JNonBlockEventListener): EventListener =
    toListener(function)

/**
 * 创建一个非阻塞事件处理器，
 * 只处理 [type] 类型的事件。
 * 其他类型的事件会直接返回 [EventResult.invalid]。
 *
 * @since 4.1.0
 *
 * @see TypedJNonBlockEventListener
 */
@Api4J
public fun <E : Event> nonBlock(
    type: Class<E>,
    function: TypedJNonBlockEventListener<E>
): EventListener =
    toListener(type, function)
