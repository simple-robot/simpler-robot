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
package love.forte.simbot.task

/**
 *
 * 阻塞任务执行器，非异步执行，本质上就是直接调用方法内容。
 *
 *
 * @author ForteScarlet <ForteScarlet></ForteScarlet>@163.com>
 * @date 2020/11/6
 * @since
 */
public class SynchronousBlockingTaskRunner : TaskRunner {
    /**
     * 阻塞执行一个[无返回值的任务][runner]，不关心其返回值。
     */
    override fun run(runner: Runner) {
        runner()
    }

    /**
     * 阻塞执行一个[有返回值的任务][caller]，并获取其返回值并封装为 [Task]。
     */
    override fun <V> call(caller: Caller<V>): Task<V> = SynchronousBlockingTask(caller())
}

/**
 * 同步阻塞任务。此任务是 [SynchronousBlockingTaskRunner] 执行后所得的结果封装，
 * 不存在异步执行内容。
 */
private class SynchronousBlockingTask<V>(private val result: V) : Task<V> {
    /**
     * blocking task不存在异步执行，因此无法被中途结束，返回值永远为true。
     */
    override fun shutdown(): Boolean = true

    /**
     * 同步阻塞任务总是开始过的。
     */
    override fun isStarted(): Boolean = true

    /**
     * 同步阻塞任务总是已经完成的。
     */
    override fun isDone(): Boolean = true

    /**
     * 同步任务总是能直接获取执行结果。
     */
    override fun await(): V = result

    /**
     * 在一段时间内等待结果。
     */
    override fun await(time: Long): V = await()
}
