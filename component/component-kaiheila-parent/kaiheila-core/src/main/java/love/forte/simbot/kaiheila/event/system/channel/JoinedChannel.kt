package love.forte.simbot.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * 用户加入语音频道
 *
 * `joined_channel`
 *
 * @author ForteScarlet
 */
@Serializable
public data class JoinedChannelExtraBody(
    /**
     * 用户id
     */
    @SerialName("user_id")
    val userId: String,
    /**
     * 加入的频道id
     */
    @SerialName("channel_id")
    val channelId: String,
    /**
     * 加入的时间（ms)
     */
    @SerialName("joined_at")
    val joinedAt: Long,
) : ChannelEventExtraBody
