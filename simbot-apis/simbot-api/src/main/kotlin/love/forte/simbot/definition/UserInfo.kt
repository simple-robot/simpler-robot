/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
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