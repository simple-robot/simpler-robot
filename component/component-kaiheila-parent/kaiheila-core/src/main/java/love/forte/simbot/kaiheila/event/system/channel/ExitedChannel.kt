package love.forte.simbot.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 用户退出语音频道
 *
 * `exited_channel`
 *
 * @author ForteScarlet
 */
@Serializable
public data class ExitedChannelExtraBody(
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
     * 退出的时间（ms)
     */
    @SerialName("exited_at")
    val exitedAt: Long,
) : ChannelEventExtraBody
