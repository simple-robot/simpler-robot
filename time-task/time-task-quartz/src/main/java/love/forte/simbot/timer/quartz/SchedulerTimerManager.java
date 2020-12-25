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

import love.forte.simbot.timer.MutableTimerManager;
import love.forte.simbot.timer.Task;
import org.quartz.Scheduler;

import java.util.Collection;

/**
 * 基于 {@link org.quartz.Scheduler} 的定时任务管理器。
 * @author ForteScarlet
 */
public class SchedulerTimerManager implements MutableTimerManager {

    private final Scheduler scheduler;

    public SchedulerTimerManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    /**
     * 添加/注册一个 task。首次任务立即执行。
     *
     * @param task task
     * @return 是否添加成功。如果失败，
     * 一般可能为task已经无效、
     * task已经开始 ({@link Task#isStarted()})、
     * task已经结束 ({@link Task#isEnded()})、task寿命({@link Task#life()}) 无效等。
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException    {@link Task#cycle()} 解析错误。
     */
    @Override
    public boolean addTask(Task task) {
        return false;
    }

    /**
     * 添加/注册一个 task，并延迟指定时间后执行。
     *
     * @param task  task
     * @param delay 延迟时间。
     * @return 是否添加成功。如果失败，
     * 一般可能为task已经无效、
     * task已经开始 ({@link Task#isStarted()})、
     * task已经结束 ({@link Task#isEnded()})、task寿命({@link Task#life()}) 无效等。
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException    {@link Task#cycle()} 解析错误。
     */
    @Override
    public boolean addTask(Task task, long delay) {
        // 判断是否为周期时间
        String cycle = task.cycle();
        return false;
    }

    /**
     * 移除/停止一个任务。
     * 移除的同时会尝试终止此任务之后的执行。
     *
     * @param id ID
     * @return 如果存在任务，返回被终止的任务，否则返回 {@code null}。
     */
    @Override
    public Task removeTask(String id) {
        return null;
    }

    /**
     * 获取当前定时任务中的任务列表。
     *
     * @return 当前已注册的定时任务列表。
     */
    @Override
    public Collection<? extends Task> taskList() {
        return null;
    }

    /**
     * 根据ID获取一个对应的task实例。
     *
     * @param id task id
     * @return 如果存在，返回task实例，否则得到 {@code null}。
     */
    @Override
    public Task getTask(String id) {
        return null;
    }
}
