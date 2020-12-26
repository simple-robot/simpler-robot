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


import java.util.Collection;

/**
 * 定时任务管理器，用于管理已经注册的定时任务。
 *
 *
 * @author ForteScarlet
 */
public interface TimerManager {

    /**
     * 获取当前定时任务中的任务列表。
     *
     * @return 当前已注册的定时任务列表。
     */
    Collection<? extends Task> taskList();


    /**
     * 根据ID获取一个对应的task实例。
     * @param id task id
     * @return 如果存在，返回task实例，否则得到 {@code null}。
     */
    Task getTask(String id);



    /**
     * 添加/注册一个 task。首次任务立即执行。
     * @param task task
     * @return 是否添加成功。
     * @throws IllegalArgumentException 如果ID已经存在。
     * @throws IllegalStateException {@link Task#cycle()} 解析错误。
     */
    boolean addTask(Task task);


    /**
     * 添加/注册一个 task，并延迟指定时间后执行。
     * @param task task
     * @param delay 延迟时间。
     * @return 是否添加成功。
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
    boolean removeTask(String id);


}
