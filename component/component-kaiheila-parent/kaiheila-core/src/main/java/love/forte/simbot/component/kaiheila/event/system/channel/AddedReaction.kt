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

package love.forte.simbot.component.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.ReactionEmoji


/**
 * [频道内用户添加 reaction](https://developer.kaiheila.cn/doc/event/channel#%E9%A2%91%E9%81%93%E5%86%85%E7%94%A8%E6%88%B7%E6%B7%BB%E5%8A%A0%20reaction)
 * @author ForteScarlet
 */
@Serializable
public data class AddedReactionExtraBody(
    /**
     * 用户点击的消息id
     */
    @SerialName("msg_id")
    val msgId: String,

    /**
     * 点击的用户
     */
    @SerialName("user_id")
    val userId: String,

    /**
     * 频道id
     */
    @SerialName("channel_id")
    val channelId: String,

    /**
     * emoji	Map	消息对象, 包含 id 表情id, name 表情名称
     */
    val emoji: ReactionEmoji,
) : ChannelEventExtraBody
