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
@file:JvmName("MsgGets")
@file:JvmMultifileClass
package love.forte.simbot.api.message.events

import love.forte.simbot.annotation.MainListenerType
import love.forte.simbot.api.message.assists.ActionMotivations
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.*

/*
 * 此模块下定义请求相关的监听接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/**
 * 好友添加请求事件。 此时申请人尚未成为好友。
 */
@MainListenerType("好友请求")
public interface FriendAddRequest : RequestGet {

    /**
     * 获取请求标识。
     *
     * @see FriendAddRequestIdFlagContent
     */
    override val flag: Flag<@JvmWildcard FlagContent>

    /**
     * 请求类型的 标识主体
     */
    public interface FlagContent : RequestGet.RequestFlagContent
}

/**
 * 使用 [id] 作为标识载体的 [标识主体][FriendAddRequest.FlagContent] 实现。
 */
public data class FriendAddRequestIdFlagContent(override val id: String) : FriendAddRequest.FlagContent


/**
 * 群添加请求。此时申请人尚未入群。
 */
public interface GroupAddRequest : RequestGet, GroupContainer {

    /**
     * 代表当前实际申请入群的用户。如果是别人申请入群，则此为这个要进群的用户的信息，
     * 如果是bot被邀请进某个群，那么这个就是bot的信息。
     */
    override val accountInfo: AccountInfo

    /**
     * 当前请求的邀请者。在 **组件不支持** 、**请求非邀请** 等情况下可能为null。
     */
    val invitor: GroupAddRequestInvitor?

    /**
     * 申请的类型
     */
    val requestType: Type

    /**
     * 获取请求标识。
     *
     * @see GroupAddRequestIdFlagContent
     */
    override val flag: Flag<@JvmWildcard FlagContent>


    /**
     * [群添加请求][GroupAddRequest] 的 [请求标识主体][RequestGet.RequestFlagContent]
     */
    public interface FlagContent : RequestGet.RequestFlagContent


    /**
     * 群添加请求的类型。
     */
    public enum class Type(override val actionMotivations: ActionMotivations): ActionMotivationContainer {
        /**
         * 被动的，一般是指被他人邀请进入的
         */
        PASSIVE(ActionMotivations.PASSIVE),

        /** 主动的, 一般指代入群者为主动申请加入群聊的 */
        PROACTIVE(ActionMotivations.PROACTIVE)
    }
}


/**
 * 使用 [id] 作为标识载体的 [GroupAddRequest.FlagContent] 实现
 */
public data class GroupAddRequestIdFlagContent(override val id: String) : GroupAddRequest.FlagContent


/**
 * [群添加请求][GroupAddRequest] 中的邀请者信息。
 *
 * 或许会考虑直接继承 [AccountInfo]。
 *
 */
public interface GroupAddRequestInvitor: OriginalDataContainer {
    /** 邀请者账号。一般情况下，当前实例存在此参数便不可为null */
    val invitorCode: String
    /** 邀请者账号。 */
    @JvmDefault
    val invitorCodeNumber: Long get() = invitorCode.toLong()
    /** 邀请者昵称。可能无法获取 */
    val invitorNickname: String?
    /** 邀请者备注。 可能无法获取*/
    val invitorRemark: String?
    /** 备注或昵称 */
    @JvmDefault val invitorRemarkOrNickname: String? get() = invitorRemark ?: invitorNickname
}

/**
 * 将 [GroupAddRequestInvitor] 转化为 [AccountInfo]。
 */
public fun GroupAddRequestInvitor.asAccount(): AccountInfo = GroupAddRequestInvitorAsAccountInfo(this)


private data class GroupAddRequestInvitorAsAccountInfo(private val invitor: GroupAddRequestInvitor) : AccountInfo {
    override val accountCode: String
        get() = invitor.invitorCode
    override val accountCodeNumber: Long
        get() = invitor.invitorCodeNumber
    override val accountNickname: String?
        get() = invitor.invitorNickname
    override val accountRemark: String?
        get() = invitor.invitorRemark
    override val accountRemarkOrNickname: String?
        get() = invitor.invitorRemarkOrNickname
    override val accountAvatar: String?
        get() = null
}

/**
 * 将 [GroupAddRequestInvitor] 转化为 [AccountInfo]。
 */
public fun AccountInfo.asInvitor(): GroupAddRequestInvitor = AccountInfoAsGroupAddRequestInvitor(this)


private data class AccountInfoAsGroupAddRequestInvitor(private val accountInfo: AccountInfo) : GroupAddRequestInvitor {
    override val invitorCode: String
        get() = accountInfo.accountCode
    override val invitorNickname: String?
        get() = accountInfo.accountNickname
    override val invitorRemark: String?
        get() = accountInfo.accountRemark
    override val originalData: String
        get() = accountInfo.toString()
}







