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

package love.forte.simbot.component.mirai.message.result

import love.forte.common.utils.secondToMill
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.results.GroupAdmin
import love.forte.simbot.api.message.results.GroupMemberInfo
import love.forte.simbot.api.message.results.GroupOwner
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.toSimbotPermissions
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.NormalMember

/**
 * mirai 的 [GroupMemberInfo] 实现。
 */
public class MiraiGroupMemberInfo(member: Member) :
    GroupMemberInfo,
    GroupInfo by MiraiGroupInfo(member.group),
    GroupAccountInfo by MiraiMemberAccountInfo(member) {

    /**
     * 入群时间。
     */
    override val joinTime = if (member is NormalMember) member.joinTimestamp.secondToMill() else -1

    /**
     * 最后发言时间。
     */
    override val lastSpeakTime = if (member is NormalMember) member.lastSpeakTimestamp.secondToMill() else -1


    override val muteTime: Long = if (member is NormalMember) member.muteTimeRemaining.secondToMill() else -1


    override val originalData: String = member.toString()

    override fun toString(): String = "MiraiGroupMemberInfo(original=$originalData)"

    override val permission: Permissions = member.toSimbotPermissions()
}


/**
 * 将一个 member 作为 管理员。
 */
public class MiraiGroupAdminInfo(member: Member) : GroupAdmin, GroupAccountInfo by MiraiMemberAccountInfo(member) {
    private val str = "GroupAdmin(group=${member.group}, admin=$member)"
    override fun toString(): String = str
}


/**
 * 将一个 member 作为 群主。
 */
public class MiraiGroupOwnerInfo(member: Member) : GroupOwner, GroupAccountInfo by MiraiMemberAccountInfo(member) {
    private val str = "GroupOwner(group=${member.group}, owner=$member)"
    override fun toString(): String = str
}
