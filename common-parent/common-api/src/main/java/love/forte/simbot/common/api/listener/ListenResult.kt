/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenResult.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.common.api.listener



/**
 * 监听函数的执行结果
 */
public interface ListenResult<T> {
    /**
     * 是否执行成功
     */
    fun isSuccess(): Boolean

    /**
     * 是否截断接下来的监听函数
     */
    fun isBreak(): Boolean

    /**
     * 得到一个执行结果
     */
    val result: T?

    /**
     * 如果出现了异常，得到这个异常。
     */
    val err: Throwable?
}