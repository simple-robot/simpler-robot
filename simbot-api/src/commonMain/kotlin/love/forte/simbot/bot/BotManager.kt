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

package love.forte.simbot.bot

import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.LifecycleAware
import love.forte.simbot.common.function.MergeableFactory
import love.forte.simbot.common.id.ID
import love.forte.simbot.plugin.PluginFactory
import love.forte.simbot.suspendrunner.ST

/**
 *
 * [Bot] 的管理器。
 *
 * ## 生命周期
 *
 * [BotManager] 持有一个会影响诞生自它的所有 [Bot] 的生命周期的 [Job][kotlinx.coroutines.Job]。
 * 当 [BotManager] 被执行了 [BotManager.cancel]，除了影响 [BotManager] 自身的生命周期以外，
 * 也会同样影响到所有由它产生的 [Bot]。
 *
 * [BotManager] 实现 [AutoConfigurableBotPlugin] 以允许其在 `quantcat` 相关模块中有更好的表现。
 *
 * @author ForteScarlet
 * @see BotPlugin
 */
public interface BotManager : AutoConfigurableBotPlugin, LifecycleAware, CompletionAware {

    /**
     * 得到所有的 [Bot]，以序列的形式。
     */
    public fun all(): Sequence<Bot>

    /**
     * 得到所有 `id` 符合条件的 [Bot]，以序列的形式。
     */
    public fun all(id: ID): Sequence<Bot> = all().filter { bot -> bot.id == id }

    /**
     * 根据一个指定的 [id] 获取匹配的bot。
     *
     * 如果当前管理的 [Bot] 中没有匹配的结果则会抛出 [NoSuchBotException]。
     *
     * [BotManager] 不保证所有的 [Bot] 的 id 是唯一的，如果当前
     * [BotManager] 允许存在多个 `id` 相同的 [Bot]，那么当获取的 [id]
     * 出现冲突时（例如存在两个或以上的 [Bot]）则会抛出 [ConflictBotException]。
     * 如果希望避免此问题，可考虑使用 [all] 自行筛选。
     *
     * @throws ConflictBotException 如果存在重复 id 的 [Bot]
     * @throws NoSuchBotException 如果不存在
     */
    public operator fun get(id: ID): Bot

    /**
     * 根据一个指定的 [id] 寻找匹配的bot。
     *
     * [BotManager] 不保证所有的 [Bot] 的 id 是唯一的，如果当前
     * [BotManager] 允许存在多个 `id` 相同的 [Bot]，那么当获取的 [id]
     * 出现冲突时（例如存在两个或以上的 [Bot]）则会抛出 [ConflictBotException]。
     *
     * @throws ConflictBotException 如果存在重复 id 的 [Bot]
     */
    public fun find(id: ID): Bot? = try {
        get(id)
    } catch (nb: NoSuchBotException) {
        null
    }

    /**
     * 挂起直到被 [cancel]。
     *
     * 即使一个 [BotManager] 没有管理任何 [Bot]，
     * 在 [cancel] 之前也会保持挂起状态。
     */
    @ST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()

    /**
     * 关闭当前 [BotManager]. 会同时关闭由其管理的所有 [Bot]。
     */
    public fun cancel(cause: Throwable? = null)
}


/**
 * [BotManager] 的工厂函数，用于配置并预构建 [BotManager] 实例。
 * 继承自 [PluginFactory].
 *
 * @see BotManager
 * @param P 目标类型
 * @param CONF 配置类型。配置类型应是一个可变类，以便于在 DSL 中进行动态配置。
 */
public interface BotManagerFactory<P : BotManager, CONF : Any> : PluginFactory<P, CONF> {
    /**
     * 用于 [BotManagerFactory] 在内部整合时的标识类型。
     *
     * 更多说明参阅 [PluginFactory.Key]。
     *
     * @see PluginFactory.key
     * @see MergeableFactory.key
     */
    public interface Key : PluginFactory.Key
}
