package love.forte.simbot.component.kaiheila.event.system.guild

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
) : GuildEventExtraBody