package love.forte.simbot.component.kaiheila.event.system.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
public data class DeletedMessageEventBody(
    /**
     * 被删除的消息id
     */
    @SerialName("msg_id")
    val msgId: String,

    /**
     * 消息所在频道id
     */
    @SerialName("channel_id")
    val channelId: String,
) : MessageEventExtraBody



/**
 *  频道消息被删除
 *
 * `deleted_message`
 *
 * @author ForteScarlet
 */
@Serializable
public data class DeletedMessageEventExtra(override val body: DeletedMessageEventBody) :
    MessageEventExtra<DeletedMessageEventBody> {
    override val type: String
        get() = "deleted_message"
}