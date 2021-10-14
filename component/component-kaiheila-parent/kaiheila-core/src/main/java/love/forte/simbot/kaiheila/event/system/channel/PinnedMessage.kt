package love.forte.simbot.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * 新的频道置顶消息
 *
 * `pinned_message`
 *
 * @author ForteScarlet
 */
@Serializable
public data class PinnedMessageExtraBody(
    /**
     * 发生操作的频道id
     */
    @SerialName("channel_id")
    val channelId: String,
    /**
     * 操作人的用户id
     */
    @SerialName("operator_id")
    val operatorId: String,
    /**
     * 被操作的消息id
     */
    @SerialName("msg_id")
    val msgId: String
) : ChannelEventExtraBody
