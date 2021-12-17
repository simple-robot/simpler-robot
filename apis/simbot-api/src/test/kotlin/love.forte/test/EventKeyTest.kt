package love.forte.test

import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.event.contains
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class EventKeyTest {


    @Test
    fun findTest() {
        val key = GroupMessageEvent

        val want = MessageEvent.id

        println(want.toString() in key)



    }

}