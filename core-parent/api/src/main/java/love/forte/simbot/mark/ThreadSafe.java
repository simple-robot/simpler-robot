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

package love.forte.simbot.mark;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 用于标记一个东西是线程安全的，可能会标记一个类，或者一个方法。
 * 此注解仅用作标记，存在于源码而非运行时。
 * @author ForteScarlet
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.CLASS)
@Target({TYPE, FIELD, CONSTRUCTOR, METHOD, PARAMETER})
@Documented
public @interface ThreadSafe {
    /** 可以有一些说明之类的东西。 */
    String value() default "";
}
