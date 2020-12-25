/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Cron.java
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

/**
 * Cron 表达式对应的定时任务。
 * @author ForteScarlet
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cron {
    /**
     * cron表达式。
     */
    String value();

    /**
     * 延迟执行。
     */
    long delay() default 0;

    /**
     * 最大重复次数。如果 <= 0则视为无限次数。
     */
    long repeat() default 0;
    
}
