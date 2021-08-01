/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import love.forte.simbot.annotation.SimbotApplication
import love.forte.simbot.bot.onSender
import love.forte.simbot.component.mirai.utils.registerEventSolver
import love.forte.simbot.core.runSimbot
import love.forte.test.event.TestPostEvent
import net.mamoe.mirai.event.events.MessagePostSendEvent
import kotlin.test.Test


@SimbotApplication
class Test2 {

    fun fun1() {
        // inline fun
        registerEventSolver<MessagePostSendEvent<*>, TestPostEvent<*>> {
            TestPostEvent(it)
        }

        // no inline fun
        registerEventSolver(MessagePostSendEvent::class, TestPostEvent::class) {
            TestPostEvent(it)
        }

    }

    @Test
    fun testBotRunWithoutBotInfo() {
        runSimbot<Test2>().also {
            it.botManager.bots.forEach { bot ->
                bot.onSender {
                    sendPrivateMsg(1149159218, "我测试好了")
                }
            }
            it.close()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callbackFlowTest() {

        val scope1 = CoroutineScope(Dispatchers.Default)

        var num = 1

        var f = channelFlow<Int> {
            scope1.launch {
                while (isActive) {
                    val n = num++
                    delay(20)
                    println("send: $n")
                    send(n)
                    if (n > 10) {
                        println("$n > 10, close.")
                        close()
                    }
                }
            }
            awaitClose {
                println("Flow closed! by await")
            }


        }

        f = f.filter { it % 2 == 0 }

        runBlocking {
            f.collect {
                println("collect: $it")
            }
        }

    }

}