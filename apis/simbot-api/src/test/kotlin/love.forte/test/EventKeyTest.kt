package love.forte.test

import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.event.contains
import love.forte.simbot.event.isSubFrom
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class EventKeyTest {


    @Test
    fun findTest() {
        val want = MessageEvent.id

        assert(want.toString() in GroupMessageEvent)
        assert(GroupMessageEvent isSubFrom MessageEvent)



    }

}