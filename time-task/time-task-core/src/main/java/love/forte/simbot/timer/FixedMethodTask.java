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
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 周期method任务
 * @author ForteScarlet
 */
public class FixedMethodTask extends MethodTask implements FixedTask {

    private final TimeUnit timeUnit;
    private final long duration;

    public FixedMethodTask(String id, String name, long duration, TimeUnit timeUnit, long repeat, long delay, Method method, Supplier<Object> instanceSupplier) {
        super(id, name, String.valueOf(timeUnit.toMillis(duration)), CycleType.FIXED, repeat, delay, method, instanceSupplier);
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    public FixedMethodTask(String id, String name, long duration, TimeUnit timeUnit, long repeat, Method method, Supplier<Object> instanceSupplier) {
        super(id, name, String.valueOf(timeUnit.toMillis(duration)), CycleType.FIXED, repeat, method, instanceSupplier);
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    public FixedMethodTask(String id, String name, long duration, TimeUnit timeUnit, Method method, Supplier<Object> instanceSupplier) {
        super(id, name, String.valueOf(timeUnit.toMillis(duration)), CycleType.FIXED, method, instanceSupplier);
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    /**
     * 固定周期时长的时间类型。
     *
     * @return 时间类型。
     */
    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    /**
     * 固定的时间时长。
     *
     * @return 时间周期
     */
    @Override
    public long duration() {
        return duration;
    }

}
