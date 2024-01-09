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

package love.forte.simbot.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 可选择应用在配置文件中、用于配置附加的调度器的可序列化配置类型。
 */
@Serializable
public sealed class DispatcherConfiguration {
    /**
     * 得到配置内最终的调度器信实例。
     * 如果配置信息不足或其他各种原因也可能不会有调度器产生。
     */
    public abstract val dispatcher: CoroutineDispatcher?

    /**
     * 以 [Dispatchers] 中默认内容作为调度器结果的实现。
     */
    public sealed class KotlinCoroutineDispatchers : DispatcherConfiguration()

    /**
     * 使用 [Dispatchers.Default] 作为调度器。
     */
    @SerialName("default")
    @Serializable
    public data object Default : KotlinCoroutineDispatchers() {
        override val dispatcher: CoroutineDispatcher get() = Dispatchers.Default
    }

    /**
     * 使用 [Dispatchers.Unconfined] 作为调度器。
     */
    @SerialName("unconfined")
    @Serializable
    public data object Unconfined : KotlinCoroutineDispatchers() {
        override val dispatcher: CoroutineDispatcher get() = Dispatchers.Unconfined
    }

    /**
     * 使用 [Dispatchers.Main] 作为调度器。
     */
    @SerialName("main")
    @Serializable
    public data object Main : KotlinCoroutineDispatchers() {
        override val dispatcher: CoroutineDispatcher get() = Dispatchers.Main
    }

    /**
     * 使用 `Dispatchers.IO` 作为调度器。
     * 不支持的平台会降级为 [demote]，降级目标默认为 `null`。
     */
    @SerialName("io")
    @Serializable
    public data class IO(
        /**
         * 如果平台不支持 `IO` 调度器，则使用 [demote] 降级。
         * 默认为 `null`。
         */
        val demote: DispatcherConfiguration? = null
    ) : DispatcherConfiguration() {
        override val dispatcher: CoroutineDispatcher? get() = ioDispatcher() ?: demote?.dispatcher
    }

    /**
     * 专供 Java21+ 的 JVM平台使用的配置，会尝试使用 Java21 的虚拟线程线程池作为调度器。
     * 如果无法获取则通过 [demote] 降级。
     *
     * 例如当不支持虚拟线程调度器时降级为 [IO] 调度器，并在不支持 [IO] 调度器时降级为 [Default]。
     *
     * ```json
     * {
     *   "type": "j21_virtual",
     *   "demote": {
     *      "type": "io",
     *      "demote": {
     *         "type": "default"
     *      }
     *   }
     * }
     * ```
     *
     * @property demote 如果平台不是 java21+ 的 JVM 平台则使用 [demote] 降级。默认为 `null`。
     */
    @Serializable
    @SerialName("j21_virtual")
    public data class Virtual(val demote: DispatcherConfiguration? = null) : DispatcherConfiguration() {
        override val dispatcher: CoroutineDispatcher? get() = virtualDispatcher() ?: demote?.dispatcher
    }

    /**
     * 使用自定义线程属性作为构建调度器的配置。
     * 不支持自定义调度器的平台会降级为 [demote]，降级目标默认为 [Default]。
     *
     * 自定义调度器在支持的情况下始终为守护线程。
     */
    @Serializable
    @SerialName("custom")
    public data class Custom(
        /**
         * 核心线程数。应当至少为 `1`。
         * 必填属性，在支持的平台中，可能会作为固定线程的数量或动态线程的最小保持数量。
         * 当平台不支持 [maxThreads] 时，[coreThreads] 作为固定线程数量（例如 native 平台）。
         */
        val coreThreads: Int,
        /**
         * 最大线程数。当调度器达到可临时扩容状态时的线程数量上限。
         * 应当至少与 [coreThreads] 相等。默认为 `null`。在平台支持的情况下，
         * `null` 代表与 [coreThreads] 相等。
         * 平台不支持 [maxThreads] 的情况下会被忽略（例如 native 平台）。
         */
        val maxThreads: Int? = null,
        /**
         * 当 [maxThreads] 大于 [coreThreads] 且 [maxThreads] 在平台中被支持时（例如 JVM 平台），
         * [keepAliveMillis] 代表在 [coreThreads] 数量之外额外扩充出来的空闲线程的存活时间。
         */
        val keepAliveMillis: Long? = null,
        /**
         * 如果平台支持，则作为调度器的名称（native 平台）或线程组及线程的前缀名（JVM平台），
         * 如果为 `null` 则会使用一个内部生成或默认的名称。
         */
        val name: String? = null,

        /**
         * 如果此配置类的使用者支持，则可以以 `key` 为标识在一定范围内共享相同 `key`
         * 的配置所产生的调度器。
         * 为 `null` 时不会共享。
         *
         * Note: 共享行为是由此类的使用者自行决定的（例如 `quantcat` 模块下某实现），
         * 其在此类中没有用法。
         */
        val key: String? = null,
        /**
         * 如果平台不支持自定义调度器，则使用 [demote] 降级。
         * 默认为 `null`。
         */
        val demote: DispatcherConfiguration? = null
    ) : DispatcherConfiguration() {
        override val dispatcher: CoroutineDispatcher?
            get() = customDispatcher(coreThreads, maxThreads, keepAliveMillis, name) ?: demote?.dispatcher
    }
}

/**
 * 获取 `IO` 调度器。不支持的情况下返回 `null`。
 */
internal expect fun ioDispatcher(): CoroutineDispatcher?

/**
 * 获取自定义调度器。不支持或无法构建时返回 `null`。
 */
internal expect fun customDispatcher(
    coreThreads: Int?,
    maxThreads: Int?,
    keepAliveMillis: Long?,
    name: String?,
): CoroutineDispatcher?

/**
 * 当平台为 Java21+ 的 JVM平台时得到虚拟线程调度器，否则得到 `null`。
 */
internal expect fun virtualDispatcher(): CoroutineDispatcher?
