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
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.LifecycleAware
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.common.collection.toImmutable
import love.forte.simbot.component.Component
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.Plugin
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

//region Components
/**
 * 用于表示一组 [Component] 。
 */
public interface Components : Collection<Component> {
    /**
     * 根据 [id] 寻找第一个匹配的 [Component]。
     */
    public fun findById(id: String): Component? = find { it.id == id }

    /**
     * 当前所有的组件内 [Component.serializersModule] 的聚合产物。
     */
    public val serializersModule: SerializersModule
}

/**
 * 根据类型寻找某个 [Component]。
 */
public inline fun <reified C : Component> Components.find(): C? = find { it is C } as C?

/**
 * 根据类型寻找某个 [Component]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified C : Component> Components.get(): C =
    find<C>() ?: throw NoSuchElementException(C::class.toString())


/**
 * 将一个 [Component] 的集合转化为 [Components]。
 */
public fun Collection<Component>.toComponents(): Components = CollectionComponents(toImmutable())

/**
 * @see Components
 */
private class CollectionComponents(private val collections: Collection<Component>) : Components,
    Collection<Component> by collections {
    override val serializersModule: SerializersModule = SerializersModule {
        collections.forEach { include(it.serializersModule) }
    }

    override fun toString(): String = "Components(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionComponents) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }


}
//endregion

//region Plugins
/**
 * 用于表示一组 [Plugin]。
 */
public interface Plugins : Collection<Plugin>

/**
 * 根据类型寻找某个 [Plugin]。
 */
public inline fun <reified P : Plugin> Plugins.find(): P? = find { it is P } as P?

/**
 * 根据类型寻找某个 [Plugin]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified P : Plugin> Plugins.get(): P = find<P>() ?: throw NoSuchElementException(P::class.toString())

/**
 * 将一个 [Plugin] 的集合转化为 [Plugins]。
 */
public fun Collection<Plugin>.toPlugins(): Plugins = CollectionPlugins(toImmutable())

/**
 * @see Plugins
 */
private class CollectionPlugins(private val collections: Collection<Plugin>) : Plugins,
    Collection<Plugin> by collections {
    override fun toString(): String = "Plugins(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionPlugins) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }
}
//endregion


//region BotManagers
/**
 * 用于表示一组 [BotManager]。
 *
 */
public interface BotManagers : Collection<BotManager> {
    /**
     * 以序列的形式获取当前 [BotManager] 中所有的 [Bot]。
     */
    public fun allBots(): Sequence<Bot> = asSequence().flatMap { it.all() }


}

/**
 * 根据类型寻找某个 [BotManager]。
 */
public inline fun <reified P : BotManager> BotManagers.find(): P? = find { it is P } as P?

/**
 * 根据类型寻找某个 [BotManager]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified B : BotManager> BotManagers.get(): B =
    find<B>() ?: throw NoSuchElementException(B::class.toString())

/**
 * 将一个 [BotManager] 的集合转化为 [BotManagers]。
 */
public fun Collection<BotManager>.toBotManagers(): BotManagers = CollectionBotManagers(toImmutable())

/**
 * @see BotManagers
 */
private class CollectionBotManagers(private val collections: Collection<BotManager>) : BotManagers,
    Collection<BotManager> by collections {
    override fun toString(): String = "BotManagers(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionBotManagers) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }
}
//endregion
