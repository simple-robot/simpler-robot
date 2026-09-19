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
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.EventModelConstructor

/**
 * @property timestamp 加入的时间戳
 * @property groupOpenid 加入群的群openid
 * @property opMemberOpenid 操作添加机器人进群的群成员openid
 */
@Serializable
public class GroupRobotManagementData @EventModelConstructor constructor(
    public val timestamp: String,
    @SerialName("group_openid")
    public val groupOpenid: String,
    @SerialName("op_member_openid")
    public val opMemberOpenid: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroupRobotManagementData) return false

        if (timestamp != other.timestamp) return false
        if (groupOpenid != other.groupOpenid) return false
        if (opMemberOpenid != other.opMemberOpenid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + groupOpenid.hashCode()
        result = 31 * result + opMemberOpenid.hashCode()
        return result
    }

    override fun toString(): String {
        return "GroupRobotManagementData(" +
            "timestamp='$timestamp', " +
            "groupOpenid='$groupOpenid', " +
            "opMemberOpenid='$opMemberOpenid')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
    public operator fun component1(): String = timestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("groupOpenid"))
    public operator fun component2(): String = groupOpenid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("opMemberOpenid"))
    public operator fun component3(): String = opMemberOpenid

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        timestamp: String = this.timestamp,
        groupOpenid: String = this.groupOpenid,
        opMemberOpenid: String = this.opMemberOpenid,
    ): GroupRobotManagementData = GroupRobotManagementData(timestamp, groupOpenid, opMemberOpenid)
    //endregion
}

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
public class GroupMemberManagementData @EventModelConstructor constructor(
    public val timestamp: String,
    @SerialName("group_openid")
    public val groupOpenid: String,
    @SerialName("member_openid")
    public val memberOpenid: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroupMemberManagementData) return false

        if (timestamp != other.timestamp) return false
        if (groupOpenid != other.groupOpenid) return false
        if (memberOpenid != other.memberOpenid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + groupOpenid.hashCode()
        result = 31 * result + memberOpenid.hashCode()
        return result
    }

    override fun toString(): String {
        return "GroupMemberManagementData(" +
            "timestamp='$timestamp', " +
            "groupOpenid='$groupOpenid', " +
            "memberOpenid='$memberOpenid')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
    public operator fun component1(): String = timestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("groupOpenid"))
    public operator fun component2(): String = groupOpenid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("memberOpenid"))
    public operator fun component3(): String = memberOpenid

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        timestamp: String = this.timestamp,
        groupOpenid: String = this.groupOpenid,
        memberOpenid: String = this.memberOpenid,
    ): GroupMemberManagementData = GroupMemberManagementData(timestamp, groupOpenid, memberOpenid)
    //endregion
}


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
public data class GroupAddRobot @EventModelConstructor constructor(
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
public data class GroupDelRobot @EventModelConstructor constructor(
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
public data class GroupMsgReject @EventModelConstructor constructor(
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
public data class GroupMsgReceive @EventModelConstructor constructor(
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
public data class GroupMemberAdd @EventModelConstructor constructor(
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
public data class GroupMemberRemove @EventModelConstructor constructor(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: GroupMemberManagementData
) : GroupMemberManagementDispatch()
