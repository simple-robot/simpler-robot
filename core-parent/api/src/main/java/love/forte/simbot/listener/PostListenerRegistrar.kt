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

package love.forte.simbot.listener


/**
 *
 * 注册监听函数。
 * 将会由核心扫描并执行所有的 [PostListenerRegistrar] 实例。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface PostListenerRegistrar {

    /**
     * 注册监听函数。
     */
    fun registerListenerFunctions(registrar: ListenerRegistrar)

}

