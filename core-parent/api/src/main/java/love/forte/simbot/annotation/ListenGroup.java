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

import java.lang.annotation.*;

/**
 *
 * 为一个监听函数提供组别。
 *
 * 如果标记在类上，则类下所有的监听函数都会被归类为此分类下。
 *
 *
 * @author ForteScarlet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
public @interface ListenGroup {

    /**
     * 对标记的监听函数进行的分组。
     * 如果为空，视为不存在。
     */
    String[] value();


    /**
     * 如果此注解标记在方法上的同时，其所在的类也存在此注解，那么是否为追加标记。
     *
     * 默认为是，即最终的分组结果会是类上标记的 + 当前标记的。
     *
     * 如果为false，那么将只会留下当前标记的。
     */
    boolean append() default true;

}
