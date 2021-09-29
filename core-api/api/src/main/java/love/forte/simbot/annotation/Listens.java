/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.annotation;

import love.forte.common.utils.annotation.MixRepeatableAnnotations;
import love.forte.simbot.constant.PriorityConstant;

import java.lang.annotation.*;

/**
 * 标注一个监听函数。
 *
 * 只能标注在方法上。
 *
 * 具体可监听内容等说明请参考 {@link Listen} 注解内注释。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@MixRepeatableAnnotations
public @interface Listens {

    /**
     * {@link Listens} 的默认优先级。
     */
    int DEFAULT_PRIORITY = PriorityConstant.TENTH;

    //// ////

    /**
     * 监听的消息类型。可以监听多个
     */
    Listen[] value() default {};

    /**
     * 优先级。
     * 假如出现了多个监听器处理同一个消息，使用此参数对其进行排序。
     * 默认为 {@link PriorityConstant#TENTH}.
     *
     * 如果监听函数上存在 {@link Priority}, 则优先使用{@link Priority}.
     *
     * @see Priority
     *
     */
    int priority() default DEFAULT_PRIORITY;

    /**
     * 当前监听函数的ID。不可重复。
     */
    String id() default "";

    /**
     * 当前监听函数的名称（简称）。
     */
    String name() default "";


}
