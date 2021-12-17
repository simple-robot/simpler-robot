package love.forte.test

import love.forte.simbot.Limiter
import love.forte.simbot.pageNum
import love.forte.simbot.pageSize
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class OffsetTest {

    @Test
    fun a() {
        val limiter = Limiter.ofPage(10, 5)
        println(limiter)

        println(limiter.pageSize)
        println(limiter.pageNum)

    }

}