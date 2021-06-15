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

package love.forte.simbot.component.mirai.message

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.secondToMill
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.component.mirai.toGender
import love.forte.simbot.mark.ThreadUnsafe
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.data.UserProfile


public fun Friend.asAccountInfo(): AccountInfo = MiraiFriendAccountInfo(this)

/**
 * 基于 mirai [Friend] 的 [AccountInfo] 实现。
 */
public data class MiraiFriendAccountInfo(private val friendId: Long, private val friend: Friend?) :
    FriendAccountInfo, AccountDetailInfo {

    constructor(friend: Friend) : this(friend.id, friend)

    private val _friend: Friend
        get() = friend ?: throw NullPointerException("Friend($friendId)")


    private lateinit var _profile: UserProfile

    @get:ThreadUnsafe
    private val profile: UserProfile
        get() {
            if (!::_profile.isInitialized) {
                _profile = runBlocking { _friend.queryProfile() }
            }
            return _profile
        }

    override val level: Long
        get() = profile.qLevel.toLong()

    override val age: Int
        get() = profile.age

    override val email: String
        get() = profile.email

    /** 无法获取手机号 */
    override val phone: String?
        get() = null
    override val signature: String
        get() = profile.sign

    override val gender: Gender
        get() = profile.sex.toGender()


    /**
     * 账号
     */
    override val accountCode: String
        get() = friendId.toString()

    override val accountCodeNumber: Long
        get() = _friend.id

    /** 昵称。 */
    override val accountNickname: String
        get() = _friend.nick

    /** [accountNickname] */
    override val accountRemark: String
        get() = _friend.remark

    // override val accountRemarkOrNickname: String
    //     get() = accountNickname
    //
    // override val accountNicknameAndRemark: String
    //     get() = accountNickname

    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    override val accountAvatar: String
        get() = _friend.avatarUrl
}


public fun Stranger.asAccountInfo(): AccountInfo = MiraiStrangerAccountInfo(this)


public data class MiraiStrangerAccountInfo(private val strangerId: Long, private val stranger: Stranger?) :
    FriendAccountInfo {
    constructor(stranger: Stranger) : this(stranger.id, stranger)

    private val _stranger: Stranger
        get() = stranger ?: throw NullPointerException("Stranger($strangerId)")

    /**
     * 账号
     */
    override val accountCode: String
        get() = strangerId.toString()

    /**
     * 昵称。
     * 可能会出现为null的情况，但是一般情况下不会。
     */
    override val accountNickname: String
        get() = _stranger.nick

    /** 好友备注或群名片。可能为null。 */
    override val accountRemark: String?
        get() = null

    /**
     * 得到账号的头像地址。
     */
    override val accountAvatar: String
        get() = _stranger.avatarUrl
}


public fun Member.asAccountInfo(): AccountInfo = MiraiMemberAccountInfo(this)

/**
 * 基于 mirai [Member] 的 [AccountInfo] 实现。
 */
public data class MiraiMemberAccountInfo constructor(private val memberId: Long, private val member: Member?) :
    GroupAccountInfo,
    GroupInfo,
    AccountDetailInfo {

    constructor(member: Member) : this(member.id, member)

    private val _member: Member
        get() = member ?: throw NullPointerException("Member($memberId)")

    private val _normalMember: NormalMember?
        get() = if(_member is NormalMember) _member as NormalMember else null

    private lateinit var _profile: UserProfile
    @get:ThreadUnsafe
    private val profile: UserProfile
        get() {
            if (!::_profile.isInitialized) {
                _profile = runBlocking { _member.queryProfile() }
            }
            return _profile
        }

    override val level: Long
        get() = profile.qLevel.toLong()

    override val age: Int
        get() = profile.age

    override val email: String
        get() = profile.email

    /** 无法获取手机号 */
    override val phone: String?
        get() = null
    override val signature: String
        get() = profile.sign

    override val gender: Gender
        get() = profile.sex.toGender()

    override val lastSpeakTime: Long
        get() = _normalMember?.lastSpeakTimestamp?.secondToMill() ?: -1

    override val muteTime: Long
        get() = _normalMember?.muteTimeRemaining?.secondToMill() ?: -1

    /**
     * 账号
     */
    override val accountCode: String
        get() = memberId.toString()

    override val accountCodeNumber: Long
        get() = _member.id

    /** 昵称。 */
    override val accountNickname: String
        get() = _member.nick

    /** [accountNickname] */
    override val accountRemark: String?
        get() = _member.nameCard.takeIf { it.isNotEmpty() }

    override val accountRemarkOrNickname: String
        get() = _member.nameCardOrNick

    override val accountNicknameAndRemark: String
        get() = super.accountNicknameAndRemark

    /**
     * 获取此用户的群头衔。
     */
    override val accountTitle: String
        get() = _member.specialTitle

    override val anonymous: Boolean
        get() = _member is AnonymousMember

    override val permission: Permissions = _member.toSimbotPermissions()

    /**
     * 得到账号的头像地址.
     */
    override val accountAvatar: String
        get() = _member.avatarUrl

    private val group: Group
        get() = _member.group

    override val groupCode: String
        get() = group.id.toString()

    override val groupCodeNumber: Long
        get() = group.id

    override val groupAvatar: String
        get() = group.avatarUrl

    override val groupName: String
        get() = group.name
}


public fun Bot.asAccountInfo(): AccountInfo = MiraiBotAccountInfo(this)


/**
 * mirai的bot对应的 [AccountInfo] 实现。
 * 内容为信息快照，不保存 [Bot] 实例。
 */
public class MiraiBotAccountInfo(bot: Bot) : AccountInfo, AccountDetailInfo {
    override val accountCode: String = bot.id.toString()
    override val accountCodeNumber: Long = bot.id
    override val accountNickname: String = bot.nick
    override val accountRemark: String? = null
    override val accountAvatar: String = bot.avatarUrl

    private val profile by lazy {
        runBlocking { bot.asStranger.queryProfile() }
    }

    override val level: Long
        get() = profile.qLevel.toLong()

    override val age: Int
        get() = profile.age

    override val email: String
        get() = profile.email

    /** 无法获取手机号 */
    override val phone: String?
        get() = null
    override val signature: String
        get() = profile.sign

    override val gender: Gender
        get() = profile.sex.toGender()

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