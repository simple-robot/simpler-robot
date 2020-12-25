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
 * 一个定时任务。
 *
 * 定时任务是一个可以重复执行有限或无限次数的任务。
 *
 * 任务没有参数，也没有返回值，但是它应当可以与依赖注入相互结合。
 *
 * 实现时需要保证 {@link #equals(Object)} 和 {@link #hashCode()} 是指向 {@link #id()} 的。
 *
 * @author ForteScarlet
 */
public interface Task {

    /**
     * 这个定时任务的ID。ID是不可重复的，用于被 {@link TimerManager} 进行检索。
     * @return 任务的唯一标识。
     */
    String id();

    /**
     * 这个定时任务的名称。
     *
     * 定时任务的名称是可以重复的，一般会与 {@link #id()} 配合进行日志输出等。
     *
     * @return 定时任务的名称。
     */
    String name();

    /**
     * 这个任务的周期信息。例如一个固定时间间隔或者一个cron表达式。
     * @return 时间周期。
     */
    String cycle();

    /**
     * 周期类型。目前为 fixed 和 cron
     * @return type
     */
    CycleType cycleType();

    /**
     * 执行一个任务。这个任务没有返回值，也没有参数。
     *
     * @throws Exception 可能会存在任何异常。
     */
    void execute() throws Exception;


    /**
     * 这个任务的最大重复执行次数。
     *
     * @return 返回最大重复执行次数。如果为无限次数，返回 {@code -1}。
     */
    long repeat();

}
