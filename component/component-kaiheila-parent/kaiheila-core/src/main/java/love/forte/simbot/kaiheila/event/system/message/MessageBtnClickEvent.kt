@file:Suppress("unused")

package love.forte.simbot.kaiheila.event.system.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.objects.User


@Serializable
public data class MessageBtnClickEventBody(
    /**
     * 用户id
     */
    @SerialName("user_id")
    val userId: String,

    /**
     * 消息id
     */
    @SerialName("msg_id")
    val msgId: String,
    /**
     * 按钮return-val的返回值
     */
    val value: String,
    /**
     * 消息所在频道的id
     */
    val targetId: String,

    /**
     * 服务器id
     */
    val guildId: String,

    @SerialName("user_info")
    private val _userInfo: User,
) : MessageEventExtraBody {
    val userInfo: User get() = _userInfo
}


/**
 * 用户点击按钮（Card Message）
 *
 * `message_btn_click`
 *
 * @author ForteScarlet
 */
@Serializable
public data class MessageBtnClickEventExtra(override val body: MessageBtnClickEventBody) :
    MessageEventExtra<MessageBtnClickEventBody> {
    override val type: String
        get() = "message_btn_click"
}

