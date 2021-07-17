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

import love.forte.simbot.annotation.*
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.Reply
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerManager
import love.forte.simbot.listener.onSwitch


/**
 *
 * @author ForteScarlet
 */
// @Beans
@OptIn(SimbotExperimentalApi::class)
class SwitchTestListener(private val listenerManager: ListenerManager) {

    private val hiSwitch: ListenerFunction.Switch = listenerManager.getListenerFunctionById("HI_LI")!!.switch.onSwitch {
        println("On Switch: $it")
    }

    @Filters(Filter("hi"))
    @Listens(name = "HI_LI", value = [Listen(PrivateMsg::class)])
    fun hi() = Reply.reply("hello")


    @Filters(Filter("h.off"))
    @OnPrivate
    fun switchOff(func: ListenerFunction) {
        hiSwitch.disable()
    }

    @Filters(Filter("h.on"))
    @OnPrivate
    fun switchOn(func: ListenerFunction) {
        hiSwitch.enable()
    }


}