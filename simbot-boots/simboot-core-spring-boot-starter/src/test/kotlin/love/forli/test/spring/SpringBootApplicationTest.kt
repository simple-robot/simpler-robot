package love.forli.test.spring

import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simboot.spring.autoconfigure.EnableSimbot
import love.forte.simbot.application.Application
import love.forte.simbot.core.event.buildSimpleListener
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.FriendMessageEvent
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest("simbot.top-level-listener-scan-package=love.forli.test.spring")
@SpringBootApplication
@EnableSimbot
open class SpringBootApplicationTest {
    
    @Autowired
    var events: List<EventListener>? = null
    
    @Autowired
    lateinit var application: Application
    
    
    @Test
    fun test1() {
    }
    
}


@Component
open class Listeners {
    @Listener
    suspend fun listener1() {
    }
    
    @Listener
    @ContentTrim
    suspend fun FriendMessageEvent.listener2() {
    }
    
    @Listener
    @Filter("Hello")
    @Filter("Hello World")
    suspend fun FriendMessageEvent.listener3(context: EventListenerProcessingContext) {
    }
    
    @Listener
    fun listener4() {
    }
    
    @Listener
    @ContentTrim
    fun FriendMessageEvent.listener5() {
    }
    
    @Listener
    @Filter("Hello")
    @Filter("Hello World")
    fun FriendMessageEvent.listener6(context: EventListenerProcessingContext) {
    }
}

@Listener
suspend fun listener1() {
}

@Listener
@ContentTrim
suspend fun FriendMessageEvent.listener2() {
}

@Listener
@Filter("Hello")
@Filter("Hello World")
suspend fun FriendMessageEvent.listener3(context: EventListenerProcessingContext) {
}

@Listener
fun listener4() {
}

@Listener
@ContentTrim
fun FriendMessageEvent.listener5() {
}

@Listener
@Filter("Hello")
@Filter("Hello World")
fun FriendMessageEvent.listener6(context: EventListenerProcessingContext) {
}