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

package love.forte.simbot.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import love.forte.simbot.event.JAsyncEventListener.Companion.toListener
import love.forte.simbot.event.JBlockingEventListener.Companion.toListener
import love.forte.simbot.event.TypedJAsyncEventListener.Companion.toListener
import love.forte.simbot.event.TypedJBlockingEventListener.Companion.toListener
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.NonBlocking
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
    override suspend fun handle(context: EventListenerContext): EventResult =
        jaListener.handle(context).await()

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
    override suspend fun handle(context: EventListenerContext): EventResult {
        val event = context.context.event
        if (type.isInstance(event)) {
            return jaListener.handle(context, type.cast(event)).await()
        }

        return EventResult.invalid
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
 * 如果希望针对某个具体的事件类型进行处理，可参考 [TypedJBlockingEventListener]
 *
 * @see EventListener
 * @see toListener
 * @see TypedJBlockingEventListener
 *
 * @author ForteScarlet
 */
public fun interface JBlockingEventListener {
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
         * Converts a [JBlockingEventListener] to an EventListener.
         *
         * @param dispatcherContext The coroutine context to be used for dispatching events. Default value is [Dispatchers.IO].
         * @param listener The [JBlockingEventListener] to be converted.
         * @return The converted [EventListener].
         */
        @JvmStatic
        @JvmOverloads
        public fun toListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO,
            listener: JBlockingEventListener
        ): EventListener = listener.toEventListener(dispatcherContext)


        /**
         * 将 [JBlockingEventListener] 转化为 [EventListener]。
         */
        private fun JBlockingEventListener.toEventListener(dispatcherContext: CoroutineContext = Dispatchers.IO): EventListener =
            JBlockingEventListenerImpl(this, dispatcherContext)
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
public fun interface TypedJBlockingEventListener<E : Event> {
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
         * Converts a [TypedJBlockingEventListener] to an EventListener.
         *
         * @param dispatcherContext The coroutine context to be used for dispatching events. Default value is [Dispatchers.IO].
         * @param listener The [TypedJBlockingEventListener] to be converted.
         * @return The converted [EventListener].
         */
        @JvmStatic
        @JvmOverloads
        public fun <E : Event> toListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO,
            type: Class<E>,
            listener: TypedJBlockingEventListener<E>
        ): EventListener = listener.toEventListener(type, dispatcherContext)


        /**
         * 将 [TypedJBlockingEventListener] 转化为 [EventListener]。
         */
        private fun <E : Event> TypedJBlockingEventListener<E>.toEventListener(
            type: Class<E>,
            dispatcherContext: CoroutineContext = Dispatchers.IO
        ): EventListener =
            TypedJBlockingEventListenerImpl(type, this, dispatcherContext)
    }
}

private class JBlockingEventListenerImpl(
    private val jbListener: JBlockingEventListener,
    private val dispatcherContext: CoroutineContext
) : EventListener {
    override suspend fun handle(context: EventListenerContext): EventResult {
        return withContext(dispatcherContext) {
            jbListener.handle(context)
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
        return "JBlockingEventListener(jbListener=$jbListener, dispatcherContext=$dispatcherContext)"
    }
}

private class TypedJBlockingEventListenerImpl<E : Event>(
    private val type: Class<E>,
    private val jbListener: TypedJBlockingEventListener<E>,
    private val dispatcherContext: CoroutineContext
) : EventListener {
    override suspend fun handle(context: EventListenerContext): EventResult {
        val event = context.context.event
        if (type.isInstance(event)) {
            return withContext(dispatcherContext) {
                jbListener.handle(context, type.cast(event))
            }
        }

        return EventResult.invalid
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
        return "TypedJBlockingEventListener(type=$type, dispatcherContext=$dispatcherContext)"
    }

}
