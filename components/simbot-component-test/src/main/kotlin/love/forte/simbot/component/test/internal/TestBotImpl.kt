/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.component.test.internal

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import love.forte.simbot.*
import love.forte.simbot.component.test.*
import love.forte.simbot.definition.*
import love.forte.simbot.event.*
import love.forte.simbot.resources.*
import org.slf4j.*
import java.util.concurrent.atomic.*
import java.util.stream.*
import kotlin.coroutines.*
import kotlin.coroutines.cancellation.*
import kotlin.streams.*


/**
 *
 * @author ForteScarlet
 */
internal class TestBotImpl(
    override val id: ID,
    override val component: TestComponent,
    override val username: String,
    override val manager: TestBotManager,
    override val eventProcessor: EventProcessor,
    parentJob: Job,
    private val configuration: TestBotManagerConfiguration,

    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    override val avatar: String,
    override val logger: Logger,
    override val status: UserStatus,


    ) : TestBot {
    override fun isMe(id: ID): Boolean = this.id == id

    private val currentJob = SupervisorJob(parentJob)
    override val coroutineContext: CoroutineContext = coroutineContext + currentJob

    // generators
    private val friendGenerator = configuration.generators.friendGenerator(configuration, this)

    override fun toString(): String = "Bot(id=$id, username=$username)"

    //region active status

    private val started = AtomicBoolean(false)
    override val isStarted: Boolean get() = started.get()
    override val isActive: Boolean get() = started.get()
    override val isCancelled: Boolean get() = currentJob.isCancelled

    private fun doStart(): Boolean {
        return started.compareAndSet(false, true)
    }

    override suspend fun start(): Boolean = doStart()

    @Api4J
    override fun startAsync() {
        doStart()
    }

    @Api4J
    override fun startBlocking(): Boolean = doStart()


    override suspend fun cancel(reason: Throwable?): Boolean {
        if (currentJob.isCancelled) return false
        if (reason != null) {
            currentJob.cancel(CancellationException(reason))
            currentJob.join()
        } else {
            currentJob.cancelAndJoin()
        }
        return true
    }

    //endregion


    override suspend fun join() {
        currentJob.join()
    }


    // impl apis


    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        return friendGenerator.asFlow(limiter.batchSize) { configuration.delay() }
    }

    @Api4J
    override fun getFriends(grouping: Grouping, limiter: Limiter): Stream<out Friend> {
        return friendGenerator.asSequence(limiter.batchSize) { configuration.sleep() }.asStream()
    }


    override suspend fun friend(id: ID): Friend? = friends().firstOrNull { it.id == id }


    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    @Api4J
    override fun getGroups(grouping: Grouping, limiter: Limiter): Stream<out Group> {
        TODO("Not yet implemented")
    }

    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Guild> {
        TODO("Not yet implemented")
    }

    @Api4J
    override fun getGuilds(grouping: Grouping, limiter: Limiter): Stream<out Guild> {
        TODO("Not yet implemented")
    }


    override suspend fun group(id: ID): Group? {
        TODO("Not yet implemented")
    }

    override suspend fun guild(id: ID): Guild? {
        TODO("Not yet implemented")
    }


    //region upload img
    override suspend fun resolveImage(id: ID): TestImage {
        configuration.delay()
        // TODO log
        return TestImage(id)
    }

    override suspend fun uploadImage(resource: Resource): TestImage {
        configuration.delay()
        // TODO log
        return TestImage(randomID(), resource)
    }
    //endregion


    companion object {
        internal val defaultUserStatus = UserStatus.builder().fakeUser().bot().build()
    }
}