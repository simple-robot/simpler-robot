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

package love.forte.simbot.core.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.ConfigBeans
import java.util.concurrent.*


/**
 * 线程池配置信息。
 */
@AsConfig(prefix = "simbot.core.task.pool")
@ConfigBeans
public class ExecutorServiceProperties {

    /**
     * 核心池的大小
     * TODO 根据CPU计算
     */
    @field:ConfigInject
    var corePoolSize = 1


    /**
     * 线程池最大线程数，这个参数也是一个非常重要的参数，它表示在线程池中最多能创建多少个线程；
     * TODO 根据CPU计算
     */
    @field:ConfigInject
    var maximumPoolSize = 4

    /**
     * 表示线程没有任务执行时最多保持多久时间会终止。
     * 默认情况下，只有当线程池中的线程数大于corePoolSize时，keepAliveTime才会起作用，
     * 直到线程池中的线程数不大于corePoolSize，即当线程池中的线程数大于corePoolSize时，
     * 如果一个线程空闲的时间达到keepAliveTime，则会终止，直到线程池中的线程数不超过corePoolSize。
     * 但是如果调用了allowCoreThreadTimeOut(boolean)方法，在线程池中的线程数不大于corePoolSize时，keepAliveTime参数也会起作用，
     * 直到线程池中的线程数为0；
     */
    @ConfigInject
    var keepAliveTime: Long = 60 * 1000

    /**
     * 默认为毫秒值
     * unit：参数keepAliveTime的时间单位，有7种取值，在TimeUnit类中有7种静态属性:
     * TimeUnit.DAYS;              //天
     * TimeUnit.HOURS;             //小时
     * TimeUnit.MINUTES;           //分钟
     * TimeUnit.SECONDS;           //秒
     * TimeUnit.MILLISECONDS;      //毫秒
     * TimeUnit.MICROSECONDS;      //微妙
     * TimeUnit.NANOSECONDS;       //纳秒
     */
    @ConfigInject(orDefault = ["MILLISECONDS"])
    lateinit var timeUnit: TimeUnit

    /**
     * 一个阻塞队列，用来存储等待执行的任务，这个参数的选择也很重要，
     * 会对线程池的运行过程产生重大影响，一般来说，这里的阻塞队列有以下几种选择：
     * ArrayBlockingQueue;
     * LinkedBlockingQueue;
     * SynchronousQueue;
     * ArrayBlockingQueue和PriorityBlockingQueue使用较少，一般使用LinkedBlockingQueue和Synchronous。
     * 线程池的排队策略与BlockingQueue有关。
     */
    var workQueue: BlockingQueue<Runnable> = SynchronousQueue()


    /**
     * 线程工厂，主要用来创建线程；
     */
    var threadFactory = ThreadFactory { Thread(it) }


    /**
     * 通过上述参数构建一个 [ExecutorService] 实例。
     * @return ExecutorService ]
     */
    fun createExecutorService(): ExecutorService {
        return ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            timeUnit,
            workQueue,
            threadFactory
        )
    }

    override fun toString(): String {
        return "ExecutorServiceProperties(corePoolSize=$corePoolSize, maximumPoolSize=$maximumPoolSize, keepAliveTime=$keepAliveTime, timeUnit=$timeUnit, workQueue=$workQueue, threadFactory=$threadFactory)"
    }

}