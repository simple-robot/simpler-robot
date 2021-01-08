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

import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.FriendAccountInfo
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.FriendMsg
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.events.BotNudgedEvent
import net.mamoe.mirai.event.events.MemberNudgedEvent
import net.mamoe.mirai.utils.MiraiExperimentalApi


/**
 * bot在群里被群成员头像戳一戳了。
 */
@MiraiExperimentalApi
public class MiraiBotGroupNudgedByMemberMsg constructor(
    event: BotNudgedEvent.InGroup.ByMember,
    member: Member,
) : AbstractMiraiMsgGet<BotNudgedEvent.InGroup.ByMember>(event),
    GroupMsg {
    override val id: String = "${event.from.id}.${event.bot.id}"

    override val accountInfo: GroupAccountInfo = MiraiMemberAccountInfo(member)

    /** 头像戳一戳消息属于系统消息。 */
    override val groupMsgType: GroupMsg.Type get() = GroupMsg.Type.SYS

    /** empty flag. */
    override val flag: EmptyMiraiGroupFlag get() = EmptyMiraiGroupFlag

    // nudge目标为bot。
    override val msgContent: MiraiNudgedMessageContent = MiraiNudgedMessageContent(event.bot.id)

    override val groupInfo: GroupInfo = MiraiGroupInfo(member.group)

    override val permission: Permissions = member.permission.toSimbotPermissions()
}


/**
 * bot在好友聊天中被头像戳一戳了。
 */
@MiraiExperimentalApi
public class MiraiBotPrivateSessionNudgedByFriendMsg(event: BotNudgedEvent.InPrivateSession.ByFriend, friend: Friend) :
    AbstractMiraiMsgGet<BotNudgedEvent.InPrivateSession.ByFriend>(event),
    FriendMsg {
    override val id: String = "${event.from.id}.${event.bot.id}"
    override val accountInfo: FriendAccountInfo = MiraiFriendAccountInfo(friend)
    //
    // /** 私聊的头像戳一戳属于系统消息 */
    // override val privateMsgType: PrivateMsg.Type get() = PrivateMsg.Type.SYS

    /** empty flag. */
    override val flag: EmptyMiraiPrivateFlag get() = EmptyMiraiPrivateFlag

    // nudge目标为bot。
    override val msgContent: MiraiNudgedMessageContent = MiraiNudgedMessageContent(event.bot.id)
}


/**
 * 群员被戳了。
 */
@MiraiExperimentalApi
public class MiraiMemberNudgedMsg(event: MemberNudgedEvent) : AbstractMiraiMsgGet<MemberNudgedEvent>(event), GroupMsg {
    override val id: String = "${event.from.id}.${event.bot.id}"
    override val accountInfo: GroupAccountInfo = MiraiMemberAccountInfo(event.member)
    override val permission: Permissions = event.member.permission.toSimbotPermissions()
    override val groupMsgType: GroupMsg.Type get() = GroupMsg.Type.SYS
    override val flag: EmptyMiraiGroupFlag get() = EmptyMiraiGroupFlag
    override val msgContent: MiraiNudgedMessageContent = MiraiNudgedMessageContent(event.bot.id)
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.member.group)
}
