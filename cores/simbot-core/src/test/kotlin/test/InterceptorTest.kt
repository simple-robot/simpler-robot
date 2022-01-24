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

package test

import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventProcessingResult
import love.forte.simbot.utils.runInBlocking

/**
 *
 * @author ForteScarlet
 */
class InterceptorTest {
    @org.junit.Test
    fun test(): Unit = runInBlocking {
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


