/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     BaseCronTask.java
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
 * {@link CronTask} 基础实现
 * @author ForteScarlet
 */
public abstract class BaseCronTask extends BaseTask implements CronTask {


    protected BaseCronTask(String id, String name, String cron, long repeat, long delay) {
        super(id, name, cron, CycleType.CRON, repeat, delay);
    }

    protected BaseCronTask(String id, String name, String cron, long repeat) {
        super(id, name, cron, CycleType.CRON, repeat);
    }

    protected BaseCronTask(String id, String name, String cron) {
        super(id, name, cron, CycleType.CRON);
    }

    /**
     * 时间周期为一个 {@code cron} 表达式字符串。
     * @return cron 表达式字符串。
     */
    @Override
    public String cron() {
        return cycle();
    }

}
