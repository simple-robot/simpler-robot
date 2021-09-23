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
 *
 * [用户加入语音频道](https://developer.kaiheila.cn/doc/event/user#%E7%94%A8%E6%88%B7%E5%8A%A0%E5%85%A5%E8%AF%AD%E9%9F%B3%E9%A2%91%E9%81%93)
 *
 * type: `joined_channel`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UserJoinedChannelEventBody(
    @SerialName("user_id")
    val userId: String,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("joined_at")
    val joinedAt: Long,
) : UserEventExtraBody


@Serializable
public data class UserJoinedChannelEventExtra(override val body: UserJoinedChannelEventBody) : UserEventExtra<UserJoinedChannelEventBody> {
    override val type: String
        get() = "joined_channel"
}