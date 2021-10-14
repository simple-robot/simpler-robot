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

package love.forte.simbot.kaiheila.event.system.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [自己新加入服务器](https://developer.kaiheila.cn/doc/event/user#%E8%87%AA%E5%B7%B1%E6%96%B0%E5%8A%A0%E5%85%A5%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 *
 * *当自己被邀请或主动加入新的服务器时, 产生该事件*
 *
 *
 * type: `self_joined_guild`
 *
 */
@Serializable
public data class SelfJoinedGuildEventBody(@SerialName("guild_id") val guildId: String) : UserEventExtraBody


@Serializable
public data class SelfJoinedGuildEventExtra(override val body: SelfJoinedGuildEventBody) :
    UserEventExtra<SelfJoinedGuildEventBody> {
    override val type: String
        get() = "self_joined_guild"
}