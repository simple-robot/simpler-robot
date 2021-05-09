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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解为仅用于标记的注解。标记一个类、方法、属性等为一个 <em>即时加载</em> 的属性。
 * <p>
 * 此注解一般在存在 {@link LazyInit} 注解的时候配合使用以达到对照表现的效果。
 *
 * <p>
 * 即被标记的元素所应体现的结果应当是即时的。
 *
 * @author ForteScarlet
 * @see LazyInit
 */
@MarkOnly
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.CLASS)
public @interface InstantInit {
}
