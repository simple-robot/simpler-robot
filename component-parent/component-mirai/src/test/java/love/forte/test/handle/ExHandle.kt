/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.test.handle

import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.core.listener.ListenResultImpl
import love.forte.simbot.exception.ExceptionHandle
import love.forte.simbot.exception.ExceptionHandleContext
import love.forte.simbot.listener.ListenResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * @author ForteScarlet
 */
@Beans
public class ExHandle : ExceptionHandle<IllegalStateException> {

    private val logger: Logger = LoggerFactory.getLogger(ExHandle::class.java)

    /**
     * do handle.
     */
    override fun doHandle(context: ExceptionHandleContext<IllegalStateException>): ListenResult<*> {

        logger.error("failed: {}", context.cause.localizedMessage)
        logger.error("cause: {}", context.cause.toString())



        return ListenResultImpl.failed(null)
    }
}