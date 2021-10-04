package love.forte.simbot.component.kaiheila.event.system.guild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
public data class AddedBlockListExtraBody(
    /**
     * 操作人id
     */
    @SerialName("operator_id")
    val operatorId: String,
    /**
     * 被封禁的用户ID列表
     */
    @SerialName("user_id")
    val userId: List<String>,
    /**
     * 备注
     */
    val remark: String,
): GuildEventExtraBody
