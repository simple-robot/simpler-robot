/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     TaskRunnerConfiguration.java
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
package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.core.task.ExecutorServiceTaskRunner
import java.util.concurrent.ExecutorService


/**
 * 任务执行器配置类。
 *
 * @author ForteScarlet <ForteScarlet></ForteScarlet>@163.com>
 * @date 2020/11/6
 * @since
 */
@ConfigBeans
public class TaskRunnerConfiguration {


    @CoreBeans
    fun executorService(executorService: ExecutorServiceProperties): ExecutorService = executorService.createExecutorService()


    @CoreBeans
    fun scheduledExecutorServiceTaskRunner(executorService: ExecutorService): ExecutorServiceTaskRunner {
        return ExecutorServiceTaskRunner(executorService)
    }


}















