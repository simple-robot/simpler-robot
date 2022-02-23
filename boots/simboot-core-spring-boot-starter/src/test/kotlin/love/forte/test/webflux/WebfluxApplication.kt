package love.forte.test.webflux

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.asFlux
import love.forte.simboot.annotation.Listener
import love.forte.simboot.autoconfigure.EnableSimbot
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.event.FriendMessageEvent
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody


/**
 *
 * @author ForteScarlet
 */
@SpringBootApplication
@EnableSimbot
open class WebfluxApplication

fun main(args: Array<String>) {
    runApplication<WebfluxApplication>(*args)
}


@Controller
open class MyController {

    @ResponseBody
    @GetMapping("/hi")
    suspend fun hello(): Map<String, Any> {
        delay(100)
        return mapOf("name" to "forte", "age" to 10)
    }

    @Listener
    suspend fun FriendMessageEvent.myListen1() = flow {
        emit(1)
        bot.logger.info("emit: {}", 1)
        delay(2000)
        emit(2)
        bot.logger.info("emit: {}", 2)
    }

    @Listener
    suspend fun FriendMessageEvent.myListen2() = flow {
        emit("3")
        bot.logger.info("emit: {}", 3)
        delay(2000)
        emit("4")
        bot.logger.info("emit: {}", 4)
    }.asFlux()

    @Listener(priority = PriorityConstant.LAST)
    suspend fun FriendMessageEvent.myListen3(context: EventProcessingContext) {
        context.results.forEach {
            friend().send(it.toString())
        }
    }

}