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
 */
public interface ListenResultProcessorManager {

    /**
     * 通过监听函数处理上下文进行处理。
     */
    fun processor(context: ListenResultProcessorContext)

}