/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

@file:JvmMultifileClass
@file:JvmName("Applications")

package love.forte.simbot.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.LifecycleAware
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotManagers
import love.forte.simbot.component.Components
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.event.EventListenerRegistrar
import love.forte.simbot.plugin.CloseablePlugin
import love.forte.simbot.plugin.Plugins
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlinx.coroutines.cancel as cancelJob

/**
 * 一个 simbot application.
 * [Application] 可以代表为一个或一组组件、插件在一起运行的单位。
 *
 * ## 生命周期
 * [Application] 的生命周期（[coroutineContext] 中的 [Job]）不会**直接**关联下述其他 Plugin，
 * 但是当任务被终止 [cancel] 时，[Application] 会尝试关闭其拥有的全部 [CloseablePlugin]。这个过程是 O(n) 的。
 *
 * @author ForteScarlet
 */
public interface Application : CoroutineScope, LifecycleAware, CompletionAware {
    /**
     * 构建 [Application] 提供并得到的最终配置信息。
     */
    public val configuration: ApplicationConfiguration

    /**
     * [Application] 作为一个协程作用域的上下文信息。
     * 必然包含一个描述生命周期的任务 [Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 当前 [Application] 持有的事件调度器。
     */
    public val eventDispatcher: EventDispatcher

    /**
     * 当前 [Application] 中注册的所有组件集。
     */
    public val components: Components

    /**
     * 当前 [Application] 中注册的所有插件集。
     */
    public val plugins: Plugins

    /**
     * 当前 [Application] 中注册地所有 [BotManager] 集。
     * [botManagers] 中的内容是 [plugins] 的子集。
     */
    public val botManagers: BotManagers

    /**
     * 终止当前 [Application]。
     *
     * Deprecated: 请直接使用 [close]。如果你希望在不传递 `reason` 的前提下终止所有任务和子任务，
     * 则直接使用 [Job.cancel][kotlinx.coroutines.cancel]。
     */
    @Deprecated(
        message = "请直接使用 `close`，如果你希望在不传递 `reason` 的前提下终止所有任务和子任务，" +
            "则直接使用 Job.cancel",
        replaceWith = ReplaceWith("close()")
    )
    public fun cancel(reason: Throwable?) {
        cancelJob(reason?.let { kotlinx.coroutines.CancellationException(it.message, it) })
    }

    /**
     * 终止当前 [Application]。
     *
     * 在真正关闭 [coroutineContext] 中的 [Job] 之前，
     * 会通过 [ApplicationLaunchStage.Cancelled] 触发。
     *
     * Deprecated: 请直接使用 [close]，如果你希望在不传递 `reason` 的前提下终止所有任务和子任务，
     * 则直接使用 [Job.cancel][kotlinx.coroutines.cancel]。
     */
    @Deprecated(
        message = "请直接使用 `close`，如果你希望在不传递 `reason` 的前提下终止所有任务和子任务，" +
            "则直接使用 Job.cancel",
        replaceWith = ReplaceWith("close()")
    )
    public fun cancel() {
        cancelJob()
    }

    /**
     * 是否处于活跃状态。
     *
     * 近似于 [kotlinx.coroutines.Job.isActive]。
     *
     * 属性值的变化规则参考 [close] 说明。
     */
    override val isActive: Boolean

    /**
     * 是否已经彻底完成。
     *
     * 近似于 [kotlinx.coroutines.Job.isCompleted]。
     */
    override val isCompleted: Boolean

    /**
     * 当前 [Application] 是否已经通过调用 [close] 而关闭了。
     * 这是一个原子属性，调用 [close] 后的瞬间被关闭，但这不代表当前 [Application] 已经 [彻底完成][isCompleted]。
     *
     * @since 5.0
     */
    public val isClosed: Boolean

    /**
     * 申请关闭当前 [Application]。
     *
     * 会依次：触发事件 [ApplicationLaunchStage.RequestCancel]、完成任务和所有子任务/子插件、
     * 触发事件 [ApplicationLaunchStage.Cancelled]。
     *
     * [close] 会将任务视为常规完成，不会强制所有子任务被迫终止，而是会等待它们**自然完成**。
     * 这行为近似于 [Job.complete][kotlinx.coroutines.CompletableJob.complete]。
     *
     * @since 5.0
     * @see kotlinx.coroutines.CompletableJob.complete
     */
    public fun close()

    /**
     * 挂起 [Application] 直到最终 [彻底完成][isCompleted]。
     */
    @ST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()
}

/**
 * 在 [block] 中操作 [EventListenerRegistrar] 来注册事件处理器。
 * 是通过 [Application] 注册事件处理器的DSL风格简化API。
 *
 * ```kotlin
 * application.listeners {
 *     listen<Event> {
 *        // ...
 *        return ...
 *     }
 *
 *     register {
 *         // ...
 *         return ...
 *     }
 * }
 * ```
 *
 *
 */
public inline fun Application.listeners(block: EventListenerRegistrar.() -> Unit) {
    eventDispatcher.block()
}

/**
 * 执行完 [block] 后挂起当前 [Application]。
 *
 * ```kotlin
 * app.joinWith { // this: Application
 *   // ...
 * }
 * ```
 *
 * @see Application.join
 */
public suspend inline fun <T : Application> T.joinWith(block: T.() -> Unit) {
    block()
    join()
}
