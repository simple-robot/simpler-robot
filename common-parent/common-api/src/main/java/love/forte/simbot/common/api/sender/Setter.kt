/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Setter.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.common.api.sender

import love.forte.simbot.common.utils.Carrier
import love.forte.simbot.common.api.message.assists.Flag
import love.forte.simbot.common.api.message.events.FriendAddRequest
import love.forte.simbot.common.api.message.events.GroupAddRequest
import love.forte.simbot.common.api.message.MessageEventGet

/**
 *
 * 状态设置器。
 * 一般用于发送一些消息无关的数据，例如设置禁言等。
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
     * @param agree 是否同意
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setFriendAddRequest(flag: Flag<FriendAddRequest.FlagContent>, friendRemark: String?, agree: Boolean) : Carrier<Boolean>


    /**
     * 处理群添加申请。
     *
     * @param flag [GroupAddRequest] 事件的 [flag][GroupAddRequest.flag]
     * @param agree 是否同意
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, agree: Boolean): Carrier<Boolean>


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


    /**
     * 设置群匿名聊天。
     *
     * @param group 群号
     * @param agree 是否允许群匿名聊天
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean>


    /**
     * 群内禁言某人。
     *
     * @param groupCode 群号
     * @param memberCode 被禁言者账号
     * @param time 时长。如果组件没有特殊说明，则此处代表 **秒** 。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupBan(groupCode: String, memberCode: String, time: Long): Carrier<Boolean>


    /**
     * 开启全群禁言。一般需要当前账号拥有对应权限。
     *
     * @param groupCode 群号
     * @param ban 是否开启
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean>


    /**
     * 设置某个群成员或自己的备注(群名片)。需要当前账号拥有对应的权限。
     *
     * @param groupCode 群号
     * @param memberCode 被设置者的账号
     * @param remark 备注内容
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     *
     */
    fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<Boolean>


    /**
     * 退出或解散某群。
     * @param groupCode 要退群的群号
     * @param forcibly 如果账号的退群操作相当于群解散操作，则需要此参数为 `true`才可以解散群聊，否则失败。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     *
     */
    fun setGroupLeave(groupCode: String, forcibly: Boolean): Carrier<Boolean>


    /**
     * 踢出某群员。需要拥有对应权限。
     *
     * @param groupCode 群号
     * @param memberCode 群员号
     * @param blackList 踢出后加入黑名单
     */
    fun setGroupMemberKick(groupCode: String, memberCode: String, blackList: Boolean): Carrier<Boolean>


    /**
     * 设置群成员专属头衔。需要拥有对应权限。
     *
     * @param groupCode 群号
     * @param memberCode 群成员账号
     * @param title 头衔。可以为null。
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<Boolean>


    /**
     * 设置消息撤回
     *
     * @param flag 消息标识
     *
     * @return 设置操作的回执，一般代表是否成功。**不会捕获异常**。
     */
    fun setMsgRecall(flag: Flag<MessageEventGet.MessageFlagContent>): Carrier<Boolean>


    /**
     * 设置群名称
     *
     * @param groupCode 群号
     * @param name 群名称
     */
    fun setGroupName(groupCode: String, name: String): Carrier<Boolean>




}