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


import com.sun.istack.internal.Nullable;

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



}
