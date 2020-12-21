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

@file:JvmName("MiraiBotEventRegistrars")

package love.forte.simbot.component.mirai.utils

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.component.mirai.message.event.*
import love.forte.simbot.component.mirai.message.flagId
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.listener.MsgGetProcessor
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//region 处理监听消息
private fun <M : MsgGet> M.onMsg(msgProcessor: MsgGetProcessor) = msgProcessor.onMsg(this)
//endregion


@ComponentBeans
public class MiraiBotEventRegistrar(private val cache: MiraiMessageCache) {

    private val logger: Logger = LoggerFactory.getLogger(MiraiBotEventRegistrar::class.java)


    @OptIn(MiraiExperimentalApi::class)
    fun registerSimbotEvents(bot: Bot, msgProcessor: MsgGetProcessor) = bot.run {

        //region 消息相关
        // 好友消息
        registerListenerAlways<FriendMessageEvent> {
            msgProcessor.onMsg(MiraiPrivateMsg(this).also {
                cache.cachePrivateMsg(it.flag.flagId, it)
            })
        }
        // 临时会话消息
        registerListenerAlways<TempMessageEvent> {
            msgProcessor.onMsg(MiraiTempMsg(this).also {
                cache.cachePrivateMsg(it.flag.flagId, it)
            })
        }
        // 群消息
        registerListenerAlways<GroupMessageEvent> {
            msgProcessor.onMsg(MiraiGroupMsg(this).also {
                cache.cacheGroupMsg(it.flag.flagId, it)
            })
        }
        // 好友申请
        registerListenerAlways<NewFriendRequestEvent> {
            msgProcessor.onMsg(MiraiFriendRequest(this))
        }

        // bot被戳事件
        registerListenerAlways<BotNudgedEvent> {
            // if (this.from.id != this.bot.id) {
                when (this) {
                    is BotNudgedEvent.InGroup.ByMember -> msgProcessor.onMsg(MiraiBotGroupNudgedByMemberMsg(this, from))
                    is BotNudgedEvent.InPrivateSession.ByFriend -> msgProcessor.onMsg(MiraiBotPrivateSessionNudgedByFriendMsg(this, from))
                    else -> {  }
                }
                // when (val f = this.from) {
                //     is Member -> msgProcessor.onMsg(MiraiBotGroupNudgedByMemberMsg(this, f))
                //     is Friend -> msgProcessor.onMsg(MiraiBotFriendNudgedMsg(this, f))
                // }
            // }
        }

        // 群里其他人被戳事件
        registerListenerAlways<MemberNudgedEvent> {
            // if (this.from.id != this.bot.id) {
                msgProcessor.onMsg(MiraiMemberNudgedMsg(this))
            // }
        }
        //endregion


        //region 申请相关
        // 入群申请
        registerListenerAlways<MemberJoinRequestEvent> {
            msgProcessor.onMsg(MiraiGroupMemberJoinRequest(this))
        }

        // bot被邀请入群申请
        registerListenerAlways<BotInvitedJoinGroupRequestEvent> {
            msgProcessor.onMsg(MiraiBotInvitedJoinGroupRequest(this))
        }
        //endregion

        //region 好友变动
        // 好友增加/减少
        registerListenerAlways<FriendAddEvent> {
            msgProcessor.onMsg(MiraiFriendAdded(this))
        }
        registerListenerAlways<FriendDeleteEvent> {
            msgProcessor.onMsg(MiraiFriendDeleted(this))
        }
        //endregion

        //region 群成员变动
        // 群成员增加 - member
        registerListenerAlways<MemberJoinEvent> {
            when (this) {
                // 主动入群
                is MemberJoinEvent.Active -> msgProcessor.onMsg(MiraiMemberJoined.Active(this))
                // 被动入群
                is MemberJoinEvent.Invite -> msgProcessor.onMsg(MiraiMemberJoined.Invite(this))
                //
                is MemberJoinEvent.Retrieve -> msgProcessor.onMsg(MiraiMemberJoined.Retrieve(this))
            }
        }
        // 群成员增加 - bot
        // bot主动同意入群
        registerListenerAlways<BotJoinGroupEvent> {
            when (this) {
                is BotJoinGroupEvent.Active -> msgProcessor.onMsg(MiraiBotJoined.Active(this))
                is BotJoinGroupEvent.Invite -> msgProcessor.onMsg(MiraiBotJoined.Invite(this))
                is BotJoinGroupEvent.Retrieve -> msgProcessor.onMsg(MiraiBotJoined.Retrieve(this))
            }
        }

        // 群成员减少
        // 群友减少
        registerListenerAlways<MemberLeaveEvent> {
            when (this) {
                is MemberLeaveEvent.Kick -> msgProcessor.onMsg(MiraiMemberLeaved.Kick(this))
                is MemberLeaveEvent.Quit -> msgProcessor.onMsg(MiraiMemberLeaved.Quit(this))
            }
        }
        // bot退群
        registerListenerAlways<BotLeaveEvent> {
            when (this) {
                is BotLeaveEvent.Kick -> msgProcessor.onMsg(MiraiBotLeaveEvent.Kick(this))
                is BotLeaveEvent.Active -> msgProcessor.onMsg(MiraiBotLeaveEvent.Active(this))
            }
        }
        //endregion

        //region 权限变动事件
        // 成员权限变动
        registerListenerAlways<MemberPermissionChangeEvent> {
            MiraiMemberPermissionChanged(this).onMsg(msgProcessor)
        }
        // bot权限变动
        registerListenerAlways<BotGroupPermissionChangeEvent> {
            MiraiBotPermissionChanged(this).onMsg(msgProcessor)
        }
        //endregion

        //region 消息撤回事件
        registerListenerAlways<MessageRecallEvent> {
            when (this) {
                //region 群消息撤回
                is MessageRecallEvent.GroupRecall -> {
                    MiraiMsgRecall.GroupRecall(this, cache).onMsg(msgProcessor)
                }
                //endregion
                //region 好友消息撤回
                is MessageRecallEvent.FriendRecall -> {
                    MiraiMsgRecall.FriendRecall(this, cache).onMsg(msgProcessor)
                }
                //endregion
            }
        }
        //endregion

        //region 禁言相关

        //region 群友被禁言
        registerListenerAlways<MemberMuteEvent> {
            MiraiMemberMuteMsg(this).onMsg(msgProcessor)
        }
        //endregion
        //region 群友被解除禁言
        registerListenerAlways<MemberUnmuteEvent> {
            MiraiMemberUnmuteMsg(this).onMsg(msgProcessor)
        }
        //endregion
        //region bot被禁言
        registerListenerAlways<BotMuteEvent> {
            MiraiBotMuteMsg(this).onMsg(msgProcessor)
        }
        //endregion
        //region bot被取消禁言
        registerListenerAlways<BotUnmuteEvent> {
            MiraiBotUnmuteMsg(this).onMsg(msgProcessor)
        }
        //endregion

        //region 全员禁言 GroupMuteAllEvent
        registerListenerAlways<GroupMuteAllEvent> {
            MiraiMuteAllMsg(this).onMsg(msgProcessor)
        }
        //endregion

        //endregion


        //region 杂项事件
        //region 好友更换头像事件
        registerListenerAlways<FriendAvatarChangedEvent> {
            MiraiFriendAvatarChanged(this).onMsg(msgProcessor)
        }
        //endregion


        //region 好友更换昵称事件
        registerListenerAlways<FriendNickChangedEvent> {
            MiraiFriendNickChanged(this).onMsg(msgProcessor)
        }
        //endregion


        //region 好友输入状态变更事件
        registerListenerAlways<FriendInputStatusChangedEvent> {
            MiraiFriendInputStatusChanged(this).onMsg(msgProcessor)
        }
        //endregion


        //region bot离线事件
        registerListenerAlways<BotOfflineEvent> {
            when (this) {
                is BotOfflineEvent.Active -> MiraiBotOffline.Active(this).onMsg(msgProcessor)
                is BotOfflineEvent.Force -> MiraiBotOffline.Force(this).onMsg(msgProcessor)
                is BotOfflineEvent.Dropped -> MiraiBotOffline.Dropped(this).onMsg(msgProcessor)
                else -> MiraiBotOffline.Other(this).onMsg(msgProcessor)
            }
        }
        //endregion

        //region bot重新登录事件
        registerListenerAlways<BotReloginEvent> {
            MiraiBotReLogin(this).onMsg(msgProcessor)
        }
        //endregion


        //region 群名称变更事件
        registerListenerAlways<GroupNameChangeEvent> {
            MiraiGroupNameChanged(this).onMsg(msgProcessor)
        }
        //endregion

        //region 群友群备注变更事件
        registerListenerAlways<MemberCardChangeEvent> {
            MiraiMemberCardChanged(this).onMsg(msgProcessor)
        }
        //endregion


        //region 群友群头衔变更事件
        registerListenerAlways<MemberSpecialTitleChangeEvent> {
            MiraiMemberSpecialTitleChanged(this).onMsg(msgProcessor)
        }
        //endregion
        //endregion



        // enable mirai log.
        logger.let { if (it is MiraiLoggerWithSwitch) it else null }?.enable()
    }
}


