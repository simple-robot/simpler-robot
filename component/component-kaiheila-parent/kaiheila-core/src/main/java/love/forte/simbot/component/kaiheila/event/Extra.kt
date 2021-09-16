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

@file:JvmName("Extras")
package love.forte.simbot.component.kaiheila.event

import kotlinx.serialization.SerialName
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User


/**
 * 文字频道消息extra.
 *
 * @see Event.Extra
 *
 */
public interface TextExtra : Event.Extra {
    override val type: Int

    /**
     * 服务器 id
     */
    @SerialName("guild_id")
    val guildId: String

    /**
     * 频道名
     */
    @SerialName("channel_name")
    val channelName: String

    /**
     * 提及到的用户 id 的列表
     */
    val mention: List<String>

    /**
     * 是否 mention 所有用户
     */
    @SerialName("mention_all")
    val mentionAll: Boolean

    /**
     * mention 用户角色的数组
     */
    @SerialName("mention_roles")
    val mentionRoles: List<Role>


    /**
     * 是否 mention 在线用户
     */
    @SerialName("mention_here")
    val mentionHere: Boolean

    /**
     * 用户信息, 见 [对象-用户User](https://developer.kaiheila.cn/doc/objects#%E7%94%A8%E6%88%B7User) ([User])
     */
    val author: User
}


