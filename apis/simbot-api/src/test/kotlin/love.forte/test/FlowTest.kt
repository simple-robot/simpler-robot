/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
