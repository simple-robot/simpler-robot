/*
 * Copyright (c) 2021-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.bot

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.bot.JobBasedBot
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.collection.computeValueIfAbsent
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.bot.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.channel.*
import love.forte.simbot.component.qguild.event.QGInternalInterceptionException
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupRelation
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGGuildRelation
import love.forte.simbot.component.qguild.internal.channel.*
import love.forte.simbot.component.qguild.internal.event.QGBotStartedEventImpl
import love.forte.simbot.component.qguild.internal.group.idGroup
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.internal.role.QGGuildRoleImpl
import love.forte.simbot.component.qguild.internal.role.QGMemberRoleImpl
import love.forte.simbot.component.qguild.internal.role.toGuildRole
import love.forte.simbot.component.qguild.internal.role.toMemberRole
import love.forte.simbot.component.qguild.message.QGMedia
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendDmsMessage
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.event.*
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.files.UploadGroupFilesApi
import love.forte.simbot.qguild.api.files.UploadUserFilesApi
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.role.GetGuildRoleListApi
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.api.user.createFlow
import love.forte.simbot.qguild.ifNotFoundThenNull
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.SimpleChannel
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.stdlib.DisposableHandle
import love.forte.simbot.qguild.stdlib.requestDataBy
import love.forte.simbot.resource.Resource
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.User as QGUser
import love.forte.simbot.qguild.stdlib.Bot as StdlibBot

/**
 *
 * @author ForteScarlet
 */
