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

package love.forte.simbot.listener


/**
 *
 * 监听函数的执行回执。
 *
 *
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ListenResult<T> {

    /**
     * 是否执行成功。
     */
    fun isSuccess(): Boolean

    /**
     * 是否阻断接下来的监听函数的执行。
     */
    fun isBreak(): Boolean

    /**
     * 最终的执行结果。
     */
    val result: T?

    /**
     * 如果执行出现了异常，此处为异常。
     */
    val throwable: Throwable?

}

/**
 * 监听函数响应值工厂。
 */
interface ListenerResultFactory {
    fun getResult(result: Any?, listenerFunction: ListenerFunction, throwable: Throwable? = null): ListenResult<*>
}

