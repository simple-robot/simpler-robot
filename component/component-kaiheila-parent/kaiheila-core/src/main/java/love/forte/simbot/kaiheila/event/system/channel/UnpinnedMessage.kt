package love.forte.simbot.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * 取消频道置顶消息
 *
 * `unpinned_message`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UnpinnedMessageExtraBody(
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("operator_id")
    val operatorId: String,
    @SerialName("msg_id")
    val msgId : String,
) : ChannelEventExtraBody