internal class QGBotImpl(
    override val source: StdlibBot,
    override val component: QQGuildComponent,
    internal val eventDispatcher: EventDispatcher,
    configuration: QGBotComponentConfiguration
) : QGBot, JobBasedBot() {
    internal val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.qguild.bot.${source.ticket.secret}")

    override val job: Job
        get() = source.coroutineContext[Job]!!

    override val coroutineContext: CoroutineContext
        get() = source.coroutineContext

    private val cacheConfig = configuration.cacheConfig
    private val cacheable = cacheConfig?.enable == true

    init {
        // check config with warning log
        if (configuration.cacheConfig?.dynamicCacheConfig?.enable == true) {
            logger.warn("DynamicCacheConfig is not supported yet, but dynamicCacheConfig.enable == `true`. This will have no real effect.")
        }
    }

    @Volatile
    private lateinit var botSelf: QGUser

    override val userId: ID
        get() {
            if (!::botSelf.isInitialized) {
                throw IllegalStateException("Information of bot has not been initialized. Please execute the `start()` method at least once first")
            }

            return botSelf.id.ID
        }

    override val name: String
        get() {
            if (!::botSelf.isInitialized) {
                throw IllegalStateException("Information of bot has not been initialized. Please execute the `start()` method at least once first")
            }

            return botSelf.username
        }

    override val avatar: String
        get() = if (!::botSelf.isInitialized) "" else botSelf.avatar


    override fun isMe(id: ID): Boolean {
        if (id == this.id) return true
        return ::botSelf.isInitialized && botSelf.id == id.literal
    }

    override val groupRelation: QGGroupRelation = GroupRelationImpl()

    private inner class GroupRelationImpl : QGGroupRelation {
        override suspend fun group(id: ID): QGGroup =
            idGroup(this@QGBotImpl, id)
    }

    override val guildRelation: QGGuildRelation = GuildRelationImpl()

    private inner class GuildRelationImpl : QGGuildRelation {
        @OptIn(ExperimentalCoroutinesApi::class)
        override fun guilds(lastId: ID?, batch: Int): Collectable<QGGuild> {
            require(batch > 0) { "'batch' must be > 0, but $batch" }
            return queryGuildList(lastId = lastId?.literal, batch).flatMapConcat { it.asFlow() }.map {
                qgGuild(this@QGBotImpl, it)
            }.asCollectable()
        }

        override suspend fun guild(id: ID): QGGuild? = queryGuild(id.literal)

        override suspend fun guildCount(): Int =
            GetBotGuildListApi.createFlow { requestDataBy(source) }.count()

        override suspend fun channel(channelId: ID): QGChannel? = queryChannel(channelId.literal, null)

        override suspend fun chatChannel(channelId: ID): QGTextChannel? {
            val channel = channel(channelId) ?: return null

            return channel.castChannel<QGTextChannel> { ChannelType.TEXT }
        }

        override suspend fun category(channelId: ID): QGCategoryChannel? {
            val channel = channel(channelId) ?: return null

            return channel.castChannel<QGCategoryChannel> { ChannelType.CATEGORY }
        }

        override suspend fun forumChannel(id: ID): QGForumChannel? {
            val channel = channel(id) ?: return null

            return channel.castChannel<QGForumChannel> { ChannelType.FORUM }
        }
    }

    internal suspend fun queryGuild(id: String): QGGuildImpl? {
        return try {
            GetGuildApi.create(id).requestDataBy(source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }?.let { guild -> qgGuild(this, guild) }
    }

    private fun queryGuildList(
        lastId: String? = null,
        batch: Int = GetBotGuildListApi.DEFAULT_LIMIT
    ): Flow<List<SimpleGuild>> = flow {
        var lastId0: String? = lastId
        while (true) {
            val list = GetBotGuildListApi.create(after = lastId0, limit = batch).requestDataBy(source)
            if (list.isEmpty()) break
            lastId0 = list.last().id
            emit(list)
        }
    }

    private suspend fun querySimpleChannel(id: String): SimpleChannel? {
        return try {
            GetChannelApi.create(id).requestDataBy(source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }
    }

    internal fun querySimpleChannelFlow(guildId: String): Flow<SimpleChannel> {
        return flow {
            GetGuildChannelListApi.create(guildId)
                .requestDataBy(source)
                .forEach {
                    emit(it)
                }
        }
    }


    internal suspend fun queryChannel(
        id: String,
        sourceGuild: QGGuildImpl? = null,
        currentMsgId: String? = null
    ): QGChannel? {
        val channelInfo = querySimpleChannel(id) ?: return null

        return when (channelInfo.type) {
            ChannelType.CATEGORY -> channelInfo.toCategoryChannel(
                bot = this,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            ChannelType.TEXT -> channelInfo.toTextChannel(
                bot = this,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
                currentMsgId = currentMsgId,
            )

            ChannelType.FORUM -> channelInfo.toForumChannel(
                bot = this,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            else -> channelInfo.toTextChannel(
                bot = this,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )
        }
    }

    /**
     * 通过API实时查询channels列表
     */
    internal fun queryChannels(guildId: String, sourceGuild: QGGuildImpl?): Flow<QGChannel> {
        val categoryMap = concurrentMutableMap<String, QGCategory>()
        return querySimpleChannelFlow(guildId)
            .map { channel ->
                val gid = channel.guildId.ID

                fun resolveCategory(): QGCategory =
                    categoryMap.computeValueIfAbsent(channel.parentId) { cid ->
                        QGCategoryImpl(
                            bot = this,
                            guildId = gid,
                            id = cid.ID,
                            sourceGuild = checkIfTransmitCacheable(sourceGuild),
                        )
                    }

                when (channel.type) {
                    ChannelType.TEXT -> channel.toTextChannel(
                        bot = this@QGBotImpl,
                        sourceGuild = checkIfTransmitCacheable(sourceGuild),
                        category = resolveCategory(),
                    )

                    ChannelType.FORUM -> channel.toForumChannel(
                        bot = this@QGBotImpl,
                        sourceGuild = checkIfTransmitCacheable(sourceGuild),
                        category = resolveCategory(),
                    )

                    ChannelType.CATEGORY -> channel.toCategoryChannel(
                        bot = this@QGBotImpl,
                        sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    )

                    else -> channel.toNonTextChannel(
                        bot = this@QGBotImpl,
                        sourceGuild = checkIfTransmitCacheable(sourceGuild),
                        category = resolveCategory()
                    )
                }
            }
    }

    override suspend fun sendTo(channelId: ID, text: String): QGMessageReceipt {
        return try {
            sendMessage(channelId.literal, text)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendTo" }
        }
    }

    override suspend fun sendTo(channelId: ID, message: Message): QGMessageReceipt {
        return try {
            sendMessage(channelId.literal, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendTo" }
        }
    }

    override suspend fun sendTo(channelId: ID, message: MessageContent): QGMessageReceipt {
        return try {
            sendMessage(channelId.literal, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendTo" }
        }
    }

    override suspend fun sendDmsTo(id: ID, text: String): QGMessageReceipt {
        return try {
            sendDmsMessage(id.literal, text)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendDmsTo" }
        }
    }

    override suspend fun sendDmsTo(id: ID, message: Message): QGMessageReceipt {
        return try {
            sendDmsMessage(id.literal, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendDmsTo" }
        }
    }

    override suspend fun sendDmsTo(id: ID, message: MessageContent): QGMessageReceipt {
        return try {
            sendDmsMessage(id.literal, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendDmsTo" }
        }
    }

    internal suspend fun queryMember(guildId: String, userId: String, sourceGuild: QGGuildImpl? = null): QGMemberImpl? {
        val member = try {
            GetMemberApi.create(guildId, userId).requestDataBy(source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        return member?.let { info ->
            QGMemberImpl(
                bot = this,
                source = info,
                guildId = guildId.ID,
                sourceGuild = checkIfTransmitCacheable(sourceGuild)
            )
        }
    }

    internal fun queryGuildRoles(guildId: String, sourceGuild: QGGuildImpl? = null): Flow<QGGuildRoleImpl> = flow {
        GetGuildRoleListApi.create(guildId).requestDataBy(source).roles.forEach {
            val role = it.toGuildRole(bot = this@QGBotImpl, guildId = guildId.ID, sourceGuild = sourceGuild)
            emit(role)
        }
    }

    internal fun queryMemberRoles(
        guildId: String,
        roleIds: Set<String>,
        memberId: ID,
        sourceGuild: QGGuildImpl? = null
    ): Flow<QGMemberRoleImpl> =
        queryGuildRoles(guildId, sourceGuild = sourceGuild)
            .filter { it.source.id in roleIds }
            .map { it.toMemberRole(bot = this@QGBotImpl, memberId = memberId) }

    private val startLock = Mutex()

    override suspend fun me(withCache: Boolean): QGUser {
        if (withCache && ::botSelf.isInitialized) {
            return botSelf
        }

        return source.me().also { botSelf = it }
    }


    @Volatile
    private var sourceListenerDisposableHandle: DisposableHandle? = null

    private suspend fun pushEventAndCollect(event: Event) {
        pushEvent(event).collect()
    }

    internal fun pushEventAndCollectAsync(event: Event): Job {
        return pushEvent(event).launchIn(this)
    }

    private fun pushEvent(event: Event): Flow<EventResult> {
        return eventDispatcher.push(event)
            .onEachError { e -> logger.error("Event {} process failed: {}", event, e, e.content) }
    }

    /**
     * 启动当前bot。
     */
    override suspend fun start() {
        startLock.withLock {
            sourceListenerDisposableHandle?.also { handle ->
                handle.dispose()
                sourceListenerDisposableHandle = null
            }

            sourceListenerDisposableHandle = registerEventProcessor()

            source.start().also {
                // set everytime.
                logger.debug("Initial @me for {}", source.ticket.appId)
                botSelf = me().also { me ->
                    logger.debug("bot own information: {}", me)
                }

                fun pushStartedEvent() {
                    launch {
                        val event = QGBotStartedEventImpl(this@QGBotImpl)
                        pushEventAndCollect(event)
                    }
                }

                if (!isStarted) {
                    pushStartedEvent()
                }
            }

            isStarted = true
        }
    }

    override suspend fun uploadGroupMedia(target: ID, url: String, type: Int): QGMedia {
        val media = UploadGroupFilesApi.create(
            openid = target.literal,
            fileType = type,
            url = url
        ).requestDataBy(source)

        return QGMedia(media)
    }

    override suspend fun uploadUserMedia(target: ID, url: String, type: Int): QGMedia {
        val media = UploadUserFilesApi.create(
            openid = target.literal,
            fileType = type,
            url = url
        ).requestDataBy(source)

        return QGMedia(media)
    }

    @ExperimentalQGMediaApi
    override suspend fun uploadGroupMedia(target: ID, resource: Resource, type: Int): QGMedia {
        val url = resource.httpUrlValue()
        if (url != null) {
            return uploadGroupMedia(target, url, type)
        }

        val media = UploadGroupFilesApi.create(
            openid = target.literal,
            fileType = type,
            fileData = kotlin.runCatching { resource.data() }.getOrElse { e ->
                throw IllegalStateException("Failed to read data from resource $resource", e)
            }
        ).requestDataBy(source)

        return QGMedia(media)
    }

    @ExperimentalQGMediaApi
    override suspend fun uploadUserMedia(target: ID, resource: Resource, type: Int): QGMedia {
        val url = resource.httpUrlValue()
        if (url != null) {
            return uploadUserMedia(target, url, type)
        }

        val media = UploadUserFilesApi.create(
            openid = target.literal,
            fileType = type,
            fileData = kotlin.runCatching { resource.data() }.getOrElse { e ->
                throw IllegalStateException("Failed to read data from resource $resource", e)
            }
        ).requestDataBy(source)

        return QGMedia(media)
    }

    private val isTransmitCacheable = cacheable && cacheConfig?.transmitCacheConfig?.enable == true

    internal fun <T> checkIfTransmitCacheable(target: T): T? = target.takeIf { isTransmitCacheable }

    internal suspend fun emitMessagePreSendEvent(event: InternalMessagePreSendEvent) {
        val errors = pushEvent(event)
            .filterIsInstance<StandardEventResult.Error>()
            .toList()

        if (errors.isNotEmpty()) {
            val ex = QGInternalInterceptionException("Internal message pre send event process failed")
            errors.forEach { ex.addSuppressed(it.content) }
            logger.error("Internal message pre send event process failed", ex)
            throw ex
        }
    }

    override fun toString(): String {
        // 还未初始化
        val uid = if (::botSelf.isInitialized) botSelf.id else "(Not initialized yet)"
        return "QGBotImpl(appId=$id, userId=$uid, isActive=$isActive)"
    }
}


/**
 * 从 [QGBot] 中分配一个拥有新的 [SupervisorJob] 的 [CoroutineContext].
 */
internal fun CoroutineScope.newSupervisorCoroutineContext(): CoroutineContext =
    coroutineContext + SupervisorJob(coroutineContext[Job])


internal inline fun <reified T : QGChannel> QGChannel.castChannel(target: () -> ChannelType): T {
    return this as? T
        ?: throw IllegalStateException("The type of channel(id=${source.id}, name=${source.name}) is not ${target()}, it is ${source.type}")
}

internal expect fun Resource.httpUrlValue(): String?
