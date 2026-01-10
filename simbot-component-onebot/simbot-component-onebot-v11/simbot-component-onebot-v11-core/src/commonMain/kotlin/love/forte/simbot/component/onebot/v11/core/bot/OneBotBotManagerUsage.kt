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

package love.forte.simbot.component.onebot.v11.core.bot

import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.plugin.Plugin

/**
 * 在 [ApplicationBuilder] 中 **尝试** 安装使用 [OneBotBotManager]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useOneBotBotManager()
 *    // 或
 *    useOneBotBotManager { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(OneBotBotManager) { ... }
 * }
 * ```
 * @see OneBotBotManager
 *
 */
public fun ApplicationFactoryConfigurer<*, *, *>.useOneBot11BotManager(
    configurator: ConfigurerFunction<OneBotBotManagerConfiguration>? = null
) {
    if (configurator != null) {
        install(OneBotBotManager, configurator)
    } else {
        install(OneBotBotManager)
    }
}

/**
 * 从中过滤取出所有 [OneBotBotManager] 实例.
 */
public fun Iterable<Plugin>.filterIsOneBotBotManagers(): List<OneBotBotManager> =
    filterIsInstance<OneBotBotManager>()

/**
 * 从序列中过滤出 [OneBotBotManager] 实例.
 */
public fun Sequence<Plugin>.filterIsOneBotBotManagers(): Sequence<OneBotBotManager> =
    filterIsInstance<OneBotBotManager>()

/**
 * 从中过滤取出所有 [OneBotBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
public fun Iterable<Plugin>.firstOneBotBotManager(): OneBotBotManager =
    first { it is OneBotBotManager } as OneBotBotManager

/**
 * 从序列中过滤出 [OneBotBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
public fun Sequence<Plugin>.firstOneBotBotManager(): OneBotBotManager =
    first { it is OneBotBotManager } as OneBotBotManager

/**
 * 从中过滤取出所有 [OneBotBotManager] 实例。
 * 如果不存在则得到null。
 */
public fun Iterable<Plugin>.firstOneBotBotManagerOrNull(): OneBotBotManager? =
    firstOrNull { it is OneBotBotManager } as OneBotBotManager?

/**
 * 从序列中过滤出 [OneBotBotManager] 实例。
 * 如果不存在则得到null。
 */
public fun Sequence<Plugin>.firstOneBotBotManagerOrNull(): OneBotBotManager? =
    firstOrNull { it is OneBotBotManager } as OneBotBotManager?
