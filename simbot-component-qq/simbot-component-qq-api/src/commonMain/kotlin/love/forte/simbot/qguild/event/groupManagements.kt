/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property timestamp 加入的时间戳
 * @property groupOpenid 加入群的群openid
 * @property opMemberOpenid 操作添加机器人进群的群成员openid
 */
@Serializable
public data class GroupRobotManagementData(
    val timestamp: String,
    @SerialName("group_openid")
    val groupOpenid: String,
    @SerialName("op_member_openid")
    val opMemberOpenid: String,
)

/**
 * [GroupMemberManagementDispatch] 事件的数据体。
 *
 * @property timestamp 触发时间戳
 * @property groupOpenid 群 openid
 * @property memberOpenid 群成员 openid
 *
 * @since 4.4.0
 */
@Serializable
public data class GroupMemberManagementData(
    val timestamp: String,
    @SerialName("group_openid")
    val groupOpenid: String,
    @SerialName("member_openid")
    val memberOpenid: String,
)

/**
 * 群聊模块-群管理相关事件。
 * [data] 类型为 [GroupRobotManagementData]
 */
public sealed class GroupRobotManagementDispatch : Signal.Dispatch() {
    abstract override val data: GroupRobotManagementData
}

/**
 * [机器人加入群聊](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/group/manage/event.html#机器人加入群聊)
 *
 * 触发场景	机器人被添加到群聊
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_ADD_ROBOT_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_ADD_ROBOT_TYPE)
public data class GroupAddRobot(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupRobotManagementData
) : GroupRobotManagementDispatch()

/**
 * [机器人退出群聊](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/group/manage/event.html#机器人退出群聊)
 *
 * 触发场景	机器人被移出群聊
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_DEL_ROBOT_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_DEL_ROBOT_TYPE)
public data class GroupDelRobot(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupRobotManagementData
) : GroupRobotManagementDispatch()

/**
 * [群聊拒绝机器人主动消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/group/manage/event.html#群聊拒绝机器人主动消息)
 *
 * 触发场景	群管理员主动在机器人资料页操作关闭通知
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_MSG_REJECT_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_MSG_REJECT_TYPE)
public data class GroupMsgReject(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupRobotManagementData
) : GroupRobotManagementDispatch()

/**
 * [群聊接受机器人主动消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/group/manage/event.html#群聊接受机器人主动消息)
 *
 * 触发场景	群管理员主动在机器人资料页操作开启通知
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_MSG_RECEIVE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_MSG_RECEIVE_TYPE)
public data class GroupMsgReceive(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupRobotManagementData
) : GroupRobotManagementDispatch()

/**
 * 群成员进退群聊事件。
 * [data] 类型为 [GroupMemberManagementData]
 *
 * @since 4.4.0
 */
public sealed class GroupMemberManagementDispatch : Signal.Dispatch() {
    abstract override val data: GroupMemberManagementData
}

/**
 * [群成员加入群聊](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/group/manage/event.html#群成员加入-退出群聊)
 *
 * 触发场景 成员加入群聊
 *
 * @since 4.4.0
 */
@Serializable
@SerialName(EventIntents.GroupMembers.GROUP_MEMBER_ADD_TYPE)
@DispatchTypeName(EventIntents.GroupMembers.GROUP_MEMBER_ADD_TYPE)
public data class GroupMemberAdd(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupMemberManagementData
) : GroupMemberManagementDispatch()

/**
 * [群成员退出群聊](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/group/manage/event.html#群成员加入-退出群聊)
 *
 * 触发场景 成员退出群聊
 *
 * @since 4.4.0
 */
@Serializable
@SerialName(EventIntents.GroupMembers.GROUP_MEMBER_REMOVE_TYPE)
@DispatchTypeName(EventIntents.GroupMembers.GROUP_MEMBER_REMOVE_TYPE)
public data class GroupMemberRemove(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupMemberManagementData
) : GroupMemberManagementDispatch()
