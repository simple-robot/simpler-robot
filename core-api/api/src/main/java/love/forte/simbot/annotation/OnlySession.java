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

import java.lang.annotation.*;

/**
 *
 * 标记用注解，用于标记当前监听函数是否只针对特定的会话事件出现回应。
 *
 * @author ForteScarlet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface OnlySession {

    /**
     * 持续会话容器的分组。
     */
    String group();


    /**
     * 此分组下持续会话的key。如果未指定，则只要存在指定分组的会话容器就通过。
     * 否则，根据key精准匹配。
     */
    String key() default "";
}
