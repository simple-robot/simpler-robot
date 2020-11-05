/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiGroupMemberInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.results.GroupAdmin
import love.forte.simbot.api.message.results.GroupMemberInfo
import love.forte.simbot.api.message.results.GroupOwner
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.toSimbotPermissions
import net.mamoe.mirai.contact.Member

/**
 * mirai 的 [GroupMemberInfo] 实现。
 */
public class MiraiGroupMemberInfo(member: Member) : GroupMemberInfo {
    override val originalData: String = member.toString()
    override fun toString(): String = "MiraiGroupMemberInfo(original=$originalData)"

    override val groupInfo: GroupInfo = MiraiGroupInfo(member.group)

    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(member)

    override val permission: Permissions = member.toSimbotPermissions()
}


/**
 * 将一个 member 作为 管理员。
 */
public class MiraiGroupAdminInfo(member: Member) : GroupAdmin {
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(member)
}


/**
 * 将一个 member 作为 群主。
 */
public class MiraiGroupOwnerInfo(member: Member) : GroupOwner {
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(member)
}
