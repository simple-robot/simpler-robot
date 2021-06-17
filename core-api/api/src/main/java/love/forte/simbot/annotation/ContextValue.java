/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     FromContext.java
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

import love.forte.simbot.api.SimbotExperimentalApi;
import love.forte.simbot.listener.ListenerContext;

import java.lang.annotation.*;

/**
 *
 * 指定方法参数从监听函数上下文所获取的参数名称与作用域。
 *
 * 需要注意的是，通过 {@link ContextValue} 从上下文中获取到的值，
 *
 * 目前将不会进行类型转化。因此请保证类型正确。
 *
 * @author ForteScarlet
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@SimbotExperimentalApi
public @interface ContextValue {

    /**
     * 从上下文中所需要寻找的值的key。
     *
     */
    String value();

    /**
     * 所寻找的作用域。 会根据数组中提供的顺序进行寻找。
     *
     * 默认情况下为仅查找当前的 {@link ListenerContext.Scope#EVENT_INSTANT 事件瞬时} 作用域。
     *
     */
    ListenerContext.Scope[] scopes() default {ListenerContext.Scope.EVENT_INSTANT};


    /**
     * 如果没有找到对应的值，是否直接尝试注入null。
     */
    boolean orNull() default false;


}
