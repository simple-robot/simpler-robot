/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiAccountInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message

import love.forte.simbot.core.api.message.containers.AccountInfo
import love.forte.simbot.core.api.message.containers.GroupInfo
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick


/**
 * 基于 mirai [Friend] 的 [AccountInfo] 实现。
 */
public data class MiraiFriendAccountInfo(private val friend: Friend) : AccountInfo {
    /**
     * 账号
     */
    override val accountCode: String
        get() = friend.id.toString()

    override val accountCodeNumber: Long
        get() = friend.id

    /** 昵称。 */
    override val accountNickname: String
        get() = friend.nick

    /** [accountNickname] */
    override val accountRemark: String
        get() = accountNickname

    override val accountRemarkOrNickname: String
        get() = accountNickname

    override val accountNicknameAndRemark: String
        get() = accountNickname

    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    override val accountAvatar: String
        get() = friend.avatarUrl
}



/**
 * 基于 mirai [Member] 的 [AccountInfo] 实现。
 */
public data class MiraiMemberAccountInfo(private val member: Member) : AccountInfo, GroupInfo {
    /**
     * 账号
     */
    override val accountCode: String
        get() = member.id.toString()

    override val accountCodeNumber: Long
        get() = member.id

    /** 昵称。 */
    override val accountNickname: String
        get() = member.nick

    /** [accountNickname] */
    override val accountRemark: String?
        get() = member.nameCard.takeIf { it.isNotEmpty() }

    override val accountRemarkOrNickname: String?
        get() = member.nameCardOrNick

    override val accountNicknameAndRemark: String
        get() = super.accountNicknameAndRemark

    /**
     * 得到账号的头像地址.
     */
    override val accountAvatar: String
        get() = member.avatarUrl

    private val group: Group get() = member.group

    override val groupCode: String
        get() = group.id.toString()

    override val groupCodeNumber: Long
        get() = group.id

    override val groupAvatar: String?
        get() = group.avatarUrl

    override val groupName: String?
        get() = group.name
}