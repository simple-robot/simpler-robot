/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread


/**
 *
 * @author ForteScarlet
 */
class FlowTest {

    private val n1 = AtomicInteger(1)
    private val n2 = AtomicInteger(1)

    private val scope1 = CoroutineScope(
        Executors.newFixedThreadPool(4) { r -> thread(isDaemon = true, start = false, name = "A-${n1.getAndIncrement()}") { r.run() } }
            .asCoroutineDispatcher()
    )

    private val scope2 = CoroutineScope(
        Executors.newFixedThreadPool(4) { r -> thread(isDaemon = true, start = false, name = "B-${n2.getAndIncrement()}") { r.run() } }
            .asCoroutineDispatcher()
    )

    @Test
    fun test() = runBlocking {
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
