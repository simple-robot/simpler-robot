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

/**
 * 标记为一个 <b>备用</b> 监听器。
 * <p>
 * 备用监听器只有所有<b>非备用</b>监听函数全部执行失败（其中包括返回值为null或者无返回值的）的情况下才会进行匹配过滤。
 *
 * @author ForteScarlet
 * @see love.forte.simbot.listener.ListenerFunction
 */
public @interface SpareListen {
}
