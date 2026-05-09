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

package love.forte.simbot.component.kook.bot.internal

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.bot.InheritanceBotApi
import love.forte.simbot.bot.JobBasedBot
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.collection.computeValueIfAbsent
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.collection.removeValue
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookChannel
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookComponent
import love.forte.simbot.component.kook.KookVoiceChannel
import love.forte.simbot.component.kook.bot.*
import love.forte.simbot.component.kook.event.internal.KookBotStartedEventImpl
import love.forte.simbot.component.kook.internal.*
import love.forte.simbot.component.kook.util.requestData
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.onEachError
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.kook.api.channel.ChannelInfo
import love.forte.simbot.kook.api.channel.GetChannelListApi
import love.forte.simbot.kook.api.channel.toChannel
import love.forte.simbot.kook.api.guild.GetGuildListApi
import love.forte.simbot.kook.api.guild.GetGuildViewApi
import love.forte.simbot.kook.api.member.GetGuildMemberListApi
import love.forte.simbot.kook.api.member.createItemFlow
import love.forte.simbot.kook.api.userchat.*
import love.forte.simbot.kook.event.SelfJoinedGuildEventExtra
import love.forte.simbot.kook.objects.Guild
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.stdlib.requestDataBy
import love.forte.simbot.logger.LoggerFactory
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.kook.stdlib.Bot as KBot

/**
 *
 * @author ForteScarlet
 */
