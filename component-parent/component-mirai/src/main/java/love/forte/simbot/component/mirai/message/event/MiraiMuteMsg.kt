/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMuteMsg.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.event

import love.forte.common.utils.secondToMill
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.events.GroupMute
import love.forte.simbot.api.message.events.MuteGet
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import net.mamoe.mirai.event.events.*


/**
 * mirai 群友被禁言。
 */
public class MiraiMemberMuteMsg(event: MemberMuteEvent) : AbstractMiraiMsgGet<MemberMuteEvent>(event), GroupMute {
    override val id: String = "MMUTE-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** 剩余禁言时间。 */
    override val muteTime: Long = event.durationSeconds.secondToMill()

    /** 禁言类型，必然是执行禁言。 */
    override val muteActionType: MuteGet.ActionType
        get() = MuteGet.ActionType.MUTE

    /** 目标类型，是个体禁言。 */
    override val muteTargetType: MuteGet.TargetType
        get() = MuteGet.TargetType.UNIT

    /** 执行禁言的操作者 */
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operatorOrBot).asOperator()
}

/**
 * 群友被取消禁言。
 */
public class MiraiMemberUnmuteMsg(event: MemberUnmuteEvent) : AbstractMiraiMsgGet<MemberUnmuteEvent>(event), GroupMute {
    override val id: String = "MUNMUTE-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** 取消禁言，time=0 */
    override val muteTime: Long get() = 0

    /** 取消禁言。 */
    override val muteActionType: MuteGet.ActionType
        get() = MuteGet.ActionType.UNMUTE
    override val muteTargetType: MuteGet.TargetType
        get() = MuteGet.TargetType.UNIT
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operatorOrBot).asOperator()
}


/**
 * bot被禁言事件。
 */
public class MiraiBotMuteMsg(event: BotMuteEvent) : AbstractMiraiMsgGet<BotMuteEvent>(event), GroupMute {
    override val id: String = "MMUTE-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)
    override val muteTime: Long = event.durationSeconds.secondToMill()
    override val muteActionType: MuteGet.ActionType
        get() = MuteGet.ActionType.MUTE
    override val muteTargetType: MuteGet.TargetType
        get() = MuteGet.TargetType.UNIT
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operator).asOperator()
}


/**
 * bot被解除禁言事件。
 */
public class MiraiBotUnmuteMsg(event: BotUnmuteEvent) : AbstractMiraiMsgGet<BotUnmuteEvent>(event), GroupMute {
    override val id: String = "MUNMUTE-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)
    override val muteTime: Long get() = 0
    override val muteActionType: MuteGet.ActionType
        get() = MuteGet.ActionType.UNMUTE
    override val muteTargetType: MuteGet.TargetType
        get() = MuteGet.TargetType.UNIT
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operator).asOperator()
}


/**
 * 全体禁言事件。
 */
public class MiraiMuteAllMsg(event: GroupMuteAllEvent) : AbstractMiraiMsgGet<GroupMuteAllEvent>(event), GroupMute {
    override val id: String = "MMUTEALL-${event.hashCode()}"
    private val _operatorAccountInfo = MiraiMemberAccountInfo(event.operatorOrBot)

    /** 开启全体禁言的人 */
    override val accountInfo: AccountInfo get() = _operatorAccountInfo
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** 全体禁言没有时间概念，当开启时为-1，关闭时为0. */
    override val muteTime: Long = if (event.new) -1 else 0
    override val muteActionType: MuteGet.ActionType =
        if (event.new) MuteGet.ActionType.MUTE else MuteGet.ActionType.UNMUTE

    /** 群体性质的禁言。 */
    override val muteTargetType: MuteGet.TargetType
        get() = MuteGet.TargetType.GROUP

    /** 操作者。 */
    override val operatorInfo: OperatorInfo = _operatorAccountInfo.asOperator()

    /** 不存在具体的被禁言者。 */
    override val beOperatorInfo: BeOperatorInfo?
        get() = null
}
