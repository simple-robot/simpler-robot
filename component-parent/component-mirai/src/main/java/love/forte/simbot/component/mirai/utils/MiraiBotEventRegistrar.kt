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

import love.forte.simbot.component.mirai.message.event.*
import love.forte.simbot.listener.MsgGetProcessor
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.TempMessageEvent
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch


public fun Bot.registerSimbotEvents(msgGetProcessor: MsgGetProcessor) {

    this.logger.let { if (it is MiraiLoggerWithSwitch) it else null }?.enable()

    // val botId: Long = this.id


    // 好友消息
    this.registerListenerAlways<FriendMessageEvent> {
        msgGetProcessor.onMsg(MiraiPrivateMsg(this))
    }
    // 临时会话消息
    this.registerListenerAlways<TempMessageEvent> {
        msgGetProcessor.onMsg(MiraiTempMsg(this))
    }
    // 群消息
    this.registerListenerAlways<GroupMessageEvent> {
        msgGetProcessor.onMsg(MiraiGroupMsg(this))
    }
    // 好友申请
    this.registerListenerAlways<NewFriendRequestEvent> {
        msgGetProcessor.onMsg(MiraiFriendRequest(this))
    }

    // bot被戳事件
    registerListenerAlways<BotNudgedEvent> {
        if(this.from.id != this.bot.id){
            when(val f = this.from) {
                is Member -> msgGetProcessor.onMsg(MiraiBotGroupNudgedMsg(this, f))
                is Friend -> msgGetProcessor.onMsg(MiraiBotFriendNudgedMsg(this, f))
            }
        }
    }

    // 群里其他人被戳事件
    registerListenerAlways<MemberNudgedEvent> {
        if(this.from.id != this.bot.id) {
             msgGetProcessor.onMsg(MiraiMemberNudgedMsg(this))
        }
    }





    // 入群申请
    this.registerListenerAlways<MemberJoinRequestEvent> {
        msgGetProcessor.onMsg(MiraiGroupMemberJoinRequest(this))
    }
    // bot被邀请入群申请
    this.registerListenerAlways<BotInvitedJoinGroupRequestEvent> {
        msgGetProcessor.onMsg(MiraiBotInvitedJoinGroupRequest(this))
    }


}

/**
 * 整合
 */
private inline fun <reified E : BotEvent> Bot.registerListenerAlways(crossinline handler: suspend E.(E) -> Unit):
        Listener<E> {
    return this.subscribeAlways {
        if (this@subscribeAlways.bot.id == this@registerListenerAlways.id) {
            handler(this)
        }
    }
}