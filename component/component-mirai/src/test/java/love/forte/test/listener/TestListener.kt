/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.test.listener

import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.Listen
import love.forte.simbot.annotation.Listens
import love.forte.simbot.api.message.Reply
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.filter.FilterTargets
import love.forte.simbot.filter.MatchType
import love.forte.simbot.listener.ListenerContext

/**
 * @author ForteScarlet
 */
@Beans
class TestListener {


    @Listens(value = [Listen(PrivateMsg::class)],
        priority = PriorityConstant.FIRST)
    @Filter("h", matchType = MatchType.STARTS_WITH)
    fun ListenerContext.listen(msgGet: MessageGet) = Reply.reply(text = "1!").also {
        if (msgGet.text == "hello") {
            this.instant("hello", true)
        }
    }


    @Listens(value = [Listen(PrivateMsg::class)],
        priority = PriorityConstant.SECOND)
    @Filter("true", target = FilterTargets.CONTEXT_INSTANT_NULLABLE + "hello")
    fun PrivateMsg.listen2() = this.msgContent


}

