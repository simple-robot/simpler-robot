/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.api.sender

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import java.util.concurrent.TimeUnit

/**
 *
 * 状态设置器。
 * 一般用于发送一些消息无关的数据，例如设置禁言等。
 *
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Setter : Communicator {

    /**
     * 一个标识用的接口，用于标记一个 [Setter] 接口的实现为 **默认** 送信器。
     */
    interface Def : Setter

    /**
     * 处理好友添加申请。
     *
     * @param flag [FriendAddRequest]事件的 [flag][FriendAddRequest.flag]。
     * @param friendRemark 如果为同意，则可以设置好友的备注。可以为null。
     * @param agree 是否同意。
     * @param blackList 是否列入黑名单，即不再接受此人申请。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    @JvmSynthetic
    suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean>

    @JvmSynthetic
    suspend fun friendAddRequestAccept(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        friendAddRequest(flag, friendRemark, true, blackList)

    @JvmSynthetic
    suspend fun friendAddRequestAccept(flag: Flag<FriendAddRequest.FlagContent>, blackList: Boolean): Carrier<Boolean> =
        friendAddRequestAccept(flag, friendRemark = null, blackList = blackList)

    @JvmSynthetic
    suspend fun friendAddRequestAccept(flag: Flag<FriendAddRequest.FlagContent>): Carrier<Boolean> =
        friendAddRequestAccept(flag, friendRemark = null, blackList = false)

    @JvmSynthetic
    suspend fun friendAddRequestReject(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        friendAddRequest(flag, friendRemark, false, blackList)

    @JvmSynthetic
    suspend fun friendAddRequestReject(flag: Flag<FriendAddRequest.FlagContent>, blackList: Boolean): Carrier<Boolean> =
        friendAddRequestReject(flag, friendRemark = null, blackList = blackList)

    @JvmSynthetic
    suspend fun friendAddRequestReject(flag: Flag<FriendAddRequest.FlagContent>): Carrier<Boolean> =
        friendAddRequestReject(flag, friendRemark = null, blackList = false)

    /////////////// blocking //////////////////
    fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> = runBlocking { friendAddRequest(flag, friendRemark, agree, blackList) }

    fun acceptFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = runBlocking { friendAddRequestAccept(flag, friendRemark, blackList) }

    fun acceptFriendAddRequest(flag: Flag<FriendAddRequest.FlagContent>, blackList: Boolean): Carrier<Boolean> =
        runBlocking { friendAddRequestAccept(flag, blackList) }

    fun acceptFriendAddRequest(flag: Flag<FriendAddRequest.FlagContent>): Carrier<Boolean> =
        runBlocking { friendAddRequestAccept(flag) }

    fun rejectFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = runBlocking { friendAddRequestReject(flag, friendRemark, blackList) }

    fun rejectFriendAddRequest(flag: Flag<FriendAddRequest.FlagContent>, blackList: Boolean): Carrier<Boolean> =
        runBlocking { friendAddRequestReject(flag, blackList) }

    fun rejectFriendAddRequest(flag: Flag<FriendAddRequest.FlagContent>): Carrier<Boolean> =
        runBlocking { friendAddRequestReject(flag) }


    /**
     * 处理群添加申请。
     *
     * @param flag [GroupAddRequest] 事件的 [flag][GroupAddRequest.flag]。
     * @param agree 是否同意。
     * @param blackList 是否列入黑名单，即不再接受此人申请。
     * @param why 如果是拒绝，则此处可以填写拒绝理由。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    @JvmSynthetic
    suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean>

    @JvmSynthetic
    suspend fun groupAddRequestAccept(
        flag: Flag<GroupAddRequest.FlagContent>,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> = groupAddRequest(flag, true, blackList, why)

    @JvmSynthetic
    suspend fun groupAddRequestAccept(flag: Flag<GroupAddRequest.FlagContent>, why: String?): Carrier<Boolean> =
        groupAddRequestAccept(flag, false, why)

    @JvmSynthetic
    suspend fun groupAddRequestAccept(flag: Flag<GroupAddRequest.FlagContent>): Carrier<Boolean> =
        groupAddRequestAccept(flag, false, null)

    @JvmSynthetic
    suspend fun groupAddRequestReject(
        flag: Flag<GroupAddRequest.FlagContent>,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> =
        groupAddRequest(flag, false, blackList, why)

    @JvmSynthetic
    suspend fun groupAddRequestReject(flag: Flag<GroupAddRequest.FlagContent>, why: String?): Carrier<Boolean> =
        groupAddRequestReject(flag, false, why)

    @JvmSynthetic
    suspend fun groupAddRequestReject(flag: Flag<GroupAddRequest.FlagContent>): Carrier<Boolean> =
        groupAddRequestReject(flag, false, null)


    //////////////// blocking //////////////////


    fun setGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> = runBlocking { groupAddRequest(flag, agree, blackList, why) }

    fun acceptGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> = runBlocking { groupAddRequestAccept(flag, blackList, why) }

    fun acceptGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, why: String?): Carrier<Boolean> =
        runBlocking { groupAddRequestAccept(flag, why) }

    fun acceptGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>): Carrier<Boolean> =
        runBlocking { groupAddRequestAccept(flag) }

    fun rejectGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> = runBlocking { groupAddRequestReject(flag, blackList, why) }

    fun rejectGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, why: String?): Carrier<Boolean> =
        runBlocking { groupAddRequestReject(flag, why) }

    fun rejectGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>): Carrier<Boolean> =
        runBlocking { groupAddRequestReject(flag) }


    /**
     * 设置群管理。 一般来讲需要账号权限为群主才能操作。
     *
     * @param groupCode 群号
     * @param memberCode 成员账号
     * @param promotion `true`为设置为管理员，`false`为取消管理员。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    @JvmSynthetic
    suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean>


    @JvmSynthetic
    suspend fun groupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> =
        groupAdmin(groupCode.toString(), memberCode.toString(), promotion)


    @JvmSynthetic
    suspend fun groupAdmin(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        promotion: Boolean,
    ): Carrier<Boolean> =
        groupAdmin(group.groupCode, member.accountCode, promotion)


    @JvmSynthetic
    suspend fun groupAdmin(group: GroupContainer, member: AccountContainer, promotion: Boolean): Carrier<Boolean> =
        groupAdmin(group.groupInfo, member.accountInfo, promotion)


    @JvmSynthetic
    suspend fun <T> groupAdmin(groupAccountMsg: T, promotion: Boolean): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        groupAdmin(groupAccountMsg, groupAccountMsg, promotion)


    @JvmSynthetic
    suspend fun <T> groupAdmin(groupAccountMsg: T, promotion: Boolean): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        groupAdmin(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, promotion)

    //////////////// blocking ///////////////////

    fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        runBlocking { groupAdmin(groupCode, memberCode, promotion) }


    fun setGroupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> =
        runBlocking { groupAdmin(groupCode, memberCode, promotion) }


    fun setGroupAdmin(group: GroupCodeContainer, member: AccountCodeContainer, promotion: Boolean): Carrier<Boolean> =
        runBlocking { groupAdmin(group, member, promotion) }


    fun setGroupAdmin(group: GroupContainer, member: AccountContainer, promotion: Boolean): Carrier<Boolean> =
        runBlocking { groupAdmin(group, member, promotion) }


    fun <T> setGroupAdmin(groupAccountMsg: T, promotion: Boolean): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { groupAdmin(groupAccountMsg, groupAccountMsg, promotion) }


    fun <T> setGroupAdmin(groupAccountMsg: T, promotion: Boolean): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { groupAdmin(groupAccountMsg, promotion) }


    /**
     * 设置群匿名聊天。
     * **不会捕获异常**。
     * @param group 群号
     * @param agree 是否允许群匿名聊天
     *
     * @return 设置操作的回执，一般代表最终设置后的开启状态。如果不支持设置的话返回值则代表当前状态。
     */
    @JvmSynthetic
    suspend fun groupAnonymous(group: String, agree: Boolean): Carrier<Boolean>


    @JvmSynthetic
    suspend fun groupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> = groupAnonymous(group.toString(), agree)


    @JvmSynthetic
    suspend fun groupAnonymous(group: GroupCodeContainer, agree: Boolean): Carrier<Boolean> =
        groupAnonymous(group.groupCode, agree)


    @JvmSynthetic
    suspend fun groupAnonymous(group: GroupContainer, agree: Boolean): Carrier<Boolean> =
        groupAnonymous(group.groupInfo, agree)
    
    ////////////// blocking ///////////////
    
    fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> = runBlocking { groupAnonymous(group, agree) }


    fun setGroupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> = runBlocking { groupAnonymous(group, agree) }


    fun setGroupAnonymous(group: GroupCodeContainer, agree: Boolean): Carrier<Boolean> =
        runBlocking { groupAnonymous(group, agree) }


    fun setGroupAnonymous(group: GroupContainer, agree: Boolean): Carrier<Boolean> =
        runBlocking { groupAnonymous(group, agree) }


    /**
     * 群内禁言某人。
     *
     * @param groupCode 群号
     * @param memberCode 被禁言者账号
     * @param time 时长。
     * @param timeUnit 时间类型。默认为 **秒**
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    @JvmSynthetic
    suspend fun groupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean>


    @JvmSynthetic
    suspend fun groupBan(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        groupBan(groupCode.toString(), memberCode.toString(), time, timeUnit)


    @JvmSynthetic
    suspend fun groupBan(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> =
        groupBan(group.groupCode, member.accountCode, time, timeUnit)


    @JvmSynthetic
    suspend fun groupBan(
        group: GroupContainer,
        member: AccountContainer,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> =
        groupBan(group.groupInfo, member.accountInfo, time, timeUnit)


    @JvmSynthetic
    suspend fun <T> groupBan(groupAccountMsg: T, time: Long, timeUnit: TimeUnit): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        groupBan(groupAccountMsg, groupAccountMsg, time, timeUnit)


    @JvmSynthetic
    suspend fun <T> groupBan(groupAccountMsg: T, time: Long, timeUnit: TimeUnit): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        groupBan(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, time, timeUnit)


    @JvmSynthetic
    suspend fun groupBan(groupCode: String, memberCode: String, time: Long): Carrier<Boolean> =
        groupBan(groupCode, memberCode, time, TimeUnit.SECONDS)


    @JvmSynthetic
    suspend fun groupBan(groupCode: Long, memberCode: Long, time: Long): Carrier<Boolean> =
        groupBan(groupCode.toString(), memberCode.toString(), time, TimeUnit.SECONDS)


    @JvmSynthetic
    suspend fun groupBan(group: GroupCodeContainer, member: AccountCodeContainer, time: Long): Carrier<Boolean> =
        groupBan(group.groupCode, member.accountCode, time)


    @JvmSynthetic
    suspend fun groupBan(group: GroupContainer, member: AccountContainer, time: Long): Carrier<Boolean> =
        groupBan(group.groupInfo, member.accountInfo, time)


    @JvmSynthetic
    suspend fun <T> groupBan(groupAccountMsg: T, time: Long): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        groupBan(groupAccountMsg, groupAccountMsg, time)


    @JvmSynthetic
    suspend fun <T> groupBan(groupAccountMsg: T, time: Long): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        groupBan(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, time)

    ///////////// blocking /////////////////

    fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        runBlocking { groupBan(groupCode, memberCode, time, timeUnit) }


    fun setGroupBan(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        runBlocking { groupBan(groupCode, memberCode, time, timeUnit) }

    fun setGroupBan(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> =
        runBlocking { groupBan(group, member, time, timeUnit) }

    fun setGroupBan(group: GroupContainer, member: AccountContainer, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        runBlocking { groupBan(group, member, time, timeUnit) }

    fun <T> setGroupBan(groupAccountMsg: T, time: Long, timeUnit: TimeUnit): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { groupBan(groupAccountMsg, time, timeUnit) }

    fun <T> setGroupBan(groupAccountMsg: T, time: Long, timeUnit: TimeUnit): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { groupBan(groupAccountMsg, time, timeUnit) }

    fun setGroupBan(groupCode: String, memberCode: String, time: Long): Carrier<Boolean> =
        runBlocking { groupBan(groupCode, memberCode, time) }

    fun setGroupBan(groupCode: Long, memberCode: Long, time: Long): Carrier<Boolean> =
        runBlocking { groupBan(groupCode, memberCode, time) }

    fun setGroupBan(group: GroupCodeContainer, member: AccountCodeContainer, time: Long): Carrier<Boolean> =
        runBlocking { groupBan(group, member, time) }

    fun setGroupBan(group: GroupContainer, member: AccountContainer, time: Long): Carrier<Boolean> =
        runBlocking { groupBan(group, member, time) }

    fun <T> setGroupBan(groupAccountMsg: T, time: Long): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { groupBan(groupAccountMsg, time) }

    fun <T> setGroupBan(groupAccountMsg: T, time: Long): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { groupBan(groupAccountMsg, time) }

    /**
     * 开启全群禁言。一般需要当前账号拥有对应权限。
     *
     * @param groupCode 群号
     * @param mute 是否开启
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    @JvmSynthetic
    suspend fun groupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean>


    @JvmSynthetic
    suspend fun groupWholeBan(groupCode: Long, mute: Boolean): Carrier<Boolean> =
        groupWholeBan(groupCode.toString(), mute)


    @JvmSynthetic
    suspend fun groupWholeBan(groupCode: GroupCodeContainer, mute: Boolean): Carrier<Boolean> =
        groupWholeBan(groupCode.groupCode, mute)


    @JvmSynthetic
    suspend fun groupWholeBan(groupCode: GroupContainer, mute: Boolean): Carrier<Boolean> =
        groupWholeBan(groupCode.groupInfo, mute)

    //////////////// blocking ///////////////////

    fun setGroupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> =
        runBlocking { groupWholeBan(groupCode, mute) }


    fun setGroupWholeBan(groupCode: Long, mute: Boolean): Carrier<Boolean> =
        runBlocking { groupWholeBan(groupCode, mute) }


    fun setGroupWholeBan(groupCode: GroupCodeContainer, mute: Boolean): Carrier<Boolean> =
        runBlocking { groupWholeBan(groupCode, mute) }


    fun setGroupWholeBan(groupCode: GroupContainer, mute: Boolean): Carrier<Boolean> =
        runBlocking { groupWholeBan(groupCode, mute) }


    /**
     * 设置某个群成员或自己的备注(群名片)。需要当前账号拥有对应的权限。
     *
     * @param groupCode 群号
     * @param memberCode 被设置者的账号
     * @param remark 备注内容
     *
     * @return 设置操作的回执，一般代表设置后的值。**不会捕获异常**。
     *
     */
    @JvmSynthetic
    suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String>


    @JvmSynthetic
    suspend fun groupRemark(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> =
        groupRemark(groupCode.toString(), memberCode.toString(), remark)


    @JvmSynthetic
    suspend fun groupRemark(group: GroupCodeContainer, member: AccountCodeContainer, remark: String?): Carrier<String> =
        groupRemark(group.groupCode, member.accountCode, remark)


    @JvmSynthetic
    suspend fun groupRemark(group: GroupContainer, member: AccountContainer, remark: String?): Carrier<String> =
        groupRemark(group.groupInfo, member.accountInfo, remark)


    @JvmSynthetic
    suspend fun <T> groupRemark(groupAccountMsg: T, remark: String?): Carrier<String>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        groupRemark(groupAccountMsg, groupAccountMsg, remark)


    @JvmSynthetic
    suspend fun <T> groupRemark(groupAccountMsg: T, remark: String?): Carrier<String>
            where T : GroupContainer,
                  T : AccountContainer =
        groupRemark(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, remark)

    ////////////// blocking //////////////////

    fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        runBlocking { groupRemark(groupCode, memberCode, remark) }


    fun setGroupRemark(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> =
        runBlocking { groupRemark(groupCode, memberCode, remark) }


    fun setGroupRemark(group: GroupCodeContainer, member: AccountCodeContainer, remark: String?): Carrier<String> =
        runBlocking { groupRemark(group, member, remark) }


    fun setGroupRemark(group: GroupContainer, member: AccountContainer, remark: String?): Carrier<String> =
        runBlocking { groupRemark(group, member, remark) }


    fun <T> setGroupRemark(groupAccountMsg: T, remark: String?): Carrier<String>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { groupRemark(groupAccountMsg, remark) }


    fun <T> setGroupRemark(groupAccountMsg: T, remark: String?): Carrier<String>
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { groupRemark(groupAccountMsg, remark) }


    /**
     * 退出或解散某群。
     * @param groupCode 要退群的群号
     * @param forcibly 如果账号的退群操作相当于群解散操作，则需要此参数为 `true`才可以解散群聊，否则失败。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     *
     */
    @JvmSynthetic
    suspend fun groupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean>


    @JvmSynthetic
    suspend fun groupQuit(groupCode: Long, forcibly: Boolean): Carrier<Boolean> =
        groupQuit(groupCode.toString(), forcibly)


    @JvmSynthetic
    suspend fun groupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        groupQuit(group.groupCode, forcibly)


    @JvmSynthetic
    suspend fun groupQuit(group: GroupContainer, forcibly: Boolean): Carrier<Boolean> =
        groupQuit(group.groupInfo, forcibly)

    //////////////// blocking /////////////////

    fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> =
        runBlocking { groupQuit(groupCode, forcibly) }


    fun setGroupQuit(groupCode: Long, forcibly: Boolean): Carrier<Boolean> =
        runBlocking { groupQuit(groupCode, forcibly) }


    fun setGroupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        runBlocking { groupQuit(group, forcibly) }


    fun setGroupQuit(group: GroupContainer, forcibly: Boolean): Carrier<Boolean> =
        runBlocking { groupQuit(group, forcibly) }


    /**
     * 踢出某群员。需要拥有对应权限。
     *
     * @param groupCode 群号
     * @param memberCode 群员号
     * @param why 踢出理由。可以为null。
     * @param blackList 踢出后加入黑名单
     */
    @JvmSynthetic
    suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean>


    @JvmSynthetic
    suspend fun groupMemberKick(groupCode: Long, memberCode: Long, why: String?, blackList: Boolean): Carrier<Boolean> =
        groupMemberKick(groupCode.toString(), memberCode.toString(), why, blackList)


    @JvmSynthetic
    suspend fun groupMemberKick(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        groupMemberKick(group.groupCode, member.accountCode, why, blackList)


    @JvmSynthetic
    suspend fun groupMemberKick(
        group: GroupContainer,
        member: AccountContainer,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        groupMemberKick(group.groupInfo, member.accountInfo, why, blackList)


    @JvmSynthetic
    suspend fun <T> groupMemberKick(groupAccountMsg: T, why: String?, blackList: Boolean): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        groupMemberKick(groupAccountMsg, groupAccountMsg, why, blackList)


    @JvmSynthetic
    suspend fun <T> groupMemberKick(groupAccountMsg: T, why: String?, blackList: Boolean): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        groupMemberKick(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, why, blackList)

    /////////////// blocking //////////////////

    fun setGroupMemberKick(groupCode: String, memberCode: String, why: String?, blackList: Boolean): Carrier<Boolean> =
        runBlocking { groupMemberKick(groupCode, memberCode, why, blackList) }


    fun setGroupMemberKick(groupCode: Long, memberCode: Long, why: String?, blackList: Boolean): Carrier<Boolean> =
        runBlocking { groupMemberKick(groupCode, memberCode, why, blackList) }


    fun setGroupMemberKick(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        runBlocking { groupMemberKick(group, member, why, blackList) }


    fun setGroupMemberKick(
        group: GroupContainer,
        member: AccountContainer,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        runBlocking { groupMemberKick(group, member, why, blackList) }


    fun <T> setGroupMemberKick(groupAccountMsg: T, why: String?, blackList: Boolean): Carrier<Boolean>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { groupMemberKick(groupAccountMsg, why, blackList) }


    fun <T> setGroupMemberKick(groupAccountMsg: T, why: String?, blackList: Boolean): Carrier<Boolean>
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { groupMemberKick(groupAccountMsg, why, blackList) }


    /**
     * 设置群成员专属头衔。需要拥有对应权限。
     *
     * @param groupCode 群号
     * @param memberCode 群成员账号
     * @param title 头衔。可以为null。
     *
     * @return 设置操作的回执，一般代表设置后的值。**不会捕获异常**。
     */
    @JvmSynthetic
    suspend fun groupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String>


    @JvmSynthetic
    suspend fun groupMemberSpecialTitle(groupCode: Long, memberCode: Long, title: String?): Carrier<String> =
        groupMemberSpecialTitle(groupCode.toString(), memberCode.toString(), title)


    @JvmSynthetic
    suspend fun groupMemberSpecialTitle(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        title: String?,
    ): Carrier<String> =
        groupMemberSpecialTitle(group.groupCode, member.accountCode, title)


    @JvmSynthetic
    suspend fun groupMemberSpecialTitle(
        group: GroupContainer,
        member: AccountContainer,
        title: String?,
    ): Carrier<String> =
        groupMemberSpecialTitle(group.groupInfo, member.accountInfo, title)


    @JvmSynthetic
    suspend fun <T> groupMemberSpecialTitle(groupAccountMsg: T, title: String?): Carrier<String>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        groupMemberSpecialTitle(groupAccountMsg, groupAccountMsg, title)


    @JvmSynthetic
    suspend fun <T> groupMemberSpecialTitle(groupAccountMsg: T, title: String?): Carrier<String>
            where T : GroupContainer,
                  T : AccountContainer =
        groupMemberSpecialTitle(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, title)

    /////////////// blocking ///////////////

    fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> =
        runBlocking { groupMemberSpecialTitle(groupCode, memberCode, title) }


    fun setGroupMemberSpecialTitle(groupCode: Long, memberCode: Long, title: String?): Carrier<String> =
        runBlocking { groupMemberSpecialTitle(groupCode, memberCode, title) }


    fun setGroupMemberSpecialTitle(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        title: String?,
    ): Carrier<String> =
        runBlocking { groupMemberSpecialTitle(group, member, title) }


    fun setGroupMemberSpecialTitle(group: GroupContainer, member: AccountContainer, title: String?): Carrier<String> =
        runBlocking { groupMemberSpecialTitle(group, member, title) }


    fun <T> setGroupMemberSpecialTitle(groupAccountMsg: T, title: String?): Carrier<String>
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { groupMemberSpecialTitle(groupAccountMsg, title) }


    fun <T> setGroupMemberSpecialTitle(groupAccountMsg: T, title: String?): Carrier<String>
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { groupMemberSpecialTitle(groupAccountMsg, title) }


    /**
     * 设置消息撤回
     *
     * @param flag 消息标识
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获意料外的异常**。
     */
    @JvmSynthetic
    suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean>

    fun setMsgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> =
        runBlocking { msgRecall(flag) }


    /**
     * 设置群名称
     *
     * @param groupCode 群号
     * @param name 群名称，不可为null。
     */
    @JvmSynthetic
    suspend fun groupName(groupCode: String, name: String): Carrier<String>

    @JvmSynthetic
    suspend fun groupName(groupCode: Long, name: String): Carrier<String> =
        groupName(groupCode.toString(), name)

    @JvmSynthetic
    suspend fun groupName(group: GroupCodeContainer, name: String): Carrier<String> = groupName(group.groupCode, name)

    @JvmSynthetic
    suspend fun groupName(group: GroupContainer, name: String): Carrier<String> = groupName(group.groupInfo, name)

    ///////// blocking ///////////

    fun setGroupName(groupCode: String, name: String): Carrier<String> = runBlocking { groupName(groupCode, name) }

    fun setGroupName(groupCode: Long, name: String): Carrier<String> =
        runBlocking { groupName(groupCode, name) }

    fun setGroupName(group: GroupCodeContainer, name: String): Carrier<String> = runBlocking { groupName(group, name) }

    fun setGroupName(group: GroupContainer, name: String): Carrier<String> = runBlocking { groupName(group, name) }


    /**
     * 删除好友
     */
    @JvmSynthetic
    suspend fun friendDelete(friend: String): Carrier<Boolean>

    @JvmSynthetic
    suspend fun friendDelete(friend: Long): Carrier<Boolean> = friendDelete(friend.toString())

    @JvmSynthetic
    suspend fun friendDelete(friend: AccountContainer): Carrier<Boolean> = friendDelete(friend.accountInfo)

    @JvmSynthetic
    suspend fun friendDelete(friend: AccountCodeContainer): Carrier<Boolean> = friendDelete(friend.accountCode)

    //////////// blocking //////////////

    fun setFriendDelete(friend: String): Carrier<Boolean> =
        runBlocking { friendDelete(friend) }

    fun setFriendDelete(friend: Long): Carrier<Boolean> = runBlocking { friendDelete(friend) }

    fun setFriendDelete(friend: AccountContainer): Carrier<Boolean> = runBlocking { friendDelete(friend) }

    fun setFriendDelete(friend: AccountCodeContainer): Carrier<Boolean> = runBlocking { friendDelete(friend) }

}