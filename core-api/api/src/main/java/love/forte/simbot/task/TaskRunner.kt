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

package love.forte.simbot.task

import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

/**
 * 用于 [TaskRunner] 的 无返回值函数，定义其执行一个任务。
 * 实现 [Runnable]。
 */
public fun interface Runner : Runnable {
    public operator fun invoke()

    /**
     * override from [Runnable.run].
     */
    
    override fun run() {
        this()
    }
}

/**
 * 用于 [TaskRunner] 的 有返回值函数，定义其执行一个任务，并在未来可以得到一个结果。
 * 实现 [Callable]。
 */
public fun interface Caller<V> : Callable<V> {
    @Throws(Exception::class)
    public operator fun invoke(): V


    /**
     * override from [Callable.call]
     */
    override fun call(): V = this()
}


/**
 * 一个任务，可等待获取并其返回值。
 *
 */
public interface Task<out V> {

    /**
     * 尝试终止当前执行的任务。
     */
    @Throws(Exception::class)
    fun shutdown(): Boolean

    /**
     * 任务是否已经开始执行。
     */
    fun isStarted(): Boolean

    /**
     * 任务是否已经结束。
     * 如果此值为 `true`, 则 [isStarted] 必然为 `true`。
     */
    fun isDone(): Boolean

    /**
     * 等待任务执行结束并获取其结果。
     */
    @Throws(Exception::class)
    fun await(): V

    /**
     * 在一段时间内等待结果。
     *
     * @throws TimeoutException 如果超时。
     */
    @Throws(Exception::class)
    fun await(time: Long): V

}



/**
 * 一个简易的任务执行器，默认提供 **线程**、**协程**、**单线程阻塞** 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface TaskRunner {

    /**
     * 执行一个[无返回值的任务][runner]，不关心其返回值。
     */
    fun run(runner: Runner)

    /**
     * 执行一个[有返回值的任务][caller]，并在未来可得到它的返回值。
     */
    fun <V> call(caller: Caller<V>): Task<V>

}
