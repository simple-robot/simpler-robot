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
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.common.coroutines.linkTo
import love.forte.simbot.component.Components
import love.forte.simbot.plugin.Plugins
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 一个 [Application] 所需的最基础的配置信息内容。
 *
 * 针对不同 [Application] 的实现可以自由扩展 [ApplicationConfiguration]，
 * 但是应当至少其要求的属性与能力，最少也应在标准属性不支持的情况下提供警告日志或异常。
 *
 */
public interface ApplicationConfiguration {
    /**
     * [Application] 中的协程上下文。
     *
     * [Application] 本身存在生命周期，如果 [coroutineContext] 中存在 [Job],
     * 则会被作为父任务被关联。
     *
     * [Application] 中的 [coroutineContext] 会在此配置中被传递给其他子配置（例如 [Plugins] 或 [Components]），
     * 而是否会使用此上下文则交由它们自行决定（[Application] 也无法干涉）。
     * 我们建议使用 [Application] 的 [coroutineContext] 作为各子配置的基础上下文，
     * 至少将生命周期与 [Application] 进行关联（使用父子任务或在存在多个任务的情况下使用 [Job.linkTo] 关联到 [Application] 的任务上），
     * 由此来保证 [Application] 生命周期的影响和有效性。
     *
     */
    public val coroutineContext: CoroutineContext

    /**
     * [ApplicationBuilder.serializersModule] 所配置的**后置**兜底的序列化模块信息。
     * 不是包含 [Components.serializersModule] 的聚合结果。
     *
     * @since 4.5.0
     */
    public val serializersModule: SerializersModule
}

/**
 * 用于构建 [Application] 的构建器，同时也提供针对 [ApplicationConfiguration] 基础属性的配置能力。
 *
 * @see Application
 * @see ApplicationConfiguration
 *
 */
public interface ApplicationBuilder {
    /**
     * [ApplicationConfiguration.coroutineContext] 配置属性，默认为 [EmptyCoroutineContext]。
     *
     * @see ApplicationConfiguration.coroutineContext
     */
    public var coroutineContext: CoroutineContext

    /**
     * 一个用于 [Components.serializersModule] 的基础序列化模块，
     * [Components] 中所有组件的 [SerializersModule] 聚合完成后，
     * 会再与此 [serializersModule] 进行合并。
     *
     * 可以将此 [serializersModule] 视为一个“兜底”的最终序列化模块，
     * 例如可以用它来覆盖部分默认序列化器的行为。
     *
     * @since 4.5.0
     */
    public var serializersModule: SerializersModule
}

/**
 * [ApplicationBuilder] 的基础抽象实现。
 *
 */
public abstract class AbstractApplicationBuilder : ApplicationBuilder {
    override var coroutineContext: CoroutineContext = EmptyCoroutineContext
    override var serializersModule: SerializersModule = EmptySerializersModule()
}
