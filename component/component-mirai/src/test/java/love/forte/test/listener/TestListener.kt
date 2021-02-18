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
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.OnPrivate
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.listener.ListenerContext

/**
 * @author ForteScarlet
 */
@Beans
class TestListener {


    @OnPrivate
    @Filters(Filter("share"))
    fun ListenerContext.listen(sender: Sender) {
        val share = "[CAT:share,coverUrl=http://nhy-file-upload.test.upcdn.net/robot/img/%E6%AC%A2%E8%BF%8E.jpg,title=抢红包啦11111,content=土豪来抢红包22222？,url=https://baidu.com]"
        sender.sendPrivateMsg(1149159218, share)
    }


}

