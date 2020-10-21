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

import love.forte.simbot.component.mirai.message.MiraiGroupMsg
import love.forte.simbot.component.mirai.message.MiraiPrivateMsg
import love.forte.simbot.component.mirai.message.MiraiTempMsg
import love.forte.simbot.listener.MsgGetProcessor
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.TempMessageEvent
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch


public fun Bot.registerSimbotEvents(msgGetProcessor: MsgGetProcessor) {

    this.logger.let { if (it is MiraiLoggerWithSwitch) it else null }?.enable()

    val botId: Long = this.id


    // 好友消息
    this.subscribeAlways<FriendMessageEvent> {
        if(this.bot.id == this@registerSimbotEvents.id) {
            msgGetProcessor.onMsg(MiraiPrivateMsg(this))
        }
    }
    // 临时会话消息
    this.subscribeAlways<TempMessageEvent> {
        if(this.bot.id == this@registerSimbotEvents.id) {
            msgGetProcessor.onMsg(MiraiTempMsg(this))
        }
    }
    // 群消息
    this.subscribeAlways<GroupMessageEvent> {
        if(this.bot.id == this@registerSimbotEvents.id) {
            msgGetProcessor.onMsg(MiraiGroupMsg(this))
        }
    }



}