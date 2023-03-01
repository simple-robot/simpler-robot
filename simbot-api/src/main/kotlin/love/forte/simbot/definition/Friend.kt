/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.bot.Bot


/**
 * 一个 **好友**。
 */
public interface Friend : Contact, BotContainer, FriendInfo {
    override val id: ID
    override val bot: Bot
    override val remark: String?
    override val username: String
    override val avatar: String
}

/**
 * 一个好友基本信息。
 *
 * 支持解构：
 * ```kotlin
 * val (id, username, avatar, remark) = friendInfo
 * ```
 *
 * 解构结果前三个属性来自 [UserInfo]。
 *
 */
public interface FriendInfo : UserInfo {
    override val id: ID
    override val username: String
    override val avatar: String
    
    /**
     * 在Bot眼中，一个好友可能存在一个备注。
     */
    public val remark: String?
    
    /**
     * 优先尝试获取好友的 [remark], 如果 [remark] 为null，则取其 [username].
     */
    public val remarkOrUsername: String get() = remark ?: username
    
}

/**
 * [FriendInfo] 的解构扩展第4个属性，相当于 [FriendInfo.remark]。
 *
 * 前三个属性来自于 [UserInfo]。
 *
 * ```kotlin
 * val (id, username, avatar, remark) = friendInfo
 * ```
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun FriendInfo.component4(): String? = remark

