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
import love.forte.simbot.plugin.Plugins
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 一个 simbot application.
 * [Application] 可以代表为一个或一组组件、插件在一起运行的单位。
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
     * 应当必然包含一个描述生命周期的任务 [Job]。
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
     * 通常来讲 [botManagers] 中的内容是 [plugins] 的子集。
     */
    public val botManagers: BotManagers

    /**
     * 申请关闭当前 [Application]。
     *
     * 在真正关闭 [coroutineContext] 中的 [Job] 之前，
     * 会通过 [ApplicationLaunchStage.Cancelled] 触发
     */
    public fun cancel(reason: Throwable?)

    /**
     * 申请关闭当前 [Application]。
     *
     * 在真正关闭 [coroutineContext] 中的 [Job] 之前，
     * 会通过 [ApplicationLaunchStage.Cancelled] 触发
     */
    public fun cancel() {
        cancel(null)
    }

    /**
     * 挂起 [Application] 直到调用 [cancel] 且其内部完成了关闭 Job 的操作后。
     */
    @ST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()
}

