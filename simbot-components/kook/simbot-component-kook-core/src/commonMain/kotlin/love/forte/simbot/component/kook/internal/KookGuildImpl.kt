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

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.*
import love.forte.simbot.component.kook.blacklist.ExperimentalBlacklistApi
import love.forte.simbot.component.kook.blacklist.KookGuildBlacklistOperator
import love.forte.simbot.component.kook.blacklist.internal.KookGuildBlacklistOperatorImpl
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.role.KookGuildRole
import love.forte.simbot.component.kook.role.KookGuildRoleCreator
import love.forte.simbot.component.kook.role.internal.KookGuildRoleImpl
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.kook.api.role.CreateGuildRoleApi
import love.forte.simbot.kook.api.role.GetGuildRoleListApi
import love.forte.simbot.kook.objects.Guild
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class KookGuildImpl(
    private val bot: KookBotImpl,
    override val source: Guild,
) : KookGuild {
    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    internal var botMember: KookMemberImpl? = null

//    override val bot: KookGuildBotImpl
//        get() {
//            return internalBot ?: (baseBot.internalMember(source.id, baseBot.sourceBot.botUserInfo.id)
//                ?: throw kookGuildNotExistsException(source.id)).let {
//                KookGuildBotImpl(baseBot, it).also { b -> internalBot = b }
//            }
//        }

    override suspend fun botAsMember(): KookMember {
        val id = bot.sourceBot.botUserInfo.id
        return botMember
            ?: bot.internalMember(source.id, id)
            ?: throw NoSuchElementException("Bot as member(id=$id)")
    }

    override val channels: Collectable<KookChannel>
        get() = bot.internalChannels(source.id).asCollectable()

    override suspend fun channel(id: ID): KookChannel? =
        bot.internalChannel(id.literal)

    override val chatChannels: Collectable<KookChatChannel>
        get() = bot.internalChatChannels(source.id).asCollectable()

    override suspend fun chatChannel(id: ID): KookChatChannel? =
        bot.internalChatChannel(id.literal)

    override suspend fun voiceChannel(id: ID): KookVoiceChannel? =
        bot.internalVoiceChannel(id.literal)

    override val voiceChannels: Collectable<KookVoiceChannel>
        get() = bot.internalVoiceChannels(source.id).asCollectable()

    @ExperimentalSimbotAPI
    override val categories: Collectable<KookCategoryChannel>
        get() = bot.internalCategories(source.id).asCollectable()

    @ExperimentalSimbotAPI
    override fun getCategory(id: ID): KookCategoryChannel? =
        bot.internalCategory(id.literal)


    override val members: Collectable<KookMember>
        get() = bot.internalMembers(source.id).asCollectable()

    override suspend fun owner(): KookMember {
        return bot.internalMember(source.id, id.literal)
            ?: throw kookMemberNotExistsException(source.id)
    }

    override val ownerId: ID
        get() = source.userId.ID

    override suspend fun member(id: ID): KookMember? =
        bot.internalMember(source.id, id.literal)

    @ExperimentalSimbotAPI
    override val roles: Collectable<KookGuildRoleImpl>
        get() = getRoles(null, null)

    @ExperimentalSimbotAPI
    override fun getRoles(startPage: Int?, pageSize: Int?): Collectable<KookGuildRoleImpl> {
        require(pageSize == null || pageSize > 0) { "'pageSize' must be null or > 0, but $pageSize" }
        require(startPage == null || startPage >= 0) { "'startPage' must be null or >= 0, but $pageSize" }

        val pageSize1 = pageSize?.takeIf { it > 0 } ?: KookApi.DEFAULT_MAX_PAGE_SIZE
        val startPage1 = startPage ?: KookApi.DEFAULT_START_PAGE

        return flow {
            var page = startPage1
            do {
                val result = GetGuildRoleListApi
                    .create(source.id, page = page, pageSize = pageSize1)
                    .requestDataBy(bot)
                val items = result.items
                items.forEach {
                    emit(it)
                }
                page = result.meta.page + 1

            } while (items.isNotEmpty() && result.meta.page < result.meta.pageTotal)
        }.map { r ->
            KookGuildRoleImpl(bot, this, r)
        }.asCollectable()
    }

    @ExperimentalSimbotAPI
    override fun roleCreator(): KookGuildRoleCreator = KookGuildRoleCreatorImpl(bot, this)

    @ExperimentalBlacklistApi
    override val blacklist: KookGuildBlacklistOperator
        get() = KookGuildBlacklistOperatorImpl(bot, id)

    override fun toString(): String {
        return "KookGuild(id=${source.id}, name=${source.name})"
    }
}


@OptIn(ExperimentalSimbotAPI::class)
private class KookGuildRoleCreatorImpl(
    private val bot: KookBotImpl,
    private val guild: KookGuildImpl,
) : KookGuildRoleCreator {
    override var name: String? = null

    override suspend fun create(): KookGuildRole {
        val role = CreateGuildRoleApi.create(guild.source.id, name).requestDataBy(bot)
        return KookGuildRoleImpl(bot, guild, role)
    }
}
