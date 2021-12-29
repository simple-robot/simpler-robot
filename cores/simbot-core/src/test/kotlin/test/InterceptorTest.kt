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

package test

import kotlinx.coroutines.runBlocking
import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventProcessingResult

/**
 *
 * @author ForteScarlet
 */
class InterceptorTest {
    @org.junit.Test
    fun test(): Unit = runBlocking {
        val inter1 = TestInterceptor {
            println("inter 1 start")
            it.proceed().also {
                println("inter 1 end")
            }
        }
        val inter2 = TestInterceptor {
            println("inter 2 start")
            it.proceed().also {
                println("inter 2 end")
            }
        }
        val inter3 = TestInterceptor {
            println("inter 3 start")
            it.proceed().also {
                println("inter 3 end")
            }
        }

        val list = listOf(inter1, inter2, inter3)

        // val entrance = InterceptorEntrance(list)
        //
        // entrance.doIntercept(TestProcessingContext()) { context ->
        //     println("Context!: $context")
        //     EventProcessingResult
        // }


    }
}


class TestInterceptor(private val run: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult) :
    EventProcessingInterceptor {
    override suspend fun intercept(context: EventProcessingInterceptor.Context): EventProcessingResult {
        return run(context)
    }

}


