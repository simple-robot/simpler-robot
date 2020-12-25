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

import java.util.concurrent.TimeUnit;

/**
 *
 * 一个固定周期时长的任务。
 * FixedDuration
 * @author ForteScarlet
 */
public interface FixedTask extends Task {


    /**
     * 固定周期时长的时间类型。
     * @return 时间类型。
     */
    TimeUnit timeUnit();

    /**
     * 固定的时间时长。
     * @return 时间周期
     */
    long duration();


    /**
     * 时间周期信息表现为 {@link #duration()} 的毫秒值。
     * @return 固定时间周期长度。
     */
    @Override
    default String cycle() {
        return String.valueOf(timeUnit().toMillis(duration()));
    }


    /**
     * 周期类型。固定为 {@link CycleType#FIXED}
     * @return type
     */
    @Override
    default CycleType cycleType() {
        return CycleType.FIXED;
    }
}
