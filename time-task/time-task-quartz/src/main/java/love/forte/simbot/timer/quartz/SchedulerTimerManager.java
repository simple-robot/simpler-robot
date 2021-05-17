/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     QuartzMutableTimerManager.java
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

import love.forte.common.ioc.DependBeanFactory;
import love.forte.simbot.LogAble;
import love.forte.simbot.exception.ExceptionProcessor;
import love.forte.simbot.timer.*;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 基于 {@link org.quartz.Scheduler} 的定时任务管理器。
 *
 * @author ForteScarlet
 */
public class SchedulerTimerManager implements TimerManager {

    private final Scheduler scheduler;
    private final DependBeanFactory dependBeanFactory;
    private final ExceptionProcessor exceptionProcessor;

    static final String TASK_KEY = "task";
    static final String LOG_KEY = "logger";
    static final String B_F_KEY = "dependBeanFactory";
    static final String E_P_KEY = "exceptionProcessor";
    static final String GROUP = "simbot-task";

    public SchedulerTimerManager(Scheduler scheduler, DependBeanFactory dependBeanFactory, ExceptionProcessor exceptionProcessor) {
        this.scheduler = scheduler;
        this.dependBeanFactory = dependBeanFactory;
        this.exceptionProcessor = exceptionProcessor;
    }


    /**
     * 添加/注册一个 task。
     *
     * @param task task
     * @return 是否添加成功。如果失败，
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException    {@link Task#cycle()} 解析错误。
     * @throws TimerException           添加到调度器失败。
     */
    @Override
    public boolean addTask(Task task) {
        return addTask(task, task.delay());
    }

    /**
     * 添加/注册一个 task，并延迟指定时间后执行。
     *
     * @param task  task
     * @param delay 延迟时间。
     * @return 是否添加成功。
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException    {@link Task#cycle()} 解析错误。
     * @throws TimerException           添加到调度器失败。
     */
    @Override
    public boolean addTask(Task task, long delay) {
        final String id = task.id();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TASK_KEY, task);
        jobDataMap.put(B_F_KEY, dependBeanFactory);
        jobDataMap.put(E_P_KEY, exceptionProcessor);

        Logger logger;

        if (task instanceof LogAble) {
            logger = ((LogAble) task).getLog();
        } else {
            logger = LoggerFactory.getLogger("love.forte.simbot.timer." + task.id());
        }

        jobDataMap.put(LOG_KEY, logger);

        JobDetail job = JobBuilder.newJob(QuartzJob.class)
                .setJobData(jobDataMap)
                .withIdentity(id, GROUP)
                .withDescription(task.name())
                .setJobData(jobDataMap)
                .build();

        // 判断是否为周期时间
        CycleType cycleType = task.cycleType();

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity("tri_" + job.getKey().getName(), job.getKey().getGroup());

        Trigger trigger;
        long repeat;

        switch (cycleType) {
            case FIXED:
                long millFixed;
                if (task instanceof FixedTask) {
                    FixedTask fixedTask = (FixedTask) task;
                    millFixed = fixedTask.timeUnit().toMillis(fixedTask.duration());
                } else {
                    millFixed = Long.parseLong(task.cycle());
                }

                if (delay > 0) {
                    triggerBuilder.startAt(new Date(System.currentTimeMillis() + delay));
                } else if (delay == 0) {
                    triggerBuilder.startNow();
                } else {
                    // 小于0，则说明不要首次延迟执行
                    // 那么开始触发的时间即为任务下次延迟的时间。
                    triggerBuilder.startAt(new Date(System.currentTimeMillis() + millFixed));
                }

                SimpleScheduleBuilder fixedScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(millFixed);

                repeat = task.repeat();
                if (repeat > 0) {
                    fixedScheduleBuilder.withRepeatCount((int) repeat);
                } else {
                    fixedScheduleBuilder.repeatForever();
                }
                trigger = triggerBuilder.withSchedule(fixedScheduleBuilder).build();

                break;
            case CRON:
                // cron下，触发器没有什么首次执行一说。
                if (delay > 0) {
                    triggerBuilder.startAt(new Date(System.currentTimeMillis() + delay));
                } else {
                    triggerBuilder.startNow();
                }
                String cron;
                if (task instanceof CronTask) {
                    CronTask cronTask = (CronTask) task;
                    cron = cronTask.cron();
                } else {
                    cron = task.cycle();
                }

                CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
                trigger = triggerBuilder.withSchedule(cronScheduleBuilder).build();

                break;


            default:
                throw new IllegalStateException("预期内未知异常-schedulerTimerManager for cycType: " + cycleType);
        }


        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new TimerException("Schedule Job '" + id + "' failed.", e);
        }

        try {
            if (!scheduler.isStarted()) {
                synchronized (scheduler) {
                    if (!scheduler.isStarted()) {
                        scheduler.start();
                    }
                }
            }
        } catch (SchedulerException e) {
            throw new TimerException("Scheduler start failed.", e);
        }

        return true;
    }

    /**
     * 移除/停止一个任务。
     * 移除的同时会尝试终止此任务之后的执行。
     *
     * @param id ID
     * @return 如果存在任务，返回被终止的任务，否则返回 {@code null}。
     */
    @Override
    public boolean removeTask(String id) {
        try {
            return scheduler.deleteJob(new JobKey(id, GROUP));
        } catch (SchedulerException e) {
            throw new TimerException("Delete scheduler Job failed.", e);
        }
    }

    /**
     * 获取当前定时任务中的任务列表。
     *
     * @return 当前已注册的定时任务列表。
     */
    @Override
    public Collection<? extends Task> taskList() {
        try {
            Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.anyGroup());
            List<Task> tasks = new ArrayList<>(keys.size());
            for (JobKey key : keys) {
                JobDetail job = scheduler.getJobDetail(key);
                Task task = (Task) job.getJobDataMap().get(TASK_KEY);
                if (task != null) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (SchedulerException e) {
            throw new TimerException("Can not get scheduler job keys.", e);
        }
    }

    /**
     * 根据ID获取一个对应的task实例。
     *
     * @param id task id
     * @return 如果存在，返回task实例，否则得到 {@code null}。
     */
    @Override
    public Task getTask(String id) {
        JobKey jobKey = new JobKey(id, GROUP);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                return null;
            }
            return (Task) jobDetail.getJobDataMap().get(TASK_KEY);
        } catch (SchedulerException e) {
            throw new TimerException("Can not get job detail by id '" + id + "' of jobKey " + jobKey, e);
        }
    }
}
