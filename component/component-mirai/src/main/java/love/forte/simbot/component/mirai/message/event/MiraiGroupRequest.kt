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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.flag
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.containers.simpleGroupInfo
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.GroupAddRequestInvitor
import love.forte.simbot.api.message.events.asInvitor
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupMemberInfo
import love.forte.simbot.component.mirai.utils.groupAvatar
import love.forte.simbot.component.mirai.utils.userAvatar
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent






/**
 * mirai的群成员入群请求事件。
 */
public class MiraiGroupMemberJoinRequest(event: MemberJoinRequestEvent) :
    AbstractMiraiMsgGet<MemberJoinRequestEvent>(event), GroupAddRequest {

    override val id: String = event.eventId.toString()

    override val accountInfo: AccountInfo = MiraiGroupMemberJoinAccountInfo(event)

    override val groupInfo: GroupInfo by lazy(LazyThreadSafetyMode.PUBLICATION) { event.group?.let { MiraiGroupInfo(it) } ?: throw IllegalStateException("Bot has dropped out of the group.") }

    override val text: String = event.message //.takeIf { it.isNotBlank() }

    /**
     * 可能获取邀请者。
     */
    override val invitor: GroupAddRequestInvitor? = event.invitor?.let { MiraiGroupMemberInfo(it) }?.asInvitor()

    /**
     * 存在邀请者即为被邀请，否则为主动入群。
     */
    override val requestType: GroupAddRequest.Type = if (event.invitor == null) GroupAddRequest.Type.PROACTIVE else GroupAddRequest.Type.PASSIVE

    /** 标识。 */
    override val flag: Flag<MiraiGroupMemberJoinRequestFlagContent> = flag { MiraiGroupMemberJoinRequestFlagContent(event) }
}

/**
 * 他人申请入群时的flag content.
 */
public data class MiraiGroupMemberJoinRequestFlagContent(val event: MemberJoinRequestEvent) :
        GroupAddRequest.FlagContent {
    override val id: String = event.eventId.toString()
}

/** 申请入群事件的用户信息。 */
private data class MiraiGroupMemberJoinAccountInfo(private val event: MemberJoinRequestEvent) : AccountInfo {
    override val accountCode: String
        get() = event.fromId.toString()
    override val accountCodeNumber: Long
        get() = event.fromId
    override val accountNickname: String
        get() = event.fromNick
    override val accountRemark: String? = null
    override val accountAvatar: String = userAvatar(event.fromId)
}



/**
 * mirai bot被邀请进入某个群。
 */
public class MiraiBotInvitedJoinGroupRequest(event: BotInvitedJoinGroupRequestEvent) :
        AbstractMiraiMsgGet<BotInvitedJoinGroupRequestEvent>(event), GroupAddRequest {

    override val id: String = event.eventId.toString()

    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)

    override val groupInfo: GroupInfo = simpleGroupInfo(event.groupId.toString(), event.groupName, groupAvatar(event.groupId))

    /** bot被邀请则不存在什么消息。 */
    override val text: String? = null

    override val invitor: GroupAddRequestInvitor? = event.invitor?.let { MiraiBotInvitedJoinInvitor(it) }


    /** 永远是被动的。 */
    override val requestType: GroupAddRequest.Type = GroupAddRequest.Type.PASSIVE

    /** 请求标识。 */
    override val flag: Flag<MiraiBotInvitedJoinRequestFlagContent> = flag { MiraiBotInvitedJoinRequestFlagContent(event) }
}


/**
 * bot被邀请时的flag content。
 */
public data class MiraiBotInvitedJoinRequestFlagContent(val event: BotInvitedJoinGroupRequestEvent) : GroupAddRequest.FlagContent {
    override val id: String = event.eventId.toString()
}


/**
 * bot被邀请时候的邀请人。
 */
private class MiraiBotInvitedJoinInvitor(friend: Friend) : GroupAddRequestInvitor {
    override val invitorCode: String = friend.id.toString()
    override val invitorCodeNumber: Long = friend.id
    override val invitorNickname: String = friend.nick
    override val invitorRemark: String = friend.nick
    override val originalData: String = friend.toString()
}








