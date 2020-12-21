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

import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.events.GroupMemberIncrease
import love.forte.simbot.api.message.events.GroupMemberReduce
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.MiraiExperimentalApi


/**
 * 群成员增加事件
 */
public sealed class MiraiMemberJoined<E : MemberJoinEvent>(event: E) : AbstractMiraiMsgGet<E>(event),
    GroupMemberIncrease {
    override val id: String = "MGMInc-${event.hashCode()}"
    private val _beOperatorAccountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val accountInfo: AccountInfo = _beOperatorAccountInfo
    override val beOperatorInfo: BeOperatorInfo = _beOperatorAccountInfo.asBeOperator()
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** 不会是bot。 */
    override fun isBot(): Boolean = false

    /** mirai(1.3.2) 无法定位操作者。 */
    override val operatorInfo: OperatorInfo?
        get() = null

    /** 主动入群者。 */
    public class Active(event: MemberJoinEvent.Active) : MiraiMemberJoined<MemberJoinEvent.Active>(event) {
        /** 类型是主动添加的。 */
        override val increaseType: GroupMemberIncrease.Type
            get() = GroupMemberIncrease.Type.PROACTIVE
    }

    /** 被邀请入群者。 */
    public class Invite(event: MemberJoinEvent.Invite) : MiraiMemberJoined<MemberJoinEvent.Invite>(event) {
        /** 类型是被邀请的。 */
        override val increaseType: GroupMemberIncrease.Type
            get() = GroupMemberIncrease.Type.INVITED
    }

    public class Retrieve(event: MemberJoinEvent.Retrieve) : MiraiMemberJoined<MemberJoinEvent.Retrieve>(event) {
        /** 类型是主动添加的。 */
        override val increaseType: GroupMemberIncrease.Type
            get() = GroupMemberIncrease.Type.PROACTIVE
    }
}

/**
 * bot入群
 */
public sealed class MiraiBotJoined<E : BotJoinGroupEvent>(event: BotJoinGroupEvent) :
    AbstractMiraiMsgGet<BotJoinGroupEvent>(event), GroupMemberIncrease {
    override val id: String = "MBMInc-${event.hashCode()}"

    private val _beOperatorAccountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    override val accountInfo: AccountInfo = _beOperatorAccountInfo
    override val beOperatorInfo: BeOperatorInfo = _beOperatorAccountInfo.asBeOperator()
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** mirai(1.3.2) 无法定位操作者。 */
    override val operatorInfo: OperatorInfo?
        get() = null

    /** 是bot。 */
    override fun isBot(): Boolean = true

    /** 主动入群者。 */
    @OptIn(MiraiExperimentalApi::class)
    public class Active constructor(event: BotJoinGroupEvent.Active) : MiraiBotJoined<BotJoinGroupEvent.Active>(event) {
        /** 类型是主动添加的。 */
        override val increaseType: GroupMemberIncrease.Type
            get() = GroupMemberIncrease.Type.PROACTIVE
    }

    /** 被邀请入群者。 */
    @OptIn(MiraiExperimentalApi::class)
    public class Invite(event: BotJoinGroupEvent.Invite) : MiraiBotJoined<BotJoinGroupEvent.Invite>(event) {
        /** 类型是被邀请的。 */
        override val increaseType: GroupMemberIncrease.Type
            get() = GroupMemberIncrease.Type.INVITED
        override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.invitor).asOperator()
    }

    @OptIn(MiraiExperimentalApi::class)
    public class Retrieve(event: BotJoinGroupEvent.Retrieve) : MiraiBotJoined<BotJoinGroupEvent.Retrieve>(event) {
        /** 类型是主动添加的。 */
        override val increaseType: GroupMemberIncrease.Type
            get() = GroupMemberIncrease.Type.PROACTIVE
    }
}


//**************** 群成员减少 ****************//

/**
 * 群成员减少。
 */
public sealed class MiraiMemberLeaved<E : MemberLeaveEvent>(event: E) :
    AbstractMiraiMsgGet<E>(event),
    GroupMemberReduce {
    override val id: String = "MGMRed-${event.hashCode()}"
    override fun isBot(): Boolean = false
    private val _beOperatorAccountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val beOperatorInfo: BeOperatorInfo = _beOperatorAccountInfo.asBeOperator()
    override val accountInfo: AccountInfo get() = _beOperatorAccountInfo
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** 群员被踢出。 */
    public class Kick(event: MemberLeaveEvent.Kick) : MiraiMemberLeaved<MemberLeaveEvent.Kick>(event) {
        override val reduceType: GroupMemberReduce.Type = GroupMemberReduce.Type.KICK
        override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operatorOrBot).asOperator()
    }
    /** 群员主动退出。 */
    public class Quit(event: MemberLeaveEvent.Quit) : MiraiMemberLeaved<MemberLeaveEvent.Quit>(event) {
        override val reduceType: GroupMemberReduce.Type = GroupMemberReduce.Type.LEAVE
        override val operatorInfo: OperatorInfo? = null
    }
}

/**
 * bot退群。
 */
public sealed class MiraiBotLeaveEvent<E : BotLeaveEvent>(event: E) :
    AbstractMiraiMsgGet<E>(event),
    GroupMemberReduce {
    override val id: String = "MGMRed-${event.hashCode()}"
    override fun isBot(): Boolean = true
    private val _beOperatorAccountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    override val beOperatorInfo: BeOperatorInfo = _beOperatorAccountInfo.asBeOperator()
    override val accountInfo: AccountInfo get() = _beOperatorAccountInfo
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    /** bot被踢出。 */
    @OptIn(MiraiExperimentalApi::class)
    public class Kick(event: BotLeaveEvent.Kick) : MiraiBotLeaveEvent<BotLeaveEvent.Kick>(event) {
        override val reduceType: GroupMemberReduce.Type = GroupMemberReduce.Type.KICK
        override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.operatorOrBot).asOperator()
    }
    /** bot主动退出。 */
    @OptIn(MiraiExperimentalApi::class)
    public class Active(event: BotLeaveEvent.Active) : MiraiBotLeaveEvent<BotLeaveEvent.Active>(event) {
        override val reduceType: GroupMemberReduce.Type = GroupMemberReduce.Type.LEAVE
        override val operatorInfo: OperatorInfo? = null
    }
}


