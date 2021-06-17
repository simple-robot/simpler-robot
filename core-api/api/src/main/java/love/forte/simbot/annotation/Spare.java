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

package love.forte.simbot.annotation;

import java.lang.annotation.*;

/**
 * 标记为一个 <b>备用</b> 监听器。
 *
 * 从注解为旧版本的习惯兼容，其效果等同于 {@link SpareListen}，但是会被标记为过时。
 *
 * @author ForteScarlet
 * @see SpareListen
 * @see love.forte.simbot.listener.ListenerFunction
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD}) //接口、类、枚举、注解、方法
@Documented
@SpareListen
@Deprecated
public @interface Spare {
}
