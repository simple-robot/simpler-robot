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
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.Listen
import love.forte.simbot.annotation.Listens
import love.forte.simbot.api.message.Reply
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.component.mirai.message.event.MiraiPrivateMsg

/**
 * @author ForteScarlet
 */
@Beans
class TestListener {

    /**
     * 监听 [私聊消息][PrivateMsg]与[群聊消息][GroupMsg],
     * 并在收到消息的时候统一回复 "Hello. this is Quick reply.", 如果在群聊中，会AT触发事件的人。
     * 其中，在群聊中需要被AT。
     */
    @Listens(
        value = [
            Listen(PrivateMsg::class),
            Listen(GroupMsg::class),
        ]
    )
    @Filters(atBot = true)
    fun listen(msg: MiraiPrivateMsg) = Reply.reply("Hello. this is Quick reply.", at = true)
}

