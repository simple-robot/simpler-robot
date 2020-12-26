/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     SimbotQuartzJob.java
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

package love.forte.simbot.timer.quartz;

import love.forte.simbot.timer.Task;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;

import static love.forte.simbot.timer.quartz.SchedulerTimerManager.LOG_KEY;
import static love.forte.simbot.timer.quartz.SchedulerTimerManager.TASK_KEY;

/**
 * @author ForteScarlet
 */
public final class QuartzJob implements Job {
    /**
     *
     */
    @Override
    public void execute(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        Task task = (Task) jobDataMap.get(TASK_KEY);

        // DependBeanFactory dependBeanFactory = (DependBeanFactory) jobDataMap.get(B_F_KEY);
        // ExceptionProcessor exceptionProcessor = (ExceptionProcessor) jobDataMap.get(E_P_KEY);

        Logger logger = (Logger) jobDataMap.get(LOG_KEY);

        try {
            task.execute();
        } catch (Exception e) {
            logger.error("Timed task ["+ jobDetail.getKey() +"] is abnormal", e);
        }


    }
}
