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

@file:JvmName("Shutdown")
package love.forte.simbot.utils

import kotlin.concurrent.thread


/**
 * 注册一个shutdown hook。
 */
public fun onShutdown(name: String? = null, block: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(thread(start = false, name = name, block = block))
}





