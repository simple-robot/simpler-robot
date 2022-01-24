/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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