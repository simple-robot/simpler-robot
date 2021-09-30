package love.forte.simbot.component.kaiheila.event.system.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.User


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
    private val _userInfo: BtnClickUserInfo,
) : MessageEventExtraBody {
    val userInfo: User get() = _userInfo
}


@Serializable
public data class BtnClickUserInfo(
    override val id: String,
    override val username: String,
    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     */
    @SerialName("identify_num")
    override val identifyNum: String,
    override val online: Boolean,
    /**
     * 用户的状态, 0代表正常，10代表被封禁
     * Allowed: 0┃10
     */
    override val status: Int,
    override val bot: Boolean,
    override val avatar: String,
    @SerialName("vip_avatar")
    override val vipAvatar: String? = null,
) : User {
    init {
        check(status == 0 || status == 10) { "Parameter status must be 0 or 10, but $status" }
    }

    override val nickname: String get() = ""
    override val mobileVerified: Boolean get() = false
    override val roles: List<Int> get() = emptyList()
    override val originalData: String get() = toString()

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

