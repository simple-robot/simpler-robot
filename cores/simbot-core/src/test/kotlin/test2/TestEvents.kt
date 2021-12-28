package test2

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.*
import love.forte.simbot.definition.*
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.GroupEvent
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import org.slf4j.Logger
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


class TestGroupEvent : GroupEvent {
    override val metadata: Event.Metadata = object : Event.Metadata {
        override val id: ID = randomID()
    }
    override val timestamp: Timestamp = Timestamp.now()
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL
    override val key: Event.Key<GroupEvent> get() = GroupEvent
    override val bot: Bot get() = TestBot
    override suspend fun organization(): Organization {
        TODO("Not yet implemented")
    }
    override suspend fun group(): Group {
        TODO("Not yet implemented")
    }
}

class TestChannelEvent : ChannelEvent {
    override val metadata: Event.Metadata = object : Event.Metadata {
        override val id: ID = randomID()
    }
    override val timestamp: Timestamp = Timestamp.now()
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL
    override val key: Event.Key<ChannelEvent> get() = ChannelEvent
    override val bot: Bot get() = TestBot

    override suspend fun organization(): Organization {
        TODO("Not yet implemented")
    }

    override suspend fun channel(): Channel {
        TODO("Not yet implemented")
    }
}


public object TestBot : Bot {
    override val coroutineContext: CoroutineContext get() = EmptyCoroutineContext
    override val id: ID = randomID()
    override val username: String = "ForteScarlet"
    override val avatar: String = "avatar"
    override val logger: Logger = LoggerFactory.getLogger(TestBot::class)
    override val manager: BotManager<out Bot> get() = TODO("Not yet implemented")
    override val eventProcessor: EventProcessor get() = TODO("Not yet implemented")
    override val component: Component get() = SimbotComponent
    override val status: UserStatus = UserStatus.builder().bot().fakeUser().build()

    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> = emptyFlow()

    @Api4J
    override fun getFriends(grouping: Grouping, limiter: Limiter): Stream<out Friend> = Stream.empty()

    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> = emptyFlow()

    @Api4J
    override fun getGroups(grouping: Grouping, limiter: Limiter): Stream<out Group> = Stream.empty()

    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Guild> = emptyFlow()

    @Api4J
    override fun getGuilds(grouping: Grouping, limiter: Limiter): Stream<out Guild> = Stream.empty()

    override suspend fun uploadImage(resource: Resource): Image {
        TODO("Not yet implemented")
    }

    override suspend fun start(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        TODO("Not yet implemented")
    }

    override suspend fun cancel(reason: Throwable?): Boolean {
        TODO("Not yet implemented")
    }

    override val isStarted: Boolean = true
    override val isActive: Boolean = true
    override val isCancelled: Boolean = false
}