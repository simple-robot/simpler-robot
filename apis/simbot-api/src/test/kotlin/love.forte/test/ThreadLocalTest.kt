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
import org.junit.jupiter.api.DisplayName
import kotlin.test.Ignore
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
@DisplayName("ThreadLocal协程测试")
class ThreadLocalTest {

    @Ignore
    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun test1() = runBlocking {
        val dis1 = newFixedThreadPoolContext(4, "dis1")
        val dis2 = newFixedThreadPoolContext(4, "dis2")
        val local = ThreadLocal<String>()
        local.set("cache")

        val sp1 = CoroutineScope(dis1)
        val sp2 = CoroutineScope(dis2)

        sp1.launch(local.asContextElement("forte")) {
            delay(1)
            println(local.get())
            withContext(dis2) {
                delay(1)
                println(local.get())
                local.set("abc")
                println(local.get())
            }
            delay(1)
            println(local.get())
        }.join()

        dis1.cancel()
        dis2.cancel()

        local.remove()


    }

}