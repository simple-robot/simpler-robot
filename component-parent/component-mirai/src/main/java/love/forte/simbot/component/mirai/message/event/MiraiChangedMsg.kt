/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiChangedMsg.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.containers.OperatorInfo
import love.forte.simbot.api.message.containers.asOperator
import love.forte.simbot.api.message.events.*
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.MiraiFriendAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import net.mamoe.mirai.event.events.*


/**
 * 好友头像变动事件。
 * mirai(1.3.2)无法获取好友曾经的头像。
 */
public class MiraiFriendAvatarChanged(event: FriendAvatarChangedEvent) : AbstractMiraiMsgGet<FriendAvatarChangedEvent>(event), FriendAvatarChanged {
    override val id: String = "MFAvatarC-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.friend)
    override val beforeChange: String? get() = null
    override val afterChange: String get() = event.friend.avatarUrl
}


/**
 * 好友昵称变更事件。
 */
public class MiraiFriendNickChanged(event: FriendNickChangedEvent) : AbstractMiraiMsgGet<FriendNickChangedEvent>(event), FriendNicknameChanged {
    override val id: String = "MFNickC-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.friend)
    override val beforeChange: String get() = event.from
    override val afterChange: String get() = event.to
}


/**
 * 好友输入状态变更事件。
 * 这是mirai的**专属事件**，属于一种[变化事件][ChangedGet]。
 * 其 **change** 的bool值代表为 **是否为输入状态**。
 */
public class MiraiFriendInputStatusChanged(event: FriendInputStatusChangedEvent) : AbstractMiraiMsgGet<FriendInputStatusChangedEvent>(event), ChangedGet<Boolean> {
    override val id: String = "MFInpStatusC-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.friend)
    override val afterChange: Boolean get() = event.inputting
    override val beforeChange: Boolean get() = !afterChange
}


/**
 * 群名称变动事件。
 */
public class MiraiGroupNameChanged(event: GroupNameChangeEvent) : AbstractMiraiMsgGet<GroupNameChangeEvent>(event), GroupNameChanged {
    override val id: String = "MGNCha-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    override val beforeChange: String get() = event.origin
    override val afterChange: String get() = event.new
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)
    /** 操作者。可能是bot自己。 */
    override val operatorInfo: OperatorInfo = (event.operator?.let { MiraiMemberAccountInfo(it) } ?: accountInfo).asOperator()
}


/**
 * 群成员昵称变动事件。
 * 无法得知操作者。
 */
public class MiraiMemberCardChanged(event: MemberCardChangeEvent) : AbstractMiraiMsgGet<MemberCardChangeEvent>(event),
    GroupMemberRemarkChanged {
    override val id: String = "MGMCCha-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val beforeChange: String get() = event.origin
    override val afterChange: String get() = event.new
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /**
     * 无法获取操作者，默认为null。
     */
    override val operatorInfo: OperatorInfo?
        get() = null
}


/**
 * 群成员专属头衔变更事件。
 */
public class MiraiMemberSpecialTitleChanged(event: MemberSpecialTitleChangeEvent) : AbstractMiraiMsgGet<MemberSpecialTitleChangeEvent>(event), GroupMemberSpecialChanged {
    override val id: String = "MGMSpTiCha-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val beforeChange: String get() = event.origin
    override val afterChange: String get() = event.new
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operatorOrBot).asOperator()
}




