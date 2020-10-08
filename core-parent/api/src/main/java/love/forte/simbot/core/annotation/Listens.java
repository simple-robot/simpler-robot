/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Listens.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.annotation;

import love.forte.simbot.core.constant.PriorityConstant;

import java.lang.annotation.*;

/**
 * 标注一个监听函数。
 *
 * 只能标注在方法上。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Listens {

    /**
     * 监听的消息类型。可以监听多个
     */
    Listen[] value();

    /**
     * 优先级。
     * 假如出现了多个监听器处理同一个消息，使用此参数对其进行排序。
     * 默认为 {@link PriorityConstant#TENTH}
     */
    int priority() default PriorityConstant.TENTH;

    /**
     * 当前监听函数的id。不可重复。
     */
    String name() default "";
}
