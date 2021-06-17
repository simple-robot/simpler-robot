/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Priority.java
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

package love.forte.simbot.annotation;

import love.forte.simbot.constant.PriorityConstant;

import java.lang.annotation.*;

/**
 * 独立的优先级注解，可作用于监听函数上。
 * 此注解使用的优先级高于 {@link Listens#priority()}.
 * @author ForteScarlet
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Priority {
    /**
     * 优先级值。
     */
    int value() default PriorityConstant.TENTH;
}
