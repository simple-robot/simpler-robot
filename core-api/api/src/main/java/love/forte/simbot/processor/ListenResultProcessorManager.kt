/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     ListenResultProcessorManager.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.processor


/**
 * 监听响应值处理 管理器。
 * 监听响应值处理器会对每一个返回值不是 [love.forte.simbot.listener.ListenResult.Default] 的监听响应。
 */
public interface ListenResultProcessorManager {

    /**
     * 通过监听函数处理上下文进行处理。
     */
    fun processor(context: ListenResultProcessorContext)


    /**
     * 不做任何处理的实现。
     */
    companion object Default : ListenResultProcessorManager {
        override fun processor(context: ListenResultProcessorContext) { }
    }

}