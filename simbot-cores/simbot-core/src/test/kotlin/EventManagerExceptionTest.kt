import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.core.event.buildSimpleListener
import love.forte.simbot.core.event.simpleListenerManager

/**
 *
 * @author ForteScarlet
 */
class EventManagerExceptionTest {
    
    private val manager = simpleListenerManager {
        listenerExceptionHandler {
            TODO()
        }
    }
    private val botManager = TestBotManager(manager)
    private val testBot = botManager.createBot()
    
    @kotlin.test.Test
    @OptIn(ExperimentalSimbotApi::class)
    fun exceptionTest() {
        manager.register(buildSimpleListener(TestEvent) {
            process {
                delay(500)
                error("ERR 1")
                // println("Listener 1: $it")
            }
        })
        
        manager.register(buildSimpleListener(TestEvent) {
            process {
                delay(500)
                println("Listener 2: $it")
            }
        })
        
        runBlocking {
            val pushJob = testBot.launch {
                val result = manager.push(TestEvent(testBot))
                println("result: $result")
            }
            
            // delay(700)
            // println("before cancel")
            // pushJob.cancel()
            pushJob.join()
            println("cancelled")
        }
        
        
    }
    
    
}