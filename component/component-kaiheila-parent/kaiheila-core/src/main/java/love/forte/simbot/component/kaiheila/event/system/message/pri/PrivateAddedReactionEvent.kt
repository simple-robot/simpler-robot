package love.forte.simbot.component.kaiheila.event.system.message.pri

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.ReactionEmoji


@Serializable
public data class PrivateAddedReactionEventBody(
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
 * 私聊内用户添加reaction
 *
 * `private_added_reaction`
 * @author ForteScarlet
 */
@Serializable
public data class PrivateAddedReactionEventExtra(override val body: PrivateAddedReactionEventBody) :
    PrivateMessageEventExtra<PrivateAddedReactionEventBody> {
    override val type: String
        get() = "private_added_reaction"
}