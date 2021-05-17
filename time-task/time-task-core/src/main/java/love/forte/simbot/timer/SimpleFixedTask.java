/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     SimpleFixedTask.java
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

package love.forte.simbot.timer;

import java.util.concurrent.TimeUnit;

/**
 *
 * 简易的固定延时任务实现。
 *
 * @see Task
 * @see BaseFixedTask
 *
 * @author ForteScarlet
 */
public class SimpleFixedTask extends BaseFixedTask {

    private final TaskRunner runTask;

    SimpleFixedTask(String id, String name, long duration, TimeUnit timeUnit, long repeat, long delay, TaskRunner taskRunner) {
        super(id, name, duration, timeUnit, repeat, delay);
        this.runTask = taskRunner;
    }

    SimpleFixedTask(String id, String name, long duration, TimeUnit timeUnit, long repeat, TaskRunner runTask) {
        super(id, name, duration, timeUnit, repeat);
        this.runTask = runTask;
    }

    SimpleFixedTask(String id, String name, long duration, TimeUnit timeUnit, TaskRunner runTask) {
        super(id, name, duration, timeUnit);
        this.runTask = runTask;
    }

    public static SimpleFixedTask newInstance(String id, String name, long duration, TimeUnit timeUnit, long repeat, long delay, TaskRunner taskRunner) {
        return new SimpleFixedTask(id, name, duration, timeUnit, repeat, delay, taskRunner);
    }

    /**
     * 执行一个任务。这个任务没有返回值，也没有参数。
     *
     * @throws Exception 可能会存在任何异常。
     */
    @Override
    public void execute() throws Exception {
        runTask.execute();
    }
}
