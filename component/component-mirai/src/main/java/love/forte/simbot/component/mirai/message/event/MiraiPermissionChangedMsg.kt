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
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.containers.OperatorInfo
import love.forte.simbot.api.message.containers.asOperator
import love.forte.simbot.api.message.events.GroupMemberPermissionChanged
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import love.forte.simbot.component.mirai.message.toSimbotPermissions
import net.mamoe.mirai.event.events.BotGroupPermissionChangeEvent
import net.mamoe.mirai.event.events.MemberPermissionChangeEvent

/**
 * 群成员权限被改变
 */
public class MiraiMemberPermissionChanged(event: MemberPermissionChangeEvent) :
    AbstractMiraiMsgGet<MemberPermissionChangeEvent>(event), GroupMemberPermissionChanged {
    override val id: String = "MGMPCgd-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)
    override val beforeChange: Permissions = event.origin.toSimbotPermissions()
    override val afterChange: Permissions = event.new.toSimbotPermissions()
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)
    /**
     * 操作者。
     * 必然是群主
     */
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.group.owner).asOperator()
}

/**
 * bot权限被改变
 */
public class MiraiBotPermissionChanged(event: BotGroupPermissionChangeEvent) :
    AbstractMiraiMsgGet<BotGroupPermissionChangeEvent>(event), GroupMemberPermissionChanged {
    override val id: String = "MGMPCgd-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    override val beforeChange: Permissions = event.origin.toSimbotPermissions()
    override val afterChange: Permissions = event.new.toSimbotPermissions()
    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)
    /**
     * 操作者。
     * 必然是群主
     */
    override val operatorInfo: OperatorInfo = MiraiMemberAccountInfo(event.group.owner).asOperator()
}

