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

package love.forte.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class FlowTest {

    private val n1 = AtomicInteger(1)
    private val n2 = AtomicInteger(1)

    private val scope1 = CoroutineScope(
        Executors.newFixedThreadPool(4) { r ->
            thread(
                isDaemon = true,
                start = false,
                name = "A-${n1.getAndIncrement()}"
            ) { r.run() }
        }
            .asCoroutineDispatcher()
    )

    private val scope2 = CoroutineScope(
        Executors.newFixedThreadPool(4) { r ->
            thread(
                isDaemon = true,
                start = false,
                name = "B-${n2.getAndIncrement()}"
            ) { r.run() }
        }
            .asCoroutineDispatcher()
    )

    @Test
    fun test() = runInBlocking {
        val flow = flow {
            var n = 0
            while (true) {
                val i = n
                repeat(10) {
                    emit(n++)
                }
                println("[${Thread.currentThread().name}] emit $i -> $n")
            }
        }

        scope2.launch {
            flow.drop(20).take(20).flowOn(scope2.coroutineContext).collect {
                println("[${Thread.currentThread().name}] take: $it")
            }
            println("End.")
        }.join()
    }


    @Test
    fun test2() {
        val flow = flow {
            var i = 0
            while (true) {
                delay(100)
                emit(i++)
            }
        }


    }


}
