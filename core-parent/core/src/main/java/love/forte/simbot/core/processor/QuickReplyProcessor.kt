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

package love.forte.simbot.core.processor

import love.forte.simbot.api.message.ReplyAble
import love.forte.simbot.processor.ListenResultProcessor
import love.forte.simbot.processor.ListenResultProcessorContext


/**
 *
 * [ListenResultProcessor] 的默认实现之一，用于解析响应值并进行快速回复操作。
 *
 * @see ReplyAble
 *
 * @author ForteScarlet
 */
public object QuickReplyProcessor : ListenResultProcessor {
    /**
     * 接收 [ListenResultProcessorContext] 进行处理（例如解析并进行自动回复等）。
     *
     * @return 是否处理成功。
     */
    override fun processor(processContext: ListenResultProcessorContext): Boolean {
        return when(val listenResult = processContext.listenResult) {
            is ReplyAble -> TODO()
            is Map<*, *> -> TODO()
            else -> false
        }
    }
}