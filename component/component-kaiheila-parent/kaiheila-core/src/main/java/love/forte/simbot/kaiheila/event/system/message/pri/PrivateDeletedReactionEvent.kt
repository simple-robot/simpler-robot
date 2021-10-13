package love.forte.simbot.kaiheila.event.system.message.pri

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.ReactionEmoji


@Serializable
public data class PrivateDeletedReactionEventBody(
    @SerialName("msg_id")
    val msgId: String,
    @SerialName("chat_code")
    val chatCode: String,
    @SerialName("channel_id")
    val channelId: String,
    val emoji: ReactionEmoji,
    @SerialName("user_id")
    val userId: String,
) : PrivateMessageEventExtraBody



/**
 *
 * 私聊内用户删除reaction
 *
 * `private_deleted_reaction`
 * @author ForteScarlet
 *
 */
@Serializable
public data class PrivateDeletedReactionEventExtra(override val body: PrivateDeletedReactionEventBody) :
    PrivateMessageEventExtra<PrivateDeletedReactionEventBody> {
    override val type: String
        get() = "private_deleted_reaction"
}
