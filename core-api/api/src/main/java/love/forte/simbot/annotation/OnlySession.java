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

import love.forte.simbot.filter.MatchType;
import love.forte.simbot.filter.MostMatchType;

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
     * 当且仅当下述的 keys 存在的时候，才会触发对应监听函数.
     *
     * @throws IllegalArgumentException 如果为空
     */
    String[] value();

    /**
     * 匹配方式。默认为全等匹配。
     */
    MatchType matchType() default MatchType.EQUALS;

    /**
     * 多值匹配方式，默认为any。
     */
    MostMatchType mostMatchType() default MostMatchType.ANY;

}
