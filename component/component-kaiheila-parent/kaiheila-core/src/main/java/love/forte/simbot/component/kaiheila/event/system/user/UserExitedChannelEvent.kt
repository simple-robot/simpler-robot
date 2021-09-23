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

package love.forte.simbot.component.kaiheila.event.system.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * [用户退出语音频道](https://developer.kaiheila.cn/doc/event/user#%E7%94%A8%E6%88%B7%E9%80%80%E5%87%BA%E8%AF%AD%E9%9F%B3%E9%A2%91%E9%81%93)
 *
 * type: `exited_channel`
 *
 */
@Serializable
public data class UserExitedChannelEventBody(
    @SerialName("user_id")
    val userId: String,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("exited_at")
    val exitedAt: Long,
) : UserEventExtraBody



@Serializable
public data class UserExitedChannelEventExtra(override val body: UserExitedChannelEventBody) : UserEventExtra<UserExitedChannelEventBody> {
    override val type: String
        get() = "exited_channel"
}


