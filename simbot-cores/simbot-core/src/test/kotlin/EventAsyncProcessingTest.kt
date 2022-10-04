import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.*
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.core.event.buildSimpleListenerRegistrationDescription
import love.forte.simbot.core.event.simpleListenerManager
import love.forte.simbot.definition.Contact
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Guild
import love.forte.simbot.event.*
import love.forte.simbot.message.Image
import love.forte.simbot.message.Image.Key.toImage
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.resources.Resource.Companion.toResource
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import org.slf4j.Logger
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class EventAsyncProcessingTest {
    private val manager = simpleListenerManager { }
    private val botManager = TestBotManager(manager)
    private val bot = botManager.createBot()
    
    @Suppress("ReactiveStreamsUnusedPublisher")
    @OptIn(ExperimentalSimbotApi::class)
    @Test
    fun reactivelyResultTest() {
        manager.register(buildSimpleListenerRegistrationDescription(TestEvent) {
            priority = PriorityConstant.PRIORITIZED_1
            handle {
                EventResult.of(bot.async {
                    delay(50)
                    "Hello"
                })
            }
        })
        manager.register(buildSimpleListenerRegistrationDescription(TestEvent) {
            priority = PriorityConstant.PRIORITIZED_2
            handle {
                EventResult.of(bot.async {
                    delay(50)
                    "World"
                }.asCompletableFuture())
            }
        })
        
        
        manager.register(buildSimpleListenerRegistrationDescription(TestEvent) {
            priority = PriorityConstant.PRIORITIZED_3
            handle {
    
    
                EventResult.of(mono {
                    delay(50)
                    "Hello"
                })
            }
        })
        
        manager.register(buildSimpleListenerRegistrationDescription(TestEvent) {
            priority = PriorityConstant.PRIORITIZED_4
            handle {
                EventResult.of(flux {
                    delay(20)
                    channel.send(1)
                    delay(20)
                    channel.send(2)
                    delay(20)
                    channel.send(3)
                })
            }
        })
    
        val resultContent = runBlocking {
            val results = manager.push(TestEvent(bot)).results
            assertEquals(4, results.size, "result size")
            results.joinToString(" ") {
                when (val c = it.content) {
                    is Iterable<*> -> c.joinToString(" ") { e -> e.toString() }
                    else -> c.toString()
                }
            }
        }
        
        assertEquals("Hello World Hello 1 2 3", resultContent, "result content")
    }
    
    @OptIn(ExperimentalSimbotApi::class)
    @Test
    fun asyncResultTest() {
        manager.register(buildSimpleListenerRegistrationDescription(TestEvent) {
            priority = PriorityConstant.PRIORITIZED_1
            isAsync = true
            handle {
                delay(50)
                EventResult.of("Hello")
            }
        })
        manager.register(buildSimpleListenerRegistrationDescription(TestEvent) {
            priority = PriorityConstant.PRIORITIZED_2
            isAsync = true
            handle {
                delay(50)
                EventResult.of("World")
            }
        })
    
        val resultContent = runBlocking {
            val results = manager.push(TestEvent(bot)).results
            assertEquals(2, results.size, "result size")
            results.joinToString(" ") {
                it as AsyncEventResult
                runBlocking { it.awaitContent() }.content.toString()
            }
        }
        
        assertEquals("Hello World", resultContent, "result content")
    }
    
    
}


private class TestEvent(override val bot: Bot) : Event {
    override val id: ID = randomID()
    override val timestamp: Timestamp = Timestamp.now()
    override val key: Event.Key<out Event>
        get() = Key
    
    companion object Key : BaseEventKey<TestEvent>("test.test") {
        override fun safeCast(value: Any): TestEvent? = doSafeCast(value)
    }
}


private class TestBot(override val manager: TestBotManager, override val eventProcessor: EventProcessor, pj: Job) :
    Bot {
    private val job = Job(pj)
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val id: ID = "forte".ID
    override val username: String = "forte"
    override val avatar: String = ""
    override val logger: Logger = LoggerFactory.getLogger<TestBot>()
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
    override val contacts: Items<Contact> = emptyItems()
    override suspend fun contact(id: ID): Contact? = null
    override val groups: Items<Group> = emptyItems()
    override suspend fun group(id: ID): Group? = null
    override val guilds: Items<Guild> = emptyItems()
    override suspend fun guild(id: ID): Guild? = null
}

private object TestComponent : Component {
    override val id: String = "test"
    override val componentSerializersModule: SerializersModule
        get() = EmptySerializersModule()
}

private class TestBotManager(private val eventProcessor: EventProcessor) : BotManager<TestBot>() {
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