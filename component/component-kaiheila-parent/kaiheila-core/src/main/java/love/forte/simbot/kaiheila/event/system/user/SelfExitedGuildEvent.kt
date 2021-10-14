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
 *
 * [自己退出服务器](https://developer.kaiheila.cn/doc/event/user#%E8%87%AA%E5%B7%B1%E9%80%80%E5%87%BA%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * type: `self_exited_guild`
 *
 */
@Serializable
public data class SelfExitedGuildEventBody(@SerialName("guild_id") val guildId: String) : UserEventExtraBody


@Serializable
public data class SelfExitedGuildEventExtra(override val body: SelfExitedGuildEventBody) :
    UserEventExtra<SelfExitedGuildEventBody> {
    override val type: String
        get() = "self_exited_guild"
}

