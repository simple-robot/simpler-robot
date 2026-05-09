/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.internal

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.component.kook.KookCategory
import love.forte.simbot.component.kook.KookCategoryChannel
import love.forte.simbot.component.kook.KookChannelUpdater
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.kook.objects.Channel
import kotlin.coroutines.CoroutineContext

internal class KookCategoryImpl(
    val bot: KookBotImpl,
    override val source: Channel,
    private val sourceChannel: KookCategoryChannel? = null
) : KookCategory {
    override fun toChannel(): KookCategoryChannel =
        sourceChannel ?: KookCategoryChannelImpl(bot, source, this)

    override fun toString(): String {
        return "KookCategory(id=${source.id}, name=${source.name})"
    }
}

internal class KookCategoryChannelImpl(
    override val bot: KookBotImpl,
    override val source: Channel,
    private val sourceCategory: KookCategory? = null
) : KookCategoryChannel {
    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    override val category: KookCategory
        get() = sourceCategory ?: KookCategoryImpl(bot, source)

    override suspend fun delete(vararg options: DeleteOption) {
        bot.deleteChannel(source.id, options)
    }

    override fun updater(): KookChannelUpdater = KookChannelUpdaterImpl(this, bot)

    override fun toString(): String {
        return "KookCategoryChannel(id=${source.id}, name=${source.name})"
    }
}

internal fun Channel.toCategory(bot: KookBotImpl, sourceChannel: KookCategoryChannel? = null): KookCategoryImpl =
    KookCategoryImpl(bot, source = this, sourceChannel)

internal fun Channel.toCategoryChannel(
    bot: KookBotImpl,
    sourceCategory: KookCategory? = null
): KookCategoryChannelImpl =
    KookCategoryChannelImpl(bot, source = this, sourceCategory)
