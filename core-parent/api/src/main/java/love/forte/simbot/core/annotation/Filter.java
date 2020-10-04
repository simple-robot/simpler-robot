/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Filter.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.annotation;

import love.forte.simbot.core.filter.MatchType;

import java.lang.annotation.*;

/**
 *
 * 注解过滤器，通过一些简单的消息匹配规则以实现对监听消息进行过滤。
 *
 * <p> 这种可复数的注解暂时不支持注解继承。如要继承，请尝试直接继承其对应上级注解 {@link Filters} </p>
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Repeatable(Filters.class)
public @interface Filter {
    /**
     * 匹配关键词内容。比如一个正则，或者一个equals字符串。
     * 如果为空字符串则忽略此参数。
     */
    String value() default "";


    /**
     * 匹配模式，默认为相等匹配。
     */
    MatchType matchType() default MatchType.EQUALS;

    /**
     * 匹配这段消息的账号列表。
     * 如果为空, 且{@link #codesByParent()} 为true，则尝试使用 {@link Filters#codes()}。
     * 如果仍为空，则对任何账号都匹配生效。
     * 如果对应消息无法获取账号信息，则此参数失效。
     *
     */
    String[] codes() default {};


    /**
     * 是否尝试使用父注解的codes。
     * 如果为true，则当 {@link #codes()} 为空，则尝试优先使用 {@link Filters#codes()}。
     */
    boolean codesByParent() default true;


    /**
     * 匹配当前消息的群列表。
     * 如果为空, 且{@link #groupsByParent()} 为true，则尝试使用 {@link Filters#groups()}。
     * 如果仍为空，则对任何群都匹配生效。
     * 如果对应消息无法获取群账号信息，则此参数失效。
     */
    String[] groups() default {};


    /**
     * 是否尝试使用父注解的groups。
     * 如果为true，则当 {@link #groups()} 为空，则尝试优先使用 {@link Filters#groups()}。
     */
    boolean groupsByParent() default true;


    /**
     * 匹配当前消息的bot列表。
     * 如果为空, 且{@link #botsByParent()} 为true，则尝试使用 {@link Filters#bots()}。
     * 如果仍为空，则对任何bot都匹配生效。
     */
    String[] bots() default {};


    /**
     * 是否尝试使用父注解的bots。
     * 如果为true，则当 {@link #bots()} 为空，则尝试优先使用 {@link Filters#bots()}。
     */
    boolean botsByParent() default true;


    /**
     * 匹配前是否去除前后空格。
     */
    boolean trim() default false;


    /**
     * 是否清理其中存在的CAT码。 参数为清理的类型，例如 “at” 之类的。
     */
    String[] clearCat() default {};


    /**
     * 是否清理全部的CAT码。如果为true, 则{@link #clearCat()} 无效。
     */
    boolean clearAllCat() default false;

}
