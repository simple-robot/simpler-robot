/*
 * Copyright (c) 2021-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. 
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import love.forte.simbot.application.Application
import love.forte.simbot.component.qguild.bot.QQGuildBotManager
import love.forte.simbot.plugin.Plugin


/**
 * 获取第一个 [QQGuildBotManager] 并使用
 * @throws [NoSuchElementException] if no such element is found.
 */
public inline fun Application.qgGuildBots(block: QQGuildBotManager.() -> Unit) {
    val manager = botManagers.first { it is QQGuildBotManager } as QQGuildBotManager
    manager.block()
}

/**
 * 尝试寻找并使用 [QQGuildBotManager]
 */
public inline fun Application.qgGuildBotsIfSupport(block: QQGuildBotManager.() -> Unit) {
    val manager = (botManagers.firstOrNull { it is QQGuildBotManager } ?: return) as QQGuildBotManager
    manager.block()
}

/**
 * 从中过滤取出所有 [QQGuildBotManager] 实例.
 */
public fun Iterable<Plugin>.filterIsQQGuildBotManagers(): List<QQGuildBotManager> =
    filterIsInstance<QQGuildBotManager>()

/**
 * 从序列中过滤出 [QQGuildBotManager] 实例.
 */
public fun Sequence<Plugin>.filterIsQQGuildBotManagers(): Sequence<QQGuildBotManager> =
    filterIsInstance<QQGuildBotManager>()

/**
 * 从中过滤取出所有 [QQGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
public fun Iterable<Plugin>.firstQQGuildBotManager(): QQGuildBotManager =
    first { it is QQGuildBotManager } as QQGuildBotManager

/**
 * 从序列中过滤出 [QQGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
public fun Sequence<Plugin>.firstQQGuildBotManager(): QQGuildBotManager =
    first { it is QQGuildBotManager } as QQGuildBotManager

/**
 * 从中过滤取出所有 [QQGuildBotManager] 实例。
 * 如果不存在则得到null。
 */
public fun Iterable<Plugin>.firstQQGuildBotManagerOrNull(): QQGuildBotManager? =
    firstOrNull { it is QQGuildBotManager } as QQGuildBotManager?

/**
 * 从序列中过滤出 [QQGuildBotManager] 实例。
 * 如果不存在则得到null。
 */
public fun Sequence<Plugin>.firstQQGuildBotManagerOrNull(): QQGuildBotManager? =
    firstOrNull { it is QQGuildBotManager } as QQGuildBotManager?
