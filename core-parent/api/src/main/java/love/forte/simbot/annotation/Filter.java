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

import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.filter.FilterTargetManager;
import love.forte.simbot.filter.FilterTargets;
import love.forte.simbot.filter.MatchType;
import love.forte.simbot.listener.ContextMap;
import love.forte.simbot.listener.ListenerContext;

import java.lang.annotation.*;

/**
 * 注解过滤器，通过一些简单的消息匹配规则以实现对监听消息进行过滤。
 *
 * <p> 这种可复数的注解暂时不支持注解继承。如要继承，请尝试直接继承其对应上级注解 {@link Filters} </p>
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@Repeatable(Filters.class)
public @interface Filter {
    /**
     * 匹配关键词内容。比如一个正则，或者一个equals字符串。
     * 如果为空字符串则忽略此参数。
     */
    String value() default "";


    /**
     * 过滤目标。默认情况下，一个过滤器是通过 {@link MsgGet#getText()} 进行过滤的。
     * 但是可能在某些情况下，你需要通过其他目标进行过滤匹配，例如 {@link MessageGet#getMsg()} 或者 {@link love.forte.simbot.listener.ListenerContext} 中的某个值。
     * <p>
     * 此参数提供一些可选值，以允许你重新指定一个目标过滤源。目前可选值如下：
     * <ul>
     *     <li>{@code text} - 为空或者为{@code 'text'}都指的是使用{@link MsgGet#getText()} 进行过滤。</li>
     *     <li>{@code msg} - {@code 'msg'}使用{@link MessageGet#getMsg()} 进行过滤。</li>
     *     <li>
     *         {@code context.global.[nonnull,nullable].xxx} - {@code 'context.global.xxx'} 使用 {@link ListenerContext#getContextMap()} 中的 {@link ContextMap#getGlobal()}(即全局变量) 进行过滤。
     *         其中的 {@code 'nonnull' 或 'nullable' } 代表其是否可以为null。如果是 {@code nonnull}, 那么当获取到的元素为null的时候，<b>不会</b>通过过滤匹配，反之，如果为 {@code nullable}, 那么如果获取到的元素为null，则<b>会</b>通过匹配 。
     *         {@code 'xxx'} 为 {@link ContextMap#getGlobal()} 中的一个任意的元素值。
     * <p>
     *          例如：{@code @Filter(value = "hello", target = "context.global.nullable.myTarget")}
     *     </li>
     *     <li>
     *         {@code context.instant.[nonnull,nullable].xxx} - 含义与上述的 {@code context.global.[nonnull,nullable].xxx} 基本一致，唯一不同的就是此处是通过 {@link ContextMap#getInstant()} 进行匹配的。
     * <p>
     *         例如：{@code @Filter(value = "hi", target = "context.instant.nullable.myTarget")}
     *     </li>
     * </ul>
     * <p>
     * 上述中，只有通过 {@code context.xxx} 进行匹配的参数才有 {@code nonnull 或 nullable}的选择，{@code text 和 msg}的null选择为核心的默认情况，即如果为null则认为当前消息 <b>不支持进行文本过滤。</b>
     * <p>
     * 提供一个常量类 {@link FilterTargets} 以降低手写的出错概率.
     * <p>
     * 例如：
     * <p>
     * {@code @Filter(target = FilterTargets.MSG) // 使用 .getMsg() 进行过滤。 }
     * <p>
     * {@code @Filter(target = FilterTargets.CONTEXT_GLOBAL_NONNULL + "myTarget") // 使用 getContextMap().getGlobal().get("myTarget") 中的值进行过滤。}
     *
     * 当然，除了上述的可选值以外，你也可以通过 {@link love.forte.common.ioc.annotation.PrePass} 操作 {@link FilterTargetManager#getCheckers()} 中的返回值来添加你的自定义解析器。
     *
     * @return filter target.
     * @see FilterTargets
     * @see FilterTargetManager
     * @see love.forte.simbot.filter.FilterTargetProcessorChecker
     * @see love.forte.simbot.filter.FilterTargetProcessor
     * <p>
     *
     * @since 2.0.0
     *
     */
    String target() default "";

    /**
     * 如果 {@link #target()} 未指定，是否尝试使用 {@link Filters#target()} .
     * @since 2.0.0
     */
    boolean targetByParent() default true;

    /**
     * 匹配模式，默认为相等匹配。
     */
    MatchType matchType() default MatchType.EQUALS;

    /**
     * 匹配这段消息的账号列表。
     * 如果为空, 且{@link #codesByParent()} 为true，则尝试使用 {@link Filters#codes()}。
     * 如果仍为空，则对任何账号都匹配生效。
     * 如果对应消息无法获取账号信息，则此参数失效。
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


}
