import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.reactor.mono
import love.forte.simbot.*
import love.forte.simbot.core.event.buildSimpleListenerRegistrationDescription
import love.forte.simbot.core.event.simpleListenerManager
import love.forte.simbot.event.*
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
            val results = manager.push(TestEvent(bot)).resultsView
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
            val results = manager.push(TestEvent(bot)).resultsView
            assertEquals(2, results.size, "result size")
            results.joinToString(" ") {
                it as AsyncEventResult
                runBlocking { it.awaitContent() }.content.toString()
            }
        }
        
        assertEquals("Hello World", resultContent, "result content")
    }
    
    
}

