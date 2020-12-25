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

package love.forte.simbot.timer;

/**
 * 可变的定时任务管理器。
 *
 * 此接口继承自 {@link TimerManager}, 提供可以动态增减 {@link Task} 的能力。
 *
 * @author ForteScarlet
 */
public interface MutableTimerManager extends TimerManager {

    /**
     * 添加/注册一个 task。首次任务立即执行。
     * @param task task
     * @return 是否添加成功。如果失败，
     * 一般可能为task已经无效、
     * task已经开始 ({@link Task#isStarted()})、
     * task已经结束 ({@link Task#isEnded()})、task寿命({@link Task#repeat()}) 无效等。
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException {@link Task#cycle()} 解析错误。
     */
    boolean addTask(Task task);


    /**
     * 添加/注册一个 task，并延迟指定时间后执行。
     * @param task task
     * @param delay 延迟时间。
     * @return 是否添加成功。如果失败，
     * 一般可能为task已经无效、
     * task已经开始 ({@link Task#isStarted()})、
     * task已经结束 ({@link Task#isEnded()})、task寿命({@link Task#repeat()}) 无效等。
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException {@link Task#cycle()} 解析错误。
     */
    boolean addTask(Task task, long delay);


    /**
     * 移除/停止一个任务。
     * 移除的同时会尝试终止此任务之后的执行。
     *
     * @param id ID
     * @return 如果存在任务，返回被终止的任务，否则返回 {@code null}。
     */
    Task removeTask(String id);


}
