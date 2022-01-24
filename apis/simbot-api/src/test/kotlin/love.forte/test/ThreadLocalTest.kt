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

import kotlinx.coroutines.*
import love.forte.simbot.utils.runInBlocking
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
    fun test1() = runInBlocking {
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