package love.forte.simbot.component.kaiheila.event.system.guild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 频道消息更新
 *
 * `updated_message`
 *
 */
@Serializable
public data class UpdatedMessageExtraBody(
    /**
     * 消息ID
     */
    @SerialName("msg_id")
    val msgId: String,
    /**
     * 消息所在频道ID
     */
    @SerialName("channel_id")
    val channelId: String,
    /**
     * 消息内容
     */
    val content: String,
    /**
     * @特定用户 的用户ID数组，与mention_info中的数据对应
     */
    val mention: List<String>,
    /**
     * 是否存在AT全体
     */
    @SerialName("mention_all")
    val mentionAll: Boolean,
    /**
     * AT特定角色 的角色ID数组，与mention_info中的数据对应
     */
    @SerialName("mention_roles")
    val mentionRoles: List<String>,

    @SerialName("mention_here")
    val mentionHere: Boolean,
    /**
     * 更新时间戳(毫秒)
     */
    @SerialName("updated_at")
    val updatedAt: Long
) : GuildEventExtraBody
