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

package love.forte.simbot.annotation;

import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.filter.MatchType;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * 注解过滤器，通过一些简单的消息匹配规则以实现对监听消息进行过滤。
 *
 * <p> 这种可复数的注解暂时不支持注解继承。如要继承，请尝试直接继承其对应上级注解 {@link Filters} </p>
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)
// @Target({ElementType.TYPE})
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
     * 当bot被at的时候才会触发。
     */
    boolean atBot() default false;

    /**
     * 如果 {@link #atBot()} 为 false 且此参数为true，
     * 则使用{@link Filters#atBot()} 的值。
     */
    boolean atBotByParent() default true;

    /**
     * 有人被at了才会触发。其中可能不包括bot自身。
     * 如果此为true，则{@link #at()} 失效。
     */
    boolean anyAt() default false;

    /**
     * 如果 {@link #anyAt()} 为 false 且此参数为true，
     * 则使用{@link Filters#anyAt()} 的值。
     */
    boolean anyAtByParent() default true;

    /**
     * 当下列账号中的人被at了才会触发。
     * 如果 {@link #anyAt()} 为true则失效。
     */
    String[] at() default {};

    /**
     * 如果 {@link #at()} 为 空 且此参数为true，
     * 则使用{@link Filters#at()} 的值。
     */
    boolean atByParent() default true;

    /**
     * 匹配前是否去除前后空格。
     */
    boolean trim() default false;


    /**
     * 是否清理其中存在的特殊码，一般为CAT码。 参数为清理的类型，例如 “at” 之类的。
     *
     * TODO 2个版本内将会被删除。
     *
     * @deprecated 使用 {@link MsgGet#getText()} 进行过滤匹配，按照约定其中不应存在任何CAT码。
     */
    @Deprecated
    String[] clearCode() default {};


    /**
     * 是否清理全部的特殊码，一般为CAT码。如果为true, 则{@link #clearCode()} 无效。
     *
     * TODO 2个版本内将会被删除。
     *
     * @deprecated 使用 {@link MsgGet#getText()} 进行过滤匹配，按照约定其中不应存在任何CAT码。
     */
    @Deprecated
    boolean clearAllCode() default false;

}
