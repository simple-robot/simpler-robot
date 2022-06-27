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

import love.forte.simboot.annotation.Listener
import love.forte.simboot.spring.autoconfigure.EnableSimbot
import love.forte.simbot.core.event.buildSimpleListener
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.FriendEvent
import love.forte.simbot.event.MessageEvent
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.stereotype.Component

@EnableSimbot
@SpringBootApplication
@EnableAspectJAutoProxy
open class SpringBootApp


fun main(vararg args: String) {
    runApplication<SpringBootApp>(*args).also { context ->
        context
    }
}

@Component
open class MyBean

@Component
open class MyListeners {
    
    @Listener
    suspend fun MessageEvent.myLis() {
        println(source())
    }
    
    @Listener
    
    fun MessageEvent.myLis2(): EventResult {
        return EventResult.defaults()
    }
}

@Listener
fun listener() = buildSimpleListener(FriendEvent) {
    match { false }
    handle { EventResult.defaults() }
}
