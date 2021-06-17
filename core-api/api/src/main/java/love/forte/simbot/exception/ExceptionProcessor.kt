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

package love.forte.simbot.exception



/**
 *
 * 异常处理中心。
 *
 * 当一个监听函数出现异常的时候，便会尝试通过异常处理器对异常进行处理。
 *
 * 每一个异常类型应当只对应一个异常处理器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ExceptionProcessor {

    /**
     * 根据一个异常类型获取对应的异常处理器。
     */
    fun <E : Throwable> getHandle(exType: Class<out E>): ExceptionHandle<E>?

}

/**
 * [ExceptionProcessor] 工厂。
 * 用于构建一个 [ExceptionProcessor] 实例。
 */
public interface ExceptionProcessorBuilder {

    /**
     * 追加一个或多个异常处理器。
     */
    fun register(vararg handle: ExceptionHandle<*>): ExceptionProcessorBuilder

    /**
     * 构建一个异常处理中心。
     */
    fun build(): ExceptionProcessor
}