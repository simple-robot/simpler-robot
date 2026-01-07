/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook

import love.forte.simbot.application.Application
import love.forte.simbot.bot.BotManagers
import love.forte.simbot.component.kook.bot.KookBotManager
import love.forte.simbot.plugin.Plugin


/**
 * 在 [Application.botManagers] 作用域中寻找并使用 [KookBotManager].
 *
 * ```kotlin
 * val app = launchSimpleApplication {
 *     useKook()
 *     // ...
 * }
 *
 * app.kookBots {
 *   // ...
 * }
 * ```
 *
 * 如果当前环境中不存在任何 [KookBotManager], 则会抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果当前环境中不存在任何 [KookBotManager]
 *
 */
public inline fun Application.kookBots(
    block: KookBotManager.(BotManagers) -> Unit,
) {
    with(botManagers) {
        val manager = firstKookBotManagerOrNull()
            ?: throw NoSuchElementException("No event provider of type [KookBotManager] in botManagers: $botManagers")

        manager.block(this)
    }
}

/**
 * 在 [Application.botManagers] 作用域中寻找并使用 [KookBotManager].
 *
 * ```kotlin
 * val app = launchSimpleApplication {
 *    useKook()
 *    // ...
 * }
 *
 * app.kookBotsIfSupport {
 *    // ...
 * }
 * ```
 *
 * 如果当前环境中不存在任何 [KookBotManager], 则不会执行 [block]。
 *
 */
public inline fun Application.kookBotsIfSupport(
    block: KookBotManager.(BotManagers) -> Unit,
) {
    with(botManagers) {
        val manager = firstKookBotManagerOrNull() ?: return@with
        manager.block(this)
    }
}

// region bot manager 获取扩展
/**
 * 从一个 [Plugin] 序列中过滤寻找所有的 [KookBotManager]。
 */
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun Sequence<Plugin>.filterIsKookBotManagers(): Sequence<KookBotManager> =
    filterIsInstance<KookBotManager>()

/**
 * 从一个 [Plugin] 列表中过滤寻找所有的 [KookBotManager]。
 */
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun Iterable<Plugin>.filterIsKookBotManagers(): List<KookBotManager> =
    filterIsInstance<KookBotManager>()

/**
 * 从一个 [Plugin] 序列中寻找第一个的 [KookBotManager]。
 *
 * @throws NoSuchElementException 当找不到时
 */
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun Sequence<Plugin>.firstKookBotManager(): KookBotManager =
    first { it is KookBotManager } as KookBotManager

/**
 * 从一个 [Plugin] 列表中过滤寻找第一个的 [KookBotManager]。
 *
 * @throws NoSuchElementException 当找不到时
 */
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun Iterable<Plugin>.firstKookBotManager(): KookBotManager =
    first { it is KookBotManager } as KookBotManager

/**
 * 从一个 [Plugin] 序列中寻找第一个的 [KookBotManager]。
 * 当找不到时得到null。
 */
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun Sequence<Plugin>.firstKookBotManagerOrNull(): KookBotManager? =
    firstOrNull { it is KookBotManager } as KookBotManager?


/**
 * 从一个 [Plugin] 列表中过滤寻找第一个的 [KookBotManager]。
 * 当找不到时得到null。
 */
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun Iterable<Plugin>.firstKookBotManagerOrNull(): KookBotManager? =
    firstOrNull { it is KookBotManager } as KookBotManager?

/**
 * 从 [Application.botManagers] 中寻找所有的 [KookBotManager].
 *
 */
public inline val Application.kookBotManagers: List<KookBotManager> get() = botManagers.filterIsKookBotManagers()
// endregion

