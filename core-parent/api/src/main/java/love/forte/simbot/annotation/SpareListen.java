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
 * <p>
 * 备用监听器只有所有<b>非备用</b>监听函数全部执行失败（其中包括返回值为null或者无返回值的）的情况下才会进行匹配过滤。
 *
 * @author ForteScarlet
 * @see love.forte.simbot.listener.ListenerFunction
 *
 * @since 2.0.0-BETA.8
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE}) //接口、类、枚举、注解、方法
@Documented
public @interface SpareListen {
}