// public fun Bot.registerSimbotEvents(msgProcessor: MsgGetProcessor) {
//
//
//     //region 消息相关
//     // 好友消息
//     registerListenerAlways<FriendMessageEvent> {
//         msgProcessor.onMsg(MiraiPrivateMsg(this))
//     }
//     // 临时会话消息
//     registerListenerAlways<TempMessageEvent> {
//         msgProcessor.onMsg(MiraiTempMsg(this))
//     }
//     // 群消息
//     registerListenerAlways<GroupMessageEvent> {
//         msgProcessor.onMsg(MiraiGroupMsg(this))
//     }
//     // 好友申请
//     registerListenerAlways<NewFriendRequestEvent> {
//         msgProcessor.onMsg(MiraiFriendRequest(this))
//     }
//
//     // bot被戳事件
//     registerListenerAlways<BotNudgedEvent> {
//         if (this.from.id != this.bot.id) {
//             when (val f = this.from) {
//                 is Member -> msgProcessor.onMsg(MiraiBotGroupNudgedMsg(this, f))
//                 is Friend -> msgProcessor.onMsg(MiraiBotFriendNudgedMsg(this, f))
//             }
//         }
//     }
//
//     // 群里其他人被戳事件
//     registerListenerAlways<MemberNudgedEvent> {
//         if (this.from.id != this.bot.id) {
//             msgProcessor.onMsg(MiraiMemberNudgedMsg(this))
//         }
//     }
//     //endregion
//
//
//     //region 申请相关
//     // 入群申请
//     registerListenerAlways<MemberJoinRequestEvent> {
//         msgProcessor.onMsg(MiraiGroupMemberJoinRequest(this))
//     }
//
//     // bot被邀请入群申请
//     registerListenerAlways<BotInvitedJoinGroupRequestEvent> {
//         msgProcessor.onMsg(MiraiBotInvitedJoinGroupRequest(this))
//     }
//     //endregion
//
//     //region 好友变动
//     // 好友增加/减少
//     registerListenerAlways<FriendAddEvent> {
//         msgProcessor.onMsg(MiraiFriendAdded(this))
//     }
//     registerListenerAlways<FriendDeleteEvent> {
//         msgProcessor.onMsg(MiraiFriendDeleted(this))
//     }
//     //endregion
//
//     //region 群成员变动
//     // 群成员增加 - member
//     registerListenerAlways<MemberJoinEvent> {
//         when (this) {
//             // 主动入群
//             is MemberJoinEvent.Active -> msgProcessor.onMsg(MiraiMemberJoined.Active(this))
//             // 被动入群
//             is MemberJoinEvent.Invite -> msgProcessor.onMsg(MiraiMemberJoined.Invite(this))
//             //
//             is MemberJoinEvent.Retrieve -> msgProcessor.onMsg(MiraiMemberJoined.Retrieve(this))
//         }
//     }
//     // 群成员增加 - bot
//     // bot主动同意入群
//     registerListenerAlways<BotJoinGroupEvent> {
//         when (this) {
//             is BotJoinGroupEvent.Active -> msgProcessor.onMsg(MiraiBotJoined.Active(this))
//             is BotJoinGroupEvent.Invite -> msgProcessor.onMsg(MiraiBotJoined.Invite(this))
//             is BotJoinGroupEvent.Retrieve -> msgProcessor.onMsg(MiraiBotJoined.Retrieve(this))
//         }
//     }
//
//     // 群成员减少
//     // 群友减少
//     registerListenerAlways<MemberLeaveEvent> {
//         when (this) {
//             is MemberLeaveEvent.Kick -> msgProcessor.onMsg(MiraiMemberLeaved.Kick(this))
//             is MemberLeaveEvent.Quit -> msgProcessor.onMsg(MiraiMemberLeaved.Quit(this))
//         }
//     }
//     // bot退群
//     registerListenerAlways<BotLeaveEvent> {
//         when (this) {
//             is BotLeaveEvent.Kick -> msgProcessor.onMsg(MiraiBotLeaveEvent.Kick(this))
//             is BotLeaveEvent.Active -> msgProcessor.onMsg(MiraiBotLeaveEvent.Active(this))
//         }
//     }
//     //endregion
//
//     //region 权限变动事件
//     // 成员权限变动
//     registerListenerAlways<MemberPermissionChangeEvent> {
//         MiraiMemberPermissionChanged(this).onMsg(msgProcessor)
//     }
//     // bot权限变动
//     registerListenerAlways<BotGroupPermissionChangeEvent> {
//         MiraiBotPermissionChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//     //region 消息撤回事件
//     registerListenerAlways<MessageRecallEvent> {
//         when (this) {
//             //region 群消息撤回
//             is MessageRecallEvent.GroupRecall -> {
//                 MiraiMsgRecall.GroupRecall(this).onMsg(msgProcessor)
//             }
//             //endregion
//             //region 好友消息撤回
//             is MessageRecallEvent.FriendRecall -> {
//                 MiraiMsgRecall.FriendRecall(this).onMsg(msgProcessor)
//             }
//             //endregion
//         }
//     }
//     //endregion
//
//     //region 禁言相关
//
//     //region 群友被禁言
//     registerListenerAlways<MemberMuteEvent> {
//         MiraiMemberMuteMsg(this).onMsg(msgProcessor)
//     }
//     //endregion
//     //region 群友被解除禁言
//     registerListenerAlways<MemberUnmuteEvent> {
//         MiraiMemberUnmuteMsg(this).onMsg(msgProcessor)
//     }
//     //endregion
//     //region bot被禁言
//     registerListenerAlways<BotMuteEvent> {
//         MiraiBotMuteMsg(this).onMsg(msgProcessor)
//     }
//     //endregion
//     //region bot被取消禁言
//     registerListenerAlways<BotUnmuteEvent> {
//         MiraiBotUnmuteMsg(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//     //region 全员禁言 GroupMuteAllEvent
//     registerListenerAlways<GroupMuteAllEvent> {
//         MiraiMuteAllMsg(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//     //endregion
//
//
//     //region 杂项事件
//     //region 好友更换头像事件
//     registerListenerAlways<FriendAvatarChangedEvent> {
//         MiraiFriendAvatarChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//
//     //region 好友更换昵称事件
//     registerListenerAlways<FriendNickChangedEvent> {
//         MiraiFriendNickChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//
//     //region 好友输入状态变更事件
//     registerListenerAlways<FriendInputStatusChangedEvent> {
//         MiraiFriendInputStatusChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//
//     //region bot离线事件
//     registerListenerAlways<BotOfflineEvent> {
//         when (this) {
//             is BotOfflineEvent.Active -> MiraiBotOffline.Active(this).onMsg(msgProcessor)
//             is BotOfflineEvent.Force -> MiraiBotOffline.Force(this).onMsg(msgProcessor)
//             is BotOfflineEvent.Dropped -> MiraiBotOffline.Dropped(this).onMsg(msgProcessor)
//             else -> MiraiBotOffline.Other(this).onMsg(msgProcessor)
//         }
//     }
//     //endregion
//
//     //region bot重新登录事件
//     registerListenerAlways<BotReloginEvent> {
//         MiraiBotReLogin(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//
//     //region 群名称变更事件
//     registerListenerAlways<GroupNameChangeEvent> {
//         MiraiGroupNameChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//     //region 群友群备注变更事件
//     registerListenerAlways<MemberCardChangeEvent> {
//         MiraiMemberCardChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//
//
//     //region 群友群头衔变更事件
//     registerListenerAlways<MemberSpecialTitleChangeEvent> {
//         MiraiMemberSpecialTitleChanged(this).onMsg(msgProcessor)
//     }
//     //endregion
//     //endregion
//
//
//
//     // enable mirai log.
//     logger.let { if (it is MiraiLoggerWithSwitch) it else null }?.enable()
// }


/**
 * 整合
 */
private inline fun <reified E : BotEvent> Bot.registerListenerAlways(crossinline handler: suspend E.(E) -> Unit):
        Listener<E> {
    // id for filter.
    val listenBotId = this.id
    return this.subscribeAlways {
        if (bot.id == listenBotId) {
            handler(this)
        }
    }
}