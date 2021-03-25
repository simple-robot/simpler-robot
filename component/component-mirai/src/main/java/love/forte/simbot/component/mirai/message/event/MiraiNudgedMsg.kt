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
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.FriendAccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.component.mirai.message.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.Stranger
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.utils.MiraiExperimentalApi


/**
 * 头像戳一戳事件。
 */
@MiraiExperimentalApi
public sealed class MiraiNudgedEvent<C : Contact> constructor(
    event: NudgeEvent,
    internal val contact: C,
) : AbstractMiraiMsgGet<NudgeEvent>(event), MessageGet {
    override val id: String = "${event.from.id}.${event.bot.id}"

    /**
     * 好友戳一戳事件。
     */
    class ByFriend(event: NudgeEvent, friend: Friend) : MiraiNudgedEvent<Friend>(event, friend),
        PrivateMsg {
        override val accountInfo: FriendAccountInfo = MiraiFriendAccountInfo(friend)
        /**
         * 获取私聊消息类型.
         */
        override val privateMsgType: PrivateMsg.Type
            get() = PrivateMsg.Type.SYS
        /**
         * empty flag.
         * 戳一戳无法被撤回。
         */
        override val flag: EmptyMiraiPrivateFlag get() = EmptyMiraiPrivateFlag
    }

    /**
     * 群戳一戳
     */
    class ByMember(event: NudgeEvent, member: Member) : MiraiNudgedEvent<Member>(event, member), GroupMsg {
        /**
         * 账号的信息。一般来讲是不可能为null的，但是其中的信息就不一定了
         */
        override val accountInfo: MiraiMemberAccountInfo = MiraiMemberAccountInfo(member)
        override val groupInfo: GroupInfo get() = accountInfo

        /** 发消息的人在群里的权限。 */
        override val permission: Permissions
            get() = contact.permission.toSimbotPermissions()

        /**
         * 获取群消息类型. 戳一戳属于系统消息。
         */
        override val groupMsgType: GroupMsg.Type
            get() = GroupMsg.Type.SYS

        /**
         * 群聊消息的标识.
         * 戳一戳不可撤回。
         */
        override val flag: EmptyMiraiGroupFlag get() = EmptyMiraiGroupFlag
    }

    class ByStranger(event: NudgeEvent, stranger: Stranger) : MiraiNudgedEvent<Stranger>(event, stranger), PrivateMsg {
        /**
         * 账号的信息。
         */
        override val accountInfo: AccountInfo = MiraiStrangerAccountInfo(stranger)


        /**
         * 获取私聊消息类型
         */
        override val privateMsgType: PrivateMsg.Type
            get() = PrivateMsg.Type.SYS

        override val flag: EmptyMiraiPrivateFlag
            get() = EmptyMiraiPrivateFlag
    }


    // nudge目标为bot。
    override val msgContent: MiraiNudgedMessageContent = MiraiNudgedMessageContent(event.from.id, event.target.id)
}

//
// /**
//  * bot在好友聊天中被头像戳一戳了。
//  */
// @MiraiExperimentalApi
// public class MiraiBotPrivateSessionNudgedByFriendMsg(event: BotNudgedEvent.InPrivateSession.ByFriend, friend: Friend) :
//     AbstractMiraiMsgGet<BotNudgedEvent.InPrivateSession.ByFriend>(event),
//     FriendMsg {
//     override val id: String = "${event.from.id}.${event.bot.id}"
//     override val accountInfo: FriendAccountInfo = MiraiFriendAccountInfo(friend)
//     //
//     // /** 私聊的头像戳一戳属于系统消息 */
//     // override val privateMsgType: PrivateMsg.Type get() = PrivateMsg.Type.SYS
//
//     /** empty flag. */
//     override val flag: EmptyMiraiPrivateFlag get() = EmptyMiraiPrivateFlag
//
//     // nudge目标为bot。
//     override val msgContent: MiraiNudgedMessageContent = MiraiNudgedMessageContent(event.bot.id)
// }
//
//
// /**
//  * 群员被戳了。
//  */
// @MiraiExperimentalApi
// public class MiraiMemberNudgedMsg(event: MemberNudgedEvent) : AbstractMiraiMsgGet<MemberNudgedEvent>(event), GroupMsg {
//     override val id: String = "${event.from.id}.${event.bot.id}"
//     override val accountInfo: GroupAccountInfo = MiraiMemberAccountInfo(event.member)
//     override val permission: Permissions = event.member.permission.toSimbotPermissions()
//     override val groupMsgType: GroupMsg.Type get() = GroupMsg.Type.SYS
//     override val flag: EmptyMiraiGroupFlag get() = EmptyMiraiGroupFlag
//     override val msgContent: MiraiNudgedMessageContent = MiraiNudgedMessageContent(event.bot.id)
//     override val groupInfo: GroupInfo = MiraiGroupInfo(event.member.group)
// }
