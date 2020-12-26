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

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * cron表达式类型的方法任务
 * @author ForteScarlet
 */
public class CronMethodTask extends MethodTask implements CronTask {

    private final String corn;

    public CronMethodTask(String id, String name, String cron, long repeat, long delay, Method method, Supplier<Object> instanceSupplier) {
        super(id, name, cron, CycleType.CRON, repeat, delay, method, instanceSupplier);
        this.corn = cron;
    }

    public CronMethodTask(String id, String name, String cron, long repeat, Method method, Supplier<Object> instanceSupplier) {
        super(id, name, cron, CycleType.CRON, repeat, method, instanceSupplier);
        this.corn = cron;
    }

    public CronMethodTask(String id, String name, String cron, Method method, Supplier<Object> instanceSupplier) {
        super(id, name, cron, CycleType.CRON, method, instanceSupplier);
        this.corn = cron;
    }

    /**
     * 时间周期为一个 {@code cron} 表达式字符串。
     *
     * @return cron 表达式字符串。
     */
    @Override
    public String cron() {
        return corn;
    }

}
