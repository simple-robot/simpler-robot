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

import kotlinx.coroutines.Job
import kotlin.jvm.JvmSynthetic

/**
 * 针对一些不同的 [ApplicationLaunchStage] 的事件处理器的统一类型。
 *
 * @see SuspendApplicationEventHandler
 * @see NormalApplicationEventHandler
 */
public sealed interface ApplicationEventHandler

/**
 * 针对一些不同的 [ApplicationLaunchStage] 的事件可挂起处理器。
 *
 * JVM 中提供了一些额外的扩展类型来支持使用 Java 的阻塞或异步风格 API 实现 [SuspendApplicationEventHandler]:
 * - [JBlockingSuspendApplicationEventHandler][love.forte.simbot.application.JBlockingSuspendApplicationEventHandler]
 * - [JAsyncSuspendApplicationEventHandler][love.forte.simbot.application.JAsyncSuspendApplicationEventHandler]
 *
 * @see love.forte.simbot.application.JBlockingSuspendApplicationEventHandler
 * @see love.forte.simbot.application.JAsyncSuspendApplicationEventHandler
 */
public fun interface SuspendApplicationEventHandler<in C> : ApplicationEventHandler {
    /**
     * invoker.
     */
    @JvmSynthetic
    public suspend fun invoke(context: C)
}

/**
 * 针对一些不同的 [ApplicationLaunchStage] 的普通事件处理器。
 */
public fun interface NormalApplicationEventHandler<in C> : ApplicationEventHandler {
    /**
     * invoker.
     */
    public fun invoke(context: C)
}

/**
 * Application 事件注册器。
 *
 * @author ForteScarlet
 */
public interface ApplicationEventRegistrar {

    /**
     * 添加一个 [Application] 启动事件。
     *
     * 当一个阶段已经过去后再添加对应的事件处理器将不会有任何效果。
     */
    public fun <H : ApplicationEventHandler> addEventHandler(stage: ApplicationLaunchStage<H>, handler: H)
}

/**
 * [ApplicationEventRegistrar] 的基础抽象实现。
 *
 * @author ForteScarlet
 */
public abstract class AbstractApplicationEventRegistrar : ApplicationEventRegistrar {
    protected open val events: MutableMap<ApplicationLaunchStage<*>, MutableList<ApplicationEventHandler>> =
        mutableMapOf()

    override fun <H : ApplicationEventHandler> addEventHandler(stage: ApplicationLaunchStage<H>, handler: H) {
        val list = events[stage] ?: mutableListOf<ApplicationEventHandler>().also { events[stage] = it }
        list.add(handler)
    }
}

/**
 * [Application] 在启动过程中的一些阶段类型。
 *
 * @author ForteScarlet
 */
public sealed class ApplicationLaunchStage<H : ApplicationEventHandler> {
    /**
     * 在 [ApplicationLauncher] 启动时。
     *
     * 会顺序的执行此事件。当此事件中的 [ApplicationEventHandler] 完全执行完成后，
     * [ApplicationLauncher.launch] 才会结束。
     */
    public data object Launch : ApplicationLaunchStage<SuspendApplicationEventHandler<Application>>()

    /**
     * 在 [ApplicationLauncher] 被要求关闭时。
     *
     * 此时的 [Application] 尚未关闭 [Job]，`Application.isActive` 仍将返回 `true`。
     *
     * [RequestCancel] 中的操作应当迅速且安全，避免抛出异常，也不应长时间阻塞。
     *
     * 在这过程中可以通过抛出异常或提前手动关闭 [Job]，但是这些行为都是**不推荐**的。
     * 这可能会导致无法预知的问题进而引发灾难。
     *
     */
    public data object RequestCancel : ApplicationLaunchStage<NormalApplicationEventHandler<Application>>()

    /**
     * 在 [ApplicationLauncher] 被关闭后。
     * 此时的 [Application] 已经被关闭，但是 [Application.cancel] 尚未返回时。
     * 此时的 `Application.isActive` 将返回 `false`，[Application.join] 也不会再被挂起。
     *
     * [Cancelled] 中的操作应当迅速且安全，避免抛出异常，也不应长时间阻塞。
     *
     * 但是这些行为都是**不推荐**的。
     * 这可能会导致无法预知的问题进而引发灾难。
     */
    public data object Cancelled : ApplicationLaunchStage<NormalApplicationEventHandler<Application>>()
}

/**
 * Add a handler for [ApplicationLaunchStage.Launch].
 *
 * @see ApplicationLaunchStage.Launch
 */
public fun ApplicationEventRegistrar.onLaunch(handler: SuspendApplicationEventHandler<Application>) {
    addEventHandler(ApplicationLaunchStage.Launch, handler)
}

/**
 * Add a handler for [ApplicationLaunchStage.RequestCancel].
 *
 * @see ApplicationLaunchStage.RequestCancel
 */
public fun ApplicationEventRegistrar.onRequestCancel(handler: NormalApplicationEventHandler<Application>) {
    addEventHandler(ApplicationLaunchStage.RequestCancel, handler)
}

/**
 * Add a handler for [ApplicationLaunchStage.Cancelled].
 *
 * @see ApplicationLaunchStage.Cancelled
 */
public fun ApplicationEventRegistrar.onCancelled(handler: NormalApplicationEventHandler<Application>) {
    addEventHandler(ApplicationLaunchStage.Cancelled, handler)
}


/**
 * 一组 [ApplicationLaunchStage] 和各对应的处理器集。
 */
public interface ApplicationLaunchStages {
    /**
     * 获取某个 [ApplicationLaunchStage] 对应的处理器集。
     */
    public operator fun get(stage: ApplicationLaunchStage<*>): Iterable<ApplicationEventHandler>?

}

/**
 * 通过一个 Map 构建得到 [ApplicationLaunchStages]
 *
 */
public fun applicationLaunchStages(map: Map<ApplicationLaunchStage<*>, Iterable<ApplicationEventHandler>>):
    ApplicationLaunchStages = MapApplicationLaunchStages(map)

private class MapApplicationLaunchStages(
    private val map: Map<ApplicationLaunchStage<*>, Iterable<ApplicationEventHandler>>
) : ApplicationLaunchStages {
    override fun get(stage: ApplicationLaunchStage<*>): Iterable<ApplicationEventHandler>? = map[stage]
}

/**
 * Find and invoke handlers form [stage]
 */
public inline fun <reified H : ApplicationEventHandler> ApplicationLaunchStages.invokeOnEach(
    stage: ApplicationLaunchStage<H>,
    block: H.() -> Unit
) {
    get(stage)?.onEach { handler ->
        (handler as? H)?.block()
    }
}
