/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forli.test

import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simboot.spring.autoconfigure.EnableSimbot
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.application.Application
import love.forte.simbot.core.event.buildSimpleListener
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.event.FriendMessageEvent
import love.forte.simbot.event.internal.BotRegisteredEvent
import love.forte.simbot.event.internal.BotStartedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component


@EnableSimbot
@SpringBootApplication
open class SpringBootApp


fun main(vararg args: String) {
    runApplication<SpringBootApp>(args = args).also { context ->
        val listeners = context.getBeanNamesForType(love.forte.simbot.event.EventListener::class.java)
        listeners.forEach {
            println(it)
        }
        println("END.")
    }
}

@Component
class Runner(val manager: EventListenerManager) : CommandLineRunner {
    override fun run(vararg args: String?) {
        println(" === Runner. manager: $manager")
    }
}

@Component
open class MyListener {
    
    @Autowired
    lateinit var application: Application
    
    // fun customListener(): love.forte.simbot.event.EventListener {
    //     println("APPLICATION?: $application")
    //     return buildSimpleListener(FriendMessageEvent) {
    //         process {
    //             println("Hello~")
    //         }
    //     }
    // }
    
    @OptIn(ExperimentalSimbotApi::class)
    @Listener
    @ContentTrim
    @Filter("hello")
    suspend fun BotRegisteredEvent.onReg() {
        println(this)
        println(bot.id)
        bot.contact(1149159218.ID)?.send("hi!")
    }
    
    @Listener
    fun BotStartedEvent.onStart() {
        println("Application: $application")
        println("hi~")
    }
    
    
}