@OptIn(InheritanceBotApi::class, InternalForInheritanceKookBotApi::class)
internal class KookBotImpl(
    internal val eventProcessor: EventProcessor,
    override val sourceBot: KBot,
    override val component: KookComponent,
    private val configuration: KookBotConfiguration
) : KookBot, JobBasedBot() {
    override val coroutineContext: CoroutineContext = sourceBot.coroutineContext
    internal val subContext: CoroutineContext = coroutineContext.minusKey(Job)
    override val job = SupervisorJob(coroutineContext[Job])

    override val isCompleted: Boolean
        get() = job.isCompleted

    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.kook.bot.${sourceBot.ticket.clientId}")

    val botUserInfo get() = sourceBot.botUserInfo

    override fun isMe(id: ID): Boolean {
        if (id.literal == sourceBot.ticket.clientId) {
            return true
        }

        return try {
            id.literal == botUserInfo.id
        } catch (_: IllegalStateException) {
            // ignore match and return true
            true
        }
    }

    private val initialingGuildJobs = concurrentMutableMap<String, Deferred<KookGuildImpl>>()

    fun initialingGuildJob(id: String): Deferred<KookGuildImpl>? = initialingGuildJobs[id]

    fun initialNewGuild(extra: SelfJoinedGuildEventExtra): Deferred<KookGuildImpl> {
        val guildId = extra.body.guildId

        fun createAsyncJob(): Deferred<KookGuildImpl> {
            return async {
                val guildInfo = GetGuildViewApi.create(guildId).requestDataBy(sourceBot)

                // guild members sync
                val members = GetGuildMemberListApi.createItemFlow { page ->
                    create(guildId = guildId, page = page).requestDataBy(sourceBot)
                }.buffer(200)

                inCacheModify {
                    val guild = KookGuildImpl(this@KookBotImpl, guildInfo)
                    guilds[guildId] = guild
                    guildInfo.channels.forEach {
                        // KookChatChannelImpl(this@KookBotImpl, it)
                        channels[it.id] = it.toChatChannel(this@KookBotImpl)
                    }

                    members.collect {
                        val member = KookMemberImpl(this@KookBotImpl, it, guildId)
                        setMember(guildId, it.id, member)
                    }

                    guild
                }
            }
        }

        val asyncJob = initialingGuildJobs.computeValueIfAbsent(guildId) { createAsyncJob() }
        asyncJob.invokeOnCompletion { initialingGuildJobs.removeValue(guildId) { asyncJob } }
        return asyncJob
    }

    /**
     * 对缓存数据进行刷新时候使用的上下文，用于保证并发线程只有1.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val cacheModifyContext =
        cacheModifyContextDispatcher().limitedParallelism(1) + CoroutineName("KookBotCacheModify")

    private val internalCache = InternalCache()

    internal fun internalGuild(guildId: String): KookGuildImpl? = internalCache.guilds[guildId]
    internal fun internalChatChannel(channelId: String): KookChatChannel? = internalCache.channels[channelId]
    internal fun internalVoiceChannel(channelId: String): KookVoiceChannel? = internalChatChannel(
        channelId
    ) as? KookVoiceChannel?
    internal fun internalCategory(categoryId: String): KookCategoryChannelImpl? = internalCache.categories[categoryId]
    internal fun internalChatChannels(guildId: String): Sequence<KookChatChannel> =
        internalCache.channels.values.asSequence().filter { it.source.guildId == guildId }

    internal fun internalVoiceChannels(guildId: String): Sequence<KookVoiceChannel> =
        internalChatChannels(guildId).filterIsInstance<KookVoiceChannel>()

    internal fun internalMembers(guildId: String): Sequence<KookMemberImpl> {
        return internalCache.members.entries.asSequence().filter { it.key.guildId == guildId }.map { it.value }
    }

    internal fun internalCategories(guildId: String): Sequence<KookCategoryChannelImpl> =
        internalCache.categories.values.asSequence().filter { it.source.guildId == guildId }

    internal fun internalMember(guildId: String, userId: String) =
        internalCache.member(guildId, userId)

    internal fun internalChannel(channelId: String): KookChannel? =
        internalCategory(channelId) ?: internalChatChannel(channelId)

    internal fun internalChannels(guildId: String): Sequence<KookChannel> =
        internalCategories(guildId) + internalChatChannels(guildId)

    internal fun internalSetMuteJob(guildId: String, userId: String, value: MuteJob): MuteJob? {
        val key = internalCache.memberCacheId(guildId, userId)
        return internalCache.memberMutes.put(key, value)
    }

    internal fun internalRemoveMuteJob(guildId: String, userId: String, target: MuteJob? = null): MuteJob? {
        val key = internalCache.memberCacheId(guildId, userId)
        if (target != null) {
            val removed = internalCache.memberMutes.removeValue(key) { target }
//            val removed = internalCache.memberMutes.computeValueIfPresent(key) { k, ol ->
//                if (ol == target) null else ol
//            }
            return if (removed) target else null
        }

        return internalCache.memberMutes.remove(key)
    }


    /**
     * 在从 [cacheModifyContext] 中的单线程作用域中进行数据更新操作。
     *
     * 所有针对缓存的多步修改操作都应该在此处完成。
     */
    @OptIn(ExperimentalContracts::class)
    internal suspend inline fun <reified T> inCacheModify(crossinline block: suspend InternalCache.() -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        // TODO inCacheModify 并不能完全保证更新同步的安全.
        //  还需调整

        return withContext(cacheModifyContext) {
            internalCache.block()
        }
    }

    private val started = atomic(false)
    private val startLock = Mutex()
    private var syncJob: Job? = null

    override suspend fun start() {
        ensureAlive()
        startLock.withLock {
            val (guildSyncPeriod, batchDelay) = configuration.syncPeriods.guild
            val first = started.compareAndSet(expect = false, value = true)

            inCacheModify {
                // 从 inCacheModify 中注册事件，防止一开始的事件对缓存有操作
                if (first) {
                    registerEvent()
                }

                sourceBot.start()

                dataSync(batchDelay)
            }

            syncJob?.cancel()
            syncJob = guildDataSyncJob(guildSyncPeriod, batchDelay)

            // publish event
            publishStartEvent()
        }
    }


    override fun beforeJobComplete() {
        sourceBot.close()
    }

    private fun guildDataSyncJob(syncPeriod: Long, batchDelay: Long): Job {
        if (syncPeriod > 0) {
            return launch {
                while (isActive && this@KookBotImpl.isAlive) {
                    delay(syncPeriod)
                    dataSync(batchDelay)
                }
            }
        }

        return Job().apply { complete() }
    }

    private suspend fun dataSync(batchDelay: Long) {
        inCacheModify {
            syncGuilds(batchDelay, this)
        }
    }

    private suspend fun syncGuilds(batchDelay: Long, internalCache: InternalCache) {
        syncRequestGuilds(batchDelay)
            .buffer(100)
            .collect { guild ->
                val guildId = guild.id
                var botMember: KookMemberImpl? = null
                var chc = 0
                var cac = 0
                var mc = 0
                // channel caches
                coroutineScope {
                    // 单并发并发的异步
                    launch {
                        requestGuildChannels(guildId, batchDelay)
                            .buffer(500)
                            .collect { channelInfo ->
                                if (channelInfo.isCategory) {
                                    val categoryImpl = KookCategoryChannelImpl(
                                        this@KookBotImpl,
                                        channelInfo.toChannel(guildId = guildId),
                                    )
                                    internalCache.categories[channelInfo.id] = categoryImpl
                                    cac++
                                } else {
                                    val channelImpl =
                                        channelInfo.toChannel(guildId = guildId).toChatChannel(this@KookBotImpl)
//                                        KookChatChannelImpl(this@KookBotImpl, channelInfo.toChannel(guildId = guildId))
                                    internalCache.channels[channelInfo.id] = channelImpl
                                    chc++
                                }
                            }
                    }

                    // member caches
                    launch {
                        requestGuildMembers(guild.id, batchDelay)
                            .buffer(500)
                            .collect { user ->
                                val memberImpl = KookMemberImpl(this@KookBotImpl, user, guild.id)
                                internalCache.setMember(guild.id, user.id, memberImpl)
                                if (user.id == sourceBot.botUserInfo.id) {
                                    // bot self
                                    botMember = memberImpl
                                }
                                mc++
                            }
                    }
                }

                val guildImpl = KookGuildImpl(this, guild)
                val bm = botMember
                if (bm != null) {
                    guildImpl.botMember = bm
                }

                internalCache.guilds[guild.id] = guildImpl

                logger.debug("Sync guild {} with {} channels, {} categories and {} members", guild, chc, cac, mc)
            }

    }

    /**
     * 以 flow 的方式查询guild列表
     */
    private fun syncRequestGuilds(batchDelay: Long = 0): Flow<Guild> = flow {
        var page = KookApi.DEFAULT_START_PAGE
        do {
            if (page > KookApi.DEFAULT_START_PAGE) {
                delay(batchDelay)
            }
            logger.debug("Sync guild data ... page {}", page)
            val guildListResult = GetGuildListApi.create(page = page).requestDataBy(sourceBot)
            val guilds = guildListResult.items
            logger.debug("{} guild data synchronized in page {}", guilds.size, page)
            guilds.forEach {
                emit(it)
            }
            page = guildListResult.meta.page + 1
        } while (guilds.isNotEmpty() && guildListResult.meta.page < guildListResult.meta.pageTotal)
    }

    private fun requestGuildChannels(guildId: String, batchDelay: Long): Flow<ChannelInfo> = flow {
        var page = KookApi.DEFAULT_START_PAGE
        do {
            if (page > KookApi.DEFAULT_START_PAGE) {
                delay(batchDelay)
            }
            logger.debug("Sync channel data for guild {} ... page {}", guildId, page)
            val result = GetChannelListApi.create(guildId = guildId, page = page).requestDataBy(sourceBot)
            val channels = result.items
            logger.debug("{} channel data synced for guild {} in page {}", channels.size, guildId, page)
            channels.forEach {
                emit(it)
            }
            page = result.meta.page + 1
        } while (channels.isNotEmpty() && result.meta.page < result.meta.pageTotal)
    }

    private fun requestGuildMembers(guildId: String, batchDelay: Long): Flow<SimpleUser> = flow {
        var page = KookApi.DEFAULT_START_PAGE
        do {
            if (page > KookApi.DEFAULT_START_PAGE) {
                delay(batchDelay)
            }
            logger.debug("Sync member data for guild {} ... page {}", guildId, page)
            val usersResult = GetGuildMemberListApi.create(guildId = guildId, page = page).requestDataBy(sourceBot)
            val users = usersResult.items
            logger.debug("{} member data synced for guild {} in page {}", users.size, guildId, page)
            users.forEach {
                emit(it)
            }
            page = usersResult.meta.page + 1
            delay(batchDelay)
        } while (users.isNotEmpty() && usersResult.meta.page < usersResult.meta.pageTotal)

    }

    private fun publishStartEvent() {
        launch {
            val event = KookBotStartedEventImpl(this@KookBotImpl)
            eventProcessor.push(event)
                .onEachError { er ->
                    logger.error("Event {} process on failure: {}", event, er.content.message, er.content)
                }
                .collect()
        }
    }

    override val guildRelation: KookGuildRelationImpl = KookGuildRelationImpl()

    internal inner class KookGuildRelationImpl : KookGuildRelation {
        override val guilds: Collectable<KookGuildImpl>
            get() = internalCache.guilds.values.asCollectable()

        override suspend fun guild(id: ID): KookGuildImpl? = internalGuild(id.literal)

        override suspend fun guildCount(): Int = internalCache.guilds.size
    }

    override val contactRelation: KookContactRelationImpl = KookContactRelationImpl()

    internal inner class KookContactRelationImpl : KookContactRelation {
        override fun getContacts(size: Int?): Collectable<KookUserChatImpl> {
            return GetUserChatListApi
                .createItemFlow { page -> create(page = page, pageSize = size).requestDataBy(sourceBot) }
                .map {
                    KookUserChatImpl(this@KookBotImpl, it.toUserChatView())
                }
                .asCollectable()
        }

        override suspend fun contact(id: ID): KookUserChatImpl {
            val chat = try {
                requestData(CreateUserChatApi.create(id.literal))
            } catch (e: Exception) {
                val stack = IllegalStateException("Cannot create user chat for user(id=$id)")
                e.addSuppressed(stack)
                throw e
            }

            return KookUserChatImpl(this@KookBotImpl, chat)
        }

        override suspend fun contactCount(): Int {
            val list = try {
                requestData(GetUserChatListApi.create(pageSize = 1))
            } catch (e: Exception) {
                val stack = IllegalStateException("Cannot query user chat list: ${e.message}")
                e.addSuppressed(stack)
                throw e
            }

            return list.meta.total
        }
    }

    @OptIn(ApiResultType::class)
    private fun UserChatListView.toUserChatView(
        isFriend: Boolean = false,
        isBlocked: Boolean = false,
        isTargetBlocked: Boolean = false,
    ): UserChatView = UserChatView(
        code = code,
        lastReadTime = lastReadTime,
        latestMsgTime = latestMsgTime,
        unreadCount = unreadCount,
        isFriend = isFriend,
        isBlocked = isBlocked,
        isTargetBlocked = isTargetBlocked,
        targetInfo = targetInfo
    )


    override fun toString(): String {
        return "KookBot(clientId=${sourceBot.ticket.clientId}, isActive=$isActive)"
    }

    companion object
}


