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

@file:JvmName("BotManagerUtil")
@file:JvmMultifileClass

package love.forte.simbot.bot

import love.forte.simbot.common.collection.toImmutable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.streamable.Streamable
import love.forte.simbot.common.streamable.Streamable.Companion.asStreamable
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 用于表示一组 [BotManager]。
 *
 */
public interface BotManagers : Collection<BotManager> {
    /**
     * 以序列的形式获取当前 [BotManager] 中所有的 [Bot]。
     */
    public fun allBots(): Sequence<Bot> = asSequence().flatMap { it.all() }

    /**
     * 获取 [allBots] 的流转化器。
     *
     * @since 4.4.0
     */
    public fun allBotsToStreamable(): Streamable<Bot> = allBots().asStreamable()

    /**
     * 尝试获取第一个 [BotManager] 中的第一个 [Bot]。
     *
     * @throws NoSuchElementException 如果无法获取
     * @since 4.2.0
     */
    public fun firstBot(): Bot =
        (firstOrNull() ?: throw NoSuchElementException("BotManagers is empty"))
            .all().firstOrNull() ?: throw NoSuchElementException("Bot is empty")

    /**
     * 根据指定ID寻找第一个匹配的 [Bot]。
     *
     * @throws NoSuchElementException 找不到匹配的bot
     * @throws ConflictBotException 从某个BotManager中获取时存在多个相同id的bot
     * @since 4.2.0
     */
    public fun firstBot(id: ID): Bot =
        asSequence().mapNotNull { it.find(id) }
            .first()
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
private class CollectionBotManagers(private val collections: Collection<BotManager>) :
    BotManagers,
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
