import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.definition.Contact
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Guild
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.message.Image
import love.forte.simbot.message.Image.Key.toImage
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.randomID
import love.forte.simbot.resources.Resource.Companion.toResource
import love.forte.simbot.utils.item.Items
import org.slf4j.Logger
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal class TestEvent(override val bot: Bot) : Event {
    override val id: ID = randomID()
    override val timestamp: Timestamp = Timestamp.now()
    override val key: Event.Key<out Event>
        get() = Key
    
    companion object Key : BaseEventKey<TestEvent>("test.test") {
        override fun safeCast(value: Any): TestEvent? = doSafeCast(value)
    }
}


internal class TestBot(override val manager: TestBotManager, override val eventProcessor: EventProcessor, pj: Job) :
    Bot {
    private val job = Job(pj)
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val id: ID = "forte".ID
    override val username: String = "forte"
    override val avatar: String = ""
    override val logger: Logger = LoggerFactory.logger<TestBot>()
    override val component: Component = TestComponent
    override fun isMe(id: ID): Boolean = id == this.id
    override suspend fun resolveImage(id: ID): Image<*> {
        return File("").toResource().toImage()
    }
    
    override suspend fun start(): Boolean = true
    override suspend fun join() = job.join()
    override suspend fun cancel(reason: Throwable?): Boolean {
        job.cancel(reason?.let { CancellationException(it.localizedMessage, it) })
        return true
    }
    
    override val isStarted: Boolean = true
    override val isActive: Boolean get() = job.isActive
    override val isCancelled: Boolean get() = job.isCancelled
    override val contacts: Items<Contact> = Items.emptyItems()
    override suspend fun contact(id: ID): Contact? = null
    override val groups: Items<Group> = Items.emptyItems()
    override suspend fun group(id: ID): Group? = null
    override val guilds: Items<Guild> = Items.emptyItems()
    override suspend fun guild(id: ID): Guild? = null
}

internal object TestComponent : Component {
    override val id: String = "test"
    override val componentSerializersModule: SerializersModule
        get() = EmptySerializersModule()
}

internal class TestBotManager(private val eventProcessor: EventProcessor) : BotManager<TestBot>() {
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val component: Component = TestComponent
    private val job = Job()
    
    override suspend fun join() = job.join()
    
    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }
    
    override suspend fun start(): Boolean = true
    
    override val isStarted: Boolean = true
    override val isActive: Boolean
        get() = job.isActive
    override val isCancelled: Boolean
        get() = job.isCancelled
    
    override fun register(verifyInfo: BotVerifyInfo): Bot = createBot()
    fun createBot(): Bot = TestBot(this, eventProcessor, job)
    
    override suspend fun doCancel(reason: Throwable?): Boolean {
        job.cancel(reason?.let { CancellationException(it.localizedMessage, it) })
        return true
    }
    
    override fun get(id: ID): TestBot? {
        return null
    }
    
    override fun all(): List<TestBot> = emptyList()
}