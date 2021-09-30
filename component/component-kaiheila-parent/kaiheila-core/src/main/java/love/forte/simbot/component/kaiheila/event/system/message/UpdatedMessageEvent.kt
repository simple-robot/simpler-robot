package love.forte.simbot.component.kaiheila.event.system.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
public data class UpdatedMessageEventBody(
    /**
     * 消息id
     */
    @SerialName("msg_id")
    val msgId: String,
    /**
     * 消息所在的频道id
     */
    @SerialName("channel_id")
    val channelId: String,
    /**
     * 消息内容
     */
    val content: String,
    /**
     * At特定用户 的用户ID数组，与mention_info中的数据对应
     */
    val mention: List<String>,
    /**
     * 是否含有@全体人员
     */
    @SerialName("mention_all")
    val mentionAll: String,
    /**
     * At特定角色 的角色ID数组，与mention_info中的数据对应
     */
    @SerialName("metnion_roles")
    val mentionRoles: List<String>,
    /** */
    @SerialName("mention_here")
    val mentionHere: Boolean,
    /**
     * 更新时间戳(毫秒)
     */
    @SerialName("updated_at")
    val updatedAt: Long,
) : MessageEventExtraBody


/**
 *
 * 频道消息更新
 *
 * `updated_message`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UpdatedMessageExtra(override val body: UpdatedMessageEventBody) :
    MessageEventExtra<UpdatedMessageEventBody> {
    override val type: String
        get() = "updated_message"
}
