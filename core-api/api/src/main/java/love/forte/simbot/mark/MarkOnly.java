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

/**
 *
 * 仅用于标记的注解，标记在“仅用于标记”的注解上。
 *
 * @author ForteScarlet
 */
@Inherited
@MarkOnly
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MarkOnly {
}
