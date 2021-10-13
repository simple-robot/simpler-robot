package love.forte.simbot.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 频道消息被删除
 *
 * deleted_message
 *
 */
@Serializable
public data class DeletedMessageExtraBody(
    @SerialName("msg_id")
    val msgId: String,
    @SerialName("channel_id")
    val channelId: String,
) : ChannelEventExtraBody