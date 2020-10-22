/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Setter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.api.sender

import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.events.MessageEventGet
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
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
public interface Setter {


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
    fun setFriendAddRequest(flag: Flag<FriendAddRequest.FlagContent>, friendRemark: String?, agree: Boolean, blackList: Boolean) : Carrier<Boolean>


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
    fun setGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, agree: Boolean, blackList: Boolean, why: String?): Carrier<Boolean>


    /**
     * 设置群管理。 一般来讲需要账号权限为群主才能操作。
     *
     * @param groupCode 群号
     * @param memberCode 成员账号
     * @param promotion `true`为设置为管理员，`false`为取消管理员。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean>
    @JvmDefault
    fun setGroupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> =
        setGroupAdmin(groupCode.toString(), memberCode.toString(), promotion)
    @JvmDefault
    fun setGroupAdmin(group: GroupCodeContainer, member: AccountCodeContainer, promotion: Boolean): Carrier<Boolean> =
        setGroupAdmin(group.groupCode, member.accountCode, promotion)

    @JvmDefault
    fun setGroupAdmin(group: GroupContainer, member: AccountContainer, promotion: Boolean): Carrier<Boolean> =
        setGroupAdmin(group.groupInfo, member.accountInfo, promotion)
    @JvmDefault
    fun <T> setGroupAdmin(groupAccountMsg: T, promotion: Boolean) : Carrier<Boolean>
            where T: GroupCodeContainer,
                  T: AccountCodeContainer =
        setGroupAdmin(groupAccountMsg, groupAccountMsg, promotion)
    @JvmDefault
    fun <T> setGroupAdmin(groupAccountMsg: T, promotion: Boolean) : Carrier<Boolean>
            where T: GroupContainer,
                  T: AccountContainer =
        setGroupAdmin(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, promotion)



    /**
     * 设置群匿名聊天。
     * **不会捕获异常**。
     * @param group 群号
     * @param agree 是否允许群匿名聊天
     *
     * @return 设置操作的回执，一般代表最终设置后的开启状态。如果不支持设置的话返回值则代表当前状态。
     */
    fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean>
    @JvmDefault
    fun setGroupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> = setGroupAnonymous(group.toString(), agree)
    @JvmDefault
    fun setGroupAnonymous(group: GroupCodeContainer, agree: Boolean): Carrier<Boolean> = setGroupAnonymous(group.groupCode, agree)
    @JvmDefault
    fun setGroupAnonymous(group: GroupContainer, agree: Boolean): Carrier<Boolean> = setGroupAnonymous(group.groupInfo, agree)


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
    fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean>
    @JvmDefault
    fun setGroupBan(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        setGroupBan(groupCode.toString(), memberCode.toString(), time, timeUnit)
    @JvmDefault
    fun setGroupBan(group: GroupCodeContainer, member: AccountCodeContainer, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        setGroupBan(group.groupCode, member.accountCode, time, timeUnit)
    @JvmDefault
    fun setGroupBan(group: GroupContainer, member: AccountContainer, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        setGroupBan(group.groupInfo, member.accountInfo, time, timeUnit)
    @JvmDefault
    fun <T> setGroupBan(groupAccountMsg: T, time: Long, timeUnit: TimeUnit) : Carrier<Boolean>
            where T: GroupCodeContainer,
                  T: AccountCodeContainer =
        setGroupBan(groupAccountMsg, groupAccountMsg, time, timeUnit)
    @JvmDefault
    fun <T> setGroupBan(groupAccountMsg: T, time: Long, timeUnit: TimeUnit) : Carrier<Boolean>
            where T: GroupContainer,
                  T: AccountContainer =
        setGroupBan(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, time, timeUnit)

    ////

    @JvmDefault
    fun setGroupBan(groupCode: String, memberCode: String, time: Long): Carrier<Boolean> =
        setGroupBan(groupCode, memberCode, time, TimeUnit.SECONDS)
    @JvmDefault
    fun setGroupBan(groupCode: Long, memberCode: Long, time: Long): Carrier<Boolean> =
        setGroupBan(groupCode.toString(), memberCode.toString(), time, TimeUnit.SECONDS)
    @JvmDefault
    fun setGroupBan(group: GroupCodeContainer, member: AccountCodeContainer, time: Long): Carrier<Boolean> =
        setGroupBan(group.groupCode, member.accountCode, time)
    @JvmDefault
    fun setGroupBan(group: GroupContainer, member: AccountContainer, time: Long): Carrier<Boolean> =
        setGroupBan(group.groupInfo, member.accountInfo, time)
    @JvmDefault
    fun <T> setGroupBan(groupAccountMsg: T, time: Long) : Carrier<Boolean>
            where T: GroupCodeContainer,
                  T: AccountCodeContainer =
        setGroupBan(groupAccountMsg, groupAccountMsg, time)
    @JvmDefault
    fun <T> setGroupBan(groupAccountMsg: T, time: Long) : Carrier<Boolean>
            where T: GroupContainer,
                  T: AccountContainer =
        setGroupBan(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, time)


    /**
     * 开启全群禁言。一般需要当前账号拥有对应权限。
     *
     * @param groupCode 群号
     * @param ban 是否开启
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean>
    @JvmDefault
    fun setGroupWholeBan(groupCode: Long, ban: Boolean): Carrier<Boolean> =
        setGroupWholeBan(groupCode.toString(), ban)
    @JvmDefault
    fun setGroupWholeBan(groupCode: GroupCodeContainer, ban: Boolean): Carrier<Boolean> =
        setGroupWholeBan(groupCode.groupCode, ban)
    @JvmDefault
    fun setGroupWholeBan(groupCode: GroupContainer, ban: Boolean): Carrier<Boolean> =
        setGroupWholeBan(groupCode.groupInfo, ban)


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
    fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String>
    @JvmDefault
    fun setGroupRemark(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> =
        setGroupRemark(groupCode.toString(), memberCode.toString(), remark)
    @JvmDefault
    fun setGroupRemark(group: GroupCodeContainer, member: AccountCodeContainer, remark: String?): Carrier<String> =
        setGroupRemark(group.groupCode, member.accountCode, remark)
    @JvmDefault
    fun setGroupRemark(group: GroupContainer, member: AccountContainer, remark: String?): Carrier<String> =
        setGroupRemark(group.groupInfo, member.accountInfo, remark)
    @JvmDefault
    fun <T> setGroupRemark(groupAccountMsg: T, remark: String?) : Carrier<String>
            where T: GroupCodeContainer,
                  T: AccountCodeContainer =
        setGroupRemark(groupAccountMsg, groupAccountMsg, remark)
    @JvmDefault
    fun <T> setGroupRemark(groupAccountMsg: T, remark: String?) : Carrier<String>
            where T: GroupContainer,
                  T: AccountContainer =
        setGroupRemark(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, remark)


    /**
     * 退出或解散某群。
     * @param groupCode 要退群的群号
     * @param forcibly 如果账号的退群操作相当于群解散操作，则需要此参数为 `true`才可以解散群聊，否则失败。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     *
     */
    fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean>
    @JvmDefault
    fun setGroupQuit(groupCode: Long, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit(groupCode.toString(), forcibly)
    @JvmDefault
    fun setGroupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit(group.groupCode, forcibly)
    @JvmDefault
    fun setGroupQuit(group: GroupContainer, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit(group.groupInfo, forcibly)

    /**
     * 踢出某群员。需要拥有对应权限。
     *
     * @param groupCode 群号
     * @param memberCode 群员号
     * @param why 踢出理由。可以为null。
     * @param blackList 踢出后加入黑名单
     */
    fun setGroupMemberKick(groupCode: String, memberCode: String, why: String?, blackList: Boolean): Carrier<Boolean>
    @JvmDefault
    fun setGroupMemberKick(groupCode: Long, memberCode: Long, why: String?, blackList: Boolean): Carrier<Boolean> =
        setGroupMemberKick(groupCode.toString(), memberCode.toString(), why, blackList)
    @JvmDefault
    fun setGroupMemberKick(group: GroupCodeContainer, member: AccountCodeContainer, why: String?, blackList: Boolean): Carrier<Boolean> =
        setGroupMemberKick(group.groupCode, member.accountCode, why, blackList)
    @JvmDefault
    fun setGroupMemberKick(group: GroupContainer, member: AccountContainer, why: String?, blackList: Boolean): Carrier<Boolean> =
        setGroupMemberKick(group.groupInfo, member.accountInfo, why, blackList)
    @JvmDefault
    fun <T> setGroupMemberKick(groupAccountMsg: T, why: String?, blackList: Boolean) : Carrier<Boolean>
            where T: GroupCodeContainer,
                  T: AccountCodeContainer =
        setGroupMemberKick(groupAccountMsg, groupAccountMsg, why, blackList)
    @JvmDefault
    fun <T> setGroupMemberKick(groupAccountMsg: T, why: String?, blackList: Boolean) : Carrier<Boolean>
            where T: GroupContainer,
                  T: AccountContainer =
        setGroupMemberKick(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, why, blackList)

    /**
     * 设置群成员专属头衔。需要拥有对应权限。
     *
     * @param groupCode 群号
     * @param memberCode 群成员账号
     * @param title 头衔。可以为null。
     *
     * @return 设置操作的回执，一般代表设置后的值。**不会捕获异常**。
     */
    fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String>
    @JvmDefault
    fun setGroupMemberSpecialTitle(groupCode: Long, memberCode: Long, title: String?): Carrier<String> =
        setGroupMemberSpecialTitle(groupCode.toString(), memberCode.toString(), title)
    @JvmDefault
    fun setGroupMemberSpecialTitle(group: GroupCodeContainer, member: AccountCodeContainer, title: String?): Carrier<String> =
        setGroupMemberSpecialTitle(group.groupCode, member.accountCode, title)
    @JvmDefault
    fun setGroupMemberSpecialTitle(group: GroupContainer, member: AccountContainer, title: String?): Carrier<String> =
        setGroupMemberSpecialTitle(group.groupInfo, member.accountInfo, title)
    @JvmDefault
    fun <T> setGroupMemberSpecialTitle(groupAccountMsg: T, title: String?) : Carrier<String>
            where T: GroupCodeContainer,
                  T: AccountCodeContainer =
        setGroupMemberSpecialTitle(groupAccountMsg, groupAccountMsg, title)
    @JvmDefault
    fun <T> setGroupMemberSpecialTitle(groupAccountMsg: T, title: String?) : Carrier<String>
            where T: GroupContainer,
                  T: AccountContainer =
        setGroupMemberSpecialTitle(groupAccountMsg.groupInfo, groupAccountMsg.accountInfo, title)

    /**
     * 设置消息撤回
     *
     * @param flag 消息标识
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获意料外的异常**。
     */
    fun setMsgRecall(flag: Flag<MessageEventGet.MessageFlagContent>): Carrier<Boolean>


    /**
     * 设置群名称
     *
     * @param groupCode 群号
     * @param name 群名称，不可为null。
     */
    fun setGroupName(groupCode: String, name: String): Carrier<String>
    @JvmDefault
    fun setGroupName(groupCode: Long, name: String): Carrier<String> =
        setGroupName(groupCode.toString(), name)
    @JvmDefault
    fun setGroupName(group: GroupCodeContainer, name: String): Carrier<String> = setGroupName(group.groupCode, name)
    @JvmDefault
    fun setGroupName(group: GroupContainer, name: String): Carrier<String> = setGroupName(group.groupInfo, name)



}