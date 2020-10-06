/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerManagerImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet


/**
 * [ListenerManager] 实现。
 */
public class ListenerManagerImpl : ListenerManager {


    /**
     * 接收到消息监听并进行处理。
     */
    override fun onMsg(msgGet: MsgGet): ListenResult<*> {
        TODO("Not yet implemented")
    }

    /**
     * 根据监听类型获取所有对应的监听函数。
     */
    override fun <T : MsgGet> getListenerFunctions(type: Class<out T>?): List<ListenerFunction> {
        TODO("Not yet implemented")
    }
}
