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

package love.forte.simbot.plugin

import love.forte.simbot.application.Application

/**
 *
 * 一个 **插件**。
 *
 * [Plugin] 应用于 [Application] 中，
 * 在所有组件 [Component][love.forte.simbot.component.Component]
 * 加载完成后进入配置阶段。
 *
 * 插件同样配置于事件处理器之后，因此 [Plugin] 最主要的职责之一便是与事件打交道——
 * 比如实现通过某种方式产生事件、并推送给事件处理器。
 *
 * [Plugin] 无所谓形式，可以是一个 [BotManager][love.forte.simbot.bot.BotPlugin],
 * 或是一个定时任务、一个http服务, 或者其他任何什么。
 *
 * [BotPlugin][love.forte.simbot.bot.BotPlugin] 是 [Plugin] 的一个特殊类型，详情可参考其说明。
 *
 * @author ForteScarlet
 */
public interface Plugin

/**
 * 表示一个可以被取消/终止的 [Plugin]。
 *
 * 取消的过程**不可逆**，当一个插件被取消后，不可再次回到活跃状态，且不应当再有任何状态改变或可用行为。
 * 对于会产生各种副作用的行为，都应当抛出 [PluginAlreadyCancelledException] 异常。
 *
 * @author ForteScarlet
 * @since 5.0
 */
public interface CancellablePlugin : Plugin {
    /**
     * 是否处于活跃状态，即尚未被 [取消][isCanceled] 的状态。
     */
    public val isActive: Boolean

    /**
     * 是否已经取消。
     *
     * 属性值的变化规则参考 [cancel] 说明。
     */
    public val isCanceled: Boolean

    /**
     * 取消此插件。
     *
     * 当插件被取消时，其应当立刻尝试等待并停止其所有工作、取消其内部的所有可取消的子工作，
     * 并且释放所有资源。过程中，应当立刻生效并拒绝所有新的行为申请。
     *
     * 当调用 [cancel] 后，[isActive] 的值立即变为 `false`，并进入 _取消流程_ 。
     * 当取消流程完全结束后，[isCanceled] 的值将会变为 `true`。而在此期间，为 _正在关闭_ 状态。
     *
     * cancel 应当是原子的，且可以多次调用。当调用一次后，后续的其他调用将无效。
     *
     * cancel 的实现应当尽可能地快速且避免阻塞。
     */
    public fun cancel(cause: Throwable?)
}

/**
 * [插件][CancellablePlugin] 已经被取消后继续尝试调用产生副作用的各类行为能力时产生的异常。
 *
 * @since 5.0
 * @see CancellablePlugin
 */
public class PluginAlreadyCancelledException(message: String?, cause: Throwable?) : RuntimeException(message, cause)
