package love.forli.test.spring

import love.forte.simboot.annotation.Listener
import love.forte.simboot.spring.autoconfigure.EnableSimbot
import love.forte.simbot.application.Application
import love.forte.simbot.core.event.buildSimpleListener
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.FriendMessageEvent
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@SpringBootApplication
@EnableSimbot
open class SpringBootApplicationTest {
    
    @Autowired
    var events: List<EventListener>? = null
    
    @Autowired
    lateinit var application: Application
    
    
    @Test
    fun test1() {
        println("All: ${events?.size ?: 0}")
        events?.forEach {
            println(it)
        }
        println(application)
        println(application.environment)
        println(application.providers)
        println(application.eventListenerManager)
        println(application.botManagers)
    }
    
}


@Component
open class Listeners {
    @Listener
    fun myListener() {
    
    }
    
    @Listener
    fun myListener2(foo: Foo) = buildSimpleListener(FriendMessageEvent) {
        match { true }
        handle {
            println(foo)
            EventResult.defaults()
        }
    }
    
    // @Bean
    // fun defaultListener() = buildSimpleListener(FriendMessageEvent) {
    //     match { true }
    //     handle {
    //         EventResult.defaults()
    //     }
    // }
}


@Component
open class Foo {
    @Listener
    fun myListener() {
    
    }
    
    @Listener
    fun myListener2() = buildSimpleListener(FriendMessageEvent) {
        match { true }
        handle {
            EventResult.defaults()
        }
    }
    
    /*
    ========
    definition: Root bean: class [null]; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=foo; factoryMethodName=getBar; initMethodName=null; destroyMethodName=(inferred); defined in class path resource [love/forli/test/spring/Foo.class]
    definition.beanClassName: null
    definition.resolvableType: ?
    definition.parentName: null
    definition.factoryBeanName: foo
    definition.factoryMethodName: getBar
    ========
     */
    
    @Bean
    fun getTar() = Tar()
    
    @Bean
    fun getBar(tar: Tar) = Bar(tar)
}

open class Bar(val tar: Tar)
open class Tar