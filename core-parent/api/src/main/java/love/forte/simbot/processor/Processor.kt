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
@file:JvmName("Processors")
package love.forte.simbot.processor

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Context


/**
 * 一个 **处理器** 接口。
 * 处理器规定一个实现拥有 **进行处理** 的能力，提供一个[处理上下文][C] 并得到一个 [处理结果][R]。
 *
 * @since 2.0.0
 */
public interface Processor<T, C : Context<T>, R> {
    /**
     * 提供一个 [处理上下文][processContext] 并进行处理，得到一个[结果][R]。
     */
    @Throws(Exception::class)
    fun processor(processContext: C): R
}


/**
 *一个提供了 `suspend` 函数的 [处理器][Processor].
 *
 * [Processor] 中原本的 [processor] 函数则默认使用阻塞的 [suspendableProcessor].
 *
 */
public interface SuspendableProcessor<T, C : Context<T>, R> : Processor<T, C, R> {

    /**
     * 阻塞的 [suspendableProcessor].
     */
    @JvmDefault
    override fun processor(processContext: C): R = runBlocking { suspendableProcessor(processContext) }

    /**
     * 提供一个 [处理上下文][processContext] 并进行处理，得到一个[结果][R]。
     */
    suspend fun suspendableProcessor(processContext: C): R
}





@JvmSynthetic
public operator fun <T, C : Context<T>, R> Processor<T, C, R>.invoke(context: C): R = this.processor(context)
