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

package love.forte.simbot.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Blocking
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext

/**
 * [Application] 的预处理启动器。
 * 当执行 [ApplicationLauncher.launch] 时会构建并启动 [Application]。
 *
 * @see ApplicationLauncher
 */
public fun interface JBlockingApplicationLauncher<out A : Application> {

    /**
     * 根据已经成型的配置，构建并启动一个 [Application][A]。
     * 启动时会触发 [ApplicationLaunchStage.Launch] 事件线，调用所有的启动事件并集此启动所有的组件或插件。
     */
    @Throws(Exception::class)
    @Blocking
    public fun launch(): A

    public companion object {
        /**
         * 将 [JBlockingApplicationLauncher] 转化为 [ApplicationLauncher]。
         *
         * @param dispatcherContext 执行阻塞API时切换到的上下文。默认会使用 [Dispatchers.IO].
         */
        @JvmStatic
        @JvmOverloads
        public fun <A : Application> JBlockingApplicationLauncher<A>.toEventListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO
        ): ApplicationLauncher<A> =
            JBlockingApplicationLauncherImpl(this, dispatcherContext)
    }
}

private class JBlockingApplicationLauncherImpl<out A : Application>(
    // TODO private:
    //  e: file:///G:/code/simbot/simbot-api/src/jvmMain/kotlin/love/forte/simbot/application/ApplicationFactory.jvm.kt:78:35 Cannot access 'val handler: JBlockingApplicationLauncher<Application>': it is private/*private to this*/ in 'love/forte/simbot/application/JBlockingApplicationLauncherImpl'
    val handler: JBlockingApplicationLauncher<A>,
    private val handlerContext: CoroutineContext
) : ApplicationLauncher<A> {
    override suspend fun launch(): A {
        return withContext(handlerContext) {
            handler.launch()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JBlockingApplicationLauncherImpl<*>) return false

        if (handlerContext != other.handlerContext) return false
        if (this.handler != other.handler) return false

        return true
    }

    override fun hashCode(): Int {
        var result = handler.hashCode()
        result = 31 * result + handlerContext.hashCode()
        return result
    }

    override fun toString(): String {
        return "JBlockingApplicationLauncher(handlerContext=$handlerContext)"
    }


}

/**
 * [Application] 的预处理启动器。
 * 当执行 [ApplicationLauncher.launch] 时会构建并启动 [Application]。
 *
 * @see ApplicationLauncher
 */
public fun interface JAsyncApplicationLauncher<out A : Application> {

    /**
     * 根据已经成型的配置，构建并启动一个 [Application][A]。
     * 启动时会触发 [ApplicationLaunchStage.Launch] 事件线，调用所有的启动事件并集此启动所有的组件或插件。
     */
    public fun launch(): CompletionStage<out A>

    public companion object {
        /**
         * 将 [JAsyncApplicationLauncher] 转化为 [ApplicationLauncher]。
         */
        @JvmStatic
        public fun <A : Application> JAsyncApplicationLauncher<A>.toEventListener(): ApplicationLauncher<A> =
            JAsyncApplicationLauncherImpl(this)
    }
}

private class JAsyncApplicationLauncherImpl<A : Application>(
    private val handler: JAsyncApplicationLauncher<A>,
) : ApplicationLauncher<A> {
    override suspend fun launch(): A {
        return handler.launch().await()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JAsyncApplicationLauncherImpl<*>) return false

        if (handler != other.handler) return false

        return true
    }

    override fun hashCode(): Int {
        return handler.hashCode()
    }

    override fun toString(): String {
        return "JAsyncApplicationLauncher($handler)"
    }
}
