import kotlinx.coroutines.*
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.core.event.*
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription
import love.forte.simbot.event.internal.InternalEvent
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class EventManagerTest {
    private val manager = SimpleEventListenerManager.newInstance(SimpleListenerManagerConfiguration()) as SimpleEventListenerManagerImpl
    
    private val scope1 = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())
    private val scope2 = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())
    
    private val keys = setOf(
        Event,
        FriendEvent,
        GroupEvent,
        GuildEvent,
        ChannelEvent,
        MessageEvent,
        InternalEvent,
        UserEvent,
        RequestEvent
    )
    
    @OptIn(ExperimentalSimbotApi::class)
    @Test
    fun `event key container async test`() {
        val job2 = scope2.launch(start = CoroutineStart.LAZY) {
            delay(5)
            while (true) {
                assertTrue { ChannelEvent in manager }
            }
        }
        
        val job1 = scope1.launch {
            repeat(5) {
                manager.register(simpleListener(ChannelEvent) { EventResult.invalid() })
            }
            job2.start()
            repeat(50000) {
                launch {
                    manager.register(simpleListener(keys.random()) { EventResult.invalid() }).also { handle ->
                        launch {
                            delay(Random.nextLong(500, 1000))
                            handle.dispose()
                        }
                    }
                }
            }
        }
        
        
        
        
        
        runBlocking { job1.join() }
        job2.cancel()
        
        assertTrue { manager.listeners.count() == 5 }
        assertTrue { ChannelEvent in manager }
        assertFalse { RequestEvent in manager }
        
    }
    
    @OptIn(ExperimentalSimbotApi::class)
    @Test
    fun `priority test`() {
        val listener1 = buildSimpleListener(Event) { process { } }
        val listener2 = buildSimpleListener(Event) { process { } }
        val listener3 = buildSimpleListener(Event) { process { } }
        val listener4 = buildSimpleListener(Event) { process { } }
        
        val job1 = scope1.launch {
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener1.toRegistrationDescription(priority = Random.nextInt(100, 200)))
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener2.toRegistrationDescription(priority = Random.nextInt(300, 400)))
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
        }
        val job2 = scope2.launch {
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener3.toRegistrationDescription(priority = Random.nextInt(500, 600)))
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener4.toRegistrationDescription(priority = Random.nextInt(700, 800)))
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
        }
        
        
        runBlocking {
            job1.join()
            job2.join()
        }
        
        val listeners = manager.listeners.toList()
        
        assertTrue { listeners.size == 10000 }
        val chunked = listeners.chunked(2500)
        assertTrue { chunked.size == 4 }
        
        assertTrue { chunked[0].all { it == listener1 } }
        assertTrue { chunked[1].all { it == listener2 } }
        assertTrue { chunked[2].all { it == listener3 } }
        assertTrue { chunked[3].all { it == listener4 } }
    }
    
    @OptIn(ExperimentalSimbotApi::class)
    @Test
    fun `manager counter test`() {
        val listener1 = buildSimpleListener(Event) { process { } }
        val listener2 = buildSimpleListener(FriendEvent) { process { } }
        val listener3 = buildSimpleListener(FriendMessageEvent) { process { } }
        val listener4 = buildSimpleListener(GroupEvent) { process { } }
        
        val job1 = scope1.launch {
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener1)
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener2)
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
        }
        val job2 = scope2.launch {
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener3)
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
            repeat(5000) {
                launch {
                    val handle =
                        manager.register(listener4)
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
        }
        
        
        runBlocking {
            job1.join()
            job2.join()
        }
        
        assertEquals(manager.count(Event), 10000L)
        assertEquals(manager.count(FriendEvent), 5000L)
        assertEquals(manager.count(FriendMessageEvent), 2500L)
        assertEquals(manager.count(GroupEvent), 2500L)
    }
    
}