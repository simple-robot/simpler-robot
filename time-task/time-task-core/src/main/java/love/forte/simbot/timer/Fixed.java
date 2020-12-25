/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Fixed.java
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

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间周期的定时任务。
 * @author ForteScarlet
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fixed {

    /**
     * 时间周期。
     */
    long value();

    /**
     * 时间周期的类型。默认毫秒。
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 延迟执行。
     */
    long delay() default 0;

    /**
     * 最大重复次数。如果 <= 0则视为无限次数。
     */
    long repeat() default 0;
}
