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

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.component.mirai.utils.userAvatar
import net.mamoe.mirai.Bot
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


/**
 * mirai的bot对应的 [AccountInfo] 实现。
 * 内容为信息快照，不保存 [Bot] 实例。
 */
public class MiraiBotAccountInfo(bot: Bot) : AccountInfo {
    override val accountCode: String = bot.id.toString()
    override val accountCodeNumber: Long = bot.id
    override val accountNickname: String = bot.nick
    override val accountRemark: String? = null
    override val accountAvatar: String = userAvatar(bot.id)
    override fun toString(): String {
        return "MiraiBotAccountInfo(accountCode='$accountCode', accountCodeNumber=$accountCodeNumber, accountNickname='$accountNickname', accountRemark=$accountRemark, accountAvatar='$accountAvatar')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MiraiBotAccountInfo

        if (accountCode != other.accountCode) return false
        if (accountCodeNumber != other.accountCodeNumber) return false
        if (accountNickname != other.accountNickname) return false
        if (accountRemark != other.accountRemark) return false
        if (accountAvatar != other.accountAvatar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accountCode.hashCode()
        result = 31 * result + accountCodeNumber.hashCode()
        result = 31 * result + accountNickname.hashCode()
        result = 31 * result + (accountRemark?.hashCode() ?: 0)
        result = 31 * result + accountAvatar.hashCode()
        return result
    }

}