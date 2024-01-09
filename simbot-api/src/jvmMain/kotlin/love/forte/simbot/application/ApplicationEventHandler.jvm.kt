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

package love.forte.simbot.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Blocking
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext


/**
 * 针对一些不同的 [ApplicationLaunchStage] 的事件阻塞处理器。
 *
 * @see SuspendApplicationEventHandler
 */
public fun interface JBlockingSuspendApplicationEventHandler<in C> {
    /**
     * invoker.
     */
    @Throws(Exception::class)
    @Blocking
    public fun invoke(configuration: C)

    public companion object {
        /**
         * Converts a [JBlockingSuspendApplicationEventHandler] to a [SuspendApplicationEventHandler].
         *
         * @param dispatcherContext The coroutine context to use for executing the handler's suspend functions. Defaults to [Dispatchers.IO].
         * @param handler The [JBlockingSuspendApplicationEventHandler] to convert.
         * @return The converted [SuspendApplicationEventHandler].
         */
        @JvmStatic
        @JvmOverloads
        public fun <C> toHandler(dispatcherContext: CoroutineContext = Dispatchers.IO, handler: JBlockingSuspendApplicationEventHandler<C>): SuspendApplicationEventHandler<C> = handler.toHandlerInternal(dispatcherContext)

                /**
         * 将 [JBlockingSuspendApplicationEventHandler] 转化为 [SuspendApplicationEventHandler]。
         *
         * @param dispatcherContext 执行阻塞API时切换到的上下文。默认会使用 [Dispatchers.IO].
         */
        private fun <C> JBlockingSuspendApplicationEventHandler<C>.toHandlerInternal(dispatcherContext: CoroutineContext = Dispatchers.IO): SuspendApplicationEventHandler<C> =
            JBlockingSuspendApplicationEventHandlerImpl(this, dispatcherContext)
    }
}

private class JBlockingSuspendApplicationEventHandlerImpl<C>(
    private val handler: JBlockingSuspendApplicationEventHandler<C>,
    private val handlerContext: CoroutineContext
) : SuspendApplicationEventHandler<C> {
    override suspend fun invoke(context: C) {
        withContext(handlerContext) {
            handler.invoke(context)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JBlockingSuspendApplicationEventHandlerImpl<*>) return false

        if (handler != other.handler) return false
        if (handlerContext != other.handlerContext) return false

        return true
    }

    override fun hashCode(): Int {
        var result = handler.hashCode()
        result = 31 * result + handlerContext.hashCode()
        return result
    }

    override fun toString(): String {
        return "JBlockingSuspendApplicationEventHandler(handlerContext=$handlerContext)"
    }
}

/**
 * 针对一些不同的 [ApplicationLaunchStage] 的事件异步处理器。
 *
 * @see SuspendApplicationEventHandler
 */
public fun interface JAsyncSuspendApplicationEventHandler<in C> {
    /**
     * invoker.
     */
    public fun invoke(configuration: C): CompletionStage<Void?>

    public companion object {
        /**
         * Converts a [JAsyncSuspendApplicationEventHandler] to a [SuspendApplicationEventHandler].
         *
         * @param handler The [JAsyncSuspendApplicationEventHandler] to convert.
         * @return The converted [SuspendApplicationEventHandler].
         */
        @JvmStatic
        public fun <C> toHandler(handler: JAsyncSuspendApplicationEventHandler<C>): SuspendApplicationEventHandler<C> = handler.toHandlerInternal()

        /**
         * 将 [JAsyncSuspendApplicationEventHandler] 转化为 [SuspendApplicationEventHandler]。
         */
        private fun <C> JAsyncSuspendApplicationEventHandler<C>.toHandlerInternal(): SuspendApplicationEventHandler<C> =
            JAsyncSuspendApplicationEventHandlerImpl(this)
    }
}

private class JAsyncSuspendApplicationEventHandlerImpl<C>(
    private val handler: JAsyncSuspendApplicationEventHandler<C>,
) : SuspendApplicationEventHandler<C> {
    override suspend fun invoke(context: C) {
        handler.invoke(context).await()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JAsyncSuspendApplicationEventHandlerImpl<*>) return false

        if (handler != other.handler) return false

        return true
    }

    override fun hashCode(): Int {
        return handler.hashCode()
    }

    override fun toString(): String {
        return "JAsyncSuspendApplicationEventHandler($handler)"
    }


}
