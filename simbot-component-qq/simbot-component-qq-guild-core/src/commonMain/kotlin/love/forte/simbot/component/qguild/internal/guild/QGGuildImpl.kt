/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.guild

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.channel.QGCategoryChannel
import love.forte.simbot.component.qguild.channel.QGChannel
import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGCategoryChannelImpl
import love.forte.simbot.component.qguild.internal.channel.toCategoryChannel
import love.forte.simbot.component.qguild.internal.channel.toForumChannel
import love.forte.simbot.component.qguild.internal.channel.toTextChannel
import love.forte.simbot.component.qguild.internal.role.QGRoleCreatorImpl
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.api.member.createFlow
import love.forte.simbot.qguild.isNotFound
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Guild
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGGuildImpl private constructor(
    internal val bot: QGBotImpl,
    override val source: Guild,
    override val coroutineContext: CoroutineContext
) : QGGuild {
    override suspend fun permissions(): ApiPermissions =
        GetApiPermissionListApi.create(source.id).requestDataBy(bot.source)

    override suspend fun botAsMember(): QGMember {
        val member = try {
            bot.queryMember(source.id, bot.userId.literal, this)
                ?: throw NoSuchElementException("Bot as member(id=${bot.userId})")
        } catch (apiEx: QQGuildApiException) {
            if (apiEx.isNotFound) throw NoSuchElementException("Bot as member(id=${bot.userId})") else throw apiEx
        }

        return member
    }

    override fun members(batch: Int): Collectable<QGMember> {
        require(batch > 0) { "'batch' must be > 0, but $batch" }
        return GetGuildMemberListApi.createFlow(guildId = source.id, batch = batch) { requestDataBy(bot.source) }
            .map { m ->
                QGMemberImpl(
                    bot = bot,
                    source = m,
                    guildId = this@QGGuildImpl.id,
                    sourceGuild = bot.checkIfTransmitCacheable(this@QGGuildImpl)
                )
            }.asCollectable()
    }

    override suspend fun member(id: ID): QGMemberImpl? =
        bot.queryMember(source.id, id.literal, this)

    @ExperimentalQGApi
    override val roles: Collectable<QGGuildRole>
        get() = bot.queryGuildRoles(guildId = source.id, this).asCollectable()


    @ExperimentalQGApi
    override fun roleCreator(): QGRoleCreator = QGRoleCreatorImpl(this)

    private fun channelFlow(): Flow<QGChannel> =
        bot.queryChannels(source.id, bot.checkIfTransmitCacheable(this))

    override val channels: Collectable<QGChannel>
        get() = channelFlow().asCollectable()

    override suspend fun channel(id: ID): QGChannel? =
        bot.queryChannel(id.literal, this)

    override val chatChannels: Collectable<QGTextChannel>
        get() = bot.querySimpleChannelFlow(source.id)
            .filter { it.type == ChannelType.TEXT }
            .map { it.toTextChannel(bot = bot, sourceGuild = this) }
            .asCollectable()

    override suspend fun chatChannel(id: ID): QGTextChannel? =
        bot.queryChannel(id.literal, this) as? QGTextChannel

    override val forums: Collectable<QGForumChannel>
        get() = bot.querySimpleChannelFlow(source.id)
            .filter { it.type == ChannelType.FORUM }
            .map { it.toForumChannel(bot = bot, sourceGuild = this) }
            .asCollectable()

    override suspend fun forum(id: ID): QGForumChannel? =
        bot.queryChannel(id.literal, this) as? QGForumChannel

    override val categories: Collectable<QGCategoryChannelImpl>
        get() = bot.querySimpleChannelFlow(source.id)
            .filter { it.type.isCategory }
            .map { it.toCategoryChannel(bot = bot, sourceGuild = this) }
            .asCollectable()

    override suspend fun category(id: ID): QGCategoryChannel? =
        bot.queryChannel(id.literal, this) as? QGCategoryChannel

    override fun toString(): String {
        return "QGGuild(id=$id, name=$name)"
    }

    companion object {
        internal fun qgGuild(bot: QGBotImpl, guild: Guild): QGGuildImpl {
            val job: CompletableJob = SupervisorJob(bot.coroutineContext[Job])
            val coroutineContext: CoroutineContext = bot.coroutineContext + job

            return QGGuildImpl(bot, guild, coroutineContext)
        }
    }
}
