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
    private val manager =
        SimpleEventListenerManager.newInstance(SimpleListenerManagerConfiguration()) as SimpleEventListenerManagerImpl
    
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
        
        assertEquals(5, manager.listeners.count(), "manager listeners count")
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
        
        val baseTimes = 5000
        
        fun CoroutineScope.launchRegister(listener: EventListener, priorityRange: IntRange) {
            repeat(baseTimes) {
                launch {
                    val handle =
                        manager.register(
                            listener.toRegistrationDescription(
                                priority = Random.nextInt(
                                    priorityRange.first,
                                    priorityRange.last
                                )
                            )
                        )
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
                launch {
                    val handle =
                        manager.register(
                            listener.toRegistrationDescription(
                                priority = Random.nextInt(
                                    priorityRange.first,
                                    priorityRange.last
                                )
                            )
                        )
                    delay(Random.nextLong(5))
                    if (it % 2 == 0) {
                        handle.dispose()
                    }
                }
            }
        }
        
        val job1 = scope1.launch {
            launchRegister(listener1, 100..200)
            launchRegister(listener2, 300..400)
        }
        val job2 = scope2.launch {
            launchRegister(listener3, 500..600)
            launchRegister(listener4, 700..800)
        }
        
        runBlocking {
            job1.join()
            job2.join()
        }
        
        val listeners = manager.listeners.toList()
        
        assertEquals(baseTimes * 4, listeners.size, "listeners' size")
        val chunked = listeners.chunked(baseTimes)
        assertEquals(4, chunked.size, "chunked listeners' size")
        
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
        
        val baseCount = 5000
        
        fun CoroutineScope.launchRegister(listener: EventListener) {
            launch {
                repeat(baseCount) {
                    repeat(2) {
                        launch {
                            val handle =
                                manager.register(listener)
                            delay(Random.nextLong(5))
                            if (it % 2 == 0) {
                                handle.dispose()
                            }
                        }
                    }
                }
            }
        }
        
        val job1 = scope1.launch {
            launchRegister(listener1)
            launchRegister(listener2)
        }
        val job2 = scope2.launch {
            launchRegister(listener3)
            launchRegister(listener4)
        }
        
        runBlocking {
            job1.join()
            job2.join()
        }
        
        // Event: only Event itSelf.
        assertEquals(baseCount.toLong(), manager.count(Event), "manager Event count")
        
        // FriendEvent: process via Event, FriendEvent
        assertEquals(baseCount * 2L, manager.count(FriendEvent), "manager FriendEvent count")
        
        // FriendMessageEvent: process via Event, FriendEvent, FriendMessageEvent
        assertEquals(baseCount * 3L, manager.count(FriendMessageEvent), "manager FriendMessageEvent count")
        
        // FriendEvent: process via Event, GroupEvent
        assertEquals(baseCount * 2L, manager.count(GroupEvent), "manager GroupEvent count")
    }
    
}

