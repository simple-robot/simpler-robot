/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     SimbotQuartzConfiguration.java
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

package love.forte.simbot.timer.quartz.configuration;

import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.ioc.annotation.SpareBeans;
import love.forte.simbot.timer.quartz.SchedulerTimerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * quartz配置类。
 * @author ForteScarlet
 */
@ConfigBeans
public class QuartzConfiguration {

    @SpareBeans("schedulerFactory")
    public SchedulerFactory schedulerFactory() {
        return new StdSchedulerFactory();
    }

    @SpareBeans("scheduler")
    public Scheduler scheduler(SchedulerFactory schedulerFactory) throws SchedulerException {
        return schedulerFactory.getScheduler();
    }

    @SpareBeans("schedulerTimerManager")
    public SchedulerTimerManager schedulerTimerManager(Scheduler scheduler){
        return new SchedulerTimerManager(scheduler);
    }


}
