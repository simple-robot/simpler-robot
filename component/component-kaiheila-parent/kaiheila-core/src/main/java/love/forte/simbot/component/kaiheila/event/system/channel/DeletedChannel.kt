package love.forte.simbot.component.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 删除频道
 *
 * `deleted_channel`
 *
 * @author ForteScarlet
 */
@Serializable
public data class DeletedChannelExtraBody(
    /**
     * 被删除的频道id
     */
    val id: String,
    /**
     * 删除操作的时间戳(毫秒)
     */
    @SerialName("deleted_at")
    val deletedAt: Long
) : ChannelEventExtraBody