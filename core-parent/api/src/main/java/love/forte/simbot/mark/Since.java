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

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 有时候可能不同的版本存在不同的更新，此注解用于记录多次更新时的变动等信息。
 * <p>
 * 仅用于标记。
 *
 * @author ForteScarlet
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.CLASS)
@Target({TYPE, FIELD, CONSTRUCTOR, METHOD, PARAMETER})
@Repeatable(Since.SinceList.class)
@Documented
@MarkOnly
public @interface Since {
    /**
     * 版本
     */
    String value() default "";

    /**
     * 说明
     */
    String[] desc() default {};

    /**
     * 时间
     */
    String time() default "";


    @Retention(RetentionPolicy.CLASS)
    @Target({TYPE, FIELD, CONSTRUCTOR, METHOD, PARAMETER})
    @Documented
    @MarkOnly
    @SuppressWarnings("AlibabaClassMustHaveAuthor")
    @interface SinceList {
        Since[] value() default {};
    }
}

