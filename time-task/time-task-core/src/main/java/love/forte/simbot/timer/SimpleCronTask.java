/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     SimpleCronTask.java
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

/**
 * @author ForteScarlet
 */
public class SimpleCronTask extends BaseCronTask {

    private final TaskRunner taskRunner;

    SimpleCronTask(String id, String name, String cron, long repeat, long delay, TaskRunner taskRunner) {
        super(id, name, cron, repeat, delay);
        this.taskRunner = taskRunner;
    }

    SimpleCronTask(String id, String name, String cron, long repeat, TaskRunner taskRunner) {
        super(id, name, cron, repeat);
        this.taskRunner = taskRunner;
    }

    SimpleCronTask(String id, String name, String cron, TaskRunner taskRunner) {
        super(id, name, cron);
        this.taskRunner = taskRunner;
    }

    public static SimpleCronTask newInstance(String id, String name, String cron, long repeat, long delay, TaskRunner taskRunner) {
        return new SimpleCronTask(id, name, cron, repeat, taskRunner);
    }


    /**
     * 执行一个任务。这个任务没有返回值，也没有参数。
     *
     * @throws Exception 可能会存在任何异常。
     */
    @Override
    public void execute() throws Exception {
        taskRunner.execute();
    }


}
