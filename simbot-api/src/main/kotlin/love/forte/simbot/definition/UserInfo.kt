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

/**
 * 一个用户的 **信息**。
 *
 * 此处仅代表普通的通用信息。
 *
 * [UserInfo] 支持解构：
 *
 * ```kotlin
 * val (id, username, avatar) = userInfo
 * ```
 *
 * @author ForteScarlet
 */
public interface UserInfo : IDContainer {
    
    /**
     * 此用户的ID。
     */
    override val id: ID
    
    /**
     * 这个账号的用户名。
     */
    public val username: String
    
    /**
     * 这个账户的头像。
     * 这年头了，应该不会有什么聊天平台的用户没有头像信息了吧。
     *
     */
    public val avatar: String
    
    /**
     * 此用户（对于bot来讲）的"分组"。
     * 通常情况下，[FriendInfo] 能够支持 "分组" 概念的概率会更大一些，
     * 但是无法保证存在分组概念或支持分组的获取。
     *
     * 因此当不支持获取分组、不存在分组等情况下，[category] 将会得到 `null`。
     */
    public val category: Category? get() = null
}

// region 解构声明

/**
 * 对 [UserInfo] 的结构解构, 第1个值。相当于 [UserInfo.id]。
 *
 * ```kotlin
 * val (id, username, avatar) = user
 * ```
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun UserInfo.component1(): ID = id

/**
 * 对 [UserInfo] 的结构解构, 第2个值。相当于 [UserInfo.username]。
 *
 * ```kotlin
 * val (id, username, avatar) = user
 * ```
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun UserInfo.component2(): String = username

/**
 * 对 [UserInfo] 的结构解构, 第3个值。相当于 [UserInfo.avatar]。
 *
 * ```kotlin
 * val (id, username, avatar) = user
 * ```
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun UserInfo.component3(): String = avatar

// endregion