internal class InternalCache {
    data class MemberCacheId(val guildId: String, val userId: String)

    val guilds = concurrentMutableMap<String, KookGuildImpl>()
    val channels = concurrentMutableMap<String, KookChatChannel>()
    val categories = concurrentMutableMap<String, KookCategoryChannelImpl>()

    /**
     * member key: guildId & userId
     */
    val members = concurrentMutableMap<MemberCacheId, KookMemberImpl>()

    val memberMutes = concurrentMutableMap<MemberCacheId, MuteJob>()

    fun memberCacheId(guildId: String, userId: String): MemberCacheId = MemberCacheId(guildId, userId)

    fun member(guildId: String, userId: String): KookMemberImpl? = members[memberCacheId(guildId, userId)]

    fun setMember(guildId: String, userId: String, value: KookMemberImpl): KookMemberImpl? =
        members.put(memberCacheId(guildId, userId), value)

    fun removeMember(guildId: String, userId: String, removeAndCancelMuteJob: Boolean = true): KookMemberImpl? {
        val key = memberCacheId(guildId, userId)
        val removed = members.remove(key)
        if (removeAndCancelMuteJob) {
            memberMutes.remove(key)?.cancel()
        }
        return removed
    }
}


/**
 * @author ForteScarlet
 */
internal class MuteJob(val job: Job) {
    fun cancel() {
        job.cancel()
    }
}

/**
 * 在 native 和 JVM 中使用 `Dispatcher.IO`，JS 中使用 [Dispatchers.Default]。
 */
internal expect fun cacheModifyContextDispatcher(): CoroutineDispatcher
