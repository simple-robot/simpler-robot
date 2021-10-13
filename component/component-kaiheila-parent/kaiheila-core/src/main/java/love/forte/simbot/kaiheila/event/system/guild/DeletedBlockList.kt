package love.forte.simbot.kaiheila.event.system.guild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * [服务器取消封禁用户](https://developer.kaiheila.cn/doc/event/guild#服务器取消封禁用户)
 *
 * `deleted_block_list`
 *
 * @author ForteScarlet
 */
@Serializable
public data class DeletedBlockListExtraBody(
    /**
     * 操作人ID
     */
    @SerialName("operator_id")
    val operatorId: String,
    /**
     * 被封禁成员id列表
     * 用户id
     */
    @SerialName("user_id")
    val userId: List<String>
) : GuildEventExtraBody
