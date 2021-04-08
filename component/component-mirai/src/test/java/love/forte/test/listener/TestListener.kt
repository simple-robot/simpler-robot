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
import love.forte.simbot.annotation.OnGroup
import love.forte.simbot.annotation.OnPrivate
import love.forte.simbot.api.sender.Sender

/**
 * @author ForteScarlet
 */
@Beans
class TestListener {


    @OnPrivate
    @Filters(Filter("share"))
    fun share(sender: Sender){
        val share1 = "[CAT:share,image=http://nhy-file-upload.test.upcdn.net/robot/img/%E6%AC%A2%E8%BF%8E.jpg,title=抢红包啦,content=土豪来抢红包？,url=https://baidu.com]"
        val share2 = "[CAT:share,title=抢红包啦,content=土豪来抢红包？,url=https://baidu.com]"
        val share3 = "[CAT:share,image=http://forte.love:15520/img/r,title=抢红包啦,content=土豪来抢红包？,url=https://baidu.com]"
        sender.sendPrivateMsg(1149159218, share1)
        sender.sendPrivateMsg(1149159218, share2)
        sender.sendPrivateMsg(1149159218, share3)
    }

    @OnGroup
    @Filters(Filter("share"))
    fun share2(sender: Sender){
        val share1 = "[CAT:share,image=http://nhy-file-upload.test.upcdn.net/robot/img/%E6%AC%A2%E8%BF%8E.jpg,title=抢红包啦,content=土豪来抢红包？,url=https://baidu.com]"
        val share2 = "[CAT:share,title=抢红包啦,content=土豪来抢红包？,url=https://baidu.com]"
        val share3 = "[CAT:share,image=http://forte.love:15520/img/r,title=抢红包啦,content=土豪来抢红包？,url=https://baidu.com]"
        sender.sendGroupMsg(1043409458, share1)
        sender.sendGroupMsg(1043409458, share2)
        sender.sendGroupMsg(1043409458, share3)
    }

    // @OnGroup
    // @Filter("#[投骰]个{{m,\\d+}}", matchType = MatchType.REGEX_MATCHES, trim = true, atBot = true)
    // fun GroupMsg.listen(@FilterValue("m") m: Int, sender: Sender) {
    //     sender.sendGroupMsg(this, CatCodeUtil.toCat("dice", false, "value=$m"))
    // }


}

