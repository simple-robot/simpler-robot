/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Filters.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.annotation;

import love.forte.simbot.filter.ListenerFilter;
import love.forte.simbot.filter.MostMatchType;

import java.lang.annotation.*;

/**
 * 监听函数过滤器。以注解的形式对监听函数进行匹配与过滤。
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD}) //接口、类、枚举、注解、方法
@Documented
public @interface Filters {

    /**
     * 过滤注解。可以有多个。
     */
    Filter[] value() default {};

    /**
     * {@link #value()} 的多项匹配规则，默认为任意匹配。
     */
    MostMatchType mostMatchType() default MostMatchType.ANY;

    /**
     * 此处为自定义过滤器的列表。
     * @see ListenerFilter
     */
    String[] customFilter() default {};

    /**
     * {@link #customFilter()} 的多项匹配规则，默认为任意匹配。
     */
    MostMatchType customMostMatchType() default MostMatchType.ANY;


    /**
     * 匹配当前消息的账号列表。
     * 如果{@link Filter}中的 {@link Filter#codes()} 为空且 {@link Filter#codesByParent()},
     * 则尝试使用此codes代替此filter的codes。
     */
    String[] codes() default {};


    /**
     * 匹配当前消息的群列表。
     * 如果{@link Filter}中的 {@link Filter#groups()} 为空且 {@link Filter#groupsByParent()},
     * 则尝试使用此groups代替此filter的groups。
     */
    String[] groups() default {};

    /**
     * 匹配当前消息的bot列表。
     * 如果{@link Filter}中的 {@link Filter#bots()} 为空且 {@link Filter#botsByParent()},
     * 则尝试使用此bots代替此filter的bots。
     */
    String[] bots() default {};

    /**
     * 当bot被at的时候才会触发。
     */
    boolean atBot() default false;

    /**
     * 有人被at了才会触发。其中可能不包括bot自身。
     * 如果此为true，则{@link #at()} 失效。
     */
    boolean anyAt() default false;

    /**
     * 当下列账号中的人被at了才会触发。
     * 如果 {@link #anyAt()} 为true则失效。
     */
    String[] at() default {};




}
