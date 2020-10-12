/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerRegister.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MiraiBotEventRegistrar")
package love.forte.simbot.component.mirai.utils

import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch


public fun Bot.registerSimbotEvents() {

    this.logger.let { if (it is MiraiLoggerWithSwitch) it else null }?.enable()


    println("register: $this")



}