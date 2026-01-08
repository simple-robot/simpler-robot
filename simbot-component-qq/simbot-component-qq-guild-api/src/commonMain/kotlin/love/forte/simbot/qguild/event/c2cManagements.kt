/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * [C2CManagementDispatch] 的事件数据
 * @property timestamp 添加时间戳
 * @property openid 用户openid
 */
@Serializable
public data class C2CManagementData(
    val timestamp: String,
    val openid: String,
)

/**
 * 用户模块-用户管理相关事件。
 * [data] 类型为 [C2CManagementData]
 */
public sealed class C2CManagementDispatch : Signal.Dispatch() {
    abstract override val data: C2CManagementData
}

/**
 * [用户添加机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/user/manage/event.html#用户添加机器人)
 *
 * 触发场景	用户添加机器人'好友'到消息列表
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.FRIEND_ADD_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.FRIEND_ADD_TYPE)
public data class FriendAdd(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: C2CManagementData
) : C2CManagementDispatch()

/**
 * [用户删除机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/user/manage/event.html#用户删除机器人)
 *
 * 触发场景	用户删除机器人'好友'
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.FRIEND_DEL_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.FRIEND_DEL_TYPE)
public data class FriendDel(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: C2CManagementData
) : C2CManagementDispatch()

/**
 * [拒绝机器人主动消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/user/manage/event.html#拒绝机器人主动消息)
 *
 * 触发场景	用户在机器人资料卡手动关闭"主动消息"推送
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.C2C_MSG_REJECT_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.C2C_MSG_REJECT_TYPE)
public data class C2CMsgReject(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: C2CManagementData
) : C2CManagementDispatch()

/**
 * [允许机器人主动消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/user/manage/event.html#允许机器人主动消息)
 *
 * 触发场景	用户在机器人资料卡手动开启"主动消息"推送开关
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.C2C_MSG_RECEIVE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.C2C_MSG_RECEIVE_TYPE)
public data class C2CMsgReceive(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: C2CManagementData
) : C2CManagementDispatch()
