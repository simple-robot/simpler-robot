package love.forte.simbot.component.kaiheila.event.system.message.pri

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
public data class UpdatedPrivateMessageEventBody(
    @SerialName("msg_id")
    val msgId: String,
    @SerialName("chat_code")
    val chatCode: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("target_id")
    val targetId: String,
    @SerialName("content")
    val content: String,
    @SerialName("updated_at")
    val updatedAt: Long,
) : PrivateMessageEventExtraBody


/**
 * 私聊消息更新
 *
 * `updated_private_message`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UpdatedPrivateMessageEventExtra(override val body: UpdatedPrivateMessageEventBody) :
    PrivateMessageEventExtra<UpdatedPrivateMessageEventBody> {
    override val type: String
        get() = "updated_private_message"
}


