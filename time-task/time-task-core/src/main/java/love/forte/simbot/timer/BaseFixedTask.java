/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     BaseFixedTask.java
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
 * {@link FixedTask} 基础实现
 * @author ForteScarlet
 */
public abstract class BaseFixedTask extends BaseTask implements FixedTask {

    private final long duration;
    private final TimeUnit timeUnit;

    protected BaseFixedTask(String id, String name, long duration, TimeUnit timeUnit, long repeat) {
        super(id, name, String.valueOf(timeUnit.toMillis(duration)), CycleType.FIXED, repeat);
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    protected BaseFixedTask(String id, String name, long duration, TimeUnit timeUnit) {
        super(id, name, String.valueOf(timeUnit.toMillis(duration)), CycleType.FIXED);
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public long duration() {
        return duration;
    }

}
