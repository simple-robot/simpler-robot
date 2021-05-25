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

import love.forte.common.configuration.annotation.AsConfig;
import love.forte.common.configuration.annotation.ConfigInject;
import love.forte.common.ioc.DependBeanFactory;
import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.ioc.annotation.SpareBeans;
import love.forte.simbot.exception.ExceptionProcessor;
import love.forte.simbot.timer.quartz.SchedulerTimerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * quartz配置类。
 *
 * @author ForteScarlet
 */
@SuppressWarnings({"FieldMayBeFinal", "unused"})
@ConfigBeans
@AsConfig(prefix = "simbot.timeTask.quartz")
public class QuartzConfiguration {

    /*
    org.quartz.scheduler.instanceName: DefaultQuartzScheduler
    org.quartz.scheduler.rmi.export:   false
    org.quartz.scheduler.rmi.proxy:    false
    org.quartz.scheduler.wrapJobExecutionInUserTransaction: false
     */

    @ConfigInject("scheduler.rmi.export")
    private Boolean rmiExport = false;
    @ConfigInject("scheduler.rmi.proxy")
    private Boolean rmiProxy = false;
    @ConfigInject("scheduler.wrapJobExecutionInUserTransaction")
    private Boolean schedulerWrapJobExecutionInUserTransaction = false;

    @ConfigInject("scheduler.makeSchedulerThreadDaemon")
    private Boolean makeSchedulerThreadDaemon = true;
    @ConfigInject("scheduler.skipUpdateCheck")
    private Boolean skipUpdateCheck = true;

    /*
    org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
    org.quartz.threadPool.threadCount: 10
    org.quartz.threadPool.threadPriority: 5
    org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true
     */

    @ConfigInject("threadPool.class")
    private String threadPoolClass = "org.quartz.simpl.SimpleThreadPool";
    @ConfigInject("threadPool.threadCount")
    private Integer threadPoolThreadCount = 10;
    @ConfigInject("threadPool.threadPriority")
    private Integer threadPoolThreadPriority = 5;
    @ConfigInject("threadPool.threadsInheritContextClassLoaderOfInitializingThread")
    private Boolean threadPoolThreadsInheritContextClassLoaderOfInitializingThread = true;


    @ConfigInject
    private String jobStoreClass = "org.quartz.simpl.RAMJobStore";


    @SpareBeans("schedulerFactory")
    public SchedulerFactory schedulerFactory() throws SchedulerException {
        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
        Properties defaultProperties = new Properties();

        defaultProperties.setProperty("org.quartz.scheduler.instanceName", "SimbotQuartzScheduler");
        defaultProperties.setProperty("org.quartz.scheduler.rmi.export", String.valueOf(rmiExport));
        defaultProperties.setProperty("org.quartz.scheduler.rmi.proxy", String.valueOf(rmiProxy));
        defaultProperties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", String.valueOf(schedulerWrapJobExecutionInUserTransaction));

        defaultProperties.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon", String.valueOf(makeSchedulerThreadDaemon));
        defaultProperties.setProperty("org.quartz.scheduler.skipUpdateCheck", String.valueOf(skipUpdateCheck));
        defaultProperties.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon", String.valueOf(makeSchedulerThreadDaemon));
        defaultProperties.setProperty("org.quartz.scheduler.skipUpdateCheck", String.valueOf(skipUpdateCheck));


        defaultProperties.setProperty("org.quartz.threadPool.class", String.valueOf(threadPoolClass));
        defaultProperties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(threadPoolThreadCount));
        defaultProperties.setProperty("org.quartz.threadPool.threadPriority", String.valueOf(threadPoolThreadPriority));
        defaultProperties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", String.valueOf(threadPoolThreadsInheritContextClassLoaderOfInitializingThread));
        defaultProperties.setProperty("org.quartz.jobStore.class", String.valueOf(jobStoreClass));

        stdSchedulerFactory.initialize(defaultProperties);
        return stdSchedulerFactory;
    }


    @SpareBeans("scheduler")
    public Scheduler scheduler(SchedulerFactory schedulerFactory) throws SchedulerException {
        return schedulerFactory.getScheduler();
    }

    @SpareBeans("schedulerTimerManager")
    public SchedulerTimerManager schedulerTimerManager(Scheduler scheduler, DependBeanFactory dependBeanFactory, ExceptionProcessor exceptionProcessor) {
        return new SchedulerTimerManager(scheduler, dependBeanFactory, exceptionProcessor);
    }


}

/*
# Default Properties file for use by StdSchedulerFactory
# to create a Quartz Scheduler Instance, if a different
# properties file is not explicitly specified.
#

org.quartz.scheduler.instanceName: DefaultQuartzScheduler
org.quartz.scheduler.rmi.export: false
org.quartz.scheduler.rmi.proxy: false
org.quartz.scheduler.wrapJobExecutionInUserTransaction: false

org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount: 10
org.quartz.threadPool.threadPriority: 5
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true

org.quartz.jobStore.misfireThreshold: 60000

org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore
 */