/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     CoroutineTaskRunner.kt
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

package love.forte.simbot.core.task

import love.forte.simbot.task.Caller
import love.forte.simbot.task.Runner
import love.forte.simbot.task.Task
import love.forte.simbot.task.TaskRunner
import java.util.concurrent.*



/**
 *
 * [线程池][ExecutorService]任务执行器。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/11/6
 * @since
 */
public class ExecutorServiceTaskRunner(
    private val executor: ExecutorService
) : TaskRunner {
    /**
     * 通过线程池执行一个[无返回值的任务][runner]，不关心其返回值。
     */
    override fun run(runner: Runner) {
        executor.execute(runner)
    }

    /**
     * 执行一个[有返回值的任务][caller]，并在未来可得到它的返回值。
     */
    override fun <V> call(caller: Caller<V>): FutureTask<V> {
        val future: Future<V> = executor.submit(caller)
        return FutureTask(future)
    }

}





/**
 * 基于 [Future] 实现的 [Task] 实例。
 * @param V
 * @property future Future<V>
 * @constructor
 */
public class FutureTask<V>(val future: Future<V>) : Task<V> {
    /**
     * 尝试终止当前执行的任务。
     */
    override fun shutdown(): Boolean = future.cancel(true)

    /**
     * 任务是否已经开始执行。
     * [Future] 没有提供判断是否开始执行的方法，因此：
     * 如果future的 `isDown` 和 `isCancelled` 都为false，则认为其已经开始。
     */
    override fun isStarted(): Boolean = !future.isCancelled && !future.isDone

    /**
     * 任务是否已经结束。
     * 如果此值为 `true`, 则 [isStarted] 必然为 `true`。
     */
    override fun isDone(): Boolean = future.isDone

    /**
     * 等待任务执行结束并获取其结果。
     * @throws InterruptedException 如果在等待的时候任务被中断。
     * @throws ExecutionException 如果任务抛出了异常。
     */
    @Throws(InterruptedException::class, ExecutionException::class)
    override fun await(): V = future.get()

    /**
     * 在一段时间内等待结果。
     *
     * @throws TimeoutException 如果超时。
     * @throws InterruptedException 如果在等待的时候任务被中断。
     * @throws ExecutionException 如果任务抛出了异常。
     */
    @Throws(TimeoutException::class, InterruptedException::class, ExecutionException::class)
    override fun await(time: Long): V = future.get(time, TimeUnit.MILLISECONDS)

}
